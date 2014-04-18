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
 */
package org.csstudio.remote.jms.command;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.utility.jms.JmsUtilityException;
import org.csstudio.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jms-based implementation.
 * 
 * @author jpenning
 * @since 17.01.2012
 */
public class JmsRemoteCommandService implements IRemoteCommandService {
    private static final Logger LOG = LoggerFactory.getLogger(JmsRemoteCommandService.class);
    
    // Given as preference
    private final String _topicName = "COMMAND";
    
    // Given as preference
    private final int _timeToLiveForAlarms = 3600000;
    
    private final Map<IRemoteCommandService.IListener, IMessageListenerSession> _listenersMap = new HashMap<IRemoteCommandService.IListener, IMessageListenerSession>();
    
    @Override
    public void register(ClientGroup group, IListener listener) throws RemoteCommandException {
        
        try {
            MessageListener listenerAdapter = new MessageListenerAdapter(group, listener);
            IMessageListenerSession listenerSession = SharedJmsConnections
                    .startMessageListener(listenerAdapter,
                                          new String[] { _topicName },
                                          Session.AUTO_ACKNOWLEDGE);
            _listenersMap.put(listener, listenerSession);
        } catch (final JMSException e) {
            throw newRemoteCommandException("JmsRemoteCommandService.register failed", e);
        }
    }
    
    @Override
    public void deregister(IListener listener) {
        IMessageListenerSession listenerSession = _listenersMap.get(listener);
        if (listenerSession != null) {
            listenerSession.close();
        }
        
    }
    
    @Override
    public void sendCommand(ClientGroup group, String command) throws RemoteCommandException {
        Session session = null;
        try {
            session = newSession();
            Message message = new JmsMessageBuilder().setGroup(group).setCommand(command)
                    .build(session);
            sendViaMessageProducer(session, message);
        } catch (final JMSException e) {
            throw newRemoteCommandException("JmsRemoteCommandService.sendCommand failed", e);
        } catch (JmsUtilityException e) {
        	throw newRemoteCommandException("JmsRemoteCommandService.sendCommand failed", e);
		} finally {
            tryToCloseSession(session);
        }
        
    }
    
    private RemoteCommandException newRemoteCommandException(@Nonnull final String message,
                                                             @Nonnull final Exception e) {
        LOG.error(message, e);
        return new RemoteCommandException(message, e);
    }
    
    private void sendViaMessageProducer(@Nonnull final Session session,
                                        @Nonnull final Message message) throws JMSException {
        MessageProducer producer = null;
        try {
            producer = newMessageProducer(session);
            producer.send(message);
        } finally {
            tryToCloseMessageProducer(producer);
        }
    }
    
    @Nonnull
    private MessageProducer newMessageProducer(@Nonnull final Session session) throws JMSException {
        Destination destination = session.createTopic(_topicName);
        MessageProducer result = session.createProducer(destination);
        result.setDeliveryMode(DeliveryMode.PERSISTENT);
        result.setTimeToLive(_timeToLiveForAlarms);
        return result;
    }
    
    private void tryToCloseMessageProducer(@CheckForNull final MessageProducer messageProducer) throws JMSException {
        if (messageProducer != null) {
            messageProducer.close();
        }
    }
    
    @Nonnull
    private Session newSession() throws JMSException, JmsUtilityException {
        return SharedJmsConnections.sharedSenderConnection()
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
    
    private void tryToCloseSession(@CheckForNull final Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (final JMSException e) {
                LOG.error("JmsRemoteCommandService.tryToCloseSession failed", e);
            }
        }
    }
    
    private static final class MessageListenerAdapter implements MessageListener {
        
        private final IListener _listener;
        private final ClientGroup _group;
        
        public MessageListenerAdapter(@Nonnull final ClientGroup group,
                                      @Nonnull final IListener listener) {
            _group = group;
            _listener = listener;
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void onMessage(@Nonnull final Message message) {
            // guard: only MapMessages can be handled
            if (! (message instanceof MapMessage)) {
                LOG.error("JmsRemoteCommandService.MessageListenerAdapter.onMessage failed: Invalid message type ({}) received",
                          message.getClass().getCanonicalName());
                return;
            }
            
            final MapMessage mapMessage = (MapMessage) message;
            try {
                if (isCommandMessage(mapMessage) && isProperClientGroup(mapMessage)) {
                    String command = mapMessage.getString("NAME");
                    _listener.receiveCommand(command);
                }
                
            } catch (JMSException e) {
                LOG.error("JmsRemoteCommandService.MessageListenerAdapter.onMessage failed", e);
            }
        }
        
        private boolean isProperClientGroup(@Nonnull final MapMessage message) throws JMSException {
            return _group.toString().equals(message.getString("GROUP"));
        }
        
        private boolean isCommandMessage(@Nonnull final MapMessage message) throws JMSException {
            return "command".equals(message.getString("TYPE"));
        }
        
    }
    
}
