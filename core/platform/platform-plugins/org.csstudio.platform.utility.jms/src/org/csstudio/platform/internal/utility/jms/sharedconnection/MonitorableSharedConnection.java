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

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;

/**
 * A shared connection to a JMS broker which can be monitored using an
 * {@link IConnectionMonitor}.
 *
 * @author Joerg Rathlev
 */
class MonitorableSharedConnection {

    private ActiveMQConnection _connection;
    private final String _brokerURI;
    private boolean _interrupted;
    private final ConnectionMonitorSupport _monitorSupport;

    /**
     * Creates a new monitorable connection to the specified broker URI. The
     * connection is not started by the constructor. It is started automatically
     * as soon as the first handle to this shared connection is requested by
     * calling the {@link #createHandle()} method.
     *
     * @param brokerURI
     *            the broker URI.
     */
    MonitorableSharedConnection(final String brokerURI) {
        _brokerURI = brokerURI;
        _interrupted = false;
        _monitorSupport = new ConnectionMonitorSupport();
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
                _connection.addTransportListener(new ConnectionStateTracker());
                _connection.start();
                // The TransportListener is not notified when the connection
                // is started explicitly, so we fire the Connected event
                // manually:
                _monitorSupport.fireConnectedEvent();
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
    @SuppressWarnings("unused") // TODO: track handles and close connection when no longer needed.
    private synchronized void close() throws JMSException {
        if (_connection != null) {
            _connection.close();
            // The connection's TransportListener is not notified when the
            // connection is closed explicitly, so we fire the Disconnected
            // event manually:
            _monitorSupport.fireDisconnectedEvent();
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
    ISharedConnectionHandle createHandle() throws JMSException {
        synchronized (this) {
            if (!isConnectedAndStarted()) {
                connectAndStart();
            }
        }
        return new ConnectionHandle();
    }

    /**
     * Handle which provides access to a {@link MonitorableSharedConnection}.
     *
     * @author Joerg Rathlev
     */
    private class ConnectionHandle implements ISharedConnectionHandle {

        /**
         * {@inheritDoc}
         */
        @Override
        public Session createSession(final boolean transacted, final int acknowledgeMode)
                throws JMSException {
            return _connection.createSession(transacted, acknowledgeMode);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void release() {
            // This implementation does not track handles and never closes the
            // shared connection.

            // TODO: track handles and close connection when no longer needed.
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
            _monitorSupport.addMonitor(monitor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeMonitor(final IConnectionMonitor monitor) {
            _monitorSupport.addMonitor(monitor);
        }
    }

    /**
     * Transport listener which is used by a {@link MonitorableSharedConnection}
     * to track its own connection state.
     *
     * @author Joerg Rathlev
     */
    private class ConnectionStateTracker implements TransportListener {

        public void onCommand(final Object command) {
            // do nothing
        }

        public void onException(final IOException e) {
            _interrupted = true;
            System.out.println("onException called"); // TODO: remove (for testing only)
            _monitorSupport.fireDisconnectedEvent();
        }

        public void transportInterupted() {
            _interrupted = true;
            System.out.println("transportInterrupted called"); // TODO: remove (for testing only)
            _monitorSupport.fireDisconnectedEvent();
        }

        public void transportResumed() {
            _interrupted = false;
            System.out.println("transportResumed called"); // TODO: remove (for testing only)
            _monitorSupport.fireConnectedEvent();
        }
    }

    // TODO: remove (for testing only)
    public static void main(final String[] args) {
        final MonitorableSharedConnection conn = new MonitorableSharedConnection("failover:(tcp://localhost:61616)");
        try {
            conn._monitorSupport.addMonitor(new IConnectionMonitor() {

                public void onConnected() {
                    System.out.println("onConnected called");
                }

                public void onDisconnected() {
                    System.out.println("onDisconnected called");
                }

            });
            conn.connectAndStart();
            for (int i = 0; i < 60; i++) {
                Thread.sleep(1000);
                System.out.println("isTransportFailed: " + conn._connection.isTransportFailed());
            }
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
