
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

package org.csstudio.utility.jms.sharedconnection;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.csstudio.utility.jms.IConnectionMonitor;
import org.csstudio.utility.jms.TransportEvent;

/**
 * A shared connection to a JMS broker which can be monitored using an
 * {@link IConnectionMonitor}.
 *
 * @author Joerg Rathlev
 */
public class MonitorableSharedConnection {

    private String _clientId;
    private ActiveMQConnection _connection;
    private final String _brokerURI;
    private boolean _interrupted;
    private final ConnectionMonitorSupport _monitorSupport;
    private int _handleCount;

    /**
     * Creates a new monitorable connection to the specified broker URI. The
     * connection is not started by the constructor. It is started automatically
     * as soon as the first handle to this shared connection is requested by
     * calling the {@link #createHandle()} method.
     *
     * @param brokerURI
     *            the broker URI.
     */
    public MonitorableSharedConnection(String clientId, final String brokerURI) {
        _clientId = clientId;
        _brokerURI = brokerURI;
        _interrupted = false;
        _monitorSupport = new ConnectionMonitorSupport();
        _handleCount = 0;
    }

    /**
     * Connects to the JMS broker and starts this connection.
     *
     * @throws JMSException
     *             if the connection could not be created or started due to an
     *             internal error.
     */
    private void connectAndStart() throws JMSException {
        if ((_connection == null) || _connection.isClosed()) {
            final ActiveMQConnectionFactory connectionFactory =
                    new ActiveMQConnectionFactory(_brokerURI);
            try {
                _connection = (ActiveMQConnection)
                        connectionFactory.createConnection();
                if (_clientId != null) {
                    _connection.setClientID(_clientId);
                }
                _connection.addTransportListener(new ConnectionStateTracker());
                _connection.start();
                // The TransportListener is not notified when the connection
                // is started explicitly, so we fire the Connected event
                // manually:
                _monitorSupport.fireConnectedEvent(new TransportEvent(_brokerURI, _clientId, _handleCount));
            } catch (final JMSException e) {
                if (_connection != null) {
                    // Something went wrong, but the connection already exists.
                    // Try to clean up by closing the connection and then set
                    // the _connection variable to null so this wrapper knows
                    // that it is not connected.
                    try {
                        _connection.close();
                    } catch (final JMSException e2) {
                        // ignore (we can't throw two exceptions -- the
                        // important thing is to set the _connection to null,
                        // which happens in the finally block)
                    } finally {
                        _connection = null;
                    }
                }
                throw e; // rethrow
            }
        }
    }

    /**
     * Closes this connection.
     *
     * @throws JMSException
     *             if the JMS provider fails to close the connection due to some
     *             internal error.
     */
//    @SuppressWarnings("unused") // TODO: track handles and close connection when no longer needed.
    synchronized void close() throws JMSException {
        if (_connection != null) {
            if (!_connection.isClosed()) {
                if (_connection.isStarted()) {
                    _connection.stop();
                }
                _connection.close();
                // The connection's TransportListener is not notified when the
                // connection is closed explicitly, so we fire the Disconnected
                // event manually:
                _monitorSupport.fireDisconnectedEvent(new TransportEvent(_brokerURI, _clientId, _handleCount));
            }
        }
    }

    private boolean isConnectedAndStarted() {
        return ((_connection != null) && _connection.isStarted());
    }

    /**
     * Returns whether this connection is started and not interrupted.
     *
     * @return <code>true</code> if this connection is started and not
     *         interrupted, <code>false</code> otherwise.
     */
    synchronized boolean isActive() {
        return (isConnectedAndStarted() && !_interrupted);
    }

    /**
     * Creates a handle to access this shared connection.
     *
     * @return a handle to this connection.
     * @throws JMSException
     *             if the underlying shared connection could not be created or
     *             started due to an internal error.
     */
    public ISharedConnectionHandle createHandle() throws JMSException {
        synchronized (this) {
            if (!isConnectedAndStarted()) {
                connectAndStart();
            }
            _handleCount++;
        }
        return new ConnectionHandle();
    }

    public synchronized Connection getConnection() {
        return _connection;
    }

    public synchronized String getBrokerURI() {
        return _brokerURI;
    }

    public synchronized ConnectionMonitorSupport getConnectionMonitorSupport() {
        return _monitorSupport;
    }

    public synchronized boolean isInterrupted() {
        return _interrupted;
    }

    public synchronized void setInterrupted(boolean isInterrupted) {
        _interrupted = isInterrupted;
    }

    public synchronized void setClientId(String id) {
        synchronized (_connection) {
            if (_connection != null) {
                try {
                    _connection.setClientID(id);
                } catch (JMSException e) {
                    // Ignore Me!
                }
            }
        }
    }

    public synchronized String getClientId() {
        String clientId = null;
        try {
            clientId = _connection.getClientID();
        } catch (JMSException e) {
         // Ignore Me!
        }
        return clientId;
    }

    public synchronized int getHandleCount() {
        return _handleCount;
    }

    public synchronized void incHandleCount() {
        _handleCount++;
    }

    public synchronized void decHandleCount() {
        if (--_handleCount < 0) {
            _handleCount = 0;
        }
    }

    public synchronized boolean existOpenHandles() {
        return (_handleCount > 0);
    }

    /**
     * Handle which provides access to a {@link MonitorableSharedConnection}.
     *
     * @author Joerg Rathlev
     */
    private class ConnectionHandle implements ISharedConnectionHandle {

        protected ConnectionHandle() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Session createSession(final boolean transacted, final int acknowledgeMode)
                throws JMSException {
            return getConnection().createSession(transacted, acknowledgeMode);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void release() {
            decHandleCount();
            if (!existOpenHandles()) {
                try {
                    close();
                } catch (JMSException e) {
                    // Ignore Me!
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isActive() {
            return MonitorableSharedConnection.this.isActive();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addMonitor(final IConnectionMonitor monitor) {
            getConnectionMonitorSupport().addMonitor(monitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeMonitor(final IConnectionMonitor monitor) {
            getConnectionMonitorSupport().addMonitor(monitor);
        }
    }

    /**
     * Transport listener which is used by a {@link MonitorableSharedConnection}
     * to track its own connection state.
     *
     * @author Joerg Rathlev
     */
    private class ConnectionStateTracker implements TransportListener {

        protected ConnectionStateTracker() {
        }

        @Override
        public void onCommand(final Object command) {
            // do nothing
        }

        @Override
        public void onException(final IOException e) {
            setInterrupted(true);
            getConnectionMonitorSupport().fireDisconnectedEvent(new TransportEvent(getBrokerURI(),
                                                                                   getClientId(),
                                                                                   getHandleCount()));
        }

        @Override
        public void transportInterupted() {
            setInterrupted(true);
            getConnectionMonitorSupport().fireDisconnectedEvent(new TransportEvent(getBrokerURI(),
                                                                                   getClientId(),
                                                                                   getHandleCount()));
        }

        @Override
        public void transportResumed() {
            setInterrupted(false);
            getConnectionMonitorSupport().fireConnectedEvent(new TransportEvent(getBrokerURI(),
                                                                                getClientId(),
                                                                                getHandleCount()));
        }
    }
}
