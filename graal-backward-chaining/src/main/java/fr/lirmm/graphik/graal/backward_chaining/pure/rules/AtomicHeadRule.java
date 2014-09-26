package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.AtomicAtomSet;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * rule with only one atom in its head
 * 
 * @author Mélanie KÖNIG
 * 
 */
public class AtomicHeadRule extends DefaultRule {

	/**
	 * Construct an AtomicHeadRule
	 * 
	 * @param b
	 *            a fact
	 * @param h
	 *            must be an AtomicFact
	 * @throws Exception
	 */
	public AtomicHeadRule(AtomSet b, Atom h) {
		super(b, new AtomicAtomSet(h));
	}

	/**
	 * change the head of this rule by the given fact
	 * 
	 * @param h
	 *            must be an AtomicFact
	 */
	public void setHead(Atom h) {
		LinkedListAtomSet atomset = new LinkedListAtomSet();
		atomset.add(h);
		super.getHead();
	}

	@Override
	public AtomicAtomSet getHead() {
		return (AtomicAtomSet) super.getHead();
	}

}
