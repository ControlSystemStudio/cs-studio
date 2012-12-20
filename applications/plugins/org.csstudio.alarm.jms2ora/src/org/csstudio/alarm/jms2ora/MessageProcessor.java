
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

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.csstudio.alarm.jms2ora.service.IMessageWriter;
import org.csstudio.alarm.jms2ora.service.IPersistenceHandler;
import org.csstudio.alarm.jms2ora.util.MessageConverter;
import org.csstudio.alarm.jms2ora.util.StatisticCollector;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.LocalDateTime;
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
 *
 * TODO:    Auslagern von bestimmten Funktionen in eigenstaendige Klassen
 *          - Die Properties der Datenbanktabellen
 *
 * @author  Markus Moeller
 * @version 2.0.0
 */
public class MessageProcessor extends Thread implements IMessageProcessor {

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);
    
    /** Time to wait for the thread MessageProcessor in ms */
    private static final long WAITFORTHREAD = 20000;

    /** The Database writer service */
    private IMessageWriter writerService;

    /** The Persistence writer service */
    private IPersistenceHandler persistenceService;

    /** Queue for processed messages */
    private final ConcurrentLinkedQueue<ArchiveMessage> archiveMessages;

    /** A container for all Collector objects */
    private final StatisticCollector collector;

    private final MessageConverter messageConverter;

    /** Time to sleep in ms */
    private long msgProcessorSleepingTime;
    
    /** Min. waiting (in sec.) time before a new storage will be started */
    private int timeBetweenStorage;

    /** The object holds the last processing time of the messages */
    private LocalDateTime nextStorageTime;

    /** Indicates if the application was initialized or not */
    private final boolean initialized;

    /** Indicates whether or not the application should stop */
    private boolean running;

    /** Indicates whether or not this thread stopped clean */
    private boolean stoppedClean;

    private boolean logStatistic;
    
    /**
     * The constructor
     *
     * Oh, really
     */
    public MessageProcessor(long sleepingTime, int storageWaitTime, boolean log) throws ServiceNotAvailableException {

        try {
            writerService = Jms2OraActivator.getDefault().getMessageWriterService();
            if (writerService.isServiceReady()) {
                LOG.info("Message writer service available.");
            } else {
                LOG.error("Message writer service is NOT available.");
                throw new ServiceNotAvailableException("Database writer service not available.");
            }
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error(e.getMessage());
            throw new ServiceNotAvailableException("Database writer service not available: " + e.getMessage());
        }
        
        try {
            persistenceService = Jms2OraActivator.getDefault().getPersistenceWriterService();
            LOG.info("Persistence service available.");
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("Persistence service NOT available.");
            throw new ServiceNotAvailableException("Persistence writer service not available: " + e.getMessage());
        }
        
        
        collector = new StatisticCollector();
        messageConverter = new MessageConverter(this, collector);
        
        timeBetweenStorage = storageWaitTime;
        msgProcessorSleepingTime = sleepingTime;
        
        nextStorageTime = new LocalDateTime();
        nextStorageTime = nextStorageTime.plusSeconds(timeBetweenStorage);

        archiveMessages = new ConcurrentLinkedQueue<ArchiveMessage>();

        running = true;
        stoppedClean = false;
        initialized = false;
        logStatistic = log;
        
        this.setName("MessageProcessor-Thread");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void putArchiveMessage(@Nonnull final ArchiveMessage m) {
        archiveMessages.add(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void putArchiveMessages(@Nonnull final Collection<ArchiveMessage> m) {
        archiveMessages.addAll(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        Vector<ArchiveMessage> storeMe;
        boolean success;

        LOG.info("Started " + VersionInfo.getAll());
        LOG.info("Waiting for messages...");

        // First look for stored messages on disk
        Vector<ArchiveMessage> old = persistenceService.readMessagesFromFile();
        if (old.size() > 0) {
            LOG.info("I've found some messages on disk.");
            success = writerService.writeMessage(old);
            if (!success) {
                LOG.warn("Could not store the message into the database. Try to re-write message(s) on disk.");
                int result = persistenceService.writeMessages(old);
                if (result == old.size()) {
                    LOG.info("OK, messages written.");
                } else {
                    LOG.error("ERROR, ERROR, ERROR");
                }
            }
        }

        while(running) {
            
            final LocalDateTime now = new LocalDateTime();

            if ((now.isAfter(nextStorageTime) || archiveMessages.size() >= 1000) && running) {

                storeMe = this.getMessagesToArchive();
                int number = storeMe.size();
                if (number > 0) {
                    success = writerService.writeMessage(storeMe);
                    if(!success) {
                        // Store the message in a file, if it was not possible to write it to the DB.
                        LOG.warn("Could not store the message into the database. Try to write message(s) on disk.");
                        int result = persistenceService.writeMessages(storeMe);
                        if (result == number) {
                            collector.addStoredMessages(number);
                        } else {
                            LOG.error("Could not store the message on disk.");
                        }
                    } else {
                        collector.addStoredMessages(number);
                    }
                    
                    storeMe.clear();
                    storeMe = null;
                }
                
                if (logStatistic) {
                    LOG.info(createStatisticString());
                }
            }

            if (now.isAfter(nextStorageTime)) {
                nextStorageTime = nextStorageTime.plusSeconds(timeBetweenStorage);
            }
            
            if(running) {
                synchronized (this) {
                    try {
                        wait(msgProcessorSleepingTime);
                    } catch(final InterruptedException ie) {
                        LOG.error("[*** InterruptedException ***]: run(): wait(): " + ie.getMessage());
                        running = false;
                    }
                }
                LOG.info("Waked up...");
                LOG.info("Next processing time: {}", nextStorageTime.toString());
            }
        }

        int waitCount = 2;
        messageConverter.stopWorking();
        do {
        try {
            LOG.info("Waiting for MessageConverter.");
            messageConverter.join(WAITFORTHREAD);
        } catch (InterruptedException e) {
            LOG.warn("[*** InterruptedException ***]: {}", e.getMessage());
        }
        } while ((waitCount-- > 0) && !messageConverter.stoppedClean());
        
        LOG.info("MessageConverter stopped clean: {}", messageConverter.stoppedClean());
        
        // Process the remaining messages
        LOG.info("Remaining archive messages: {}", archiveMessages.size());
        LOG.info("Remaining   raw   messages: {}", messageConverter.getQueueSize());
        
        int writtenToDb = 0;
        int writtenToHd = 0;

        // Store the remaining archive messages
        if (!archiveMessages.isEmpty()) {

            storeMe = this.getMessagesToArchive();
            success = writerService.writeMessage(storeMe);
            LOG.info("Remaining messages written to database: {}", success);
            if(!success) {

                // Store the message in a file, if it was not possible to write it to the DB.
                persistenceService.writeMessages(storeMe);
                writtenToHd = storeMe.size();

            } else {
                writtenToDb = storeMe.size();
            }
        }
        
        // TODO
        // Store the remaining raw messages
//        if (messageConverter.getQueueSize() > 0) {
//            
//        }
        
        if (writerService != null) {
            writerService.close();
        }

        stoppedClean = true;

        LOG.info("Remaining messages stored in the database: " + writtenToDb);
        LOG.info("Remaining   messages   stored   on   disk: " + writtenToHd);
    }

    public final boolean stoppedClean() {
        return stoppedClean;
    }

    /**
     *
     * @return Vector object that contains the messages in the queue
     */
    @CheckForNull
    public final Vector<ArchiveMessage> getMessagesToArchive() {
        Vector<ArchiveMessage> result = null;
        if (!archiveMessages.isEmpty()) {
            synchronized (archiveMessages) {
                result = new Vector<ArchiveMessage>(archiveMessages);
                archiveMessages.removeAll(result);
            }
        } else {
            result = new Vector<ArchiveMessage>();
        }
        return result;
    }

    @Nonnull
    public final String createDatabaseNameFromRecord(@Nonnull final String record) {

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
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getArchiveMessageQueueSize() {
        return archiveMessages.size();
    }

    /**
     * Returns the sum of the RawMessage queue size and the ArchiveMessage queue size.
     *
     * @return The sum of both message queues
     */
    public final int getCompleteQueueSize() {
        return messageConverter.getQueueSize() + archiveMessages.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMessageFiles() {
        return persistenceService.getNumberOfMessageFiles();
    }

    public final synchronized void stopWorking() {
        LOG.info("I will stop soon.");
        running = false;
        this.notify();
    }

    @Nonnull
    public final String createStatisticString() {

        final StringBuffer result = new StringBuffer();

        result.append("Statistic:\n\n");
        result.append("Received Messages:  " + collector.getReceivedMessageCount() + "\n");
        result.append("Stored Messages:    " + collector.getStoredMessagesCount() + "\n");
        result.append("Discarded Messages: " + collector.getDiscardedMessagesCount() + "\n");
        result.append("Filtered Messages:  " + collector.getFilteredMessagesCount() + "\n");

        return result.toString();
    }
}
