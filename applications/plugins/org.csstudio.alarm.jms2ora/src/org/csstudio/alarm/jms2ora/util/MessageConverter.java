
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

package org.csstudio.alarm.jms2ora.util;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.csstudio.alarm.jms2ora.IMessageConverter;
import org.csstudio.alarm.jms2ora.IMessageProcessor;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 29.08.2011
 */
public class MessageConverter extends Thread implements IMessageConverter {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageConverter.class);

    /** Writes the ArchiveMessage content to the DB */
    private IMessageProcessor messageProcessor;
    
    /** Object that gets all JMS messages */
    private MessageAcceptor messageAcceptor;
    
    /** Queue for received messages */
    private ConcurrentLinkedQueue<RawMessage> rawMessages;

    /** Object that creates the MessageContent objects */
    private MessageContentCreator contentCreator;

    private Object lock;
    
    private boolean working;
    
    public MessageConverter(IMessageProcessor processor, StatisticCollector c) {
        messageProcessor = processor;
        messageAcceptor = new MessageAcceptor(this, c);
        rawMessages = new ConcurrentLinkedQueue<RawMessage>();
        contentCreator = new MessageContentCreator();
        lock = new Object();
        working = true;
    }
    
    @Override
    public void run() {
        
        LOG.info("Running thread {}", MessageConverter.class.getSimpleName());
        
        while (working) {
            
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ie) {
                    // Can be ignored
                }
            }
            
            if (rawMessages.size() > 0) {
                
                // TODO: Convert the messages
                Vector<RawMessage> convertMe = new Vector<RawMessage>(rawMessages);
                Vector<ArchiveMessage> am = contentCreator.convertRawMessages(convertMe);
            }
        }
        
        messageAcceptor.closeAllReceivers();
    }

    public int getQueueSize() {
        return rawMessages.size();
    }
    
    @Override
    public synchronized void putRawMessage(RawMessage m) {
        rawMessages.add(m);
    }
    
    /**
     * Sets the working flag to false. The thread will leave then.
     */
    public void stopWorking() {
        working = false;
        synchronized (lock) {
            lock.notify();
        }
    }
}
