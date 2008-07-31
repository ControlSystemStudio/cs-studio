
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

package org.csstudio.alarm.jms2ora;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.csstudio.alarm.jms2ora.database.DatabaseLayer;
import org.csstudio.alarm.jms2ora.database.OracleService;
import org.csstudio.alarm.jms2ora.util.MessageContent;
import org.csstudio.alarm.jms2ora.util.MessageContentCreator;
import org.csstudio.alarm.jms2ora.util.MessageReceiver;
import org.csstudio.alarm.jms2ora.util.WaifFileHandler;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;
import de.desy.epics.singleton.EpicsSingleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <code>StoreMessages</code> gets all messages from the topics <b>ALARM and LOG</b> and stores them into the
 * database.
 * 
 * Steps:
 * 
 * 1. Read all message properties from the database and store them into a hash table
 * 2. Create a receiver and set the asynchronous message receiving.
 * 3. Wait for messages
 * 4. If a message is received, process it. If the processing fails, store the message in a file.
 * 
 * Message processing:
 * 
 * 1. Check for the EVENTTIME property
 *    Set the property EVENTTIME to the current date, if the message does not contain it.
 * 2. Create a hash table with the following entries:
 *    Long   - The key (ID from MSG_PROPERTY_TYPE)
 *    String - The value (value from the received map message)
 * 3. Create an entry in the table MESSAGE and return the ID for the entry
 * 4. With the ID from step 3 create the entries in the table MESSAGE_CONTENT
 * 5. If the last step fails, delete all created entries in MESSAGE and MESSAGE_CONTENT
 * 
 * @author  Markus Moeller
 * @version 2.0.0
 */

/*
 * TODO:    Auslagern von bestimmten Funktionen in eigenständige Klassen
 *          - Die Properties der Datenbanktabellen
 */

public class MessageProcessor implements MessageListener
{
    /** The object instance of this class */
    private static MessageProcessor instance = null;
    
    /** Queue for received messages */
    private ConcurrentLinkedQueue<MapMessage> messages = new ConcurrentLinkedQueue<MapMessage>();
    
    /** Object for database handling */
    private DatabaseLayer dbLayer = null;
    
    private MessageContentCreator contentCreator = null;
    
    /** Array of message receivers */
    private MessageReceiver[] receivers = null;
    
    /** Reads and holds the configuration stored in the confid file */
    private PropertiesConfiguration config = null;
    
    /** The logger */
    private Logger logger = null;
    
    /** Array with JMS server URLs */
    private String[] urlList = null;
    
    /** Array with topic names */
    private String[] topicList = null;
    
    /** Date object that indicates the last update of the quota record */
    private Date lastQuotaUpdate = null;
    
    /** Number of stored message files */
    private int fileNumber = 0;
        
    /** Indicates if the application was initialized or not */
    private boolean initialized = false;
    
    /** Indicates wether or not the application should stop */
    private boolean running = true;
    
    /** True if the folder 'nirvana' exists. This folder holds the stored message object content. */
    private boolean existsObjectFolder = false;

    private final String version = " 2.0.0";
    private final String build = " - BUILD 2008-07-31 16:00";
    private final String application = "Jms2Ora";
    private final String objectDir = ".\\nirvana\\";
    
    
    public final long RET_ERROR = -1;
    public static final int CONSOLE = 1;
    
    public final int PM_RETURN_OK = 0;
    public final int PM_RETURN_DISCARD = 1;
    public final int PM_RETURN_EMPTY = 2;
    public final int PM_ERROR_DB = 3;
    public final int PM_ERROR_JMS = 4;
    public final int PM_ERROR_GENERAL = 5;
    
    public final String[] infoText = { "Message have been written into the database.",
                                       "Message have been discarded.",
                                       "Message is empty.",
                                       "Database error",
                                       "JMS error",
                                       "General error"};
    
    /**
     * A nice private constructor...
     *
     */    
    private MessageProcessor()
    {
        int i;
        
        // Create the logger
        logger = Logger.getLogger(MessageProcessor.class);

        // Create the folder that will hold message objects that could not be stored into the database
        checkObjectFolder();
        
        // Get the configuration
        config = Jms2OraPlugin.getDefault().getConfiguration();
        
        dbLayer = new DatabaseLayer(config.getString("database.url"), config.getString("database.user"), config.getString("database.password"));
        
        contentCreator = new MessageContentCreator(dbLayer);
        
        lastQuotaUpdate = Calendar.getInstance().getTime();
        
        if(config.containsKey("provider.url") && config.containsKey("topic.names"))
        {
            urlList = config.getStringArray("provider.url");
            topicList = config.getStringArray("topic.names");
            
            for(i = 0;i < urlList.length;i++)
            {
                logger.info("[" + urlList[i] + "]");
            }
            
            for(i = 0;i < topicList.length;i++)
            {
                logger.info("[" + topicList[i] + "]");
            }
            
            receivers = new MessageReceiver[urlList.length];
            
            for(i = 0;i < urlList.length;i++)
            {
                try
                {
                    receivers[i] = new MessageReceiver("org.apache.activemq.jndi.ActiveMQInitialContextFactory", urlList[i], topicList);
                    
                    receivers[i].startListener(this, application);
                    
                    initialized = true;
                }
                catch(Exception e)
                {
                    logger.error("*** Exception *** : " + e.getMessage());
                    
                    initialized = false;
                }
            }
            
            initialized = (initialized == true) ? true : false;
        }
        else
        {
            initialized = false;
        }
    }
    
    public static synchronized MessageProcessor getInstance()
    {
        if(instance == null)
        {
            instance = new MessageProcessor();
        }
        
        return instance;
    }
    
    /**
     * <code>executeMe</code> is the main method of the class StoreMessages.
     *
     */
    
    public void run()
    {
        MessageContent content = null;
        MapMessage mapMessage  = null;
        int result;
        
        logger.info("Started" + application + version + build);
        
        logger.info("Waiting for messages...");
        
        while(running)
        {
            while(!messages.isEmpty() && running)
            {
                mapMessage = messages.poll();
                
                content = contentCreator.convertMapMessage(mapMessage);               
                result = processMessage(content);
                if((result != PM_RETURN_OK) && (result != PM_RETURN_DISCARD) && (result != PM_RETURN_EMPTY))
                {                    
                    // Store the message in a file, if it was not possible to write it to the DB.
                    writeMessageContentToFile(content);
                    
                    logger.warn(infoText[result] + ": Could not store the message in the database. Message is written on disk.");
                }
                else
                {
                    if(result != PM_RETURN_OK)
                    {
                        logger.info(infoText[result]);
                    }
                    else
                    {
                        logger.debug(infoText[result]);
                    }
                }
            }

            if(running)
            {
                synchronized(this)
                {
                    try
                    {
                        wait();                    
                    }
                    catch(InterruptedException ie)
                    {
                        logger.error("*** InterruptedException *** : executeMe() : wait() : " + ie.getMessage());
                    
                        running = false;
                    }               
                }
            }
        }
        
        // Process the remaining messages
        logger.info("Remaining messages: " + messages.size() + " -> Processing...");
        
        int writtenToDb = 0;
        int writtenToHd = 0;
        
        while(!messages.isEmpty())
        {
            mapMessage = messages.poll();
            content = contentCreator.convertMapMessage(mapMessage);
            
            result = processMessage(content);
            if((result != PM_RETURN_OK) && (result != PM_RETURN_DISCARD) && (result != PM_RETURN_EMPTY))
            {                    
                // Store the message in a file, if it was not possible to write it to the DB.
                writeMessageContentToFile(content);
                
                writtenToHd++;
            }
            else
            {
                writtenToDb++;
            }
            
            mapMessage = null;
        }
        
        logger.info("Remaining messages stored in the database: " + writtenToDb);
        logger.info("Remaining messages stored on disk:         " + writtenToHd);

        logger.info("executeMe() : ** DONE **");
    }

    public void onMessage(Message message)
    {
        if(message instanceof MapMessage)
        {
            messages.add((MapMessage)message);
        
            synchronized(this)
            {
                notify();
            }
        }
        else
        {
            logger.info("Received a non MapMessage object. Discarded...");
        }        
    }

    public int processMessage(MessageContent content)
    {
        Date currentDate = null;
        String[] recordNames = null;
        String temp = null;
        long typeId = 0;
        long msgId = 0;
        int result = PM_RETURN_OK;

        if(content.discard())
        {
            return PM_RETURN_DISCARD;
        }

        if(!content.hasContent())
        {
            return PM_RETURN_EMPTY;
        }
                
        // Create an entry in the table MESSAGE
        // TODO: typeId is always 0!!! We don not use it anymore. Delete the column in a future version.
        msgId = dbLayer.createMessageEntry(typeId, content.getPropertyValue("EVENTTIME"));
        if(msgId == RET_ERROR)
        {
            logger.error("createMessageEntry(): No message entry created");
            
            return PM_ERROR_DB;
        }
        
        if(dbLayer.createMessageContentEntries(msgId, content) == false)
        {
            logger.error("createMessageContentEntries()");
            
            dbLayer.deleteMessage(msgId);
            
            result = PM_ERROR_DB;
        }
        else
        {
            // Refresh the used table quota
            // ONLY if we use ORACLE!!!!!
            if(dbLayer.getDialect() == Dialect.Oracle)
            {
                currentDate = Calendar.getInstance().getTime();
                
                // Wait min. 5 minutes for updating the EPICS record
                if((currentDate.getTime() - lastQuotaUpdate.getTime()) >= 10000 /*300000*/)
                {
                    if(config.containsKey("record.tablequota"))
                    {
                        recordNames = config.getStringArray("record.tablequota");
                        for(int i = 0;i < recordNames.length;i++)
                        {
                            temp = createDatabaseNameFromRecord(recordNames[i]);
                            
                            if(temp.compareToIgnoreCase(config.getString("database.user")) != 0)
                            {
                                updateDBRecord(recordNames[i], temp, temp.toLowerCase());
                            }
                            else
                            {
                                updateDBRecord(recordNames[i]);
                            }
                        }
                    }
                    else
                    {
                        logger.warn("No EPICS record name for the database quota is defined.");
                    }
                    
                    // Always get the quota for user KRYKLOG
                    // NOT USED: The MAX_BYTES value seemed always to be -1 (unlimited)
                    // updateDBRecord("krykLog:UsedQuota_ai", "KRYKLOG", "kryklog");
                    
                    lastQuotaUpdate = currentDate;
                    currentDate = null;
                }
            }
            else
            {
                logger.warn("Database system is NOT ORACLE. No update of the used table quota record started.");
            }
            
            result = PM_RETURN_OK;
        }
        
        return result;
    }
    
    /**
     * Writes the new ORACLE table space quota to the record.
     *  
     *  @param name Record name
     */
    public void updateDBRecord(String name)
    {
        int quota = dbLayer.getUsedQuota();

        EpicsSingleton.getInstance().setValue(name, String.valueOf(quota));
        String record = EpicsSingleton.getInstance().get(name);
        logger.info(name + " quota: " + record);    
    }
    
    /**
     * Writes the new ORACLE table space quota to the record.
     * 
     * Remark: In fact, it is not the best place to update this record in Jms2Ora.
     * 
     *  @param recordName Record name
     *  @param dbUser Oracle user name
     *  @param dbPassword Oracle password name
     */
    public void updateDBRecord(String recordName, String dbUser, String dbPassword)
    {
        int quota = OracleService.getUsedQuota(logger, dbUser, dbPassword);

        EpicsSingleton.getInstance().setValue(recordName, String.valueOf(quota));
        String record = EpicsSingleton.getInstance().get(recordName);
        logger.info(recordName + " quota: " + record);    
    }

    public String createDatabaseNameFromRecord(String record)
    {
        String result = null;
        
        if(record.indexOf(':') != -1)
        {
            result = record.substring(0, record.indexOf(':')).toUpperCase();
        }
        
        return result;
    }
    
    /**
     * <code>writeMapMessageToFile</code> writes a map message object to disk.
     * 
     * @param content - The MessageContent object that have to be stored on disk.
     */

    public void writeMessageContentToFile(MessageContent content)
    {
        SimpleDateFormat dfm = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        GregorianCalendar cal = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        String fn = null;

        if(!content.hasContent())
        {
            logger.info("Message does not contain content.");
            
            return;
        }

        if(existsObjectFolder == false)
        {
            logger.warn("Object folder 'nirvana' does not exist. Message cannot be stored.");
            
            return;
        }
        
        cal = new GregorianCalendar();
        fn  = "waif_" + dfm.format(cal.getTime());                

        try
        {
            fos = new FileOutputStream(".\\nirvana\\" + fn + ".ser");
            oos = new ObjectOutputStream(fos);
            
            // Write the MessageContent object to disk
            oos.writeObject(content);            
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
        
        System.out.println(content.toString());
        
        readMessageContent(".\\nirvana\\" + fn + ".ser");
    }
    
    private void readMessageContent(String fileName)
    {
        MessageContent content = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try
        {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            
            // Write the MessageContent object to disk
            content = (MessageContent)ois.readObject();            
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("FileNotFoundException : " + fnfe.getMessage());
        }
        catch(IOException ioe)
        {
            logger.error("IOException : " + ioe.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            logger.error("ClassNotFoundException : " + e.getMessage());
        }
        finally
        {
            if(ois != null){try{ois.close();}catch(IOException ioe){}}
            if(fis != null){try{fis.close();}catch(IOException ioe){}}
            
            ois = null;
            fis = null;            
        }

        System.out.println(content.toString());
    }
    
    /**
     * <code>isInitialized</code>
     * 
     * @return true, if the initialization was successfull ; false, if it was not
     */
    
    public boolean isInitialized()
    {
        return initialized;
    }
    
    /**
     * The method returns the number of MapMessage objects which were serialized. Uses the {@link WaifFileHandler}
     * class.
     * 
     * @return Number of MapMessages objects which were written to the database because of errors during
     *         processing the objects.
     */
    
    public int getNumberOfWaifFiles()
    {
        WaifFileHandler waifFiles   = new WaifFileHandler();
        int             result      = 0;

        result = waifFiles.getNumberOfWaifFiles();
        
        waifFiles = null;
        
        return result;
    }
    
    /**
     * The method returns an array of String with the file names of all serialized MapMessage objects.
     * Uses the {@link WaifFileHandler} class.
     * 
     * @return Array of String with the file names.
     */
    
    public String[] getNameOfWaifFiles()
    {
        WaifFileHandler waifFiles   = new WaifFileHandler();
        String[]        result      = null;

        result = waifFiles.getWaifFileNames();
        
        waifFiles = null;
        
        return result;
    }

    public String[] getWaifFileContent()
    {
        MapMessage  msg     = null;
        String[]    result  = null;
        String      name    = null;
        
        WaifFileHandler waifFiles   = new WaifFileHandler();

        msg = waifFiles.getWaifFileContent(fileNumber);

        waifFiles = null;
        
        if(msg != null)
        {
            try
            {
                Enumeration<?> lst = msg. getMapNames();
             
                int count = 0;
                
                result = new String[32];
                
                while(lst.hasMoreElements())
                {
                    name = (String)lst.nextElement();
                    
                    result[count++] = name + "=" + msg.getString(name);
                }
            }
            catch (JMSException e)
            {
                result = null;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return The number of files that could not be deleted.
     */
    
    public int deleteAllObjectFiles()
    {
        int result = 0;
        
        WaifFileHandler waifFiles   = new WaifFileHandler();

        result = waifFiles.deleteAllFiles();
        
        waifFiles = null;
        
        return result;
    }
    
    public int getFileNumber()
    {
        return fileNumber;
    }
    
    public void setFileNumber(int number)
    {
        fileNumber = number;
    }
    
    public int getNumberOfQueuedMessages()
    {
        if(messages != null)
        {
            return messages.size();
        }
        else
        {
            return 0;
        }
    }
    
    public void closeAllReceivers()
    {
        logger.info("closeAllReceivers(): Closing all receivers.");
        
        if(receivers != null)
        {
            for(int i = 0;i < receivers.length;i++)
            {
                receivers[i].stopListening();
            }
        }
    }
    
    public void shutdown()
    {
        running = false;
        
        logger.info("The application will shutdown now...");
        
        // Very important to close all receivers and connections!!!!!
        closeAllReceivers();
        
        synchronized(this)
        {
            notify();
        }
    }

    // Not used for the Jms2Ora headless eclipse application
    public boolean restart()
    {
        boolean result = false;
        
        running  = false;
        
        // Very important to close all receivers and connections!!!!!
        closeAllReceivers();
        
        ProcessBuilder pb = new ProcessBuilder("java.exe", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=8101", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-jar", "JMS2Ora.jar");

        pb.directory(new File(System.getProperty("user.dir")));
        
        try
        {
             pb.start();
             
             result = true;
        }
        catch(IOException ioe)
        {
            logger.info("ProcessRestart : restart() :\n   *** IOException *** : " + ioe.getMessage());
            
            result = false;
        }
        
        logger.info("Restarting me...");
        
        synchronized(this)
        {
            notify();
        }

        return result;
    }
    
    /**
     * 
     */
    private void checkObjectFolder()
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
}
