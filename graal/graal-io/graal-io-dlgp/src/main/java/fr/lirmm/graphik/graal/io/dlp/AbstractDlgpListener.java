/**
 * 
 */
package fr.lirmm.graphik.graal.io.dlp;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parser.ParserListener;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.Constant;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.PrefixManager;
import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
abstract class AbstractDlgpListener implements ParserListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractDlgpListener.class);

	private List<Term> answerVars;
	private LinkedListAtomSet atomSet = null;
	private LinkedListAtomSet atomSet2 = null;
	private DefaultAtom atom;
	private String label;

	private OBJECT_TYPE objectType;

	protected abstract void createAtom(DefaultAtom atom);

	protected abstract void createQuery(DefaultConjunctiveQuery query);

	protected abstract void createRule(DefaultRule basicRule);

	protected abstract void createNegConstraint(
			NegativeConstraint negativeConstraint);

	@Override
	public void startsObject(OBJECT_TYPE objectType, String name) {
		this.label = (name == null) ? "" : name;

		atomSet = atomSet2 = null;
		this.objectType = objectType;

		switch (objectType) {
		case QUERY:
			this.answerVars = new LinkedList<Term>();
			this.atomSet = new LinkedListAtomSet();
			break;
		case RULE:
		case NEG_CONSTRAINT:
			this.atomSet = new LinkedListAtomSet();
			break;
		case FACT:
			break;
		default:
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Unrecognized object type: " + objectType);
			}
			break;
		}

	}

	@Override
	public void createsAtom(Object predicate, Object[] terms) {
		List<Term> list = new LinkedList<Term>();
		for (Object t : terms) {
			list.add(createTerm(t));
		}

		atom = new DefaultAtom(createPredicate((URI) predicate, terms.length),
				list);

		switch (objectType) {
		case FACT:
			this.createAtom(atom);
			break;
		case QUERY:
		case RULE:
		case NEG_CONSTRAINT:
			this.atomSet.add(atom);
			break;
		default:
			break;
		}
	}

	@Override
	public void createsEquality(Object term1, Object term2) {
		atom = new DefaultAtom(Predicate.EQUALITY, createTerm(term1),
				createTerm(term2));
	}

	@Override
	public void answerTermList(Object[] terms) {
		for (Object t : terms) {
			this.answerVars.add((Term) t);
		}
	}

	@Override
	public void endsConjunction(OBJECT_TYPE objectType) {
		switch (objectType) {
		case QUERY:
			this.createQuery(new DefaultConjunctiveQuery(this.label,
					this.atomSet, this.answerVars));
			break;
		case NEG_CONSTRAINT:
			this.createNegConstraint(new NegativeConstraint(this.label,
					this.atomSet));
			break;
		case RULE:
			if (this.atomSet2 == null) {
				this.atomSet2 = this.atomSet;
				this.atomSet = new LinkedListAtomSet();
			} else {
				this.createRule(new DefaultRule(this.label, this.atomSet,
						this.atomSet2));
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void declarePrefix(String prefix, String ns) {
		Prefix p = new Prefix(prefix.substring(0, prefix.length()), ns);
		PrefixManager.getInstance().putPrefix(p);
	}

	@Override
	public void declareBase(String base) {
		System.out.println(base);
	}

	@Override
	public void declareTop(String top) {
		System.out.println("top " + top);
	}

	@Override
	public void declareUNA() {
		System.out.println("una ");
	}

	@Override
	public void directive(String text) {
		System.out.println("directive " + text);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private Predicate createPredicate(URI uri, int arity) {
		return new Predicate(uri, arity);
	}

	private Constant createConstant(URI uri) {
		return DefaultTermFactory.instance().createConstant(uri);
	}

	private Term createTerm(Object t) {
		if (t instanceof Term) {
			return (Term) t;
		} else if (t instanceof URI) {
			return createConstant((URI) t);
		}
		return null;
	}

}