
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

package org.csstudio.platform.utility.jms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 22.07.2011
 */
public class JmsMultipleProducer {
    
    private Hashtable<String, String> properties;
    private Context context;
    private ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private Hashtable<String, MessageProducer> producer;
    private String clientId;
    private String jmsUrl;

    private SimpleDateFormat dateFormater;
    
    public JmsMultipleProducer(String id, String url, String f) {
        
        clientId = id;
        jmsUrl = url;
        
        dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        producer = new Hashtable<String, MessageProducer>();
        
        properties = new Hashtable<String, String>();
        
        // Set the properties for the context
        properties.put(Context.INITIAL_CONTEXT_FACTORY, f);
        properties.put(Context.PROVIDER_URL, jmsUrl);
                
        try {
            
            // Create a context
            context = new InitialContext(properties);
           
            // Create a connection factory
            factory = (ConnectionFactory)context.lookup("ConnectionFactory");
            
            // Create a connection
            connection = factory.createConnection();
            
            // Set client id
            connection.setClientID(clientId);
            
            // Start the connection
            connection.start();
            
            // Create a session
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
        } catch(NamingException ne) {
            // logger.info(" *** NamingException *** : " + ne.getMessage());
            closeAll();
        } catch(JMSException jmse) {
            // logger.info(" *** JMSException *** : " + jmse.getMessage());
            closeAll();
        }       

    }
    
    public boolean addMessageProducer(String producerName, String topicName) {
        
        boolean success = false;
        
        if (producer.containsKey(producerName)) {
            return false;
        }
        
        try {

            Topic topic = session.createTopic(topicName);
            
            // Create a message producer
            MessageProducer p = session.createProducer(topic);
            producer.put(producerName, p);
            success = true;
            
        } catch (JMSException jmse) {
            // Can be ignored
        }

        return success;
    }
    
    /**
     * 
     * @return The fresh MapMessage
     */
    public MapMessage createMapMessage() {
        
        MapMessage message = null;
        
        if(session != null) {
            try {
                message = session.createMapMessage();
            } catch(JMSException jmse) {
                // Can be ignored
            }
        }
        
        return message;
    }

    /**
     * 
     * @param message
     * @return True if the message has been sent, otherwise false
     */
    public boolean sendMessage(String producerName, Message message) {
        
        boolean success = false;
        
        if (producer.containsKey(producerName) == false) {
            return false;
        }
        
        try {
            // TODO: producer.send(message, DeliveryMode.PERSISTENT, 1, 0);
            producer.get(producerName).send(message);
        } catch (JMSException jmse) {
            // Can be ignored
        }
        
        return success;
    }

    public String getCurrentDateAsString() {
        return dateFormater.format(Calendar.getInstance().getTime());
    }

    public boolean isConnected() {
        return (connection != null);
    }
    
    public void closeAll() {
        
        Enumeration<String> keys = producer.keys();
        while (keys.hasMoreElements()) {
            
            String key = keys.nextElement();
            try {
                producer.get(key).close();
            } catch(Exception e) {
                /*Can be ignored*/
            }
            
            if (key != null) {
                producer.remove(key);
            }
        }
        
        if(connection!=null){try{connection.stop();}catch(Exception e){/*Can be ignored*/}}
        if(session!=null){try{session.close();}catch(Exception e){/*Can be ignored*/}session=null;}
        if(connection!=null){try{connection.close();}catch(Exception e){/*Can be ignored*/}connection=null;}
        factory = null;
        if(context!=null){try{context.close();}catch(Exception e){/*Can be ignored*/}context=null;}
        if(properties != null) {
            properties.clear();
            properties = null;
        }
    }
}
