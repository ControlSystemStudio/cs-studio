
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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
 */

package org.csstudio.alarm.jms2ora.util;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.csstudio.alarm.jms2ora.VersionInfo;
import org.csstudio.platform.statistic.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class MessageAcceptor implements MessageListener {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageAcceptor.class);

    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector receivedMessages;

    /** Queue for received messages */
    private ConcurrentLinkedQueue<MapMessage> messages;

    /** Array of message receivers */
    private JmsMessageReceiver[] receivers;

    /** Indicates if the application was initialized or not */
    private boolean initialized;

    public MessageAcceptor(String[] urlList, String[] topicList) {
        
        messages = new ConcurrentLinkedQueue<MapMessage>();
        receivers = new JmsMessageReceiver[urlList.length];

        receivedMessages = new Collector();
        receivedMessages.setApplication(VersionInfo.NAME);
        receivedMessages.setDescriptor("Received messages");
        receivedMessages.setContinuousPrint(false);
        receivedMessages.setContinuousPrintCount(1000.0);

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
    }
    
    public void closeAllReceivers() {
        
        LOG.info("closeAllReceivers(): Closing all receivers.");
        
        if(receivers != null) {
            for(int i = 0;i < receivers.length;i++) {
                receivers[i].stopListening();
            }
        }
    }

    public synchronized Vector<MapMessage> getCurrentMessages() {
        
        Vector<MapMessage> result = null;
        
        if(messages.isEmpty() == false) {
            result = new Vector<MapMessage>(messages);
            messages.removeAll(result);
        }
        
        return result;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public void onMessage(Message message) {
        
        if(message instanceof MapMessage) {
            messages.add((MapMessage)message);
            receivedMessages.incrementValue();
        } else {
            LOG.info("Received a non MapMessage object. Discarded...");
        }        
    }
}
