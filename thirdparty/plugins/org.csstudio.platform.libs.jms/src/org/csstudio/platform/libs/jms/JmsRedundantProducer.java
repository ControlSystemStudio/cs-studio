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
 package org.csstudio.platform.libs.jms;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * A message producer uses several connections (fallbacks).
 * 
 * @author C1 WPS / Kai Meyer, Matthias Zeimer
 * @deprecated org.csstudio.platform.utility.jms.JmsRedundantProducer instead
 */
@Deprecated
public class JmsRedundantProducer implements IJmsProducer {

	/**
	 * An Id for Message-Producers.
	 * 
	 * @author C1 WPS / Kai Meyer, Matthias Zeimer
	 */
	static public abstract class ProducerId {
	}

	/**
	 * Lookup name of jms connection factory.
	 */
	private static final String CONNECTION_FACTORY_LOOKUP = "ConnectionFactory";

	/**
	 * Class name of jms context factory (currently only ActiveMQ).
	 */
	private static final String ACTIVEMQ_JNDI_ACTIVE_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

	/** Number of redundant connections relay on number of given urls */
	private final int CONNECTION_COUNT;

	/**
	 * The contexts for the JMS-Connection. Required as field, because the
	 * contexts have to be closed
	 */
	private Context[] _contexts;

	/**
	 * The factories for the JMS-Connection. Required as field, because the
	 * factories have to be closed
	 */
	private ConnectionFactory[] _factories;

	/**
	 * The connection for the JMS-Connection. Required as field, because the
	 * connection have to be closed
	 */
	private Connection[] _connections;

	/**
	 * Array of JMS sessions
	 */
	private Session[] _sessions = null;

	/**
	 * Message Producers. Key -> ProducerId, Value -> Array of message producers
	 */
	private Map<ProducerId, MessageProducer[]> _producers = null;

	/**
	 * Array of URL
	 */
	private String[] _urls = null;

	/**
	 * Creates a redundant producer to connect JMS-Server at specified URLs.
	 * 
	 * @param clientId
	 *            The JMS client Id
	 * @param urlsToConnect
	 *            The Server URLs to connect
	 * @throws RuntimeException
	 *             If an error occurs, the JMS-Exception will be the nested
	 *             exception.
	 * @require clientId != null
	 */
	public JmsRedundantProducer(String clientId, String[] urlsToConnect)
			throws RuntimeException {
		boolean atLeastOneConnected = false;
		assert clientId != null : "Precondition violated: clientId != null";

		_producers = new HashMap<ProducerId, MessageProducer[]>();

		_urls = JmsRedundantProducer.copyOf(urlsToConnect,
				urlsToConnect.length, String[].class);
		CONNECTION_COUNT = _urls.length;

		_contexts = new Context[CONNECTION_COUNT];
		_factories = new ConnectionFactory[CONNECTION_COUNT];
		_connections = new Connection[CONNECTION_COUNT];
		_sessions = new Session[CONNECTION_COUNT];

		Exception lastException = null;

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(Context.INITIAL_CONTEXT_FACTORY,
					ACTIVEMQ_JNDI_ACTIVE_CONTEXT_FACTORY);
			properties.put(Context.PROVIDER_URL, _urls[index]);

			try {
				_contexts[index] = new InitialContext(properties);
				_factories[index] = (ConnectionFactory) _contexts[index]
						.lookup(CONNECTION_FACTORY_LOOKUP);
				_connections[index] = _factories[index].createConnection();
				_connections[index].setClientID(clientId);
				_sessions[index] = _connections[index].createSession(false,
						Session.CLIENT_ACKNOWLEDGE);

				_connections[index].start();

				atLeastOneConnected = true;
			} catch (NamingException ne) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"lookup or context failure!", ne);
				lastException = ne;
			} catch (JMSException jmse) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"jms connection to url " + _urls[index] + " failed!",
						jmse);
				lastException = jmse;
			}
		}

		if (!atLeastOneConnected) {
			closeAll();
			throw new RuntimeException("No connection possible!", lastException);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeAll();
	}

	/**
	 * Copied from SUN Java 1.6 API, class java.util.Arrays.
	 */
	@SuppressWarnings("unchecked")
	private static <T, U> T[] copyOf(U[] original, int newLength,
			Class<? extends T[]> newType) {
		T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
				: (T[]) Array
						.newInstance(newType.getComponentType(), newLength);
		System.arraycopy(original, 0, copy, 0, Math.min(original.length,
				newLength));
		return copy;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#closeAll()
	 */
	public void closeAll() {
		assert !isClosed() : "Precondition violated: !isClosed()";

		if (_connections != null) {
			for (int i = 0; i < CONNECTION_COUNT; i++) {
				if (_connections[i] != null) {
					try {
						_connections[i].stop();
					} catch (JMSException jmse) {
					}
				}
			}
		}

		if (_producers != null) {
			for (MessageProducer[] producerArray : _producers.values()) {
				for (MessageProducer producer : producerArray) {
					try {
						producer.close();
					} catch (JMSException jmse) {
						Logger.getLogger(this.getClass().getName()).log(
								Level.WARNING,
								"could not close Producer " + producer, jmse);
					}
				}
			}
		}

		for (int i = 0; i < CONNECTION_COUNT; i++) {
			if (_sessions != null) {
				if (_sessions[i] != null) {
					try {
						_sessions[i].close();
					} catch (JMSException jmse) {
					}

					_sessions[i] = null;
				}
			}

			if (_connections != null) {
				if (_connections[i] != null) {
					try {
						_connections[i].close();
					} catch (JMSException jmse) {
					}

					_connections[i] = null;
				}
			}

			if (_factories != null) {
				_factories[i] = null;
			}

			if (_contexts != null) {
				if (_contexts[i] != null) {
					try {
						_contexts[i].close();
					} catch (NamingException ne) {
					}

					_contexts[i] = null;
				}
			}
		}

		_factories = null;
		_connections = null;
		_sessions = null;
		_contexts = null;

	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#createProducer(java.lang.String)
	 */
	public ProducerId createProducer(String topicName) throws RuntimeException {
		assert !isClosed() : "Precondition violated: !isClosed()";

		MessageProducer[] producers = new MessageProducer[CONNECTION_COUNT];

		int numberOfFailures = 0;
		Exception lastException = null;

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			try {
				Topic topic = (topicName != null ? _sessions[index]
						.createTopic(topicName) : null);
				producers[index] = _sessions[index].createProducer(topic);
			} catch (JMSException jmsex) {
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING,
						"MessageProducer for Topic " + topicName + " to url "
								+ _urls[index].toString() + " failed!", jmsex);
				numberOfFailures++;
				lastException = jmsex;
			}
		}

		if (numberOfFailures >= CONNECTION_COUNT) {
			throw new RuntimeException("No producers could be created!",
					lastException);
		}

		ProducerId id = new ProducerId() {
		};

		this._producers.put(id, producers);

		return id;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#hasProducerDestiantion(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId)
	 */
	public boolean hasProducerDestiantion(ProducerId id)
			throws RuntimeException {
		assert !isClosed() : "Precondition violated: !isClosed()";
		assert knowsProducer(id) : "Precondition violated: knowsProducer(id)";

		MessageProducer[] messageProducers = _producers.get(id);

		int numberOfFailures = 0;
		Exception lastException = null;
		boolean result = false;

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			try {
				result = result
						|| messageProducers[index].getDestination() != null;
			} catch (JMSException e) {
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING,
						"Could not retrieve destination of producers on url "
								+ _urls[index].toString(), e);
				lastException = e;
				numberOfFailures++;
			}
		}

		if (numberOfFailures >= CONNECTION_COUNT) {
			throw new RuntimeException("No producers could be created!",
					lastException);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#knowsProducer(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId)
	 */
	public boolean knowsProducer(final ProducerId id) {
		return this._producers.containsKey(id);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#isClosed()
	 */
	public boolean isClosed() {
		return _sessions == null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#send(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId, javax.jms.Message)
	 */
	public String[] send(ProducerId id, Message message)
			throws RuntimeException {
		assert !isClosed() : "Precondition violated: !isClosed()";
		assert knowsProducer(id) : "Precondition violated: knowsProducer(id)";

		return this.send(id, null, message);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsRedundantProducer#send(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId, java.lang.String, javax.jms.Message)
	 */
	public String[] send(ProducerId id, String topicName, Message message)
			throws RuntimeException {
		assert !isClosed() : "Precondition violated: !isClosed()";
		assert knowsProducer(id) : "Precondition violated: knowsProducer(id)";

		MessageProducer[] messageProducers = _producers.get(id);

		int numberOfFailures = 0;
		Exception lastException = null;

		List<String> result = new ArrayList<String>(CONNECTION_COUNT);

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			try {
				if (topicName == null) {
					messageProducers[index].send(message);
				} else {
					Topic topic = _sessions[index].createTopic(topicName);
					messageProducers[index].send(topic, message);
				}
				result.add(_urls[index]);
			} catch (JMSException e) {
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING,
						"Could not send Message for Topic " + topicName
								+ " to url " + _urls[index], e);
				lastException = e;
				numberOfFailures++;
			}
		}

		if (numberOfFailures >= CONNECTION_COUNT) {
			throw new RuntimeException("No producers could be created!",
					lastException);
		}

		return result.toArray(new String[result.size()]);
	}

	public MapMessage createMapMessage() throws RuntimeException {
		try {
			return _sessions[0].createMapMessage();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
