/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class DefaultRdbmsPredicateReader extends AbstractReader<Predicate> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRdbmsPredicateReader.class);
	
	private static final String GET_ALL_PREDICATES_QUERY = "SELECT * FROM "
														+ DefaultRdbmsStore.PREDICATE_TABLE_NAME
														+ ";";
	private boolean hasNextCallDone = false;
	private boolean hasNext; 

	private ResultSet results;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	DefaultRdbmsPredicateReader(RdbmsDriver driver) throws AtomSetException {
		Statement stat;
		try {
			stat = driver.createStatement();
			results = stat.executeQuery(GET_ALL_PREDICATES_QUERY);
		} catch (SQLException e) {
			throw new AtomSetException(e);
		} catch (DriverException e) {
			throw new AtomSetException(e);
		}
		
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;
			try {
				this.hasNext = this.results.next();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
				this.hasNext = false;
			}
		}
		return this.hasNext;
	}

	@Override
	public Predicate next() {
		if (!this.hasNextCallDone)
			this.hasNext();
		this.hasNextCallDone = false;
		try {
			return this.readPredicate(this.results);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @param results2
	 * @return
	 * @throws SQLException 
	 */
	private Predicate readPredicate(ResultSet results2) throws SQLException {
		return new Predicate(results.getString(1), results.getInt(2));
	}

}
