package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.api.core.EffectiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

public class DefaultEffectiveQuery<Q extends Query, S extends Substitution> implements EffectiveQuery<Q, S>, AppendableToStringBuilder {
	Q query;
	S substitution;

	public DefaultEffectiveQuery(Q q, S s) {
		query = q;
		substitution = s;
	}

	@Override
	public S getSubstitution() {
		return substitution;
	}

	@Override
	public Q getQuery() {
		return query;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.appendTo(sb);
		return sb.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		sb.append(getClass().getSimpleName() + ": ");
		sb.append(getQuery());
		sb.append(getSubstitution());
		sb.append("\n");
	}
}
