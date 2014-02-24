
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

package org.csstudio.platform.libs.jms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * The class <code>JmsRedundantReceiver</code> handles the redundant connections to JMS servers. It uses queues
 * to store messages if two or more servers holds messages for a consumer. They will be stored chronological. The
 * oldest message will be returned first to a client.
 * 
 * @author Markus Moeller
 * @version 1.0
 * @deprecated org.csstudio.platform.utility.jms.JmsRedundantReceiver instead
 */

@Deprecated
public class JmsRedundantReceiver implements IJmsRedundantReceiver
{
    /** Number of redundant connections */
    private final int CONNECTION_COUNT = 2;

    /** Client id */
    private String clientId = null;
    
    /** Property set for JNDI */
    private Hashtable<String, String> properties = null;
    
    /** Array of contexts */
    private Context[] context = null;
    
    /** Array of factories */
    private ConnectionFactory[] factory = null;
    
    /** Array of JMS connections */
    private Connection[] connection  = null;
    
    /** Array of JMS sessions */
    private Session[] session = null;
    
    /** Message consumers. Key -> name, Value -> Array of message consumers */
    private Hashtable<String, MessageConsumer[]> subscriber = null;
    
    /** Queues for the messages */
    private Hashtable<String, ConcurrentLinkedQueue<Message>> messages = null;
    
    /** Array of URL strings */
    private String[] urls = null;
    
    /** Number of redundant connections */
    private boolean connected = false;
    
    /**
     * 
     * @param id - The client Id used by the connection object.
     * @param url1 - URL of the first JMS Server
     * @param url2 - URL of the second JMS Server
     */
    
    public JmsRedundantReceiver(String id, String url1, String url2)
    {
        urls = new String[CONNECTION_COUNT];
        
        urls[0] = url1;
        urls[1] = url2;
        
        clientId = id;

        context = new Context[CONNECTION_COUNT];
        factory = new ConnectionFactory[CONNECTION_COUNT];
        connection = new Connection[CONNECTION_COUNT];
        session = new Session[CONNECTION_COUNT];

        for(int i = 0;i < CONNECTION_COUNT;i++)
        {
            properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            properties.put(Context.PROVIDER_URL, urls[i]);
            
            try
            {
                context[i] = new InitialContext(properties);
                factory[i] = (ConnectionFactory)context[i].lookup("ConnectionFactory");
                connection[i] = factory[i].createConnection();
                connection[i].setClientID(clientId);
                session[i] = connection[i].createSession(false, Session.CLIENT_ACKNOWLEDGE);
                
                connection[i].start();
                
                connected = true;
            }
            catch(NamingException ne)
            {
                connected = false;
            }
            catch(JMSException jmse)
            {
                connected = false;
            }
        }
    }
            
    public boolean createRedundantSubscriber(String name, String destination, String durableName, boolean durable)
    {
        MessageConsumer[] sub = null;
        Topic topic = null;
        boolean result = false;
        
        if(subscriber == null)
        {
            subscriber = new Hashtable<String, MessageConsumer[]>();
        }
        
        if(subscriber.containsKey(name))
        {
            return false;
        }
        
        sub = new MessageConsumer[CONNECTION_COUNT];
        
        try
        {
            for(int i = 0;i < CONNECTION_COUNT;i++)
            {
                topic = session[i].createTopic(destination);
                
                if((durable == true) && (durableName != null))
                {
                    sub[i] = session[i].createDurableSubscriber(topic, durableName);
                }
                else
                {
                    sub[i] = session[i].createConsumer(topic);
                }
                
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, name + " -> Topic: " + destination + " " + urls[i]);
            }
            
            subscriber.put(name, sub);
            
            if(messages == null)
            {
                messages = new Hashtable<String, ConcurrentLinkedQueue<Message>>();
            }
            
            messages.put(name, new ConcurrentLinkedQueue<Message>());
            
            result = true;
        }
        catch(JMSException jmse)
        {
            result = false;
        }
        catch(NullPointerException npe)
        {
            result = false;
        }
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#createRedundantSubscriber(java.lang.String, java.lang.String)
     */
    
    public boolean createRedundantSubscriber(String name, String destination)
    {
        return createRedundantSubscriber(name, destination, null, false);
    }

    
    /* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#receive(java.lang.String)
	 */
    public Message receive(String name)
    {
        return receive(name, 0);
    }
    
    /* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#receive(java.lang.String, long)
	 */   
    public Message receive(String name, long waitTime)
    {
        ConcurrentLinkedQueue<Message> queue = null;
        MessageConsumer[] c = null;
        Message[] m = null;
        Message result = null;
        
        // First check the internal message queue
        if(messages.containsKey(name))
        {
            queue = messages.get(name);
            
            if(!queue.isEmpty())
            {
                result = queue.poll();
            }
        }
        
        // Return when a message was found in the queue
        if(result != null)
        {
            return result;
        }
        
        // Do we have a subscriber with the given name?
        if(subscriber.containsKey(name))
        {
            // Get the MessageConsumer objects for all hosts
            c = subscriber.get(name);
            
            // Create a new array of Message
            m = new Message[c.length];
            
            // Receive the next message from all hosts
            for(int i = 0;i < c.length;i++)
            {
                try
                {
                    if(waitTime > 0)
                    {
                        m[i] = c[i].receive(waitTime);
                    }
                    else
                    {
                        m[i] = c[i].receiveNoWait();
                    }
                }
                catch(JMSException jmse)
                {
                    m[i] = null;
                }
            }
            
            // All servers sent a message
            if((m[0] != null) && (m[1] != null))
            {
                try
                {
                    // Check the time stamp
                    if(m[0].getJMSTimestamp() <= m[1].getJMSTimestamp())
                    {
                        // The oldest message first
                        result = m[0];
                        
                        // The newest message will be stored in the queue
                        if(queue != null)
                        {
                            queue.add(m[1]);
                        }
                    }
                    else // and vice versa...
                    {
                        result = m[1];
                        
                        if(queue != null)
                        {
                            queue.add(m[0]);
                        }
                    }
                }
                catch(JMSException jmse)
                {
                    result = m[0];
                    
                    if(queue != null)
                    {
                        queue.add(m[1]);
                    }
                }            
            }
            else if(m[0] == null && m[1] != null) // Only one message
            {
                result = m[1];
            }
            else if(m[0] != null && m[1] == null) // Only one message
            {
                result = m[0];
            }
        }
        
        return result;
    }
    
    /* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#isConnected()
	 */
    
    public boolean isConnected()
    {
        return connected;
    }
    
    /* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IjmsRedundantReceiver#closeAll()
	 */
    public void closeAll()
    {
        MessageConsumer[] c = null;

        if(connection != null)
        {
            for(int i = 0;i < CONNECTION_COUNT;i++)
            {
                if(connection[i] != null)
                {
                    try
                    {
                        connection[i].stop();
                    }
                    catch(JMSException jmse) { }
                }
            }
        }
        
        if(subscriber != null)
        {
            Enumeration<MessageConsumer[]> list = subscriber.elements();
            
            while(list.hasMoreElements())
            {
                c = list.nextElement();
                
                for(int i = 0;i < c.length;i++)
                {
                    try
                    {
                        c[i].close();
                    }
                    catch(JMSException jmse) { }
                }
            }
            
            subscriber.clear();
            subscriber = null;
        }

        for(int i = 0;i < CONNECTION_COUNT;i++)
        {
            if(session != null)
            {
                if(session[i] != null)
                {
                    try
                    {
                        session[i].close();
                    }
                    catch(JMSException jmse) { }
                    
                    session[i] = null;
                }
            }
            
            if(connection != null)
            {
                if(connection[i] != null)
                {
                    try
                    {
                        connection[i].close();
                    }
                    catch(JMSException jmse) { }
                    
                    connection[i] = null;
                }
            }
            
            if(factory != null)
            {
                factory[i] = null;
            }
            
            if(context != null)
            {
                if(context[i] != null)
                {
                    try
                    {
                        context[i].close();
                    }
                    catch(NamingException ne) { }
                    
                    context[i] = null;
                }
            }
        }
        
        factory = null;
        connection = null;
        session = null;
        context = null;
        if (properties!=null) {
        	properties.clear();
        }
        properties = null;
    }   
}
