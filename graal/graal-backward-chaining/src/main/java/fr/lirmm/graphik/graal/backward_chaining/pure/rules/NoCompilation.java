/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NoCompilation extends AbstractRulesCompilation {

	@Override
	public void compile(Iterator<Rule> ruleset) {
	}

	@Override
	public void load(Iterator<Rule> ruleset, Iterator<Rule> compilation) {
	}

	@Override
	public Iterable<Rule> getSaturation() {
		return Collections.emptyList();
	}

	@Override
	public boolean isCompilable(Rule r) {
		return false;
	}

	@Override
	public boolean isMappable(Atom father, Atom son) {
		return false;
	}

	@Override
	public Collection<Substitution> getMapping(Atom father, Atom son) {
		return Collections.emptyList();
	}

	@Override
	public boolean isUnifiable(Atom father, Atom son) {
		return father.getPredicate().equals(son.getPredicate());
	}

	@Override
	public Collection<TermPartition> getUnification(Atom father, Atom son) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		TermPartition p = TermPartition.getPartitionByPosition(father, son);
		if (p != null)
			res.add(p);
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		return false;
	}

	@Override
	public Collection<Atom> getRewritingOf(Atom father) {
		return Collections.singleton(father);
	}

	@Override
	public Collection<Predicate> getUnifiablePredicate(Predicate p) {
		return Collections.singleton(p);
	}

}
