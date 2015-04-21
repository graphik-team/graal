/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.rule_applier;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleApplicationException;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleApplier;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;

/**
 * SQLRuleApplier transform rules into INSERT ... SELECT SQL statement when it
 * is possible. If not, it call the apply method of the specified RuleApplier.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SQLRuleApplier<T extends RdbmsStore> implements
		RuleApplier<Rule, T> {

	private RuleApplier<Rule, T> fallback;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a SQLRuleApplier with a DefaultRuleApplier as fallback.
	 */
	public SQLRuleApplier(Homomorphism<ConjunctiveQuery, T> homomorphismSolver) {
		this.fallback = new DefaultRuleApplier<>(homomorphismSolver);
	}

	/**
	 * Construct a SQLRuleApplier with the specified rule applier as fallback.
	 * 
	 * @param ruleApplierFallback
	 */
	public SQLRuleApplier(RuleApplier<Rule, T> ruleApplierFallback) {
		this.fallback = ruleApplierFallback;
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public boolean apply(Rule rule, T store)
			throws RuleApplicationException {
		boolean returnValue = false;
		if (rule.getExistentials().isEmpty()) {
			Statement statement = null;
			try {
				statement = store.getDriver().createStatement();
				Iterator<String> sqlQueries = store.transformToSQL(rule);
				while (sqlQueries.hasNext()) {
					statement.addBatch(sqlQueries.next());
				}
				int[] res = statement.executeBatch();

				for (int i = 0; i < res.length; ++i) {
					if (res[i] > 0) {
						returnValue = true;
						break;
					}
				}
			} catch (DriverException | SQLException | AtomSetException e) {
				throw new RuleApplicationException(
						"An error has been occured during rule application.", e);
			} finally {
				if (statement != null) {
					try {
						statement.getConnection().commit();
						statement.close();
					} catch (SQLException e) {
					}
				}
			}
		} else {
			returnValue = this.fallback.apply(rule, store);
		}
		return returnValue;
	}

}
