package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.util.ArrayList;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;

import org.csstudio.alarm.dbaccess.archivedb.IMessageTypes;
import org.csstudio.platform.logging.CentralLogger;

public class MessageTypes implements IMessageTypes {

    private ArrayList<String[]> _propertyIdMapping;

    public MessageTypes() {
        CentralLogger.getInstance().debug(this,
                "Read Property ID mapping from table msg_property_type");
        String sql = "select * from msg_property_type mpt order by id";
        DBConnectionHandler connectioHandler = new DBConnectionHandler();
        try {
            Connection _databaseConnection = connectioHandler.getConnection();
            OracleStatement stmt = (OracleStatement) _databaseConnection
                    .createStatement();

            stmt.execute(sql);

            OracleResultSet rset = (OracleResultSet) stmt.getResultSet();
            _propertyIdMapping = new ArrayList<String[]>();
            while (rset.next()) {
                String id = rset.getString("ID");
                String name = rset.getString(2);
                _propertyIdMapping.add(new String[] { id, name });
            }
        } catch (Exception e) {
            CentralLogger.getInstance().debug(this, "SQL Exception " + e.getMessage());
        }
        if(connectioHandler != null) {
            connectioHandler.closeConnection();
            connectioHandler = null;
        }
    }

    public String[][] getMsgTypes() {
        return _propertyIdMapping.toArray(new String[0][2]);
    }

}
