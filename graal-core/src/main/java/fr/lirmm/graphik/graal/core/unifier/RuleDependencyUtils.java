package fr.lirmm.graphik.graal.core.unifier;

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.unifier.DependencyChecker;

/**
 * @author Olivier Rodriguez
 */
public class RuleDependencyUtils {

	private RuleDependencyUtils() {
	}

	/**
	 * Validate a dependency between r1 (source) and r2 (target) with one of their unifier according to the checkers.
	 * 
	 * @param r1          Source rule
	 * @param r2          Target rule
	 * @param unifierSubs One unifier to check
	 * @param checkers    The DependencyCheckers
	 * @return True if is a valid dependency according to the checkers
	 */
	public static boolean validateUnifier(Rule r1, Rule r2, Substitution unifierSubs, DependencyChecker checkers[]) {

		for (DependencyChecker checker : checkers) {

			if (!checker.isValidDependency(r1, r2, unifierSubs))
				return false;
		}
		return true;
	}

	/**
	 * Delete the unifiers from $unifiers that doesn't validate all the checkers.
	 * 
	 * @param r1       Source rule
	 * @param r2       Target rule
	 * @param unifiers The unifiers to check (clean)
	 * @param checkers The DependencyCheckers
	 */
	public static void cleanUnifiers(Rule r1, Rule r2, Iterator<Substitution> unifiers, DependencyChecker checkers[]) {

		while (unifiers.hasNext()) {

			if (!validateUnifier(r1, r2, unifiers.next(), checkers))
				unifiers.remove();
		}
	}

	/**
	 * @see #cleanUnifiers(Rule, Rule, Iterator, DependencyChecker[])
	 */
	public static void cleanQueryUnifiers(Rule r1, Rule r2, Iterator<QueryUnifier> unifiers, DependencyChecker checkers[]) {

		while (unifiers.hasNext()) {

			if (!RuleDependencyUtils.validateUnifier(r1, r2, unifiers.next().getAssociatedSubstitution(), checkers))
				unifiers.remove();
		}
	}
}
