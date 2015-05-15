
/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.jms.consumer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;

/**
 *
 * This class handles the redundant connections to JMS servers. It uses queues
 * to store messages if two or more servers holds messages for a consumer.
 * They will be stored chronological. The oldest message will be returned first to a client.
 *
 * It uses the new shared connection implemtations and replaces the old class JmsRedundantReceiver.
 *
 * @author Markus Moeller
 * @version 2.0
 * @since 02.04.2012
 */

public class JmsRedundantConsumer implements IJmsRedundantConsumer {

    /** The handles for the shared connections */
    private ISharedConnectionHandle[] handle;

    /** Array of JMS sessions */
    private Session[] session;

    /** Message consumers. Key -> name, Value -> Array of message consumers */
    private Hashtable<String, MessageConsumer[]> subscriber;

    /** Queues for the messages */
    // private Hashtable<String, ConcurrentLinkedQueue<Message>> messages;
    private Hashtable<String, TreeSet<Message>> messages;

    /** Flag that indicates whether or not the connections are established. */
    private boolean connected;

    public JmsRedundantConsumer(ISharedConnectionHandle[] handles) {
        handle = handles;
        int connectionCount = handle.length;
        session = new Session[connectionCount];
        for(int i = 0;i < connectionCount;i++) {
            try {
                session[i] = handle[i].createSession(false, Session.CLIENT_ACKNOWLEDGE);
                connected = true;
            } catch(JMSException jmse) {
                connected = false;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#createRedundantSubscriber(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public boolean createRedundantSubscriber(String name,
                                             String destination,
                                             String durableName,
                                             boolean durable) {
        MessageConsumer[] sub = null;
        Topic topic = null;
        boolean result = false;

        if(subscriber == null) {
            subscriber = new Hashtable<String, MessageConsumer[]>();
        }

        if(subscriber.containsKey(name)) {
            return false;
        }

        int connectionCount = handle.length;
        sub = new MessageConsumer[connectionCount];

        try {
            for(int i = 0;i < connectionCount;i++) {
                topic = session[i].createTopic(destination);

                if((durable == true) && (durableName != null)) {
                    sub[i] = session[i].createDurableSubscriber(topic, durableName);
                } else {
                    sub[i] = session[i].createConsumer(topic);
                }

                //Logger.getLogger(this.getClass().getName()).log(Level.INFO, name + " -> Topic: " + destination + " " + urlList[i]);
            }

            subscriber.put(name, sub);

            if(messages == null) {
                messages = new Hashtable<String, TreeSet<Message>>();
            }

            messages.put(name, new TreeSet<Message>(new MessageComparator()));

            result = true;
        } catch(JMSException jmse) {
            result = false;
        } catch(NullPointerException npe) {
            result = false;
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#createRedundantSubscriber(java.lang.String, java.lang.String)
     */

    @Override
    public boolean createRedundantSubscriber(String name, String destination) {
        return createRedundantSubscriber(name, destination, null, false);
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#receive(java.lang.String)
     */
    @Override
    public Message receive(String name) {
        return receive(name, 0);
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#receive(java.lang.String, long)
     */
    @Override
    public Message receive(String name, long waitTime) {

        TreeSet<Message> subscriberQueue = null;
        Message result = null;

        // First check the internal subscriber message queue
        if(messages.containsKey(name)) {

            // Get the message queue for the subscriber
            subscriberQueue = messages.get(name);

            // If we have a message in this queue, deliver it first!
            if(!subscriberQueue.isEmpty()) {
                // Get the oldest message == the first element in the TreeSet
                result = subscriberQueue.first();
                subscriberQueue.remove(result);
            }
        }

        // Return when a message was found in the queue
        if(result != null) {
            return result;
        }

        // Do we have a subscriber with the given name?
        if(subscriber.containsKey(name) && subscriberQueue != null) {

            // Get the MessageConsumer objects for all hosts
            MessageConsumer[] c = subscriber.get(name);

            // Receive the next message from all hosts
            for(int i = 0;i < c.length;i++) {

                try {
                    // Wait for a message for some miliseconds
                    if(waitTime > 0) {
                        Message m = c[i].receive(waitTime);
                        if(m != null) subscriberQueue.add(m);
                    } else {
                        // ... or just have a look and return immediately
                        Message m = c[i].receiveNoWait();
                        if(m != null) subscriberQueue.add(m);
                    }
                } catch(JMSException jmse) {
                    // Ignore Me!
                }
            }

            // Get the first message. It is the oldest one. Maybe we just have one message.
            if(!subscriberQueue.isEmpty()) {
                result = subscriberQueue.first();
                subscriberQueue.remove(result);
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#isConnected()
     */

    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * The client has to acknowledge the received message
     *
     * @param msg
     * @return True, if everything works fine
     */
    public boolean acknowledge(Message msg) {
        try {
            msg.acknowledge();
            return true;
        } catch(Exception e) {
            // Can be ignored
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#closeAll()
     */
    @Override
    public void closeAll() {

        MessageConsumer[] c = null;

        if(subscriber != null) {

            Enumeration<MessageConsumer[]> list = subscriber.elements();
            while(list.hasMoreElements()) {
                c = list.nextElement();

                for(int i = 0;i < c.length;i++) {
                    try{c[i].close();}catch(JMSException jmse){System.err.println(jmse.getMessage());}
                }
            }

            subscriber.clear();
            subscriber = null;
        }

        int connectionCount = handle.length;
        for(int i = 0;i < connectionCount;i++) {

            if(session != null) {
                if(session[i] != null) {
                    try{session[i].close();}catch(JMSException jmse){System.err.println(jmse.getMessage());}
                    session[i] = null;
                }
            }

            if(handle != null) {
                if(handle[i] != null) {
                    handle[i].release();
                    handle[i] = null;
                }
            }
        }
    }
}
