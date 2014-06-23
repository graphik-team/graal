package fr.lirmm.graphik.alaska.store.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.SymbolGenerator;

public class RdbmsSymbolGenenrator implements SymbolGenerator {

    private Connection dbConnection;
    private final String counterName;
    private final String getCounterValueQuery;
    private final String updateCounterValueQuery;

    private static final Logger logger = LoggerFactory
            .getLogger(RdbmsSymbolGenenrator.class);

    public RdbmsSymbolGenenrator(Connection connection, String counterName, String getCounterValueQuery,
            String updateCounterValueQuery) {
        this.dbConnection = connection;
        this.getCounterValueQuery = getCounterValueQuery;
        this.updateCounterValueQuery = updateCounterValueQuery;
        this.counterName = counterName;
    }

    @Override
    public Term getFreeVar() {
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
            logger.warn(e.getMessage(), e);
            value = 0; // FIXME
        } finally {
            if( pstat != null ) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return new Term("X" + value, Term.Type.VARIABLE);
    }

}
