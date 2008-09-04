
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.alarm.jms2ora.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.csstudio.alarm.jms2ora.util.MessageContent;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/**
 *  @author Markus Moeller
 *
 */
public class DatabaseLayer
{
    /** Database service */
    private RDBUtil dbService = null;
    
    /** Database URL */
    private String url = null;

    /** Database user */
    private String user = null;
    
    /** Database password */
    private String password = null;
    
    /** True if the folder 'var\columns' exists. This folder holds the stored message object content. */
    private boolean existsObjectFolder = false;

    /** Name of the folder that holds the stored message content */
    private final String objectDir = ".\\var\\columns\\";

    /**
     *  Contains the names of the columns of the table MESSAGE. The key is the name of column and the value
     *  is the precision.
     */
    private Hashtable<String, Integer> messageCol = null;
    
    /** Logger */
    private Logger logger = null;

    public DatabaseLayer(String url, String user, String password)
    {
        logger = Logger.getLogger(DatabaseLayer.class);
        
        this.url = url;
        this.password = password;
        this.user = user;
        
        createObjectFolder();
        readTableColumns();
    }
    
    /**
     * 
     */
    private void readTableColumns()
    {
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        Statement st = null;
        String name = null;
        int prec = 0;
        int count = 0;
        
        messageCol = new Hashtable<String, Integer>();
        
        if(!connect())
        {
            logger.error("Cannot read the table column names.");
            
            return;
        }
        
        try
        {
            st = dbService.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM message WHERE id = 1");
            
            meta = rs.getMetaData();
            count = meta.getColumnCount();
            
            for(int i = 1;i <= count;i++)
            {
                name = meta.getColumnName(i);
                prec = meta.getPrecision(i);
                
                if((name.compareToIgnoreCase("id") != 0) && (name.compareToIgnoreCase("datum") != 0) && (name.compareToIgnoreCase("msg_type_id") != 0))
                {
                    messageCol.put(name, new Integer(prec));
                }
            }
            
            saveColumnNames();
        }
        catch(SQLException sqle)
        {
            logger.error("*** SQLException *** : Cannot read the table column names: " + sqle.getMessage());
            logger.error("Using stored column names.");
            
            readColumnNames();
        }
        finally
        {
            close();
        }
    }
    
    private void saveColumnNames()
    {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        if(messageCol.isEmpty())
        {
            logger.info("Column list is empty.");
            
            return;
        }

        if(existsObjectFolder == false)
        {
            logger.warn("Object folder '" + objectDir + "' does not exist. Columns cannot be stored.");
            
            return;
        }
        
        try
        {
            fos = new FileOutputStream(objectDir + "ColumnNames.ser");
            oos = new ObjectOutputStream(fos);
            
            // Write the MessageContent object to disk
            oos.writeObject(messageCol);            
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("FileNotFoundException : " + fnfe.getMessage());
        }
        catch(IOException ioe)
        {
            logger.error("IOException : " + ioe.getMessage());
        }
        finally
        {
            if(oos != null){try{oos.close();}catch(IOException ioe){}}
            if(fos != null){try{fos.close();}catch(IOException ioe){}}
            
            oos = null;
            fos = null;            
        }
    }
    
    @SuppressWarnings("unchecked")
    private Hashtable<String, Integer> readColumnNames()
    {
        Hashtable<String, Integer> content = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try
        {
            fis = new FileInputStream(objectDir + "ColumnNames.ser");
            ois = new ObjectInputStream(fis);
            
            // Write the MessageContent object to disk
            content = (Hashtable<String, Integer>)ois.readObject();            
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("FileNotFoundException : " + fnfe.getMessage());
            content = null;
        }
        catch(IOException ioe)
        {
            logger.error("IOException : " + ioe.getMessage());
            content = null;
        }
        catch (ClassNotFoundException e)
        {
            logger.error("ClassNotFoundException : " + e.getMessage());
            content = null;
        }
        finally
        {
            if(ois != null){try{ois.close();}catch(IOException ioe){}}
            if(fis != null){try{fis.close();}catch(IOException ioe){}}
            
            ois = null;
            fis = null;            
        }
        
        return content;
    }
    
    /**
     * Connect to the database.
     *
     * @return true/false
     * 
     */
    
    public boolean connect()
    {        
        try
        {
            dbService = RDBUtil.connect(url, user, password);
        }
        catch(Exception e)
        {
            logger.error(" *** EXCEPTION *** : " + e.getMessage());
            
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     */
    public Dialect getDialect()
    {
        return dbService.getDialect();
    }
    
    /**
     * <code>close</code> closes the connection to the database. 
     *
     */
    
    public void close()
    {
        dbService.close();
    }
    
    /**
     * <code>isConnected()</code>
     * 
     * @return true=connection is available ; false=no connection available
     */
    
    public boolean isConnected()
    {
        boolean result = false;
        
        try
        {
            result = !(dbService.getConnection().isClosed());
        }
        catch(SQLException sqle)
        {
            logger.error("*** EXCEPTION *** : " + sqle.getMessage());
            
            result = false;
        }
        
        return result;
    }
    
    /**
     * <code>executeSQLQuery</code>
     * 
     * @param query - String with the SQL statement
     * @return The ResultSet-Object containing the results of the query.
     */
    
    public ResultSet executeSQLQuery(String query)
    {
        ResultSet rset = null;
        Statement stmt = null;
        
        try
        {
            stmt = dbService.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rset = stmt.executeQuery(query);
        }
        catch (SQLException sqle)
        {
            logger.error("*** EXCEPTION *** : " + sqle.getMessage());
            
            rset = null;
        }
        
        return (ResultSet)rset;
    }

    /**
     * <code>executeSQLQuery</code>
     * 
     * @param query - String with the SQL statement
     * @return The ResultSet-Object containing the results of the query.
     */
    
    public long executeSQLUpdateQuery(String query)
    {
        Statement stmt = null;
        long result = 0;
        
        try
        {
            stmt = dbService.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            result = stmt.executeUpdate(query);
        }
        catch (SQLException sqle)
        {
            logger.error("*** EXCEPTION *** : " + sqle.getMessage());
            
            result = -1;
        }
        finally
        {
            if(stmt!=null){try{stmt.close();}catch(Exception e){}stmt=null;}
        }

        return result;
    }

    /**
     * <code>createMessageEntry</code> creates an entry in the table <i>MESSAGE</i>.
     * 
     * @param typeId - The ID of the message type which is defined in the table <i>MSG_TYPE</i>.
     * @param datum  - The date of the event(contained in the map message) or the current date
     * @param name - The value of property NAME(contained in the map message)
     * @param status - The value of property STATUS(contained in the map message)
     * @return The ID of the new entry or -1 if it fails.
     */
    
    public long createMessageEntry(long typeId, MessageContent content)
    {
        ResultSet rsMsg = null;
        String query = null;
        String values = null;
        String name = null;
        long msgId = -1;
        
        // Connect the database
        if(connect() == false)
        {
            return msgId;
        }

        // Get the highest ID in the table
        rsMsg = executeSQLQuery("SELECT MAX(ID) from MESSAGE");
        if(rsMsg != null)
        {
            try
            {
                rsMsg.next();
                msgId = rsMsg.getLong(1);                
                msgId += 1;
            }
            catch(SQLException sqle)
            {
                System.out.println(" *** EXCEPTION *** : createMessageEntry() : " + sqle.getMessage());
                msgId = -1;
            }
            
            try
            {
                rsMsg.getStatement().close();
                rsMsg.close();
            }
            catch (SQLException sqle) { sqle.printStackTrace(); }
        }

        // Do we have a valid ID?
        if(msgId > 0)
        {
            // Insert a new entry           
            query = "INSERT INTO message (id,msg_type_id,datum";            
            values = ") VALUES (" + msgId + "," + typeId + ",TIMESTAMP '" + content.getPropertyValue("EVENTTIME") + "'";
            
            // Get names of columns of table MESSAGE
            Enumeration<String> keys = messageCol.keys();
            while(keys.hasMoreElements())
            {
                name = keys.nextElement();
                if(content.containsPropertyName(name.toUpperCase()))
                {
                    query = query + "," + name;
                    values = values + ",'" + content.getPropertyValue(name.toUpperCase()) + "'";
                }
                else
                {
                    query = query + "," + name;
                    values = values + ",'n/a'";
                }
            }
            
            query = query + values + ")";
            
            System.out.println(query + "\n");
            
            if(executeSQLUpdateQuery(query) == -1)
            {
                msgId = -1;
            }
        }
        
        close();
        
        return msgId;
    }

    /**
     * <code>createMessageContentEntry</code> creates an entry in the table <i>MESSAGE_CONTENT</i>.
     * 
     * @param msgId - The ID of the entry in the table MESSAGE which is related to this content entry.
     * @param msgContent - A hash table with the list of properties which have to be stored into the DB.      
     * @return true - false
     * 
     */
    
    public boolean createMessageContentEntries(long msgId, MessageContent msgContent)
    {
        Enumeration<?> lst = null;
        ResultSet rsMsg = null;
        String  value = null;
        String query = null;
        boolean result = false;
        long contentId = -1;
        long key;
        
        // Connect the database
        if(connect() == false)
        {
            return false;
        }
        
        // Get the highest ID in the table
        rsMsg = executeSQLQuery("SELECT MAX(id) FROM message_content");
        if(rsMsg != null)
        {
            try
            {
                rsMsg.next();
                contentId = rsMsg.getLong(1);                
                contentId += 1;
            }
            catch(SQLException sqle)
            {
                logger.error("*** EXCEPTION *** : createMessageContentEntries() : " + sqle.getMessage());
                
                contentId = -1;
            }
            
            try
            {
                rsMsg.getStatement().close();
                rsMsg.close();
            }
            catch (SQLException sqle) { sqle.printStackTrace(); }
        }
        
        // Did we get an valid ID?
        if(contentId > 0)
        {
            // First write the known message content
            lst = msgContent.keys();
            
            while(lst.hasMoreElements())
            {
                query = "INSERT INTO message_content (id,message_id,msg_property_type_id,value) VALUES(";

                key = (Long)lst.nextElement();
                value = msgContent.getPropertyValue(key);

                // Replace a single ' with '' (then the entry could be stored into the database)
                value = value.replace("'", "''");
                
                query = query + contentId + "," + msgId + "," + key + ",'" + value + "')";
                
                if(executeSQLUpdateQuery(query) == -1)
                {
                    result = false;
                    
                    break;
                }
                else
                {
                    result = true;
                }
                
                contentId++;
            }
            
            // Write the unknown properties, if we have some
            if((result == true) && (msgContent.unknownPropertiesAvailable()))
            {
                for(int i = 0;i < msgContent.countUnknownProperties();i++)
                {
                    value = msgContent.getUnknownProperty(i);

                    // Replace a single ' with '' (then the entry could be stored into the database)
                    value = value.replace("'", "''");
                    
                    query = "INSERT INTO message_content (id,message_id,msg_property_type_id,value) VALUES("
                        + contentId + ","
                        + msgId + ","
                        + msgContent.getUnknownTableId() + ","
                        + "'" + value + "')";
                    
                    if(executeSQLUpdateQuery(query) == -1)
                    {
                        result = false;
                        
                        break;
                    }
                    else
                    {
                        result = true;
                    }
                    
                    contentId++;  
                }
            }
        }
        
        close();
        
        return result;
    }
    
    /**
     * 
     */
    private void createObjectFolder()
    {
        File folder = new File(objectDir);
                
        existsObjectFolder = true;

        if(!folder.exists())
        {
            boolean result = folder.mkdir();
            if(result)
            {
                logger.info("Folder " + objectDir + " was created.");
                
                existsObjectFolder = true;
            }
            else
            {
                logger.warn("Folder " + objectDir + " was NOT created.");
                
                existsObjectFolder = false;
            }
        }
    }

    /**
     * The method deletes the message with the given id from the table.
     * 
     *  @param msgId The id of the message that has to be deleted.
     */
    public void deleteMessage(long msgId)
    {        
        // Connect the database
        if(connect() == false)
        {
            return;
        }

        executeSQLUpdateQuery("DELETE FROM message_content where message_id=" + msgId);
        executeSQLUpdateQuery("DELETE FROM message where id=" + msgId);

        close();
    }
}
