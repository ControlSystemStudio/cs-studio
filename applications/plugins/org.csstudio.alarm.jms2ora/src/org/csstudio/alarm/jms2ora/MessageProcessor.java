
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
import org.csstudio.alarm.jms2ora.database.DatabaseLayer;
import org.csstudio.alarm.jms2ora.service.IMessageWriter;
import org.csstudio.alarm.jms2ora.service.MessageContent;
import org.csstudio.alarm.jms2ora.util.MessageAcceptor;
import org.csstudio.alarm.jms2ora.util.MessageFileHandler;
import org.csstudio.alarm.jms2ora.util.StatisticCollector;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
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

public class MessageProcessor extends Thread {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    /** Queue for received messages */
    private ConcurrentLinkedQueue<MessageContent> messages;
    
    /** The Database writer service */
    private IMessageWriter writerService;

    /** Object that gets all JMS messages */
    private MessageAcceptor messageAcceptor;
    
    /** Object for database handling */
    private DatabaseLayer dbLayer;
    
    /** A container for all Collector objects */
    private StatisticCollector collector;
    
    /** Indicates if the application was initialized or not */
    private boolean initialized;
    
    /** Indicates whether or not the application should stop */
    private boolean running;

    /** Indicates whether or not this thread stopped clean */
    private boolean stoppedClean;
    
    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 15000 ;

    public final long RET_ERROR = -1;
    public static final int CONSOLE = 1;
    
    /**
     * The constructor
     * 
     * Oh, really
     */    
    public MessageProcessor() throws ServiceNotAvailableException {
        
        this.setName("MessageProcessor-Thread");
        
        messages = new ConcurrentLinkedQueue<MessageContent>();
        messageAcceptor = new MessageAcceptor(collector);
        
        try {
            writerService = Jms2OraPlugin.getDefault().getMessageWriterService();
            if (writerService.isServiceReady()) {
                LOG.info("Message writer service available.");
            } else {
                LOG.error("Message writer service is NOT available.");
                throw new ServiceNotAvailableException("Database writer service not available.");
            }
        } catch (OsgiServiceUnavailableException e) {
            LOG.error(e.getMessage());
            throw new ServiceNotAvailableException("Database writer service notavailable: " + e.getMessage());
        }
                
        running = true;
        stoppedClean = false;
        initialized = false;
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
                            collector.incrementDiscardedMessages();
                        } else if(result == ReturnValue.PM_RETURN_EMPTY) {
                            collector.incrementFilteredMessages();
                        }
                    } else {
                        collector.incrementStoredMessages();
                        LOG.debug(result.getErrorMessage());
                    }
                }
                
                // LOG.debug(statistic.toString());
                LOG.debug(createStatisticString());
            }

            if(running) {
                
                synchronized (this) {
                    try {
                        wait(SLEEPING_TIME);
                    } catch(InterruptedException ie) {
                        LOG.error("[*** InterruptedException ***]: run(): wait(): " + ie.getMessage());
                        running = false;
                    }               
                }
                
                LOG.debug("Waked up...");
            }
        }
        
        messageAcceptor.closeAllReceivers();
        
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
        
//        if (writerService != null) {
//            writerService.close();
//        }

        stoppedClean = true;
        
        LOG.info("Remaining messages stored in the database: " + writtenToDb);
        LOG.info("Remaining messages stored on disk:         " + writtenToHd);
    }

    public boolean stoppedClean() {
        return stoppedClean;
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
        
        writerService.writeMessage(messageAcceptor.getCurrentMessages());
        
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
        
    public int getMessageQueueSize() {
        int result = 0;
        if(messages != null) {
            result = messages.size();
        }
        return result;
    }
    
    public synchronized void stopWorking() {
        running = false;
        this.notify();
    }
    
    public String createStatisticString() {
        
        StringBuffer result = new StringBuffer();
        
        result.append("Statistic:\n\n");
        result.append("Received Messages:  " + collector.getReceivedMessageCount() + "\n");
        result.append("Stored Messages:    " + collector.getStoredMessagesCount() + "\n");
        result.append("Discarded Messages: " + collector.getDiscardedMessagesCount() + "\n");
        result.append("Filtered Messages:  " + collector.getFilteredMessagesCount() + "\n");
        
        return result.toString();
    }
}
