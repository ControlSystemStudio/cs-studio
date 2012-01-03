package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This class holds the entries of table msg_property_type to prevent needless
 * DB access.
 *
 * @author jhatje
 *
 */
public class MessagePropertyTypeContent {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePropertyTypeContent.class);

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
            final Connection _databaseConnection = connectionHandler.getConnection();
            final PreparedStatement getMessages = _databaseConnection
                    .prepareStatement("select mpt.id, mpt.name from msg_property_type mpt");
            final ResultSet result = getMessages.executeQuery();
            while (result.next()) {
                final String one = result.getString(1);
                final String two = result.getString(2);
                propertyIDMapping.put(two, one);
                idPropertyMapping.put(one, two);
            }
        } catch (final Exception e) {
            LOG.error(
                    "MessagePropertyTypeContetn, SQLException: Cannot read table column names: ", e);
        }
        if(connectionHandler != null) {
            connectionHandler.closeConnection();
            connectionHandler = null;
        }
    }
}
