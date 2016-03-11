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
 */

package org.csstudio.platform.utility.jms.asyncreceiver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.transport.TransportListener;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;

/**
 * <p>A connection to one JMS broker. Instances of this class encapsulate a JMS
 * connection, session, and the message consumers. A <code>JmsConnection</code>
 * monitors the state of its underlying JMS connection and reports changes to
 * the <code>JmsConnector</code> from which it was created.</p>
 *
 * <p>This class is intended to be used only by a <code>JmsConnector</code>.</p>
 *
 * @author Joerg Rathlev
 */
final class JmsConnection implements TransportListener {

    /**
     * The initial context factory used for JNDI.
     */
    // Note: this used to be a preference setting, but this class requires
    // ActiveMQ anyway for the connection monitoring, so this cannot actually
    // be changed. So we can use a constant here and keep the UI simple.
    private static final String JNDI_CONTEXT_FACTORY =
        "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    /**
     * The logger used by this object.
     */
//    private final CentralLogger _log = CentralLogger.getInstance();

    /**
     * The topics that this connection will subscribe to.
     */
    private String[] _topics;

    /**
     * The URI of the JMS broker which this connection connects to.
     */
    private String _brokerUri;

    /**
     * The message listener to which messages will be delievered.
     */
    private final MessageListener _listener;

    /**
     * The JMS connection.
     */
    private Connection _connection;

    /**
     * The JMS connector to which this connection belongs.
     */
    private JmsConnector _connector;

    /**
     * Whether this connection is interrupted.
     */
    private boolean _interrupted = false;

    /**
     * Creates a new JMS connection.
     *
     * @param connector
     *            the <code>JmsConnector</code> to which this connection
     *            belongs.
     * @param brokerUri
     *            the URI of the broker to connect to.
     * @param topics
     *            the JMS topics to connect to.
     * @param listener
     *            the message listener to which messages will be delivered.
     */
    JmsConnection(final JmsConnector connector, final String brokerUri,
            final String[] topics, final MessageListener listener) {
        _connector = connector;
        _brokerUri = brokerUri;
        _listener = listener;
        _topics = new String[topics.length];
        System.arraycopy(topics, 0, _topics, 0, topics.length);
    }

    /**
     * Starts this connection. This method blocks until the connection is
     * established successfully. Note that this does not guarantee that the
     * connection is available when this method returns; the connection might
     * fail after it was established but before this method returns.
     *
     * @throws JmsConnectionException
     *             when an error occurs that prevents this connection from
     *             connecting to the JMS broker.
     */
    void start() throws JmsConnectionException {
        try {
            _interrupted = false;
            _connection = JMSConnectionFactory.connect(_brokerUri);
            ((ActiveMQConnection) _connection).addTransportListener(this);

            // The following call blocks for ActiveMQ when using the failover
            // transport
            Session session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            createMessageConsumers(session);

//            _log.debug(this, "Starting connection.");
            _connection.start();
//            _log.debug(this, "Connection started.");
            _connector.onConnectionStateChanged();
        } catch (JMSException e) {
//            _log.error(this, "Error creating JMS connection.", e);
            throw new JmsConnectionException("Error connecting to broker", e);
        }
    }

    /**
     * Closes this connection.
     */
    void close() {
        if (_connection != null) {
//            _log.debug(this, "Closing JMS connection.");
            try {
                // Closing the connection will also close the session and
                // the message consumers.
                _connection.close();
                _connector.onConnectionStateChanged();
            } catch (JMSException e) {
//                _log.warn(this, "Error while closing JMS connection.", e);
            }
        }

        // Allow garbage collection to do its work
        _connection = null;
    }

    /**
     * Creates the <code>MessageConsumers</code> for the topics.
     *
     * @param session
     *            the session to use.
     * @throws JMSException
     *             if the creation of the message consumers fails.
     */
    private void createMessageConsumers(final Session session)
            throws JMSException {
        for (String topicName : _topics) {
//            _log.debug(this, "Creating MessageConsumer for topic " + topicName);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(_listener);
        }
    }

    /**
     * Looks up the JMS connection factory via JNDI.
     *
     * @return the connection factory.
     * @throws JmsConnectionException
     *             if the connection factory cannot be looked up.
     */
    @SuppressWarnings("unused")
    private ConnectionFactory lookupConnectionFactory() throws JmsConnectionException {
//        _log.debug(this, "Looking up JMS connection factory.");
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_CONTEXT_FACTORY);
        properties.put(Context.PROVIDER_URL, _brokerUri);
        try {
            Context context = new InitialContext(properties);
            return (ConnectionFactory) context.lookup("ConnectionFactory");
        } catch (NamingException e) {
//            _log.error(this, "Error getting connection factory.");
            throw new JmsConnectionException("Error looking up connection factory", e);
        }
    }

    /**
     * Returns whether this connection is connected. A connection is connected
     * if it is started and its transport is not interrupted.
     *
     * @return <code>true</code> if this connection is connected,
     *         <code>false</code> otherwise.
     */
    boolean isConnected() {
        return (_connection != null)
                && (((ActiveMQConnection) _connection).isStarted())
                && !_interrupted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCommand(final Object command) {
        // do nothing
    }

    /**
     * Called when an unrecoverable exception has occurred on the underlying JMS
     * connection.
     *
     * @param e
     *            the exception.
     */
    @Override
    public void onException(final IOException e) {
        // TODO perform recovery in this case?
//        _log.error(this, "Exception occurred.", e);
        _connector.onConnectionStateChanged();
    }

    /**
     * Called when the JMS transport is interrupted.
     */
    @Override
    public void transportInterupted() {
//        _log.debug(this, "Transport interrupted.");
        _interrupted = true;
        _connector.onConnectionStateChanged();
    }

    /**
     * Called when the JMS transport is resumed.
     */
    @Override
    public void transportResumed() {
//        _log.debug(this, "Transport resumed.");
        _interrupted = false;
        _connector.onConnectionStateChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "JmsConnection {broker=" + _brokerUri + ", topics="
                + Arrays.toString(_topics) + "}";
    }
}
