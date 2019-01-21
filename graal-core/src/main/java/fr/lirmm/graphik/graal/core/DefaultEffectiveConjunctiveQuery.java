package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.EffectiveConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Substitution;

public class DefaultEffectiveConjunctiveQuery extends DefaultEffectiveQuery<ConjunctiveQuery, Substitution> implements EffectiveConjunctiveQuery {

	public DefaultEffectiveConjunctiveQuery(ConjunctiveQuery q, Substitution s) {
		super(q, s);
	}
}
