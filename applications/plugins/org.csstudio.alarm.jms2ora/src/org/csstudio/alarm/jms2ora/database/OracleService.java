
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleResultSet;
import oracle.jdbc.driver.OracleStatement;
import oracle.jdbc.pool.OracleDataSource;

/**
 *  The class <code>OracleDBLayer</code> 
 *  
 *  @author Markus Moeller
 *
 */

public class OracleService
{
    private Logger logger = null;
    private OracleDataSource dataSource = null;
    private OracleConnection conn = null;
    private String oracleUser = null;
    private String oraclePassword = null;

    private static final String dbOracle = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP) (HOST = dbsrv01.desy.de)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP) (HOST = dbsrv02.desy.de)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP) (HOST = dbsrv03.desy.de) (PORT = 1521)) (LOAD_BALANCE = yes) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = desy_db.desy.de) (FAILOVER_MODE =(TYPE = NONE) (METHOD = BASIC) (RETRIES = 180) (DELAY = 5))))";
    
    /**
     * 
     * @param l Logger
     * @param user ORACLE user name
     * @param password ORACLE password
     */
    public OracleService(Logger l, String user, String password)
    {
        logger = l;
        oracleUser = user;
        oraclePassword = password;
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
            dataSource = new OracleDataSource();
            dataSource.setURL(dbOracle);
            
            conn = (OracleConnection)dataSource.getConnection(oracleUser, oraclePassword);
        }
        catch (SQLException sqle)
        {
            logger.error(" *** EXCEPTION *** : " + sqle.getMessage());
            
            return false;
        }
        
        return true;
    }
    
    /**
     * <code>close</code> closes the connection to the database. 
     *
     */
    
    public void close()
    {
        try
        {
            conn.close();
        }
        catch (SQLException e) { }
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
            result = conn.isClosed();
            
            result = !result;
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
        OracleResultSet rset = null;
        OracleStatement stmt = null;
        
        try
        {
            stmt = (OracleStatement)conn.createStatement(OracleResultSet.TYPE_SCROLL_INSENSITIVE, OracleResultSet.CONCUR_UPDATABLE);
            rset = (OracleResultSet)stmt.executeQuery(query);
        }
        catch (SQLException sqle)
        {
            logger.error("*** EXCEPTION *** : " + sqle.getMessage());
            
            rset = null;
        }
        
        return (ResultSet)rset;
    }
    
    /**
     * <code>createMessageEntry</code> creates an entry in the table <i>MESSAGE</i>.
     * 
     * @param typeId - The ID of the message type which is defined in the table <i>MSG_TYPE</i>.
     * @param datum  - The date of the event(contained in the map message) or the current date
     * @return The ID of the new entry or -1 if it fails.
     */
    
    public long createMessageEntry(long typeId, String datum)
    {
        ResultSet   rsMsg   = null;
        String      query   = null;
        long        msgId   = -1;
        
        // Connect the database
        if(connect() == false)
        {
            return msgId;
        }

        // Get the highest ID in the table
        rsMsg      = executeSQLQuery("SELECT MAX(ID) from MESSAGE");
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
            query = "INSERT INTO message VALUES(" + msgId + "," + typeId + ",TIMESTAMP '" + datum + "')";            
            
            // System.out.println(query + "\n");
            
            rsMsg = executeSQLQuery(query);
            if(rsMsg == null)
            {
                msgId = -1;
            }
            else
            {
                try
                {
                    rsMsg.getStatement().close();
                    rsMsg.close();
                }
                catch (SQLException sqle) { sqle.printStackTrace(); }
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
    
    public boolean createMessageContentEntries(long msgId, Hashtable<Long, String> msgContent)
    {
        Enumeration<?>  lst         = null;
        ResultSet       rsMsg       = null;
        String          value       = null;
        String          query       = null;
        boolean         result      = false;
        long            contentId   = -1;
        long            key;
        
        // Connect the database
        if(connect() == false)
        {
            return false;
        }
        
        // Get the highest ID in the table
        rsMsg      = executeSQLQuery("SELECT MAX(id) FROM message_content");
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
            lst = msgContent.keys();
            while(lst.hasMoreElements())
            {
                query = "INSERT INTO message_content VALUES(";
            
                key = (Long)lst.nextElement();
                value = msgContent.get(key);
                
                // Replace a single ' with '' (then the entry could be stored into the database)
                value = value.replace("'", "''");
                
                query = query + contentId + "," + msgId + "," + key + ",'" + value + "')";
                
                rsMsg = executeSQLQuery(query);
                if(rsMsg == null)
                {
                    result = false;
                    
                    break;
                }
                else
                {
                    try
                    {
                        rsMsg.getStatement().close();
                        rsMsg.close();
                    }
                    catch (SQLException sqle) { sqle.printStackTrace(); }
                    
                    result = true;
                }
                
                contentId++;
            }
        }
        
        close();
        
        return result;
    }
    
    /**
     * The method deletes the message with the given id from the table.
     * 
     *  @param msgId The id of the message that has to be deleted.
     */
    public void deleteMessage(long msgId)
    {
        ResultSet rsMsg = null;
        
        // Connect the database
        if(connect() == false)
        {
            return;
        }

        try
        {
            rsMsg = executeSQLQuery("DELETE FROM message_content where message_id=" + msgId);
            rsMsg.getStatement().close();
        }
        catch (SQLException sqle) { }

        try
        {
            rsMsg = executeSQLQuery("DELETE FROM message where id=" + msgId);
            rsMsg.getStatement().close();
        }
        catch (SQLException sqle) { }              

        close();
    }
    
    /**
     * Gets the used tablespace quota.
     * 
     *  @return Used tablespace quota in %
     */
    public int getUsedQuota()
    {
        ResultSet rsMsg = null;
        long usedBytes = 0;
        long maxBytes = 0;
        int result = 0;

        // Connect the database
        if(connect() == false)
        {
            return result;
        }

        try
        {
            rsMsg = executeSQLQuery("SELECT BYTES,MAX_BYTES FROM USER_TS_QUOTAS WHERE TABLESPACE_NAME LIKE 'DATA%'");
            
            while(rsMsg.next())
            {
                usedBytes = rsMsg.getLong("BYTES");
                maxBytes = rsMsg.getLong("MAX_BYTES");
            }
            
            rsMsg.getStatement().close();
        }
        catch(SQLException sqle)
        {
            usedBytes = 0;
            maxBytes = 0;
        }

        close();
        
        if((usedBytes > 0) && (maxBytes > 0))
        {
            long wert = (usedBytes * 100) / maxBytes;
            result = (int)wert;
        }
        
        return result;
    }
    
    public static int getUsedQuota(Logger logger, String dbUser, String dbPassword)
    {
        OracleDataSource dataSource = null;
        OracleConnection conn = null;
        OracleStatement stmt = null;
        OracleResultSet rsMsg = null;
        long usedBytes = 0;
        long maxBytes = 0;        
        int result = 0;
        
        try
        {
            dataSource = new OracleDataSource();
            dataSource.setURL(dbOracle);
            
            conn = (OracleConnection)dataSource.getConnection(dbUser, dbPassword);
            stmt = (OracleStatement)conn.createStatement(OracleResultSet.TYPE_SCROLL_INSENSITIVE, OracleResultSet.CONCUR_UPDATABLE);
            rsMsg = (OracleResultSet)stmt.executeQuery("SELECT BYTES,MAX_BYTES FROM USER_TS_QUOTAS WHERE TABLESPACE_NAME LIKE 'DATA%'");
            
            while(rsMsg.next())
            {
                usedBytes = rsMsg.getLong("BYTES");
                maxBytes = rsMsg.getLong("MAX_BYTES");
            }
            
            rsMsg.close();
            rsMsg = null;
            
            stmt.close();
            stmt = null;
        }
        catch(SQLException sqle)
        {
            logger.error("*** SQLException *** : " + sqle.getMessage());
            usedBytes = 0;
            maxBytes = 0;

            result = -1;
        }
        finally
        {
            if(rsMsg!=null){try{rsMsg.close();rsMsg=null;}catch(SQLException e){}}
            if(stmt!=null){try{stmt.close();stmt=null;}catch(SQLException e){}}
            if(conn!=null){try{conn.close();conn=null;}catch(SQLException e){}}
            if(dataSource!=null){try{dataSource.close();dataSource=null;}catch(SQLException e){}}            
        }

        if((usedBytes > 0) && (maxBytes > 0))
        {
            long wert = (usedBytes * 100) / maxBytes;
            result = (int)wert;
        }

        return result;
    }
}
