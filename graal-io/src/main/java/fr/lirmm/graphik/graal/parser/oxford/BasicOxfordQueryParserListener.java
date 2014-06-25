/**
 * 
 */
package fr.lirmm.graphik.graal.parser.oxford;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class BasicOxfordQueryParserListener implements
		OxfordQueryParserListener {
	
	private enum State {
		HEAD, BODY
	}
	
	private DefaultConjunctiveQuery cquery = null;
	private State state;
	private LinkedList<Term> awsweredVariables = new LinkedList<Term>();
	private LinkedListAtomSet body = new LinkedListAtomSet();

	private LinkedList<Term> termsOfCurrentAtom = null;
	private String predicateLabelOfCurrentAtom = null;

	@Override
	public DefaultConjunctiveQuery getQuery() {
		return this.cquery;
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#startQuery()
	 */
	@Override
	public void startQuery() {
		this.state = State.HEAD;
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#endOfQuery()
	 */
	@Override
	public void endOfQuery() {
		this.cquery = new DefaultConjunctiveQuery(this.body, this.awsweredVariables);
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#startBody()
	 */
	@Override
	public void startBody() {
		this.state = State.BODY;
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#startAtom()
	 */
	@Override
	public void startAtom() {
		this.termsOfCurrentAtom = new LinkedList<Term>();
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#endOfAtom()
	 */
	@Override
	public void endOfAtom() {
		Predicate predicate = new Predicate(this.predicateLabelOfCurrentAtom, this.termsOfCurrentAtom.size());
		Atom atom = new DefaultAtom(predicate, this.termsOfCurrentAtom);
		this.body.add(atom);
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#predicate(java.lang.String)
	 */
	@Override
	public void predicate(String label) {
		this.predicateLabelOfCurrentAtom = label;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#constant(java.lang.String)
	 */
	@Override
	public void constant(String label) {
		Term term = new Term(label, Term.Type.CONSTANT);
		switch(state) {
		case HEAD:
			this.awsweredVariables.add(term);
			break;
		case BODY:
			this.termsOfCurrentAtom.add(term);
		}
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParserListener#variable(java.lang.String)
	 */
	@Override
	public void variable(String label) {
		Term term = new Term(label, Term.Type.VARIABLE);
		switch(state) {
		case HEAD:
			this.awsweredVariables.add(term);
			break;
		
		case BODY:
			this.termsOfCurrentAtom.add(term);
			break;
		}
	}



}
