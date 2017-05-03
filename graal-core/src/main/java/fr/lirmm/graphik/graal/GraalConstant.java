package fr.lirmm.graphik.graal;

import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

public final class GraalConstant {

	public static final Prefix INTERNAL_PREFIX = new Prefix("graal","http://www.lirmm.fr/graphik/graal/");
	public static final Prefix FRESH_PREFIX    = new Prefix("graal-fresh","http://www.lirmm.fr/graphik/graal/fresh/");

	public static final Predicate freshPredicate(final int arity) {
		return new Predicate(URIUtils.createURI("p" + (_predicate_count++), FRESH_PREFIX), arity);
	}

	public static final Term freshConstant() {
		return DefaultTermFactory.instance().createConstant(URIUtils.createURI("c" + (_constant_count++), FRESH_PREFIX));

	}

	private static int _predicate_count = 0;
	private static int _constant_count  = 0;

	private GraalConstant() { }
};

