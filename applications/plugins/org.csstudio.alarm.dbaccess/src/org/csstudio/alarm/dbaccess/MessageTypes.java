package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.util.ArrayList;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;

import org.csstudio.alarm.dbaccess.archivedb.IMessageTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageTypes implements IMessageTypes {

    private static final Logger LOG = LoggerFactory.getLogger(MessageTypes.class);

    private ArrayList<String[]> _propertyIdMapping;

    public MessageTypes() {
        LOG.debug("Read Property ID mapping from table msg_property_type");
        final String sql = "select * from msg_property_type mpt order by id";
        DBConnectionHandler connectioHandler = new DBConnectionHandler();
        try {
            final Connection _databaseConnection = connectioHandler.getConnection();
            final OracleStatement stmt = (OracleStatement) _databaseConnection
                    .createStatement();

            stmt.execute(sql);

            final OracleResultSet rset = (OracleResultSet) stmt.getResultSet();
            _propertyIdMapping = new ArrayList<String[]>();
            while (rset.next()) {
                final String id = rset.getString("ID");
                final String name = rset.getString(2);
                _propertyIdMapping.add(new String[] { id, name });
            }
        } catch (final Exception e) {
            LOG.debug("SQL Exception ", e);
        }
        if(connectioHandler != null) {
            try {
				connectioHandler.closeConnection();
			} catch (final Exception e) {
				LOG.error("Unknown error, set connector = null", e);
			}
            connectioHandler = null;
        }
    }

    public String[][] getMsgTypes() {
        return _propertyIdMapping.toArray(new String[0][2]);
    }

}
