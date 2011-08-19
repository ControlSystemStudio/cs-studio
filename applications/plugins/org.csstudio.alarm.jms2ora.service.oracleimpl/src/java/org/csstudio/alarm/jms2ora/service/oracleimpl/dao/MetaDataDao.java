
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
import java.sql.ResultSetMetaData;
import java.util.Hashtable;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 19.08.2011
 */
public class MetaDataDao implements MessageArchiveDao {
    
    /** The connection handler */
    private OracleConnectionHandler connectionHandler;
    
    /**
     * Constructor. Oh, really!
     */
    public MetaDataDao() {
        connectionHandler = new OracleConnectionHandler();
    }
    
    @Override
    public void close() {
        connectionHandler.disconnect();
    }
    
    public int getValueLength() {
        
        PreparedStatement pst = null;
        ResultSetMetaData rsMetaData = null;
        ResultSet rs = null;
        int result = -1;
        
        Connection connection;
        try {
            connection = connectionHandler.getConnection();            
            pst = connection.prepareStatement("SELECT * from message_content WHERE id=?");
            pst.setLong(1, 1);
            rs = pst.executeQuery();
            rsMetaData = rs.getMetaData();
            
            // Check the result sets 
            if(rsMetaData != null) {
                int count = rsMetaData.getColumnCount();
                for(int i = 1;i <= count;i++) {
                    if(rsMetaData.getColumnName(i).compareToIgnoreCase("value") == 0) {
                        result = rsMetaData.getPrecision(i);
                    }
                }
            }
        } catch(Exception e) {
            result = -1;
        } finally {
            rsMetaData = null;
            if(rs!=null){try{rs.close();}catch(Exception e){/*Ignore me*/}rs=null;}
            if(pst!=null){try{pst.close();}catch(Exception e){/*Ignore me*/}pst=null;}
            connectionHandler.disconnect();
        }
                
        return result;
    }
    
    public Hashtable<String, Long> getMessageProperties() {
        
        PreparedStatement pst = null;
        ResultSet rsProperty = null;
        Hashtable<String, Long> msgProperty = new Hashtable<String, Long>();
        
        Connection connection;
        try {
            connection = connectionHandler.getConnection(); 
            pst = connection.prepareStatement("SELECT * from MSG_PROPERTY_TYPE");
            
            // Execute the query to get all properties
            rsProperty = pst.executeQuery();
        
            // Check the result sets 
            if(rsProperty != null) {
                // Fill the hash table with the received data of the property table
                while(rsProperty.next()) {
                    msgProperty.put(rsProperty.getString(2), rsProperty.getLong(1));                 
                }
            }
        } catch(Exception e) {
            msgProperty.clear();
        } finally {
            if(rsProperty!=null){try{rsProperty.close();}catch(Exception e){/*Ignore me*/}rsProperty=null;}
            if(pst!=null){try{pst.close();}catch(Exception e){/*Ignore me*/}pst=null;}
            connectionHandler.disconnect();
        }
        
        return msgProperty;
    }
}
