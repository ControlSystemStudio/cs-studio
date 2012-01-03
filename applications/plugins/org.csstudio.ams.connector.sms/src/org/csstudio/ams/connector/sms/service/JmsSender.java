
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
 *
 */

package org.csstudio.ams.connector.sms.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.ams.Log;

/**
 * @author Markus Moeller
 * @version 1.1
 *
 */
public class JmsSender
{
    private Hashtable<String, String> properties = null;
    private Context context = null;
    private ConnectionFactory factory = null;
    private Connection connection  = null;
    private Session session = null;
    private Destination dest = null;
    private MessageProducer sender = null;
    private String clientId;
    private String jmsUrl;
    private String jmsTopic;
    private boolean connected;
    
    public JmsSender(String clientid, String url, String topic)
    {
        clientId = clientid;
        jmsUrl = url;
        jmsTopic = topic;
        connected = false;
        properties = new Hashtable<String, String>();
        
        // Set the properties for the context
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.put(Context.PROVIDER_URL, jmsUrl);
                
        try
        {
            // Create a context
            context = new InitialContext(properties);
           
            // Create a connection factory
            factory = (ConnectionFactory)context.lookup("ConnectionFactory");
            
            // Create a connection
            connection = factory.createConnection();
            
            // Set client id
            connection.setClientID(clientId + "@" + Environment.getInstance().getHostName());
            
            // Start the connection
            connection.start();
            
            // Create a session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            dest = session.createTopic(jmsTopic);
            
            // Create a message producer
            sender = session.createProducer(dest);
            
            connected = true;
        }
        catch(NamingException ne)
        {
            connected = false;
            Log.log(this, Log.ERROR, " *** NamingException *** : " + ne.getMessage());
            closeAll();
        }
        catch(JMSException jmse)
        {
            connected = false;
            Log.log(this, Log.ERROR, " *** JMSException *** : " + jmse.getMessage());
            closeAll();
        }       
    }
        
    public boolean sendMessage(String type, String messageText, String severity)
    {
        return sendMessage(type, null, messageText, severity, null);
    }
    
    public boolean sendMessage(String type, String name, String messageText, String severity)
    {
        return sendMessage(type, name, messageText, severity, null);
    }

    public boolean sendMessage(String type, String name, String messageText, String severity, String destination)
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
                message.setString("APPLICATION-ID", clientId);
                
                if(severity != null)
                {
                    message.setString("SEVERITY", severity);
                }
                
                if(destination != null)
                {
                    message.setString("DESTINATION", destination);
                }
                
                if(name != null)
                {
                    message.setString("NAME", name);
                }
                
                // Send the message
                sender.send(message, DeliveryMode.PERSISTENT, 1, 0);
                
                // Clean up
                clearMessage(message);
                message = null;
                
                Log.log(this, Log.INFO, " JmsSender.sendMessage(): *** JMS DONE ***.");
                
                result = true;
            }
            else
            {
                Log.log(this, Log.WARN, " JmsSender.sendMessage(): *** JMS NOT DONE *** : MapMessage not created.");
            }
        }
        catch(JMSException jmse)
        {
            Log.log(this, Log.ERROR, " JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());
            
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
                
                Log.log(this, Log.INFO, " JmsSender.sendMessage(): *** JMS DONE ***.");
                
                result = true;
            }
            else
            {
                Log.log(this, Log.WARN, " JmsSender.sendMessage(): *** JMS NOT DONE *** : MapMessage not created.");
            }
        }
        catch(JMSException jmse)
        {
            Log.log(this, Log.ERROR, " JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());
            
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
                        
                        Log.log(this, Log.DEBUG, name + " = " + messages[i].getValue(name));
                        
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
                    Log.log(this, Log.WARN, " JmsSender.sendMessage(): MapMessage object was not created.");
                }
            }
            
            Log.log(this, Log.INFO, " JmsSender.sendMessage(): *** JMS DONE ***.");
        }
        catch(JMSException jmse)
        {
            Log.log(this, Log.ERROR, " JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());            
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
        catch(JMSException e) {
            // Can be ignored
        }
    }
    
    public void closeAll()
    {
        if(sender!=null){try{sender.close();}catch(Exception e){/*Can be ignored*/}sender=null;}
        dest = null;
        if(session!=null){try{session.close();}catch(Exception e){/*Can be ignored*/}session=null;}
        if(connection!=null){try{connection.stop();}catch(Exception e){/*Can be ignored*/}}
        if(connection!=null){try{connection.close();}catch(Exception e){/*Can be ignored*/}connection=null;}
        factory = null;
        if(context!=null){try{context.close();}catch(Exception e){/*Can be ignored*/}context=null;}
        if(properties != null) {
            properties.clear();
            properties = null;
        }
    }
    
    public boolean isConnected()
    {
        return connected;
    }
    
    public boolean isNotConnected()
    {
        return !connected;
    }

    /**
     * Creates date and time for the JMS message.
     * 
     * @return String with the date and time
     */
    private String createTimeString()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return format.format(Calendar.getInstance().getTime());
    }
}
