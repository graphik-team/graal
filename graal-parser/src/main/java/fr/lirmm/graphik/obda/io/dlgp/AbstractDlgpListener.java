/**
 * 
 */
package fr.lirmm.graphik.obda.io.dlgp;

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
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractDlgpListener implements ParserListener {
    
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractDlgpListener.class);
	
	private List<Term> answerVars;
	private LinkedListAtomSet AtomSet = null;
	private LinkedListAtomSet AtomSet2 = null;
	private DefaultAtom atom;
	private String label;

	private OBJECT_TYPE objectType;

	protected abstract void createAtom(DefaultAtom atom);

	protected abstract void createQuery(DefaultConjunctiveQuery query);
	
	protected abstract void createRule(DefaultRule basicRule);
	
	protected abstract void createNegConstraint(NegativeConstraint negativeConstraint);

	@Override
	public void startsObject(OBJECT_TYPE objectType, String name) {
		this.label = (name == null)? "" : name;
		
		AtomSet = AtomSet2 = null;
		this.objectType = objectType;
		
		switch (objectType) {
		case QUERY:
			this.answerVars = new LinkedList<Term>();
			this.AtomSet = new LinkedListAtomSet();
			break;
		case RULE:
		case NEG_CONSTRAINT:
			this.AtomSet = new LinkedListAtomSet();
			break;
		case FACT:
			break;
		default:
			logger.warn("Unrecognized object type: " + objectType);
			break;
		}
		
	}
	
	

	@Override
	public void createsAtom(String predicate, Object[] terms) {

		List<Term> list = new LinkedList<Term>();
		for (Object t : terms)
			list.add((Term) t);
		
		predicate = removeQuotes(predicate);

		atom = new DefaultAtom(new Predicate(predicate, terms.length), list);

		switch (objectType) {
		case FACT:
			this.createAtom(atom);
			break;
		case QUERY:
		case RULE:
		case NEG_CONSTRAINT:
			this.AtomSet.add(atom);
			break;
		default:
			break;
		}
	}

	/**
	 * @param predicate
	 */
	private String removeQuotes(String predicate) {
		if(predicate.startsWith("\"") && predicate.endsWith("\"")) {
			return predicate.substring(1, predicate.length() - 1);
		} else {
			return predicate;
		}
	}

	@Override
	public void createsEquality(Object term1, Object term2) {
		logger.warn("Unsupported equality predicate !");
	}

	@Override
	public void answerVariableList(Object[] terms) {
		for (Object t : terms)
			this.answerVars.add((Term) t);
	}

	@Override
	public void endsConjunction(OBJECT_TYPE objectType) {
		switch (objectType) {
		case QUERY:
			this.createQuery(new DefaultConjunctiveQuery(this.AtomSet, this.answerVars));
			break;
		case NEG_CONSTRAINT:
			this.createNegConstraint(new NegativeConstraint(this.label, this.AtomSet));
			break;
		case RULE:
			if(this.AtomSet2 == null) {
    			this.AtomSet2 = this.AtomSet;
    			this.AtomSet = new LinkedListAtomSet();
			} else {
				this.createRule(new DefaultRule(this.label, this.AtomSet, this.AtomSet2));
			}
			break;
		default:
			break;
		}
	}

}