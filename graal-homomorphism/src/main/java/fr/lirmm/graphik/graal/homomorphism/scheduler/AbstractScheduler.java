package fr.lirmm.graphik.graal.homomorphism.scheduler;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;

public abstract class AbstractScheduler extends AbstractProfilable implements Scheduler {
	
	
	protected static InMemoryAtomSet computeFixedQuery(InMemoryAtomSet atomset, Iterable<? extends Variable> fixedTerms) {
		// create a Substitution for fixed query
		Substitution fixSub = DefaultSubstitutionFactory.instance().createSubstitution();
		for (Variable t : fixedTerms) {
			fixSub.put(t, DefaultTermFactory.instance().createConstant(t.getLabel()));
		}

		return fixSub.createImageOf(atomset);
	}

}
