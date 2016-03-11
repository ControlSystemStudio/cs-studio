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
 */

package org.csstudio.platform.internal.utility.jms.sharedconnection;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;

/**
 * Connects a {@link MessageListener} to multiple JMS brokers using shared
 * connections.
 *
 * @author Joerg Rathlev
 */
class MultiConnectionReceiver implements IMessageListenerSession {

    /*
     * Implementation note: this class was designed to be used only through
     * the IMessageListenerSession interface. Its only public methods are
     * methods defined by that interface.
     */

    private final ISharedConnectionHandle[] _connections;
    private final Session[] _sessions;
    private final MessageListener _listener;
    private final String[] _topics;
    private final int _acknowledgeMode;
    private boolean _allConnectionsActive;
    private final ConnectionMonitorSupport _monitorSupport;

    /**
     * Creates a new <code>MultiConnectionReceiver</code>.
     *
     * @param connections
     *            handles to the shared connections that this receiver will use.
     * @param listener
     *            the message listener.
     * @param topics
     *            the topics the listener will be connected to.
     * @param acknowledgeMode
     *            the acknowledgement mode to use for the receiver sessions.
     *            Legal values are <code>Session.AUTO_ACKNOWLEDGE</code>,
     *            <code>Session.CLIENT_ACKNOWLEDGE</code>, and
     *            <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
     */
    private MultiConnectionReceiver(ISharedConnectionHandle[] connections,
            MessageListener listener, String[] topics, int acknowledgeMode) {
        _connections = new ISharedConnectionHandle[connections.length];
        System.arraycopy(connections, 0, _connections, 0, _connections.length);
        _sessions = new Session[_connections.length];
        _listener = listener;
        _topics = new String[topics.length];
        System.arraycopy(topics, 0, _topics, 0, _topics.length);
        _acknowledgeMode = acknowledgeMode;
        _monitorSupport = new ConnectionMonitorSupport();
    }

    /**
     * Connects a listener to one or more shared JMS connections. The listener
     * will already be started when this method returns.
     *
     * @param connections
     *            the shared connections from which the listener will receive
     *            messages.
     * @param listener
     *            the listener.
     * @param topics
     *            the topics to listen on.
     * @param acknowledgeMode
     *            the acknowledgement mode for the JMS sessions. Legal values
     *            are <code>Session.AUTO_ACKNOWLEDGE</code>,
     *            <code>Session.CLIENT_ACKNOWLEDGE</code>, and
     *            <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
     *
     * @return An <code>IMessageListenerSession</code> which can be used to
     *         control the listener session.
     * @throws JMSException
     *             if an internal error occured in the underlying JMS provider.
     */
    static IMessageListenerSession createListenerSession(
            ISharedConnectionHandle[] connections, MessageListener listener,
            String[] topics, int acknowledgeMode) throws JMSException {
        MultiConnectionReceiver mcr = new MultiConnectionReceiver(connections,
                listener, topics, acknowledgeMode);
        mcr.startMonitoringConnections();
        mcr.startListening();
        return mcr;
    }

    /**
     * Creates the sessions with the underlying JMS connections and starts
     * listening.
     *
     * @throws JMSException
     *             if one of the sessions could not be created or one of the
     *             topics or message consumers could not be created or the
     *             message listener could not be assigned to one of the message
     *             consumers due to an internal error.
     */
    private void startListening() throws JMSException {
        try {
            for (int i = 0; i < _connections.length; i++) {
                _sessions[i] =
                        _connections[i].createSession(false, _acknowledgeMode);
                createMessageConsumers(_sessions[i]);
            }
        } catch (JMSException e) {
            // Something went wrong. Try to clean up by closing the sessions.
            for (int i = 0; i < _sessions.length; i++) {
                try {
                    if (_sessions[i] != null) {
                        _sessions[i].close();
                    }
                } catch (JMSException e2) {
                    // Ignore (we want to rethrow the exception that occured
                    // when creating the session because it is more likely to
                    // contain information about the root cause of the problem)
                } finally {
                    _sessions[i] = null;
                }
            }
            throw e; // rethrow
        }
    }

    /**
     * Creates the <code>MessageConsumer</code>s for the specified session.
     *
     * @param session
     *            the session.
     * @throws JMSException
     *             if the topic or the message consumer could not be created or
     *             the message listener could not be set due to an internal
     *             error.
     */
    private void createMessageConsumers(Session session) throws JMSException {
        for (String topicName : _topics) {
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(_listener);
        }
    }

    /**
     * Closes this receiver. The receiver cannot be restarted after it was
     * closed.
     */
    public void close() {
        for (int i = 0; i < _sessions.length; i++) {
            try {
                _sessions[i].close();
            } catch (JMSException e) {
                // Ignored because the loop should continue with the next
                // session, and this method should not throw an exception.
                // TODO: we really need a logging facility here!
            } finally {
                _sessions[i] = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMonitor(IConnectionMonitor monitor) {
        _monitorSupport.addMonitor(monitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMonitor(IConnectionMonitor monitor) {
        _monitorSupport.removeMonitor(monitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return _allConnectionsActive;
    }

    /**
     * Starts monitoring the shared connections used by this receiver.
     */
    private synchronized void startMonitoringConnections() {
        IConnectionMonitor monitor = new ConnectionMonitor();
        _allConnectionsActive = true;
        for (ISharedConnectionHandle connection : _connections) {
            connection.addMonitor(monitor);
            _allConnectionsActive &= connection.isActive();
        }
    }

    /**
     * Called by the {@link ConnectionMonitor} when the state of one of the
     * connections used by this receiver has changed.
     */
    private void onConnectionStateChanged() {
        boolean newAllConnectionsActive = true;
        for (ISharedConnectionHandle connection : _connections) {
            newAllConnectionsActive &= connection.isActive();
        }

        // If the state has changed, update the state and fire the appropriate
        // event.
        if (newAllConnectionsActive != _allConnectionsActive) {
            _allConnectionsActive = newAllConnectionsActive;
            if (_allConnectionsActive) {
                _monitorSupport.fireConnectedEvent();
            } else {
                _monitorSupport.fireDisconnectedEvent();
            }
        }
    }

    /**
     * Used by a <code>MultiConnectionReceiver</code> to track the connection
     * state of its connections.
     *
     * @author Joerg Rathlev
     */
    private class ConnectionMonitor implements IConnectionMonitor {

        public void onConnected() {
            onConnectionStateChanged();
        }

        public void onDisconnected() {
            onConnectionStateChanged();
        }
    }
}
