
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Hashtable;
import org.csstudio.alarm.jms2ora.service.DataDirectory;
import org.csstudio.alarm.jms2ora.service.DataDirectoryException;
import org.csstudio.alarm.jms2ora.service.oracleimpl.Activator;
import org.csstudio.alarm.jms2ora.service.oracleimpl.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author mmoeller
 * @version 1.0
 * @since 19.08.2011
 */
public class MetaDataDao implements IMessageArchiveDao {
    
    /** The logger of this class */
    private static Logger LOG = LoggerFactory.getLogger(MetaDataDao.class);
    
    /** The connection handler */
    private OracleConnectionHandler connectionHandler;
    
    /** The object that holds the paths to the data directories */
    private DataDirectory dataDirectories;
    
    /**
     *  Contains the names of the columns of the table MESSAGE. The key is the name of column and the value
     *  is the precision.
     */
    private Hashtable<String, Integer> messageCol;

    /**
     * Constructor. Oh, really!
     */
    public MetaDataDao() {

        connectionHandler = new OracleConnectionHandler(false);
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String metaDataDir = prefs.getString(Activator.getPluginId(),
                                             PreferenceConstants.META_DATA_DIRECTORY,
                                             "./var/columns",
                                             null);
        dataDirectories = new DataDirectory(metaDataDir);
        messageCol = new Hashtable<String, Integer>();
        
        readTableColumns();
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
    
    /**
     * 
     * @return Hashtable containg the VARCHAR2 types and precision of table 'MESSAGE'
     */
    public Hashtable<String, Integer> getMessageProperties() {
        
        if (messageCol.isEmpty()) {
            this.readTableColumns();
        }
        
        return messageCol;
    }
    
    public Hashtable<String, Long> getMessageContentProperties() {
        
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
    
    private void saveColumnNames() {
        
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        if(messageCol.isEmpty()) {
            LOG.info("Column list is empty.");
            return;
        }

        if(dataDirectories.existsDataDirectory() == false) {
            LOG.warn("Object folder does not exist. Columns cannot be stored.");
            return;
        }
        
        try {
            fos = new FileOutputStream(dataDirectories.getDataDirectoryAsString() + "ColumnNames.ser");
            oos = new ObjectOutputStream(fos);
            
            // Write the MessageContent object to disk
            oos.writeObject(messageCol);
            oos.flush();
        } catch(FileNotFoundException fnfe) {
            LOG.error("FileNotFoundException : " + fnfe.getMessage());
        } catch(IOException ioe) {
            LOG.error("IOException : " + ioe.getMessage());
        } catch (DataDirectoryException dde) {
            LOG.error("DataDirectoryException : " + dde.getMessage());
        } finally {
            if(oos != null){try{oos.close();}catch(IOException ioe){/*Ignore me*/}}
            if(fos != null){try{fos.close();}catch(IOException ioe){/*Ignore me*/}}
            
            oos = null;
            fos = null;            
        }
    }
    
    /**
     * 
     */
    private void readTableColumns() {
        
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        Statement st = null;
        String name = null;
        int prec = 0;
        int count = 0;
        
        messageCol.clear();
        
        Connection connection;        
        try {
            
            connection = connectionHandler.getConnection();
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM message WHERE id = 1");
            
            meta = rs.getMetaData();
            count = meta.getColumnCount();
            
            for(int i = 1;i <= count;i++) {
                name = meta.getColumnName(i);
                prec = meta.getPrecision(i);
                
                if((name.compareToIgnoreCase("id") != 0) 
                    && (name.compareToIgnoreCase("datum") != 0)
                    && (name.compareToIgnoreCase("msg_type_id") != 0)) {
                    messageCol.put(name, new Integer(prec));
                }
            }
            
            saveColumnNames();
            
        } catch(Exception e) {
            LOG.warn("[*** Exception ***]: Cannot read the table column names: " + e.getMessage());
            LOG.warn("Using stored column names.");
            readColumnNames();
        } finally {
            close();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void readColumnNames() {
        
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(dataDirectories.getDataDirectoryAsString() + "ColumnNames.ser");
            ois = new ObjectInputStream(fis);
            
            // Write the MessageContent object to disk
            messageCol = (Hashtable<String, Integer>)ois.readObject();            
        } catch(Exception e) {
            LOG.warn("[*** " + e.getClass().getSimpleName() + " ***]: " + e.getMessage());
            messageCol = new Hashtable<String, Integer>();
        } finally {
            if(ois != null){try{ois.close();}catch(IOException ioe){/*Ignore me*/}}
            if(fis != null){try{fis.close();}catch(IOException ioe){/*Ignore me*/}}
            
            ois = null;
            fis = null;            
        }
    }
}
