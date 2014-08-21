package fr.lirmm.graphik.graal.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class HashMapSubstitution extends AbstractSubstitution {

	private HashMap<Term, Term> map = new HashMap<Term, Term>();

	public HashMapSubstitution() {
		super();
	}

	public HashMapSubstitution(Substitution substitution) {
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
		return new HashMapSubstitution();
	}
};
