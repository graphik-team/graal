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
 package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

public class RdbmsVariableGenenrator implements VariableGenerator {

    private Connection dbConnection;
    private final String counterName;
    private final String getCounterValueQuery;
    private final String updateCounterValueQuery;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RdbmsVariableGenenrator.class);

    public RdbmsVariableGenenrator(Connection connection, String counterName, String getCounterValueQuery,
            String updateCounterValueQuery) {
        this.dbConnection = connection;
        this.getCounterValueQuery = getCounterValueQuery;
        this.updateCounterValueQuery = updateCounterValueQuery;
        this.counterName = counterName;
    }

    @Override
	public Variable getFreshSymbol() {
        long value;
        PreparedStatement pstat = null;
        try {
            pstat = dbConnection.prepareStatement(this.getCounterValueQuery);
            pstat.setString(1, counterName);
            ResultSet result = pstat.executeQuery();
            result.next();
            value = result.getLong("value") + 1;
            pstat.close();
            pstat = dbConnection.prepareStatement(this.updateCounterValueQuery);
            pstat.setLong(1, value);
            pstat.setString(2, this.counterName);
            pstat.executeUpdate();
            pstat.close();
        } catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
            value = 0; // FIXME
        } finally {
            if( pstat != null ) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
		return DefaultTermFactory.instance().createVariable("EE" + value);
    }

}
