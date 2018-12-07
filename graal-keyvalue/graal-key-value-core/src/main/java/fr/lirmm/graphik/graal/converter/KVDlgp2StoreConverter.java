package fr.lirmm.graphik.graal.converter;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.factory.AtomFactory;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

/**
 * Converter from DlgpParserInput KeyValue data format to standard Graal Atom.
 * <br>
 * <br>
 * In this format the Predicate can be a path, with its different parts (ie.
 * keys) delimited with a point '.'.<br>
 * An example of such Atom:<br>
 * <br>
 * <code>&lt;part1.part2&gt;(X)</code><br>
 * <br>
 * This converter cut the paths and create for each part a binary Atom, and for
 * the first path an unary Atom (ie. the root). With our example we will have:
 * <code>root\1(X0). part1\2(X0,X1). part2\2(X1,X).</code> <br>
 * Note that the described Atom's format is valid for a Rule. For representing a
 * fact you must add a number path to identify the record you're writing. <br>
 * <br>
 * As an example : <br>
 * <br>
 * <code>
 * &#64;rules<br>
 * &lt;human.name&gt;(Y) :- &lt;human&gt;(X).<br>
 * <br>
 * &#64;facts<br>
 * &lt;0.human&gt;(alice).<br>
 * &lt;0.human.name&gt;("Alice").<br>
 * &lt;1.human&gt;(bob).<br>
 * &lt;1.human.name&gt;("Bob").<br>
 * </code>
 * 
 * @author Olivier Rodriguez
 */
public class KVDlgp2StoreConverter implements Converter<Object, Object> {

	/**
	 * Bucket for state storage, it stores the state of the conversion between
	 * multiple calls. The DlgpParser send indeed one Atom or Rule at a time, and in
	 * our case of KeyValue Data we must be able to remember some informations about
	 * the data structure already converted.
	 * 
	 * @author zuri
	 */
	private static class ConvertState {
		private boolean isFact = true;
		private String varPrefix = "X";
		private int currentVarSuffix = 0;

		/**
		 * Associated a path of the form part1.part2 with is second variable, which
		 * correspond to the output node, in the fact part2\2(X1,X2).
		 */
		Map<String, Term> relationTerms = new HashMap<>();
	}

	ConvertState atomsState = new ConvertState();
	{
		atomsState.varPrefix = "NODE";
	}

	@Override
	public Object convert(Object object) throws ConversionException {

		try {

			if (object instanceof Rule)
				return convertRule((Rule) object);

			if (object instanceof Atom)
				return convertAtom((Atom) object, atomsState).iterator();

			return object;
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}

	// ========================================================================
	// Private static
	// ========================================================================

	/**
	 * Create a new predicate
	 * 
	 * @param prefix May be null if the predicate is not an URI
	 * @param part   Identifier
	 * @param arity
	 * @return URI|String according to the presence of $prefix
	 */
	private static Predicate makeNewPredicate(String prefix, String part, int arity) {
		Object id;

		if (prefix == null)
			id = part;
		else
			id = new DefaultURI(prefix, part);

		return new Predicate(id, arity);
	}

	/**
	 * Create a new Term according to the current $state.
	 * 
	 * @param state
	 * @return
	 */
	private static Term makeNewTerm(ConvertState state) {
		return DefaultTermFactory.instance().createVariable(state.varPrefix + state.currentVarSuffix++);
	}

	/**
	 * Convert a path Atom into a set of binary Atoms and one unary Atom for the
	 * root.
	 * 
	 * @param atom
	 * @param state
	 * @return
	 * @throws ConversionException
	 */
	private static InMemoryAtomSet convertAtom(Atom atom, ConvertState state) throws ConversionException {
		Map<String, Term> relationTerms = state.relationTerms;
		AtomFactory atomFactory = DefaultAtomFactory.instance();
		List<Atom> newAtoms = new ArrayList<>();
		String prefix = null;
		String id_s;

		// Get id_s and prefix if exists.
		{
			Object id = atom.getPredicate().getIdentifier();

			if (id instanceof URI) {
				prefix = ((URI) id).getPrefix();
				id_s = ((URI) id).getLocalname();
			} else if (id instanceof String) {
				id_s = (String) id;
			} else
				throw new InvalidParameterException("Predicate identifier must be an URI or a String");
		}
		StringBuilder pathAccu = new StringBuilder();
		Term lastTerm = null;
		Atom lastAtom = null;
		boolean isPrefix = true;
		boolean rootComputed = false;
		String rootPart = "";

		for (String part : id_s.split("\\.")) {

			if (rootComputed)
				pathAccu.append('.' + part);
			// Add the root term
			else {
				Term root;

				/*
				 * Special case for a input fact. The Predicate identifier must be prefixed by a
				 * number which is the identifier of the record. However this prefix must not be
				 * a part of a new predicate identifier. This block handle this problem.
				 */
				if (state.isFact && rootPart.isEmpty()) {

					if (!StringUtils.isNumeric(part))
						throw new ConversionException(
								"The first element of a fact must be a number: " + part + " given");

					rootPart = part;
					// The root of the fact will be the next part
					continue;
				}
				rootComputed = true;
				pathAccu.append(part);

				String termId = rootPart + '.' + part;

				root = relationTerms.get(termId);

				// This is the first time this root is meet.
				if (root == null) {
					root = makeNewTerm(state);
					relationTerms.put(termId, root);
				}
				lastTerm = root;
				newAtoms.add(atomFactory.create(makeNewPredicate(prefix, "root", 1), root));
			}
			Term newTerm;
			String termId = rootPart + pathAccu;

			// Get the current term to add
			{
				Term alreadyComputed = relationTerms.get(termId);

				// Found
				if (isPrefix && alreadyComputed != null) {
					newTerm = alreadyComputed;
				}
				// New
				else {
					isPrefix = false;
					newTerm = makeNewTerm(state);
					relationTerms.put(termId, newTerm);
				}
			}
			lastAtom = atomFactory.create(makeNewPredicate(prefix, part, 2), lastTerm, newTerm);
			newAtoms.add(lastAtom);
			lastTerm = newTerm;
		}
		lastAtom.setTerm(1, atom.getTerm(0));
		return DefaultAtomSetFactory.instance().create(newAtoms.toArray(new Atom[0]));

	}

	/**
	 * Convert an AtomSet of one path Atom to an equivalent AtomSet with binary
	 * relations and unary root\1(x).
	 * 
	 * @param atomSet
	 * @param relationTerms A list of pair which associate a path part with its
	 *                      first variable from its atom conversion.
	 * @return
	 * @throws ConversionException
	 */
	private static InMemoryAtomSet convertAtomSet(InMemoryAtomSet atomSet, ConvertState state)
			throws ConversionException {
		Atom atomRef = DefaultAtomFactory.instance().create(atomSet.iterator().next());
		return convertAtom(atomRef, state);
	}

	/**
	 * Convert an input Rule containing path Atoms into an equivalent Rule with
	 * binary relations and unary root\1(x).
	 * 
	 * @param rule
	 * @return
	 * @throws AtomSetException
	 * @throws IteratorException
	 * @throws ConversionException
	 */
	private static Rule convertRule(Rule rule) throws AtomSetException, IteratorException, ConversionException {
		InMemoryAtomSet rhead = rule.getHead();
		InMemoryAtomSet rbody = rule.getBody();
		int headSize = rhead.size();
		int bodySize = rbody.size();

		if (headSize != 1)
			throw new AtomSetException("The head must contains 1 atom: " + headSize + "given");

		if (bodySize != 1)
			throw new AtomSetException("The body must contains 1 atom: " + bodySize + "given");

		ConvertState state = new ConvertState();
		state.isFact = false;

		return new DefaultRule(convertAtomSet(rbody, state), convertAtomSet(rhead, state));
	}
}