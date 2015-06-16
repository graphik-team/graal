/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Variable;

public class RdbmsSymbolGenenrator implements VariableGenerator {

    private Connection dbConnection;
    private final String counterName;
    private final String getCounterValueQuery;
    private final String updateCounterValueQuery;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RdbmsSymbolGenenrator.class);

    public RdbmsSymbolGenenrator(Connection connection, String counterName, String getCounterValueQuery,
            String updateCounterValueQuery) {
        this.dbConnection = connection;
        this.getCounterValueQuery = getCounterValueQuery;
        this.updateCounterValueQuery = updateCounterValueQuery;
        this.counterName = counterName;
    }

    @Override
	public Variable getFreshVar() {
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
            LOGGER.warn(e.getMessage(), e);
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
		return DefaultTermFactory.instance().createVariable("X" + value);
    }

}
