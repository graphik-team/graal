package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleWithBuiltInPredicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQueryWithBuiltInPredicates;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseHaltingCondition;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Olivier Rodriguez
 */
public class RuleWithBuiltInPredicateApplier<T extends AtomSet> extends AbstractRuleApplier<T> {

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a DefaultRuleApplier with a {@link RestrictedChaseHaltingCondition}
	 * and a {@link SmartHomomorphism}
	 */
	public RuleWithBuiltInPredicateApplier() {
		super();
	}

	/**
	 * Construct a DefaultRuleApplier with a {@link RestrictedChaseHaltingCondition}
	 * and the given homomorphism solver.
	 */
	public RuleWithBuiltInPredicateApplier(Homomorphism<? super Query, ? super T> homomorphismSolver) {
		super(homomorphismSolver);
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition.
	 * 
	 * @param haltingCondition
	 */
	public RuleWithBuiltInPredicateApplier(ChaseHaltingCondition haltingCondition) {
		super(haltingCondition);
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition, homomorphism
	 * solver and SymbolGenerator
	 * 
	 * @param haltingCondition
	 * @param homomorphismSolver
	 */
	public RuleWithBuiltInPredicateApplier(Homomorphism<? super Query, ? super T> homomorphismSolver,
			ChaseHaltingCondition haltingCondition) {
		super(homomorphismSolver, haltingCondition);
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	protected Query generateQuery(Rule rule) {
		ConjunctiveQuery matchQuery = DefaultConjunctiveQueryFactory.instance().create(rule.getBody(),
				new LinkedList<Term>(rule.getFrontier()));

		if (rule instanceof RuleWithBuiltInPredicate) {
			RuleWithBuiltInPredicate btrule = (RuleWithBuiltInPredicate) rule;

			// TODO: propagation of exceptions
			try {
				return new DefaultConjunctiveQueryWithBuiltInPredicates(matchQuery, btrule.getBuiltInPredicates());
			} catch (IteratorException | AtomSetException e) {
				e.printStackTrace();
			}
		}
		return matchQuery;
	}
}