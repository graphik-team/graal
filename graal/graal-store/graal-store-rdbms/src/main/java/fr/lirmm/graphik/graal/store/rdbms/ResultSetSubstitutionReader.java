/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.SQLException;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class ResultSetSubstitutionIterator extends AbstractResultSetIterator<Substitution> {

	private List<Term> ans;

    // /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////


    /**
	 * 
	 * @param store
	 * @param sqlQuery
	 * @param ans
	 * @throws SQLException
	 * @throws StoreException
	 */
	public ResultSetSubstitutionIterator(RdbmsStore store, String sqlQuery, List<Term> ans) throws SQLException {
		super(store, sqlQuery);
		this.ans = ans;
	}



    // /////////////////////////////////////////////////////////////////////////
    //  METHODS
    // /////////////////////////////////////////////////////////////////////////

	/**
	 * @return
	 * @throws AtomSetException
	 * @throws SQLException
	 */
	@Override
	protected Substitution computeNext() throws SQLException, AtomSetException {
		Substitution substitution = new TreeMapSubstitution();
		if (!ans.isEmpty()) {
			for (Term t : ans) {
				if(!t.isConstant()) {
					String value = this.results.getString(t.getLabel());
					Term substitut = this.store.getTerm(value);
					substitution.put(t, substitut);
				}
			}
		}
		return substitution;
	}
    
}
