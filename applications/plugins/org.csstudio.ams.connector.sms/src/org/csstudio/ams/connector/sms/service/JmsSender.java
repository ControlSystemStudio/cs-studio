
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

package org.csstudio.ams.connector.sms.service;

import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

/**
 * @author Markus Moeller
 *
 */
public class JmsSender
{
    private Logger logger;
    private ActiveMQConnectionFactory factory = null;
    private Connection connection  = null;
    private Session session = null;
    private Destination dest = null;
    private MessageProducer sender = null;
    private String clientId;
    private String jmsUrl;
    private String jmsTopic;
    
    public JmsSender(String clientid, String url, String topic)
    {
        logger = Logger.getLogger(JmsSender.class);
        clientId = clientid;
        jmsUrl = url;
        jmsTopic = topic;
               
        try
        {           
            // Create a connection factory
            factory = new ActiveMQConnectionFactory(jmsUrl);
            
            // Create a connection
            connection = factory.createConnection();
            
            // Set client id
            connection.setClientID("ServerConnector" + clientId + "@" + Environment.getInstance().getHostName());
            
            // Start the connection
            connection.start();
            
            // Create a session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            dest = (Destination)session.createTopic(jmsTopic);
            
            // Create a message producer
            sender = session.createProducer(dest);
        }
        catch(JMSException jmse)
        {
            logger.fatal("*** JMSException *** : " + jmse.getMessage());
            closeAll();
        }       
    }
    
    public boolean sendMessage(String type, String messageText, String severity)
    {
        return sendMessage(type, messageText, severity, null);
    }
    
    public boolean sendMessage(String type, String messageText, String severity, String destination)
    {
        MapMessage message = null;        
        boolean result = false;

        try
        {                        
            message = this.createMapMessage();
            if(message != null)
            {
                message.setString("TYPE", type);
                message.setString("EVENTTIME", createTimeString());
                message.setString("TEXT", messageText);
                message.setString("USER", Environment.getInstance().getUserName());
                message.setString("HOST", Environment.getInstance().getHostName());
                message.setString("APPLICATION-ID", "AmsSmsConnector");
                
                if(severity != null)
                {
                    message.setString("SEVERITY", severity);
                }
                
                if(destination != null)
                {
                    message.setString("DESTINATION", destination);
                }
                
                // Send the message
                sender.send(message, DeliveryMode.PERSISTENT, 1, 0);
                
                // Clean up
                clearMessage(message);
                message = null;
                                
                result = true;
            }
            else
            {
                logger.error("JmsSender.sendMessage(): *** JMS NOT DONE *** : MapMessage not created.");
            }
        }
        catch(JMSException jmse)
        {
            logger.error("JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());
            
            return false;
        }

        return result;
    }
    
    public boolean sendMessage(Hashtable<String, String> messageContent)
    {
        MapMessage message = null;
        boolean result = false;
        
        try
        {                        
            message = this.createMapMessageFromHashtable(messageContent);
            if(message != null)
            {                
                // Send the message
                sender.send(message, DeliveryMode.PERSISTENT, 1, 0);
                
                // Clean up
                clearMessage(message);
                message = null;
                
                logger.debug("JmsSender.sendMessage(): *** JMS DONE ***.");
                
                result = true;
            }
            else
            {
                logger.error("JmsSender.sendMessage(): *** JMS NOT DONE *** : MapMessage not created.");
            }
        }
        catch(JMSException jmse)
        {
            logger.error("JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());
            
            return false;
        }
    
        return result;
    }
    
    public boolean sendMessage(AlarmMessage[] messages)
    {
        MapMessage message = null;
        String name = null;
        boolean result = false;
        
        try
        {
            // Create a MapMessage-Object from the message array                                   
            for(int i = 0;i < messages.length;i++)
            {
                message = createMapMessage();
                if(message != null)
                {
                    Set<String> keys = messages[i].getKeys();                
                    Iterator<String> iter = keys.iterator();               
                    while(iter.hasNext())
                    {
                        name = iter.next();
                        
                        logger.debug(name + " = " + messages[i].getValue(name));
                        
                        // TODO: Check whether or not the key names are valid
                        //       Use only valid names
                        message.setString(name, messages[i].getValue(name));
                    }
                                                      
                    // Send the message
                    sender.send(message, DeliveryMode.PERSISTENT, 1, 0);
                    
                    // Clean up
                    clearMessage(message);
                    message = null;
                }
                else
                {
                    logger.error("JmsSender.sendMessage(): MapMessage object was not created.");
                }
            }
            
            logger.debug("JmsSender.sendMessage(): *** JMS DONE ***.");
        }
        catch(JMSException jmse)
        {
            logger.error("JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());            
        }
        
        return result;
    }

    public MapMessage createMapMessage()
    {
        MapMessage message = null;
        
        if(session != null)
        {
            try
            {
                message = session.createMapMessage();
            }
            catch(JMSException jmse)
            {
                message = null;
            }
        }
        
        return message;
    }
    
    public MapMessage createMapMessageFromHashtable(Hashtable<String, String> messageContent)
    {
        MapMessage message = null;
        String key = null;

        if(session == null)
        {
            return message; // At this point always null
        }
        
        try
        {
            message = session.createMapMessage();
            
            Enumeration<String> keys = messageContent.keys();
            while(keys.hasMoreElements())
            {
                key = keys.nextElement();
                message.setString(key, messageContent.get(key));
            }
        }
        catch(JMSException jmse)
        {
            message = null;
        }
        
        return message;
    }
    
    public void clearMessage(MapMessage message)
    {
        try
        {
            message.clearBody();
            message.clearProperties();
        }
        catch(JMSException e) { }
    }
    
    public void closeAll()
    {
        if(sender!=null){try{sender.close();}catch(Exception e){}sender=null;}
        dest = null;
        if(session!=null){try{session.close();}catch(Exception e){}session=null;}
        if(connection!=null){try{connection.stop();}catch(Exception e){}}
        if(connection!=null){try{connection.close();}catch(Exception e){}connection=null;}
        factory = null;
    }
    
    public boolean isConnected()
    {
        return (connection != null);
    }
    
    /**
     * Creates date and time for the JMS message.
     * 
     * @return String with the date and time
     */
    private String createTimeString()
    {
        SimpleDateFormat format = new SimpleDateFormat(Property.AMS_DATE_FORMAT);

        return format.format(GregorianCalendar.getInstance().getTime());
    }
}
