
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

import org.csstudio.alarm.jms2ora.Jms2OraActivator;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  @author Markus Moeller
 *
 */
@Deprecated
public class DatabaseLayer {

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseLayer.class);

    /** Database service */
    private RDBUtil dbService = null;

    /** Database URL */
    private String url = null;

    /** Database user */
    private String user = null;

    /** Database password */
    private String password = null;

    /** True if the folder 'var/columns' exists. This folder holds the stored message object content. */
    private boolean existsObjectFolder = false;

    /** Name of the folder that holds the stored message content */
    private String objectDir;

    /**
     *  Contains the names of the columns of the table MESSAGE. The key is the name of column and the value
     *  is the precision.
     */
    private Hashtable<String, Integer> messageCol = null;

    public DatabaseLayer(final String url, final String user, final String password) {

        final IPreferencesService prefs = Platform.getPreferencesService();
        String temp = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.STORAGE_DIRECTORY, "./var/", null);
        if(temp.endsWith("/") == false) {
            temp += "/";
        }

        objectDir = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.META_DATA_DIRECTORY, "columns/", null);
        objectDir = temp + objectDir;

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
            LOG.error("Cannot read the table column names.");

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

                if(name.compareToIgnoreCase("id") != 0 && name.compareToIgnoreCase("datum") != 0 && name.compareToIgnoreCase("msg_type_id") != 0)
                {
                    messageCol.put(name, new Integer(prec));
                }
            }

            saveColumnNames();
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : Cannot read the table column names: " + e.getMessage());
            LOG.error("Using stored column names.");

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
            LOG.info("Column list is empty.");

            return;
        }

        if(existsObjectFolder == false)
        {
            LOG.warn("Object folder '" + objectDir + "' does not exist. Columns cannot be stored.");

            return;
        }

        try
        {
            fos = new FileOutputStream(objectDir + "ColumnNames.ser");
            oos = new ObjectOutputStream(fos);

            // Write the MessageContent object to disk
            oos.writeObject(messageCol);
            oos.flush();
        }
        catch(final FileNotFoundException fnfe)
        {
            LOG.error("FileNotFoundException : " + fnfe.getMessage());
        }
        catch(final IOException ioe)
        {
            LOG.error("IOException : " + ioe.getMessage());
        }
        finally
        {
            if(oos != null){try{oos.close();}catch(final IOException ioe){}}
            if(fos != null){try{fos.close();}catch(final IOException ioe){}}

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
        catch(final FileNotFoundException fnfe)
        {
            LOG.error("FileNotFoundException : " + fnfe.getMessage());
            content = null;
        }
        catch(final IOException ioe)
        {
            LOG.error("IOException : " + ioe.getMessage());
            content = null;
        }
        catch (final ClassNotFoundException e)
        {
            LOG.error("ClassNotFoundException : " + e.getMessage());
            content = null;
        }
        finally
        {
            if(ois != null){try{ois.close();}catch(final IOException ioe){}}
            if(fis != null){try{fis.close();}catch(final IOException ioe){}}

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
            dbService = RDBUtil.connect(url, user, password, false);
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : connect(): " + e.getMessage());

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
            result = !dbService.getConnection().isClosed();
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : isConnected(): " + e.getMessage());

            result = false;
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

    public synchronized long createMessageEntry(final long typeId, final ArchiveMessage content)
    {
        PreparedStatement pst = null;
        String sql = null;
        String values = null;
        String name = null;
        String[] preparedValues = null;
        long msgId = -1;

        // Connect the database
        if(connect() == false)
        {
            return msgId;
        }

        // Get the highest ID in the table
        msgId = getNextId("message");

        // Do we have a valid ID?
        if(msgId > 0)
        {
            // Insert a new entry
            sql = "INSERT INTO message (id,msg_type_id,datum";

            // ORACLE
            values = ") VALUES (?,?,TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF3')";

            // MySQL
            // values = ") VALUES (?,?,TIMESTAMP(?)";

            // Get names of columns of table MESSAGE
            final Enumeration<String> keys = messageCol.keys();
            preparedValues = new String[messageCol.size()];
            int n = 0;
            while(keys.hasMoreElements())
            {
                name = keys.nextElement();
                if(content.containsPropertyName(name.toUpperCase()))
                {
                    sql = sql + "," + name;
                    values = values + ",?";
                    preparedValues[n++] = content.getPropertyValue(name.toUpperCase());
                }
                else
                {
                    sql = sql + "," + name;
                    values = values + ",?";
                    preparedValues[n++] = "n/a";
                }
            }

            sql = sql + values + ")";

            try
            {
                pst = dbService.getConnection().prepareStatement(sql);

                pst.setLong(1, msgId);
                pst.setLong(2, typeId);
                pst.setString(3, content.getPropertyValue("EVENTTIME"));

                for(int i = 0;i < n;i++)
                {
                    pst.setString((i + 4), preparedValues[i]);
                }

                if(pst.executeUpdate() < 0)
                {
                    msgId = -1;
                }

            }
            catch(final Exception e)
            {
                LOG.error("*** Exception ***: createMessageEntry(): " + e.getMessage());

                msgId = -1;
                if(pst!=null){try{pst.close();}catch(final SQLException sqle){}pst=null;}
            }

            if(pst!=null){try{pst.close();}catch(final SQLException e){}pst=null;}
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

    public synchronized boolean createMessageContentEntries(final long msgId, final ArchiveMessage msgContent)
    {
        Enumeration<?> lst = null;
        PreparedStatement pst = null;
        String  value = null;
        String sql = null;
        boolean result = false;
        long contentId = -1;
        long key;

        // Connect the database
        if(connect() == false)
        {
            return false;
        }

        // Get the highest ID in the table
        contentId = getNextId("message_content");

        // Did we get an valid ID?
        if(contentId > 0)
        {
            sql = "INSERT INTO message_content (id,message_id,msg_property_type_id,value) VALUES(?,?,?,?)";

            try
            {
                pst = dbService.getConnection().prepareStatement(sql);

                // First write the known message content
                lst = msgContent.keys();

                while(lst.hasMoreElements())
                {
                    key = (Long)lst.nextElement();
                    value = msgContent.getPropertyValue(key);

                    // Replace a single ' with '' (then the entry could be stored into the database)
                    value = value.replace("'", "''");

                    pst.setLong(1, contentId);
                    pst.setLong(2, msgId);
                    pst.setLong(3, key);
                    pst.setString(4, value);

                    if(pst.executeUpdate() < 0)
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
            catch(final Exception e)
            {
                LOG.error("*** Exception ***: createMessageContentEntries(): Write known messages: " + e.getMessage());

                result = false;
                if(pst!=null){try{pst.close();}catch(final SQLException sqle){}pst=null;}
            }

            // Write the unknown properties, if we have some
            if(result == true && msgContent.unknownPropertiesAvailable())
            {
                for(int i = 0;i < msgContent.countUnknownProperties();i++)
                {
                    value = msgContent.getUnknownProperty(i);

                    // Replace a single ' with '' (then the entry could be stored into the database)
                    value = value.replace("'", "''");

                    try
                    {
                        pst.setLong(1, contentId);
                        pst.setLong(2, msgId);
                        pst.setLong(3, msgContent.getUnknownTableId());
                        pst.setString(4, value);

                        if(pst.executeUpdate() < 0)
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
                    catch(final SQLException sqle)
                    {
                        LOG.error("*** SQLException ***: createMessageContentEntries(): Write unknown properties: " + sqle.getMessage());

                        result = false;
                        if(pst!=null){try{pst.close();}catch(final SQLException e){}pst=null;}
                    }
                }
            }

            if(pst!=null){try{pst.close();}catch(final SQLException e){}pst=null;}
        }

        close();

        return result;
    }

    /**
     *
     */
    private void createObjectFolder()
    {
        final File folder = new File(objectDir);

        existsObjectFolder = true;

        if(!folder.exists())
        {
            final boolean result = folder.mkdir();
            if(result)
            {
                LOG.info("Folder " + objectDir + " was created.");

                existsObjectFolder = true;
            }
            else
            {
                LOG.warn("Folder " + objectDir + " was NOT created.");

                existsObjectFolder = false;
            }
        }
    }

    public synchronized Hashtable<String, Long> getMessageProperties()
    {
        PreparedStatement pst = null;
        ResultSet rsProperty = null;
        final Hashtable<String, Long> msgProperty = new Hashtable<String, Long>();

        // Connect the database
        if(connect() == false)
        {
            return msgProperty;
        }

        try
        {
            pst = dbService.getConnection().prepareStatement("SELECT * from MSG_PROPERTY_TYPE");

            // Execute the query to get all properties
            rsProperty = pst.executeQuery();

            // Check the result sets
            if(rsProperty != null)
            {
                // Fill the hash table with the received data of the property table
                while(rsProperty.next())
                {
                    msgProperty.put(rsProperty.getString(2), rsProperty.getLong(1));
                }
            }
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : getMessageProperties(): " + e.getMessage());

            msgProperty.clear();
        }
        finally
        {
            if(rsProperty!=null){try{rsProperty.close();}catch(final Exception e){}rsProperty=null;}

            // Close the database
            close();
        }

        return msgProperty;
    }

    public synchronized int getMaxNumberofValueBytes()
    {
        PreparedStatement pst = null;
        ResultSetMetaData rsMetaData = null;
        ResultSet rs = null;
        int result = 0;

        // Connect the database
        if(connect() == false)
        {
            return result;
        }

        try
        {
            pst = dbService.getConnection().prepareStatement("SELECT * from message_content WHERE id=?");
            pst.setLong(1, 1);
            rs = pst.executeQuery();
            rsMetaData = rs.getMetaData();

            // Check the result sets
            if(rsMetaData != null)
            {
                final int count = rsMetaData.getColumnCount();

                for(int i = 1;i <= count;i++)
                {
                    if(rsMetaData.getColumnName(i).compareToIgnoreCase("value") == 0)
                    {
                        result = rsMetaData.getPrecision(i);
                    }
                }
            }
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : getMaxNumberofValueBytes(): " + e.getMessage());

            result = 0;
        }
        finally
        {
            rsMetaData = null;
            if(rs!=null){try{rs.close();}catch(final Exception e){}rs=null;}

            close();
        }

        return result;
    }

    public synchronized long getNextId(final String tableName)
    {
        PreparedStatement pst = null;
        ResultSet rsMsg = null;
        long result = -1;

        try
        {
            pst = dbService.getConnection().prepareStatement("SELECT MAX(ID) from " + tableName);
            rsMsg = pst.executeQuery();

            if(rsMsg != null)
            {
                rsMsg.next();
                result = rsMsg.getLong(1);
                result += 1;
            }
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : getNextId(): " + e.getMessage());
            result = -1;
        }
        finally
        {
            if(rsMsg!=null){try{rsMsg.close();}catch(final SQLException sqle){}rsMsg=null;}
            if(pst!=null){try{pst.close();}catch(final SQLException sqle){}pst=null;}
        }

        return result;
    }

    /**
     * The method deletes the message with the given id from the table.
     *
     *  @param msgId The id of the message that has to be deleted.
     */
    public synchronized void deleteMessage(final long msgId)
    {
        PreparedStatement pst = null;

        // Connect the database
        if(connect() == false)
        {
            return;
        }

        try
        {
            pst = dbService.getConnection().prepareStatement("DELETE FROM message_content WHERE message_id=?");
            pst.setLong(1, msgId);
            pst.executeUpdate();
            if(pst!=null){try{pst.close();}catch(final SQLException sqle){}pst=null;}

            pst = dbService.getConnection().prepareStatement("DELETE FROM message WHERE id=?");
            pst.setLong(1, msgId);
            pst.executeUpdate();
            if(pst!=null){try{pst.close();}catch(final SQLException sqle){}pst=null;}
        }
        catch(final Exception e)
        {
            LOG.error("*** Exception *** : deleteMessage(): " + e.getMessage());
        }

        close();
    }
}
