
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.jms.publisher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;

/**
 * @author mmoeller
 * @version 1.0
 * @since 17.04.2012
 */
public class JmsMultiplePublisher {
    
    private ISharedConnectionHandle connectionHandle;
    private Hashtable<String, MessageProducer> producer;
    private SimpleDateFormat dateFormater;
    private Session session;

    public JmsMultiplePublisher(ISharedConnectionHandle handle) {
        connectionHandle = handle;
        dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        producer = new Hashtable<String, MessageProducer>();
        try {
            session = connectionHandle.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
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
            producer.get(producerName).send(message);
            success = true;
        } catch (JMSException jmse) {
            // Can be ignored
        }
        
        return success;
    }

    public String getCurrentDateAsString() {
        return dateFormater.format(Calendar.getInstance().getTime());
    }

    public boolean isConnected() {
        return connectionHandle.isActive();
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
        if(session!=null){try{session.close();}catch(Exception e){/*Can be ignored*/}session=null;}
        if(connectionHandle!=null){connectionHandle.release();}
    }
}
