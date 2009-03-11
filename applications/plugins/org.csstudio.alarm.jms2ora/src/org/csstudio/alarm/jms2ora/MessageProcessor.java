
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

import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.csstudio.alarm.jms2ora.database.DatabaseLayer;
import org.csstudio.alarm.jms2ora.util.ApplicState;
import org.csstudio.alarm.jms2ora.util.MessageContent;
import org.csstudio.alarm.jms2ora.util.MessageContentCreator;
import org.csstudio.alarm.jms2ora.util.MessageReceiver;
import org.csstudio.alarm.jms2ora.util.MessageFileHandler;
import org.csstudio.alarm.jms2ora.util.SimpleStatistic;
import org.csstudio.platform.statistic.Collector;

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

public class MessageProcessor extends Thread implements MessageListener
{
    /** The object instance of this class */
    private static MessageProcessor instance = null;
    
    /** Queue for received messages */
    private ConcurrentLinkedQueue<MessageContent> messages = new ConcurrentLinkedQueue<MessageContent>();
    
    /** Object for database handling */
    private DatabaseLayer dbLayer = null;
    
    /** Object that creates the MessageContent objects */
    private MessageContentCreator contentCreator = null;
    
    /** Array of message receivers */
    private MessageReceiver[] receivers = null;
    
    /** Reads and holds the configuration stored in the confid file */
    private PropertiesConfiguration config = null;
    
    /** A very simple statistic class */
    private SimpleStatistic statistic = null;
    
    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector receivedMessages = null;
    
    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector emptyMessages = null;

    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector discardedMessages = null;

    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector storedMessages = null;

    /** The logger */
    private Logger logger = null;
    
    /** Array with JMS server URLs */
    private String[] urlList = null;
    
    /** Array with topic names */
    private String[] topicList = null;
    
    /** Indicates if the application was initialized or not */
    private boolean initialized = false;
    
    /** Indicates whether or not the application should stop */
    private boolean running = true;

    /** Indicates whether or not this thread stopped clean */
    private boolean stoppedClean = false;
    
    private Jms2OraApplication parent = null;
    
    private final String version = " 2.2.1";
    private final String build = " - BUILD 2009-01-13 08:15";
    private final String application = "Jms2Ora";

    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 15000 ;

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
                                       "General error" };

    /**
     * A nice private constructor...
     *
     */    
    private MessageProcessor()
    {
        // Create the logger
        logger = Logger.getLogger(MessageProcessor.class);
        
        // Get the configuration
        config = Jms2OraPlugin.getDefault().getConfiguration();
        
        String url = config.getString("database.url");
        String user = config.getString("database.user");
        String password = config.getString("database.password");
        
        dbLayer = new DatabaseLayer(url, user, password);

        // Instantiate MessageContentCreator that uses its own database layer
        // IMPORTANT: Do not let it use the database layer created above
        contentCreator = new MessageContentCreator(url, user, password);
        
        statistic = SimpleStatistic.getInstance();
        
        receivedMessages = new Collector();
        receivedMessages.setApplication("Jms2Ora");
        receivedMessages.setDescriptor("Received messages");
        receivedMessages.setContinuousPrint(false);
        receivedMessages.setContinuousPrintCount(1000.0);
        
        emptyMessages = new Collector();
        emptyMessages.setApplication("Jms2Ora");
        emptyMessages.setDescriptor("Filtered messages");
        emptyMessages.setContinuousPrint(false);
        emptyMessages.setContinuousPrintCount(1000.0);
        
        discardedMessages = new Collector();
        discardedMessages.setApplication("Jms2Ora");
        discardedMessages.setDescriptor("Discarded messages");
        discardedMessages.setContinuousPrint(false);
        discardedMessages.setContinuousPrintCount(1000.0);
        
        storedMessages = new Collector();
        storedMessages.setApplication("Jms2Ora");
        storedMessages.setDescriptor("Stored messages");
        storedMessages.setContinuousPrint(false);
        storedMessages.setContinuousPrintCount(1000.0);
        
        if(config.containsKey("provider.url") && config.containsKey("topic.names"))
        {
            urlList = config.getStringArray("provider.url");
            topicList = config.getStringArray("topic.names");
            
            for(int i = 0;i < urlList.length;i++)
            {
                logger.info("[" + urlList[i] + "]");
            }
            
            for(int i = 0;i < topicList.length;i++)
            {
                logger.info("[" + topicList[i] + "]");
            }
            
            receivers = new MessageReceiver[urlList.length];
            
            for(int i = 0;i < urlList.length;i++)
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
        int result;
        
        logger.info("Started" + application + version + build);        
        logger.info("Waiting for messages...");
        
        while(running)
        {
            parent.setStatus(ApplicState.WORKING);

            while(!messages.isEmpty() && running)
            {
                content = messages.poll();
                result = processMessage(content);
                if((result != PM_RETURN_OK) && (result != PM_RETURN_DISCARD) && (result != PM_RETURN_EMPTY))
                {                    
                    // Store the message in a file, if it was not possible to write it to the DB.
                    MessageFileHandler.getInstance().writeMessageContentToFile(content);
                    
                    logger.warn(infoText[result] + ": Could not store the message in the database. Message is written on disk.");
                }
                else
                {
                    if(result != PM_RETURN_OK)
                    {
                        logger.info(infoText[result]);
                        if(result == PM_RETURN_DISCARD)
                        {
                            statistic.incrementNumberOfDiscardedMessages();
                            discardedMessages.incrementValue();
                        }
                        else if(result == this.PM_RETURN_EMPTY)
                        {
                            statistic.incrementNumberOfEmptyMessages();
                            emptyMessages.incrementValue();
                        }
                    }
                    else
                    {
                        statistic.incrementNumberOfStoredMessages();
                        storedMessages.incrementValue();
                        logger.debug(infoText[result]);
                    }
                }
                
                logger.debug(statistic.toString());
                
                // IMPORTANT: Refresh the current state, otherwise Jms2Ora will restart if many messages
                // are stored in the queue and state switching does not happen while storing all
                // the messages.
                parent.setStatus(ApplicState.WORKING);
            }

            if(running)
            {
                parent.setStatus(ApplicState.SLEEPING);
                
                synchronized(this)
                {
                    try
                    {
                        wait(SLEEPING_TIME);
                    }
                    catch(InterruptedException ie)
                    {
                        logger.error("*** InterruptedException *** : executeMe(): wait(): " + ie.getMessage());
                    
                        running = false;
                    }               
                }
                
                logger.debug("Waked up...");
            }
        }
        
        parent.setStatus(ApplicState.LEAVING);
        
        closeAllReceivers();
        
        // Process the remaining messages
        logger.info("Remaining messages: " + messages.size() + " -> Processing...");
        
        int writtenToDb = 0;
        int writtenToHd = 0;
        
        while(!messages.isEmpty())
        {
            content = messages.poll();
            
            result = processMessage(content);
            if((result != PM_RETURN_OK) && (result != PM_RETURN_DISCARD) && (result != PM_RETURN_EMPTY))
            {                    
                // Store the message in a file, if it was not possible to write it to the DB.
                MessageFileHandler.getInstance().writeMessageContentToFile(content);
                
                writtenToHd++;
            }
            else
            {
                writtenToDb++;
            }
            
            content = null;
        }
        
        stoppedClean = true;
        
        logger.info("Remaining messages stored in the database: " + writtenToDb);
        logger.info("Remaining messages stored on disk:         " + writtenToHd);
        
        parent.setStatus(ApplicState.STOPPED);

        logger.info("executeMe() : ** DONE **");
    }

    public boolean stoppedClean()
    {
        return stoppedClean;
    }
    
    public void onMessage(Message message)
    {
        MessageContent content = null;
        
        logger.debug("onMessage(): " + message.toString());
        
        if(message instanceof MapMessage)
        {
            content = contentCreator.convertMapMessage((MapMessage)message);
            messages.add(content);
            statistic.incrementNumberOfReceivedMessages();
            receivedMessages.incrementValue();

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
        // TODO: typeId is always 0!!! We do not use it anymore. Delete the column in a future version.
        msgId = dbLayer.createMessageEntry(typeId, content);
        if(msgId == RET_ERROR)
        {
            logger.error("createMessageEntry(): No message entry created in database.");
            
            return PM_ERROR_DB;
        }
        
        if(dbLayer.createMessageContentEntries(msgId, content) == false)
        {
            logger.error("createMessageContentEntries(): No entry created in message_content. Delete message from database and store it to disk.");
            
            dbLayer.deleteMessage(msgId);
            
            result = PM_ERROR_DB;
        }
        else
        {
            result = PM_RETURN_OK;
        }
        
        return result;
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
     * <code>isInitialized</code>
     * 
     * @return true, if the initialization was successfull ; false, if it was not
     */
    
    public boolean isInitialized()
    {
        return initialized;
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
    
    public void setParent(Jms2OraApplication parent)
    {
        this.parent = parent;
    }
    
    public synchronized void stopWorking()
    {
        contentCreator.stopWorking();
        running = false;
        
        this.notify();
    }
}
