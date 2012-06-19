
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
 */

package org.csstudio.ams.application.monitor.jms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.ams.application.monitor.util.Environment;
import org.csstudio.utility.jms.JmsUtilityException;
import org.csstudio.utility.jms.sharedconnection.ClientConnectionException;
import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 * @version 2.0
 */
public class JmsPublisher {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(JmsPublisher.class);

    private ISharedConnectionHandle handle;
    private Session session;
    private MessageProducer publisher;
    
    public JmsPublisher(String topic) throws ClientConnectionException {
        
        try {
            handle = SharedJmsConnections.sharedSenderConnection();
            session = handle.createSession(false, Session.AUTO_ACKNOWLEDGE);
            publisher = session.createProducer(session.createTopic(topic));
        } catch (JMSException e) {
            throw new ClientConnectionException(e);
        } catch (JmsUtilityException e) {
            throw new ClientConnectionException(e);
        }
    }
    
    public Session getSession() {
        return session;
    }
    
    public void sendMessage(MapMessage message) throws JMSException {
        publisher.send(message, DeliveryMode.PERSISTENT, 1, 0);
        LOG.info("Message sent.");
    }
    
    public boolean sendMessage(String type, String messageText, String severity) {
        return sendMessage(type, null, messageText, severity, null);
    }
    
    public boolean sendMessage(String type, String name, String messageText, String severity) {
        return sendMessage(type, name, messageText, severity, null);
    }

    public boolean sendMessage(String type, String name,
                               String messageText, String severity,
                               String destination) {
        
        MapMessage message = null;        
        boolean result = false;

        try {                        
            
            message = this.createMapMessage();
            if(message != null) {
                
                message.setString("TYPE", type);
                message.setString("EVENTTIME", createTimeString());
                message.setString("TEXT", messageText);
                message.setString("USER", Environment.getInstance().getUserName());
                message.setString("HOST", Environment.getInstance().getHostName());
                message.setString("APPLICATION-ID", "AmsSystemMonitor");
                
                if(severity != null) {
                    message.setString("SEVERITY", severity);
                }
                
                if(destination != null) {
                    message.setString("DESTINATION", destination);
                }
                
                if(name != null) {
                    message.setString("NAME", name);
                }
                
                // Send the message
                publisher.send(message, DeliveryMode.PERSISTENT, 1, 0);
                
                // Clean up
                clearMessage(message);
                message = null;
                
                LOG.info("Message sent.");
                
                result = true;
            } else {
                LOG.warn("Cannot create MapMessage object.");
            }
        } catch(JMSException jmse) {
            LOG.error("[*** JMSException ***]: " + jmse.getMessage());
            result = false;
        }

        return result;
    }
    
    public boolean sendMessage(Hashtable<String, String> messageContent) {
        
        MapMessage message = null;
        boolean result = false;
        
        try {                        
            
            message = this.createMapMessageFromHashtable(messageContent);
            if(message != null) {                
                // Send the message
                publisher.send(message, DeliveryMode.PERSISTENT, 1, 0);
                
                // Clean up
                clearMessage(message);
                message = null;
                
                LOG.info("Message sent.");
                
                result = true;
            } else {
                LOG.warn("Cannot create MapMessage.");
            }
        } catch(JMSException jmse) {
            LOG.error("[*** JMSException ***]: {}", jmse.getMessage());
            result = false;
        }
    
        return result;
    }
    
    public boolean sendMessage(AlarmMessage[] messages) {
        
        MapMessage message = null;
        String name = null;
        boolean result = false;
        
        try {
            
            // Create a MapMessage-Object from the message array                                   
            for(int i = 0;i < messages.length;i++) {
                message = createMapMessage();
                if(message != null) {
                    Set<String> keys = messages[i].getKeys();                
                    Iterator<String> iter = keys.iterator();               
                    while(iter.hasNext()) {
                        name = iter.next();
                        
                        LOG.debug(name + " = " + messages[i].getValue(name));
                        
                        // TODO: Check whether or not the key names are valid
                        //       Use only valid names
                        message.setString(name, messages[i].getValue(name));
                    }
                    
                    // Send the message
                    publisher.send(message, DeliveryMode.PERSISTENT, 1, 0);
                    
                    // Clean up
                    clearMessage(message);
                    message = null;
                } else {
                    LOG.warn("Cannot create MapMessage object.");
                }
            }
            
            LOG.info("Messages sent.");
        } catch(JMSException jmse) {
            LOG.error("[*** JMSException ***]: {}", jmse.getMessage());            
        }

        return result;
    }

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
    
    public MapMessage createMapMessageFromHashtable(Hashtable<String, String> messageContent) {
        
        MapMessage message = null;
        String key = null;

        if(session == null) {
            return message; // At this point always null
        }
        
        try {
            message = session.createMapMessage();
            
            Enumeration<String> keys = messageContent.keys();
            while(keys.hasMoreElements()) {
                key = keys.nextElement();
                message.setString(key, messageContent.get(key));
            }
        } catch(JMSException jmse) {
            // Can be ignored
        }
        
        return message;
    }
    
    public void clearMessage(MapMessage message) {
        
        try {
            message.clearBody();
            message.clearProperties();
        } catch(JMSException e) {
            // Can be ignnred
        }
    }
    
    public void closeAll() {
        if(publisher!=null){try{publisher.close();}catch(Exception e){/*Ignore Me*/}}
        if(session!=null){try{session.close();}catch(Exception e){/*Ignore Me*/}}
        handle.release();
    }
    
    public boolean isConnected() {
        boolean result = false;
        if (handle != null) {
            result = handle.isActive();
        }
        return result;
    }
    
    public boolean isNotConnected() {
        return !isConnected();
    }

    /**
     * Creates date and time for the JMS message.
     * 
     * @return String with the date and time
     */
    private String createTimeString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return format.format(Calendar.getInstance().getTime());
    }
}
