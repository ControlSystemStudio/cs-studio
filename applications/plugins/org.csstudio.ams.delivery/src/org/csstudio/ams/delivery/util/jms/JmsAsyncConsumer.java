
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.delivery.util.jms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 11.12.2011
 */
public class JmsAsyncConsumer {
    
    private static Logger LOG = LoggerFactory.getLogger(JmsAsyncConsumer.class);
    
    /** Client id */
    private String clientId;
    
    /** Array of factories */
    private ActiveMQConnectionFactory[] factory;
    
    /** Array of JMS connections */
    private Connection[] connection;
    
    /** Array of JMS sessions */
    private Session[] session;
    
    /** Message consumers. Key -> name, Value -> Array of message consumers */
    private Hashtable<String, MessageConsumer[]> subscriber;
    
    /** Array of URL strings */
    private String[] urlList;
    
    /** Number of redundant connections */
    private int connectionCount;

    /** Flag that indicates whether or not the connections are established. */
    private boolean connected = false;

    public JmsAsyncConsumer(String id, String[] urls) {
        
        Vector<String> tempList = null;
        
        if(urls == null) {
            return;
        }
        
        tempList = new Vector<String>();
        
        for(String s : urls) {
            // Count the valid url entries
            if(s != null) {
                if(s.trim().length() > 0) {
                    tempList.add(s.trim());
                }
            }
        }
        
        connectionCount = tempList.size();
        urlList = new String[connectionCount];
        tempList.toArray(urlList);        
        
        clientId = id;

        factory = new ActiveMQConnectionFactory[connectionCount];
        connection = new Connection[connectionCount];
        session = new Session[connectionCount];

        for(int i = 0;i < connectionCount;i++) {            
            try {
                factory[i] = new ActiveMQConnectionFactory(urlList[i]);
                connection[i] = factory[i].createConnection();
                connection[i].setClientID(clientId);
                session[i] = connection[i].createSession(false, Session.CLIENT_ACKNOWLEDGE);
                
                connection[i].start();
                
                connected = true;
            } catch(JMSException jmse) {
                connected = false;
            }
        }
        
        subscriber = new Hashtable<String, MessageConsumer[]>();
    }
    
    public JmsAsyncConsumer(String id, String url1, String url2) {
        this(id, new String[] { url1, url2 });
    }
    
    /* TODO: Throw exception if the name is not found */
    public void addMessageListener(String forName, MessageListener listener) {
        
        if (subscriber.containsKey(forName) == false) {
            return;
        }
        
        MessageConsumer[] cons = subscriber.get(forName);
        for (MessageConsumer o : cons)
        if (o != null) {
            try {
                o.setMessageListener(listener);
            } catch (JMSException jmse) {
                LOG.error("[*** JMSException ***]: addMessageListener(): {}", jmse.getMessage());
            }
        }
    }
    
    public boolean createRedundantSubscriber(String name,
                                             String destination,
                                             String durableName,
                                             boolean durable) {
        
        MessageConsumer[] sub = null;
        Topic topic = null;
        boolean result = false;
        
        if(subscriber.containsKey(name)) {
            return false;
        }
        
        sub = new MessageConsumer[connectionCount];
        
        try {
            for(int i = 0;i < connectionCount;i++) {
                topic = session[i].createTopic(destination);
                
                if((durable == true) && (durableName != null)) {
                    sub[i] = session[i].createDurableSubscriber(topic, durableName);
                } else {
                    sub[i] = session[i].createConsumer(topic);
                }
                
                LOG.info(name + " -> Topic: " + destination + " " + urlList[i]);
            }
            
            subscriber.put(name, sub);
            result = true;
        } catch(JMSException jmse) {
            result = false;
        } catch(NullPointerException npe) {
            result = false;
        }
        
        return result;
    }

    public boolean createRedundantSubscriber(String name, String destination) {
        return createRedundantSubscriber(name, destination, null, false);
    }

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

    public void closeSubscriber(String name) {
        if (subscriber.containsKey(name)) {
            MessageConsumer[] c = subscriber.get(name);
            for(int i = 0;i < c.length;i++) {
                try {
                    c[i].close();
                } catch (JMSException jmse) {
                    LOG.warn("Cannot close JMS message consumer: " + c[i].toString());
                }
            }
            subscriber.remove(name);
        }
    }
    
    public void closeAll() {
        
        MessageConsumer[] c = null;

        if(connection != null) {
            for(int i = 0;i < connectionCount;i++) {
                if(connection[i] != null) {
                    try{connection[i].stop();}catch(JMSException jmse){/* Ignore me */}}
            }
        }
        
        if(subscriber != null) {
            Enumeration<MessageConsumer[]> list = subscriber.elements();
            
            while(list.hasMoreElements()) {
                c = list.nextElement();
                for(int i = 0;i < c.length;i++) {
                    try{c[i].close();}catch(JMSException jmse){/* Ignore me */}
                }
            }
            
            subscriber.clear();
            subscriber = null;
        }

        for(int i = 0;i < connectionCount;i++) {
            if(session != null) {
                if(session[i] != null)  {
                    try{session[i].close();}catch(JMSException jmse){/* Ignore me */}
                    session[i] = null;
                }
            }
            
            if(connection != null) {
                if(connection[i] != null) {
                    try{connection[i].close();}catch(JMSException jmse){/* Ignore me */}
                    connection[i] = null;
                }
            }
            
            if(factory != null){factory[i]=null;}            
        }
        
        factory = null;
        connection = null;
        session = null;
    }   
}
