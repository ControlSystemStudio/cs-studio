package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.csstudio.platform.logging.CentralLogger;

/**
 * This class holds the entries of table msg_property_type to prevent needless
 * DB access.
 * 
 * @author jhatje
 * 
 */
public class MessagePropertyTypeContent {

    private static HashMap<String, String> propertyIDMapping = null;

    private static HashMap<String, String> idPropertyMapping = null;

    public static HashMap<String, String> getPropertyIDMapping() {
        if (propertyIDMapping == null) {
            readMessageTypes();
        }
        return propertyIDMapping;
    }

    public static HashMap<String, String> getIDPropertyMapping() {
        if (idPropertyMapping == null) {
            readMessageTypes();
        }
        return idPropertyMapping;
    }

    /**
     * Read mapping of message types to an id.
     */
    public static void readMessageTypes() {
        propertyIDMapping = new HashMap<String, String>();
        idPropertyMapping = new HashMap<String, String>();
        DBConnectionHandler connectionHandler = new DBConnectionHandler();
        try {
            Connection _databaseConnection = connectionHandler.getConnection();
            PreparedStatement getMessages = _databaseConnection
                    .prepareStatement("select mpt.id, mpt.name from msg_property_type mpt");
            ResultSet result = getMessages.executeQuery();
            while (result.next()) {
                String one = result.getString(1);
                String two = result.getString(2);
                propertyIDMapping.put(two, one);
                idPropertyMapping.put(one, two);
            }
        } catch (Exception e) {
            CentralLogger.getInstance().error(
                    new MessagePropertyTypeContent(),
                    "MessagePropertyTypeContetn, SQLException: Cannot read table column names: "
                            + e.getMessage());
        }
        if(connectionHandler != null) {
            connectionHandler.closeConnection();
            connectionHandler = null;
        }
    }
}
