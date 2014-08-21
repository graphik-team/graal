package fr.lirmm.graphik.graal.core;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class TreeMapSubstitution extends AbstractSubstitution {

	private TreeMap<Term, Term> map = new TreeMap<Term, Term>();

	public TreeMapSubstitution() {
		super();
	}

	public TreeMapSubstitution(Substitution substitution) {
		super();
		for (Term term : substitution.getTerms())
			this.map.put(term, substitution.getSubstitut(term));
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AbstractSubstitution#getMap()
	 */
	@Override
	protected Map<Term, Term> getMap() {
		return this.map;
	}

	@Override
	protected Substitution getNewInstance() {
		return new TreeMapSubstitution();
	}

	
	
};
