
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
import org.csstudio.alarm.jms2ora.database.DatabaseLayer;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.util.ApplicState;
import org.csstudio.alarm.jms2ora.util.Hostname;
import org.csstudio.alarm.jms2ora.util.MessageContent;
import org.csstudio.alarm.jms2ora.util.MessageContentCreator;
import org.csstudio.alarm.jms2ora.util.JmsMessageReceiver;
import org.csstudio.alarm.jms2ora.util.MessageFileHandler;
import org.csstudio.platform.statistic.Collector;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * TODO:    Auslagern von bestimmten Funktionen in eigenstaendige Klassen
 *          - Die Properties der Datenbanktabellen
 */

public class MessageProcessor extends Thread implements MessageListener {
    
    /** The object instance of this class */
    private static MessageProcessor instance = null;
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    /** Queue for received messages */
    private ConcurrentLinkedQueue<MessageContent> messages;
    
    /** Object for database handling */
    private DatabaseLayer dbLayer;
    
    /** Object that creates the MessageContent objects */
    private MessageContentCreator contentCreator;
    
    /** Array of message receivers */
    private JmsMessageReceiver[] receivers;
            
    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector receivedMessages;
    
    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector filteredMessages;

    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector discardedMessages;

    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector storedMessages;

    /** Array with JMS server URLs */
    private String[] urlList;
    
    /** Array with topic names */
    private String[] topicList;
    
    /** Indicates if the application was initialized or not */
    private boolean initialized;
    
    /** Indicates whether or not the application should stop */
    private boolean running;

    /** Indicates whether or not this thread stopped clean */
    private boolean stoppedClean;
    
    private Jms2OraApplication parent;
    
    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 15000 ;

    public final long RET_ERROR = -1;
    public static final int CONSOLE = 1;
    
    /**
     * A nice private constructor...
     *
     */    
    private MessageProcessor() {
        
        this.setName("MessageProcessor-Thread");
        
        messages = new ConcurrentLinkedQueue<MessageContent>();
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String url = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.DATABASE_URL, "", null);
        String user = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.DATABASE_USER, "", null);
        String password = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.DATABASE_PASSWORD, "", null);

        dbLayer = new DatabaseLayer(url, user, password);

        // Instantiate MessageContentCreator that uses its own database layer
        // IMPORTANT: Do not let it use the database layer created above
        contentCreator = new MessageContentCreator(url, user, password);
        
        receivedMessages = new Collector();
        receivedMessages.setApplication(VersionInfo.NAME);
        receivedMessages.setDescriptor("Received messages");
        receivedMessages.setContinuousPrint(false);
        receivedMessages.setContinuousPrintCount(1000.0);
        
        filteredMessages = new Collector();
        filteredMessages.setApplication(VersionInfo.NAME);
        filteredMessages.setDescriptor("Filtered messages");
        filteredMessages.setContinuousPrint(false);
        filteredMessages.setContinuousPrintCount(1000.0);
        
        discardedMessages = new Collector();
        discardedMessages.setApplication(VersionInfo.NAME);
        discardedMessages.setDescriptor("Discarded messages");
        discardedMessages.setContinuousPrint(false);
        discardedMessages.setContinuousPrintCount(1000.0);
        
        storedMessages = new Collector();
        storedMessages.setApplication(VersionInfo.NAME);
        storedMessages.setDescriptor("Stored messages");
        storedMessages.setContinuousPrint(false);
        storedMessages.setContinuousPrintCount(1000.0);
        
        String urls = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.JMS_PROVIDER_URLS, "", null);
        String topics = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.JMS_TOPIC_NAMES, "", null);
        
        running = true;
        stoppedClean = false;
        
        if((urls.length() > 0) && (topics.length() > 0)) {
            
            urlList = urls.split(",");
            topicList = topics.split(",");
            
            for(int i = 0;i < urlList.length;i++) {
                LOG.info("[" + urlList[i] + "]");
            }
            
            for(int i = 0;i < topicList.length;i++) {
                LOG.info("[" + topicList[i] + "]");
            }
            
            receivers = new JmsMessageReceiver[urlList.length];
            
            String hostName = Hostname.getInstance().getHostname();
            
            for(int i = 0;i < urlList.length;i++) {
                
                try {
                    receivers[i] = new JmsMessageReceiver("org.apache.activemq.jndi.ActiveMQInitialContextFactory", urlList[i], topicList);
                    receivers[i].startListener(this, VersionInfo.NAME + "@" + hostName + "_" + this.hashCode());
                    initialized = true;
                } catch(Exception e) {
                    LOG.error("*** Exception *** : " + e.getMessage());
                    initialized = false;
                }
            }
            
            initialized = (initialized == true) ? true : false;
        } else {
            initialized = false;
        }
    }

    public static synchronized MessageProcessor getInstance() {
        
        if(instance == null) {
            instance = new MessageProcessor();
        }
        
        return instance;
    }
    
    /**
     * <code>executeMe</code> is the main method of the class StoreMessages.
     *
     */
    
    @Override
    public void run() {
        
        MessageContent content = null;
        ReturnValue result;
        
        LOG.info("Started " + VersionInfo.getAll());        
        LOG.info("Waiting for messages...");
        
        while(running) {
            
            parent.setStatus(ApplicState.WORKING);

            while(!messages.isEmpty() && running) {
                
                content = messages.poll();
                result = processMessage(content);
                if((result != ReturnValue.PM_RETURN_OK)
                        && (result != ReturnValue.PM_RETURN_DISCARD)
                        && (result != ReturnValue.PM_RETURN_EMPTY)) {                    
                    
                    // Store the message in a file, if it was not possible to write it to the DB.
                    MessageFileHandler.getInstance().writeMessageContentToFile(content);
                    LOG.warn(result.getErrorMessage() + ": Could not store the message in the database. Message is written on disk.");
                } else {
                    
                    if(result != ReturnValue.PM_RETURN_OK) {
                        
                        LOG.info(result.getErrorMessage());
                        if(result == ReturnValue.PM_RETURN_DISCARD) {
                            discardedMessages.incrementValue();
                        } else if(result == ReturnValue.PM_RETURN_EMPTY) {
                            filteredMessages.incrementValue();
                        }
                    } else {
                        storedMessages.incrementValue();
                        LOG.debug(result.getErrorMessage());
                    }
                }
                
                // LOG.debug(statistic.toString());
                LOG.debug(createStatisticString());
                
                // IMPORTANT: Refresh the current state, otherwise Jms2Ora will restart if many messages
                // are stored in the queue and state switching does not happen while storing all
                // the messages.
                parent.setStatus(ApplicState.WORKING);
            }

            if(running) {
                
                parent.setStatus(ApplicState.SLEEPING);
                
                synchronized(this) {
                    try {
                        wait(SLEEPING_TIME);
                    } catch(InterruptedException ie) {
                        LOG.error("*** InterruptedException *** : executeMe(): wait(): " + ie.getMessage());
                        running = false;
                    }               
                }
                
                LOG.debug("Waked up...");
            }
        }
        
        parent.setStatus(ApplicState.LEAVING);
        
        closeAllReceivers();
        
        // Process the remaining messages
        LOG.info("Remaining messages: " + messages.size() + " -> Processing...");
        
        int writtenToDb = 0;
        int writtenToHd = 0;
        
        while(!messages.isEmpty()) {
            
            content = messages.poll();
            
            result = processMessage(content);
            if((result != ReturnValue.PM_RETURN_OK)
                    && (result != ReturnValue.PM_RETURN_DISCARD)
                    && (result != ReturnValue.PM_RETURN_EMPTY)) {                    
                
                // Store the message in a file, if it was not possible to write it to the DB.
                MessageFileHandler.getInstance().writeMessageContentToFile(content);
                
                writtenToHd++;
            } else {
                writtenToDb++;
            }
            
            content = null;
        }
        
        stoppedClean = true;
        
        LOG.info("Remaining messages stored in the database: " + writtenToDb);
        LOG.info("Remaining messages stored on disk:         " + writtenToHd);
        
        parent.setStatus(ApplicState.STOPPED);

        LOG.info("Leaving method executeMe().");
    }

    public boolean stoppedClean() {
        return stoppedClean;
    }
    
    public void onMessage(Message message) {
        
        MessageContent content = null;
        
        LOG.debug("onMessage(): " + message.toString());
        
        if(message instanceof MapMessage) {

            MapMessage mapMessage = (MapMessage)message;
            LOG.debug("onMessage(): ", mapMessage.toString());
            content = contentCreator.convertMapMessage((MapMessage)message);
            messages.add(content);
            receivedMessages.incrementValue();
            mapMessage = null;
            
            synchronized(this) {
                notify();
            }
        } else {
            LOG.warn("Received a non MapMessage object: ", message.toString());
            LOG.warn("Discarding invalid message.");
        }        
    }

    public ReturnValue processMessage(MessageContent content) {
        
        long typeId = 0;
        long msgId = 0;
        ReturnValue result = ReturnValue.PM_RETURN_OK;

        if(content.discard()) {
            return ReturnValue.PM_RETURN_DISCARD;
        }

        if(!content.hasContent()) {
            return ReturnValue.PM_RETURN_EMPTY;
        }
                
        // Create an entry in the table MESSAGE
        // TODO: typeId is always 0!!! We do not use it anymore. Delete the column in a future version.
        msgId = dbLayer.createMessageEntry(typeId, content);
        if(msgId == RET_ERROR) {
            LOG.error("createMessageEntry(): No message entry created in database.");
            return ReturnValue.PM_ERROR_DB;
        }
        
        if(dbLayer.createMessageContentEntries(msgId, content) == false) {
            
            LOG.error("createMessageContentEntries(): No entry created in message_content. Delete message from database and store it to disk.");
            dbLayer.deleteMessage(msgId);
            result = ReturnValue.PM_ERROR_DB;
        } else {
            result = ReturnValue.PM_RETURN_OK;
        }
        
        return result;
    }
    

    public String createDatabaseNameFromRecord(String record) {
        
        String result = null;
        
        if(record.indexOf(':') != -1) {
            result = record.substring(0, record.indexOf(':')).toUpperCase();
        }
        
        return result;
    }
    
    /**
     * <code>isInitialized</code>
     * 
     * @return true, if the initialization was successfull ; false, if it was not
     */
    
    public boolean isInitialized() {
        return initialized;
    }
        
    public int getNumberOfQueuedMessages() {
        
        if(messages != null) {
            return messages.size();
        }
        
        return 0;
    }
    
    public void closeAllReceivers() {
        
        LOG.info("closeAllReceivers(): Closing all receivers.");
        
        if(receivers != null) {
            for(int i = 0;i < receivers.length;i++) {
                receivers[i].stopListening();
            }
        }
    }
    
    public void setParent(Jms2OraApplication p) {
        this.parent = p;
    }
    
    public synchronized void stopWorking() {
        contentCreator.stopWorking();
        running = false;
        
        this.notify();
    }
    
    public String createStatisticString() {
        
        StringBuffer result = new StringBuffer();
        
        result.append("Statistic:\n\n");
        result.append("Received Messages:  " + receivedMessages.getActualValue().getValue() + "\n");
        result.append("Stored Messages:    " + storedMessages.getActualValue().getValue() + "\n");
        result.append("Discarded Messages: " + discardedMessages.getActualValue().getValue() + "\n");
        result.append("Filtered Messages:     " + filteredMessages.getActualValue().getValue() + "\n");
        
        return result.toString();
    }
    
    /**
     * 
     * @return Number of received messages
     */
    public Collector getReceivedMessagesCollector() {
        return receivedMessages;
    }
    
    /**
     * 
     * @return Number of filtered messages
     */
    public Collector getFilteredMessagesCollector() {
        return filteredMessages;
    }

    /**
     * 
     * @return Number of discarded messages
     */
    public Collector getDiscardedMessagesCollector() {
        return discardedMessages;
    }

    /**
     * 
     * @return Number of stored messages
     */
    public Collector getStoredMessagesCollector() {
        return storedMessages;
    }
}
