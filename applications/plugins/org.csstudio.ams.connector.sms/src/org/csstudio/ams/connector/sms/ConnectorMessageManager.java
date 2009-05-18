
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

package org.csstudio.ams.connector.sms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.csstudio.ams.Log;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;

/**
 * This class handles the communication between the connector message manager plugin and the connector.
 * <p>
 * The message that is used for the communication has the following format:
 * <pre>
 * AMS-CON-MANAGER-CMD = {stop | work | send}
 * AMS-CON-MANAGER-STATUS = {ok | error}
 * AMS-CON-MANAGER-TEXT = {description for the current status}
 * </pre>
 * 
 * @author Markus Moeller
 *
 */
public class ConnectorMessageManager
{
    /** Redundant receiver used by the connector working class */
    private JmsRedundantReceiver workerReceiver = null;

    /** Connection object used by the SmsConnectorWork thread */
    private Connection managerSenderConnection = null;
    
    /** The Session object used by this class */
    private Session managerSenderSession = null;
    
    /** The message producer */
    private MessageProducer managerSenderReply = null;

    /** The keys and values of the current message */
    private HashMap<String, String> current = null;

    /** Message buffer that collects all messages before they will be sent */
    private ConcurrentLinkedQueue<Message> msgQueue = null;
    
    /** The length of a wait cycle in ms */
    private final int WAITING_TIME = 30000;

    /**
     * The constructor creates the session and the message producer
     * 
     * @param connection The connection object for the JMS communication
     * @param receiver Redundant receiver used by the connector working class
     * @throws JMSException
     */
    public ConnectorMessageManager(Connection connection, JmsRedundantReceiver receiver) throws JMSException
    {
        workerReceiver = receiver;
        managerSenderConnection = connection;
        managerSenderSession = managerSenderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        managerSenderReply = managerSenderSession.createProducer(managerSenderSession.
                                                        createTopic(SmsConnectorWork.MANAGE_REPLY_TOPIC));
    }
    
    /**
     * The main method of the class is called by the parent class (the connector working class).
     * 
     * @param message The first message sent by the connector manager plugin.
     * @throws JMSException
     */
    public void begin(Message message) throws JMSException
    {
        // First, check the type of the message. MapMessage is the only valid type.
        if(!(message instanceof MapMessage))
        {
            Log.log(Log.INFO, "The message is not a valid map message.");
            
            return;
        }

        MapMessage mapMsg = (MapMessage)message;
        
        // If the command that the message contains is identify
        // the connector have to sent its connector identifyer
        if(isValidCommand(mapMsg, "identify"))
        {
            sendIdentifyerMessage(SmsConnectorPlugin.CONNECTOR_ID);
            Log.log(Log.INFO, "Identifyer sent.");
            
            return;
        }
        
        // If the message does not contain a valid stop command, return
        if(!isValidCommand(mapMsg, "stop"))
        {
            Log.log(Log.INFO, "The message is not a valid stop message.");
            
            sendErrorMessage("The message is not a valid stop message.\nReturning to normal work...");
            
            return;
        }
        
        // O.K., it is a stop command. Check if this connector is the correct receiver.
        if(!isMessageForMe(mapMsg))
        {
            Log.log(Log.INFO, "The message is not for me.");
            
            return;
        }
        
        mapMsg = null;
        
        sendOkMessage("Stopped normal work and now waiting for commands...\n");
        
        // Wait for the next command (max. 30 sec.)
        mapMsg = waitForMessage();
        if(mapMsg == null)
        {
            Log.log(Log.INFO, "Did not get an answer.");
            
            sendErrorMessage("Did not get an answer.\nReturning to normal work...");
            
            return;
        }
        
        // The new command have to be 'send'
        if(!isValidCommand(mapMsg, "send"))
        {
            Log.log(Log.INFO, "The message is not a valid send message.");
            
            sendErrorMessage("The message is not a valid send message.\nReturning to normal work...");
            
            return;
        }
        
        mapMsg = null;
        
        sendOkMessage("That's all for now. Bye...\n");
    }

    /**
     * Retrieves the head of the queue(next message).
     * 
     * @return
     */
    public Message getTopMessage()
    {
        return msgQueue.poll();
    }
    
    /**
     * Waits for a message. 
     * 
     * @return
     */
    private MapMessage waitForMessage()
    {
        MapMessage msg = null;
        
        // Try to receive a message
        msg = (MapMessage)workerReceiver.receive("amsConnectorManager", WAITING_TIME);
        
        return msg;
    }

    /**
     * Sends a map message containg an ok message to the plugin.
     * 
     * @param text String that contains a description
     * @throws JMSException
     */
    private void sendIdentifyerMessage(String text) throws JMSException
    {
        MapMessage msg = managerSenderSession.createMapMessage();
        
        msg.setString("AMS-CON-MANAGER-CMD", "none");
        msg.setString("AMS-CON-MANAGER-STATUS", "id");
        msg.setString("AMS-CON-MANAGER-TEXT", text);
        
        managerSenderReply.send(msg);
        
        msg = null;
    }

    /**
     * Sends a map message containg an ok message to the plugin.
     * 
     * @param text String that contains a description
     * @throws JMSException
     */
    private void sendOkMessage(String text) throws JMSException
    {
        MapMessage msg = managerSenderSession.createMapMessage();
        
        msg.setString("AMS-CON-MANAGER-CMD", "none");
        msg.setString("AMS-CON-MANAGER-STATUS", "ok");
        msg.setString("AMS-CON-MANAGER-TEXT", text);
        
        managerSenderReply.send(msg);
        
        msg = null;
    }

    /**
     * Sends a map message containg an error message to the plugin.
     * 
     * @param text String that contains a description
     * @throws JMSException
     */
    private void sendErrorMessage(String text) throws JMSException
    {
        MapMessage msg = managerSenderSession.createMapMessage();
        
        msg.setString("AMS-CON-MANAGER-CMD", "none");
        msg.setString("AMS-CON-MANAGER-STATUS", "error");
        msg.setString("AMS-CON-MANAGER-TEXT", text);
        
        managerSenderReply.send(msg);
        
        msg = null;
    }

    /**
     * Checks whether the message from the plugin is a valid message or not.
     * <p>
     * Possible commands are: <code>stop, send, work</code>
     * 
     * @param message
     *        Message object containing the command
     * @param command
     *        The command that we expect
     * @return true, if the message contains a valid command, otherwise false
     */
    private boolean isValidCommand(MapMessage message, String command)
    {
        boolean result = false;

        messageToHashMap(message);
        
        if(!current.isEmpty())
        {
            if(current.containsKey("AMS-CON-MANAGER-CMD"))
            {
                if(current.get("AMS-CON-MANAGER-CMD").compareToIgnoreCase(command) == 0)
                {
                    result = true;
                }
            }
        }
        
        return result;
    }

    /**
     * Checks whether the message from the plugin is for this connector or not.
     * 
     * @param message
     *        Message object containing the command
     *
     * @return true, if the message is for this connector
     */
    private boolean isMessageForMe(MapMessage message)
    {
        boolean result = false;

        messageToHashMap(message);
        
        if(!current.isEmpty())
        {
            if(current.containsKey("AMS-CON-MANAGER-TEXT"))
            {
                if(current.get("AMS-CON-MANAGER-TEXT").compareToIgnoreCase(SmsConnectorPlugin.CONNECTOR_ID) == 0)
                {
                    result = true;
                }
            }
        }
        
        return result;
    }

    /**
     * Creates a HashMap containg all key / property values of the message.
     * 
     * @param message
     *         The MapMessage object containing the key/value pairs that are to be copied into a HashMap
     */
    private void messageToHashMap(MapMessage message)
    {
        Enumeration<?> keys = null;
        String name = null;
        
        if(current == null)
        {
            current = new HashMap<String, String>();
        }
        
        current.clear();
        
        try
        {
            keys = message.getMapNames();
            while(keys.hasMoreElements())
            {
                name = (String)keys.nextElement();
                current.put(name, message.getString(name));
            }
            
            keys = null;
        }
        catch(JMSException jmse) { }
    }
    
    /**
     * Closes the message producer and the session.
     */
    public void closeJms()
    {
        if(managerSenderReply!=null){try{managerSenderReply.close();managerSenderReply=null;}catch(JMSException e){}}
        if(managerSenderSession!=null){try{managerSenderSession.close();managerSenderSession=null;}catch(JMSException e){}}
    }
}
