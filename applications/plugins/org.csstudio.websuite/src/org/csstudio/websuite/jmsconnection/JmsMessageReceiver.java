
/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.websuite.jmsconnection;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.csstudio.websuite.dataModel.BasicMessage;
import org.csstudio.websuite.dataModel.MessageList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the receiver jms connection (via the shared jsm connection
 * in css platform) New messages were added to the model (_messageList).
 * 
 * @author jhatje
 * 
 */
public class JmsMessageReceiver implements MessageListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(JmsMessageReceiver.class);
    
    /**
     * List of messages displayed in the table.
     */
    MessageList messageList;

    /**
     * JMS Session for the listener
     */
    IMessageListenerSession listenerSession;

    public JmsMessageReceiver(MessageList messageList) {
    	this.messageList = messageList;
    }

    /**
     * A new message is received. Add it to the model.
     */
    @Override
	public void onMessage(final Message message) {
        if (message == null) {
        	LOG.warn("Recived message is null");
        }else{
        	try {
                if (message instanceof TextMessage) {
                	LOG.warn("Recived message is not MapMessage");
                } else if (message instanceof MapMessage) {
                    final MapMessage mm = (MapMessage) message;
                    Object[] logArgs = new Object[] {mm.getString("EVENTTIME"), mm.getString("NAME"), mm.getString("ACK")}; 
                    LOG.debug("Received map message: EVENTTIME: {} NAME: {} ACK: {}", logArgs );
                    
                    Map<String, String> messageProperties = readMapMessageProperties(mm);
                    messageList.addMessage(new BasicMessage(messageProperties));
                } else {
                	LOG.warn("Recived message of unknown type");
                }
            } catch (JMSException e) {
                LOG.error("JMS Problem", e);
            }
        }
    }

    /**
     * Read properties from a {@link MapMessage} and put them into a Map.
     * 
     * @param mm
     * @return message properties in a Map
     * @throws JMSException 
     */
    private Map<String, String> readMapMessageProperties(MapMessage mm) throws JMSException
    {
        Map<String, String> messageProperties = new HashMap<String, String>();
        Enumeration<?> mapNames = mm.getMapNames();
        while (mapNames.hasMoreElements())
        {
            String key = (String)mapNames.nextElement();
            messageProperties.put(key.toUpperCase(), mm.getString(key));
        }
        
        messageProperties.put("TOPICNAME", mm.getJMSDestination().toString());
        
        return messageProperties;
    }

    /**
     * Start jms message listener. If there is a previous session active (the
     * user has edited the topics) it will be closed and a new session is
     * created.
     * 
     * @param _deafultTopicSet
     *            JMS topics to be monitored
     */
    public void initializeJMSConnection(String defaultTopicSet) {
        String[] topicList = null;
        if ((defaultTopicSet == null) || (defaultTopicSet.length() == 0)) {
            LOG.error("Could not initialize JMS Listener. JMS topics == NULL!");
        } else {
            topicList = defaultTopicSet.split(",");
        }
        try {
            if ((listenerSession != null) && (listenerSession.isActive())) {
                listenerSession.close();
                listenerSession = null;
            }
            listenerSession = SharedJmsConnections.startMessageListener(this,
                    topicList, Session.AUTO_ACKNOWLEDGE);
            LOG.info("Initialize JMS connection with topics: {}", defaultTopicSet);

        } catch (JMSException e) {
           LOG.error("JMS Connection error",e);
        } catch (IllegalArgumentException e) {
            LOG.error("JMS Connection error, invalid arguments",e);
        }
    }

    /**
     * Stop the jms connection.
     */
    public void stopJMSConnection() {
        if (listenerSession != null) {
            listenerSession.close();
        }
    }
}
