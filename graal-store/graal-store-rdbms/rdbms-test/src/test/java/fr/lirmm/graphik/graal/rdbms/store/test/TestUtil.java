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
package fr.lirmm.graphik.graal.rdbms.store.test;

import java.sql.SQLException;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.adhoc.AdHocRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.HSQLDBDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class TestUtil {

	private TestUtil() {
	}

	private static final String DEFAULT_TEST = "test_default";
	private static final String PLAIN_TABLE_TEST = "test_plaintable";

	private static AdHocRdbmsStore defaultRdbms = null;
	private static NaturalRDBMSStore plainTableRdbms = null;

	public static RdbmsStore[] getStores() {
		if (defaultRdbms != null) {
			try {
				defaultRdbms.getDriver().getConnection().createStatement()
						.executeQuery("DROP SCHEMA PUBLIC CASCADE");
			} catch (SQLException e) {
				throw new Error(e);
			}
			defaultRdbms.close();
		}
		if (plainTableRdbms != null) {
			try {
				plainTableRdbms.getDriver().getConnection().createStatement()
				               .executeQuery("DROP SCHEMA PUBLIC CASCADE");
			} catch (SQLException e) {
				throw new Error(e);
			}
			plainTableRdbms.close();
		}
		try {
			defaultRdbms = new AdHocRdbmsStore(new HSQLDBDriver(DEFAULT_TEST, null));
			plainTableRdbms = new NaturalRDBMSStore(new HSQLDBDriver(PLAIN_TABLE_TEST, null));
		} catch (AtomSetException e) {
			throw new Error(e);
		} catch (SQLException e) {
			throw new Error(e);
		}
		return new RdbmsStore[] { defaultRdbms, plainTableRdbms };
	}

}
