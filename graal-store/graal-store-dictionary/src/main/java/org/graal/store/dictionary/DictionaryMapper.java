package org.graal.store.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.core.mapper.TermMapper;
import fr.lirmm.graphik.graal.api.factory.PredicateFactory;
import fr.lirmm.graphik.graal.api.factory.TermFactory;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.mapper.AbstractMapper;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * Abstract class which implements {@link DictionaryMapping}
 * 
 * @author mathieu dodard
 * @author renaud colin
 *
 */
public abstract class DictionaryMapper extends AbstractMapper implements DictionaryMapping, TermMapper {

	public static final int CONSTANT_TERM = 0;
	public static final int LITERAL_TERM = 1;
	public static final int CONSTANT_PREDICATE = 2;
	public static final int URI_PREDICATE = 3;
	public static final int EXISTENTIAL_VAR = 4;

	/**
	 * List of each term URI/predicate URI
	 */
	protected ArrayList<String> identifiers;

	/**
	 * The begin index into {@link identifiers} of existential variables
	 * identifiers[0,existentialBeginIdx[ contains URI of terms and predicate
	 * identifiers identifiers[existentialBeginIdx,|identifiers|] contains
	 * existential var identifiers
	 */
	protected int existentialBeginIdx;

	/**
	 * The data type for each identifiers
	 */
	protected ArrayList<Integer> dataTypes;

	/**
	 * 
	 */
	protected ArrayList<Predicate> predicates;

	/**
	 * Indicate if predicate list is updated or not
	 */
	protected boolean predicatesUpdate;

	/**
	 * Association between original predicate URI(s) and their arity
	 */
	protected HashMap<String, Integer> predicateAritys;

	/**
	 * 
	 */
	protected ArrayList<Term> terms;

	/**
	 * 
	 */
	private TermFactory termFactory;

	/**
	 * 
	 */
	private PredicateFactory predicateFactory;

	/**
	 * 
	 */
	protected ArrayList<Boolean> usedTerms;

	/**
	 * 
	 */
	protected ArrayList<Boolean> usedPredicates;

	public DictionaryMapper() {
		super();
		// termIds=new TreeMap<>();
		identifiers = new ArrayList<>();
		dataTypes = new ArrayList<>();
		termFactory = DefaultTermFactory.instance();
		predicateFactory = DefaultPredicateFactory.instance();

//		termsTypes = new HashMap<>();
//		predicateTypes = new HashMap<>();
		predicateAritys = new HashMap<>();
		predicates = new ArrayList<>();
		terms = new ArrayList<>();
		predicatesUpdate = false;

		usedTerms = new ArrayList<>();
		usedPredicates = new ArrayList<>();
	}

	/**
	 * Fill the list of mapped predicates and terms
	 */
	protected void fillTermsAndPredicates() {
		for (int i = 0; i < existentialBeginIdx; i++) {
			int datatype = dataTypes.get(i);

			if (datatype == CONSTANT_PREDICATE || datatype == URI_PREDICATE) {
				String predUri = identifiers.get(i);
				int predArity = predicateAritys.get(predUri);
				Predicate pred = predicateFactory.create(i, predArity);
				predicates.add(pred);
			} else {
				Term term = termFactory.createConstant(i);
				terms.add(term);
			}
		}
		predicatesUpdate = true;
	}

	///////////////////////////////
	////// Getter METHODS ////////
	///////////////////////////////

	/**
	 * Return the list of mapped predicates Complexity O(1)
	 */
	public Collection<Predicate> getAllMappedPredicates() {
		return predicates;
	}

	/**
	 * @return the list of unmapped predicates Complexity O(n) with n predicate into
	 *         dictionary
	 */
	public Collection<Predicate> getAllUnMappedPredicates() {
		List<Predicate> unmappedPredicates = new ArrayList<>(predicates.size());
//		predicates.forEach(pred -> unmappedPredicates.add(map(pred)));

		for (Predicate pred : predicates)
			unmappedPredicates.add(map(pred));

		return unmappedPredicates;
	}

	/**
	 * 
	 */
	public CloseableIterator<Predicate> predicateIterator() {
		return new CloseableIteratorAdapter<Predicate>(predicates.iterator());
	}

	/**
	 * Return the list of all mapped terms Complexity O(1)
	 */
	public ArrayList<Term> getAllMappedTerms() {
		return terms;
	}

	/**
	 * @return the list of unmapped terms Complexity O(n) with n term into
	 *         dictionary
	 */
	public Collection<Term> getAllUnMappedTerms() {
		List<Term> unmappedPredicates = new ArrayList<>(predicates.size());
//		terms.forEach(pred -> unmappedPredicates.add(map(pred)));

		for (Term term : terms)
			unmappedPredicates.add(map(term));

		return unmappedPredicates;
	}

	/**
	 * 
	 */
	public CloseableIterator<Term> termIterator() {
		return new CloseableIteratorAdapter<Term>(terms.iterator());
	}

	/**
	 * Return the list of all URI(s)
	 */
	public ArrayList<String> getIdentifiers() {
		return identifiers;
	}

	/**
	 * The data type for each identifiers
	 */
	public ArrayList<Integer> getDataTypes() {
		return dataTypes;
	}

	/**
	 * 
	 * @return the map which associate an integer index to an URI as string
	 */
	public abstract Map<String, Integer> getIdentifierDictionary();

	// /////////////////////////////////////////////////////////////////////////
	// BUILD Method
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Add a list of atoms to the dictionary
	 * 
	 * @param atoms : list of atoms from a iterator
	 * @return
	 */
	public boolean addAll(CloseableIterator<Object> atoms) {
		try {
			while (atoms.hasNext()) {
				Object next = atoms.next();
				if (next instanceof Atom) {
					add((Atom) next);
				} else if (next instanceof Rule) {
					add((Rule) next);
				}
			}
		} catch (IteratorException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Add a list of atoms to the dictionary
	 * 
	 * @param atoms
	 * @return
	 */
	public boolean addAll(Collection<Object> data) {
		for (Object atom : data) {
			if (atom instanceof Atom) {
				add((Atom) atom);
			} else if (atom instanceof Rule) {
				add((Rule) atom);
			}
		}
		return true;
	}

	/**
	 * add the body and the head of a rule to the dictionary
	 * 
	 * @param rule
	 */
	public void add(Rule rule) {

		try {
			CloseableIterator<Atom> it = rule.getHead().iterator();
			while (it.hasNext()) {
				add(it.next());
			}
			it.close();
			it = rule.getBody().iterator();
			while (it.hasNext()) {
				add(it.next());
			}
			it.close();
		} catch (IteratorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add the predicate and the terms of an atom to dictionary
	 * 
	 * @param atom
	 */
	public void add(Atom atom) {

		for (Term term : atom.getTerms()) {
			if (!term.isVariable()) {
				Object termId = term.getIdentifier();
				identifiers.add(termId.toString());
				dataTypes.add(termId instanceof URI ? LITERAL_TERM : CONSTANT_TERM);
			}
		}
		Object predId = atom.getPredicate().getIdentifier();
		identifiers.add(predId.toString());
		dataTypes.add(predId instanceof URI ? URI_PREDICATE : CONSTANT_PREDICATE);
		predicateAritys.put(predId.toString(), atom.getPredicate().getArity());
	}

	/**
	 * add obj to the dictionary
	 * 
	 * @param obj
	 */
	public void add(Object obj) {
		if (obj instanceof Atom) {
			add((Atom) obj);
		} else if (obj instanceof Rule) {
			add((Rule) obj);
		}
	}

	/**
	 * Build the dictionary by computing id for each term into the map
	 */
	public void build() {
		buildDictionary();
		fillTermsAndPredicates();
	}

	/**
	 * Build the dictionary
	 */
	public abstract void buildDictionary();

	// /////////////////////////////////////////////////////////////////////////
	// MAPPING Method
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Integer getPredicateId(Predicate predicate) {
		String predicateId = predicate.getIdentifier().toString();
		return getIntegerIdOf(predicateId);
	}

	/**
	 * 
	 * @param term
	 * @throws @return
	 */
	public Term mapExistentialVar(Term term) {
		if (!term.isVariable()) {
			throw new IllegalArgumentException("Error term must be a variable :" + term.toString());
		}
		Integer termId = getTermId(term);
		Term newTerm = termFactory.createConstant(termId);
		return newTerm;
	}

	@Override
	public Term map(Term term) {
		if (term.isVariable()) {
			return term;
		}
		Integer termId = getTermId(term);
		if (term.isLiteral() || term.isConstant()) {
			return termFactory.createConstant(termId);
		}
		return term;
	}

	@Override
	public Term unmap(Term term) {
		if (term.isVariable()) {
			return term;
		}
		Object identifier = term.getIdentifier();
		if (identifier instanceof Integer) {
			int intId = (Integer) identifier;
			String termURI = getStringIdOf(intId);
			if (intId >= existentialBeginIdx) { // term is existential
				return termFactory.createVariable(termURI);
			}
			int datatype = dataTypes.get(intId);
			return datatype == CONSTANT_TERM ? termFactory.createConstant(termURI)
					: termFactory.createConstant(new DefaultURI(termURI));
		}
		return term;
	}

	@Override
	public Predicate map(Predicate predicate) {
		Integer id = getIntegerIdOf(predicate.getIdentifier().toString());
		Predicate newPredicate = predicateFactory.create(id, predicate.getArity());
		return newPredicate;
	}

	@Override
	public Predicate unmap(Predicate predicate) {
		Object predId = predicate.getIdentifier();
		if (predId instanceof Integer) {
			int intId = (Integer) predId;
			int predType = dataTypes.get(intId);
			String oldPredUri = getStringIdOf(intId);
			Object newIdentifier = predType == URI_PREDICATE ? new DefaultURI(oldPredUri) : oldPredUri;
			return predicateFactory.create(newIdentifier, predicate.getArity());
		}
		return predicate;
	}

	public Substitution map(Substitution substitution) {
		TreeMapSubstitution newSub = new TreeMapSubstitution();
		for (Variable var : substitution.getTerms()) {
			newSub.put(var, map(substitution.createImageOf(var)));
		}
		return newSub;
	}

	public Substitution unmap(Substitution substitution) {
		TreeMapSubstitution newSub = new TreeMapSubstitution();
		for (Variable var : substitution.getTerms()) {
			Term term = unmap(substitution.createImageOf(var));
			newSub.put(var, term);
		}
		return newSub;
	}

	public List<Term> map(List<Term> terms) {
		List<Term> newTerms = new ArrayList<>(terms.size());
		for (Term term : terms) {
			newTerms.add(map(term));
		}
		return newTerms;
	}

	public List<Term> unmap(List<Term> terms) {
		List<Term> oldTerms = new ArrayList<>(terms.size());
		for (Term term : terms) {
			oldTerms.add(unmap(term));
		}
		return oldTerms;
	}

	@Override
	public String getStringIdOf(int termId) {
		return (identifiers.size() > termId && termId >= 0) ? identifiers.get(termId) : null;
	}

	@Override
	public Integer getTermId(Term term) {
		String termURI = term.getIdentifier().toString();
		return getIntegerIdOf(termURI);
	}

	/**
	 * Add a existential variable to the dictionary You should use this method to
	 * insert existential term
	 * 
	 * @param term
	 */
	public void addExistentialTerm(Term term) {
		Map<String, Integer> map = getIdentifierDictionary();
		String termURI = term.getIdentifier().toString();
		map.put(termURI, map.size());
		identifiers.add(termURI);

		Term newTerm = termFactory.createVariable(map.size() - 1);
		dataTypes.add(EXISTENTIAL_VAR);
		terms.add(newTerm);
	}

	/**
	 * Add a list of existential variables to the dictionary You should use this
	 * method to insert existential terms
	 * 
	 * @param existentialTerms
	 */
	public void addAllExistentialVariables(Collection<Term> existentialTerms) {
		for (Term term : existentialTerms) {
			addExistentialTerm(term);
		}
	}

}
