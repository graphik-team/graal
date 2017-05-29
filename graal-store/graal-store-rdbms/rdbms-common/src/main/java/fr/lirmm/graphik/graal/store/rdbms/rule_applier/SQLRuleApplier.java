/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.rule_applier;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.DirectRuleApplier;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;

/**
 * SQLRuleApplier transform rules into INSERT ... SELECT SQL statement when it
 * is possible. If not, it call the apply method of the specified RuleApplier.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SQLRuleApplier implements
		DirectRuleApplier<Rule, RdbmsStore> {

	private DirectRuleApplier<Rule, ? super RdbmsStore> fallback;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a SQLRuleApplier with a DefaultRuleApplier as fallback.
	 */
	public SQLRuleApplier(Homomorphism<ConjunctiveQuery, ? super RdbmsStore> homomorphismSolver) {
		this.fallback = new DefaultRuleApplier<RdbmsStore>(homomorphismSolver);
	}

	/**
	 * Construct a SQLRuleApplier with the specified rule applier as fallback.
	 * 
	 * @param ruleApplierFallback
	 */
	public SQLRuleApplier(DirectRuleApplier<Rule, ? super RdbmsStore> ruleApplierFallback) {
		this.fallback = ruleApplierFallback;
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public boolean apply(Rule rule, RdbmsStore store)
			throws RuleApplicationException {
		boolean returnValue = false;
		if (rule.getExistentials().isEmpty()) {
			Statement statement = null;
			try {
				statement = store.getDriver().createStatement();
				Iterator<SQLQuery> sqlQueries = store.getConjunctiveQueryTranslator().translate(rule);
				while (sqlQueries.hasNext()) {
					SQLQuery query = sqlQueries.next();
					if (!query.hasSchemaError())
						statement.addBatch(query.toString());
				}
				int[] res = statement.executeBatch();

				for (int i = 0; i < res.length; ++i) {
					if (res[i] > 0) {
						returnValue = true;
						break;
					}
				}
			} catch (AtomSetException e) {
				throw new RuleApplicationException(
						"An error has been occured during rule application.", e);
			} catch (SQLException e) {
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
