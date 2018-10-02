package fr.lirmm.graphik.graal.api.util;

import java.util.List;

import fr.lirmm.graphik.graal.api.core.BuiltInPredicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.util.URIUtils;

/**
 * @author Olivier Rodriguez
 */
final public class ClassicBuiltInPredicates {

	private ClassicBuiltInPredicates() {
	}

	public static BuiltInPredicate[] defaultPredicates() {
		return new BuiltInPredicate[] { new PredicateNeq("bt__neq"), new PredicateEq("bt__eq") };
	}

	public static BuiltInPredicate[] owlPredicates() {
		return new BuiltInPredicate[] {
				new PredicateNeq(URIUtils.createURI("http://www.w3.org/2002/07/owl#differentFrom")),
				new PredicateEq(URIUtils.createURI("http://www.w3.org/2002/07/owl#sameAs")) };
	}

	@SuppressWarnings("serial")
	public static class PredicateNeq extends BuiltInPredicate {

		public PredicateNeq(Object label) {
			super(label, 2);
		}

		@Override
		public boolean validate(List<Term> terms) throws HomomorphismException {

			if (terms.size() != getArity())
				return false;

			int i = 0;

			if (terms.get(i++).isVariable() || terms.get(i++).isVariable())
				throw new HomomorphismException("The argument at position " + i + " from predicate " + this
						+ " is a variable wich cannot be validated.");

			return !terms.get(0).equals(terms.get(1));
		}
	}

	@SuppressWarnings("serial")
	public static class PredicateEq extends BuiltInPredicate {

		public PredicateEq(Object label) {
			super(label, 2);
		}

		@Override
		public boolean validate(List<Term> terms) throws HomomorphismException {

			if (terms.size() != getArity())
				return false;

			int i = 0;

			if (terms.get(i++).isVariable() || terms.get(i++).isVariable())
				throw new HomomorphismException("The argument at position " + i + " from predicate " + this
						+ " is a variable wich cannot be validated.");

			return terms.get(0).equals(terms.get(1));
		}
	}
}