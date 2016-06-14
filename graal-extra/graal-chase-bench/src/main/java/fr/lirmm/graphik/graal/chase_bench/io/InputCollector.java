package fr.lirmm.graphik.graal.chase_bench.io;

import java.util.Collection;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

class InputCollector implements InputProcessor {
	protected final Collection<? super Rule> m_rules;
	protected final Collection<? super Atom> m_facts;
    protected Predicate m_currentFactPredicate;

	public InputCollector(Collection<? super Rule> rules, Collection<? super Atom> facts) {
        m_rules = rules;
        m_facts = facts;
    }

    @Override
	public void startProcessing() {
    	m_currentFactPredicate = null;
    }

    @Override
	public void processRule(Rule rule) {
        if (m_rules != null)
            m_rules.add(rule);
    }

    @Override
	public void processFact(List<Object> argumentRawForms, List<Term.Type> argumentTypes) {
        if (m_facts != null) {
			Term[] arguments = new Term[argumentRawForms.size()];
			for (int index = 0; index < argumentRawForms.size(); ++index)
				arguments[index] = DefaultTermFactory.instance().createTerm(argumentRawForms.get(index),
				    argumentTypes.get(index));
			Predicate p = DefaultPredicateFactory.instance().create(m_currentFactPredicate.getIdentifier(),
			    arguments.length);
			m_facts.add(DefaultAtomFactory.instance().create(p, arguments));
        }
    }

    @Override
	public void setFactPredicate(Predicate predicate) {
    	m_currentFactPredicate = predicate;
    }

    @Override
	public void endProcessing() {
    	m_currentFactPredicate = null;
    }

}
