
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 */

package org.csstudio.alarm.jms2ora.service.oracleimpl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.csstudio.alarm.jms2ora.service.IMetaDataReader;
import org.csstudio.alarm.jms2ora.service.MessageArchiveConnectionException;
import org.csstudio.alarm.jms2ora.service.MessageContent;
import org.csstudio.alarm.jms2ora.service.MetaDataReaderServiceTracker;
import org.csstudio.alarm.jms2ora.service.oracleimpl.Activator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 19.08.2011
 */
public class MessageDao implements MessageArchiveDao {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageDao.class);

    /** The connection handler */
    private OracleConnectionHandler connectionHandler;

    /** The service tracker for the meta data reader service */
    private MetaDataReaderServiceTracker metaDataServiceTracker;
    
    /** The service that reads the meta data */
    private IMetaDataReader metaDataReaderService;
    
    /** The SQL fragment for the INSERT statement */
    private final String INSERT_SQL = "INSERT INTO message (id,msg_type_id,datum";
    
    /** The SQL fragment for the values */
    private final String VALUES_PART = ") VALUES (?,?,TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF3')";
    
    /** The SQL fragment for the INSERT statement for table 'message_content' */
    private final String INSERT_CONTENT_SQL = "INSERT INTO message_content (id,message_id,msg_property_type_id,value) VALUES(?,?,?,?)";
    
    /**
     *  Contains the names of the columns of the table MESSAGE. The key is the name of column and the value
     *  is the precision.
     */
    private Hashtable<String, Integer> messageCol;

    /**
     * Constructor. Oh, really?
     */
    public MessageDao() {

        connectionHandler = new OracleConnectionHandler(false);
        
        metaDataServiceTracker = new MetaDataReaderServiceTracker(Activator.getContext());
        metaDataServiceTracker.open();
        
        metaDataReaderService = (IMetaDataReader) metaDataServiceTracker.getService();
        
        messageCol = metaDataReaderService.getMessageProperties();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        connectionHandler.disconnect();
        metaDataReaderService.close();
        metaDataServiceTracker.close();
    }

    public synchronized boolean writeMessages(Vector<MessageContent> messages) {
        
        if (messages.isEmpty()) {
            return true;
        }
        
        Connection con = null;
        try {
            con = connectionHandler.getConnection();
        } catch (MessageArchiveConnectionException mace) {
            LOG.error("[*** MessageArchiveConnectionException ***]: " + mace.getMessage());
        }
        
        if (con == null) {
            return false;
        }

        long messageId = this.getNextId(con, "message");
        long contentId = this.getNextId(con, "message_content");
        
        LOG.info("The new ID's: messageID = {}, contentID = {}", messageId, contentId);
        
        if ((messageId == -1) || (contentId == -1)) {
            LOG.error("Cannot get the new ID: messageID = {}, contentID = {}", messageId, contentId);
            return false;
        }
        
        // Get names of columns of table MESSAGE
        String[] keys = new String[messageCol.size()];
        keys = messageCol.keySet().toArray(keys);
        
        boolean success = false;

        Savepoint savePoint = null;
        PreparedStatement messageStatement = null;
        PreparedStatement contentStatement = null;
        
        try {
            
            savePoint = con.setSavepoint();
            
            String sql = INSERT_SQL;
            String values = VALUES_PART;
            
            // The SQL query contains allways the columns id, msg_type_id and datum
            // Add the remaining columns
            int n = 0;
            for (String name : keys) {
                sql = sql + "," + name;
                values = values + ",?";
                n++;
            }
            
            sql = sql + values + ")";
            
            messageStatement = con.prepareStatement(sql);
            contentStatement = con.prepareStatement(INSERT_CONTENT_SQL);
            
            for (MessageContent o : messages) {                
                
                // Table 'MESSAGE'
                messageStatement.clearParameters();
                
                messageStatement.setLong(1, messageId);
                messageStatement.setLong(2, 0L); // Type ID is allways 0
                messageStatement.setString(3, o.getPropertyValue("EVENTTIME"));

                for (int i = 0;i < n;i++) {
                    messageStatement.setString((i + 4), o.getPropertyValue(keys[i]));
                }
                
                messageStatement.addBatch();
                
                // Table 'MESSAGE_CONTENT'
                
                Enumeration<?> lst = o.keys();
                while(lst.hasMoreElements()) {
                    
                    contentStatement.clearParameters();

                    long propertyId = (Long) lst.nextElement();
                    String value = o.getPropertyValue(propertyId);
    
                    // Replace a single ' with '' (then the entry could be stored into the database)
                    value = value.replace("'", "''");
                    
                    contentStatement.setLong(1, contentId++);
                    contentStatement.setLong(2, messageId);
                    contentStatement.setLong(3, propertyId);
                    contentStatement.setString(4, value);

                    contentStatement.addBatch();
                }
                
                messageId++;
            }
            
            messageStatement.executeBatch();
            contentStatement.executeBatch();
            
            con.commit();
            success = true;
            
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
            try {
                con.rollback(savePoint);
            } catch (SQLException e) {
                LOG.error("Rollback FAILED: " + sqle.getMessage());
            }
        } finally {
            if(messageStatement!=null) {
                try{messageStatement.close();}catch(SQLException sqle){/*Ignore Me*/}
                messageStatement=null;
            }
            
            if(contentStatement!=null) {
                try{contentStatement.close();}catch(SQLException sqle){/*Ignore Me*/}
                contentStatement=null;
            }
        }
        
        return success;
    }

    /**
     * 
     * @param con
     * @param tableName
     * @return A long value that represents the next table row ID
     */
    public synchronized long getNextId(Connection con, String tableName) {
        
        PreparedStatement pst = null;
        ResultSet rsMsg = null;
        long result = -1;
        
        try {
            pst = con.prepareStatement("SELECT MAX(ID) from " + tableName);
            rsMsg = pst.executeQuery();

            if(rsMsg != null) {
                rsMsg.next();
                result = rsMsg.getLong(1);                
                result += 1;
            }
        } catch(Exception e) {
            LOG.error("[*** Exception ***]: getNextId(): " + e.getMessage());
            result = -1;
        } finally {
            if(rsMsg!=null){try{rsMsg.close();}catch(SQLException sqle){/*Ignore me*/}rsMsg=null;}
            if(pst!=null){try{pst.close();}catch(SQLException sqle){/*Ignore me*/}pst=null;}
        }
        
        return result;
    }
}
