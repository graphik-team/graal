package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.term.Term;


public class BuiltInPredicate extends Predicate {

	private static final long serialVersionUID = 201407180000L;

	private PredicateFunction function;

	public BuiltInPredicate(String label, int arity) {
		this(label,arity,null);
	}

	public BuiltInPredicate(String label, int arity, PredicateFunction function) {
		super(label,arity);
		this.function = function;
	}

	public void setFunction(PredicateFunction f) {
		this.function = f;
	}

	public PredicateFunction getFunction() {
		return this.function;
	}

	public boolean evaluate(Term... t) {
		return (this.function != null) && (this.function.evaluate(t));
	}

};
