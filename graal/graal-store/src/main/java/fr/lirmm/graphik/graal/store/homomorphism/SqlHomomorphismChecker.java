/**
 * 
 */
package fr.lirmm.graphik.graal.store.homomorphism;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.checker.AbstractChecker;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SqlHomomorphismChecker extends AbstractChecker {

	@Override
	public boolean check(Query query, AtomSet atomset) {
		 return query instanceof ConjunctiveQuery && atomset instanceof RdbmsStore;
	}

	@Override
	public SqlHomomorphism getSolver() {
         return SqlHomomorphism.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 100;
	}

}
