package org.csstudio.platform.libs.jms;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
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
 */
public class JmsRedundantProducer {

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

	/** Array of JMS sessions */
	private Session[] _sessions = null;

	/** Message Producers. Key -> ProducerId, Value -> Array of message producers */
	private Hashtable<ProducerId, MessageProducer[]> producers = null;

	/** Array of URL */
	private URL[] urls = null;

	public JmsRedundantProducer(String clientId, URL[] urlsToConnect)
			throws RuntimeException {
		boolean atLeastOneConnected = false;

		this.urls = JmsRedundantProducer.copyOf(urlsToConnect, urlsToConnect.length, URL[].class);
		CONNECTION_COUNT = urls.length;

		_contexts = new Context[CONNECTION_COUNT];
		_factories = new ConnectionFactory[CONNECTION_COUNT];
		_connections = new Connection[CONNECTION_COUNT];
		_sessions = new Session[CONNECTION_COUNT];

		Exception lastException = null;

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(Context.INITIAL_CONTEXT_FACTORY,
					ACTIVEMQ_JNDI_ACTIVE_CONTEXT_FACTORY);
			properties.put(Context.PROVIDER_URL, urls[index].toString());

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
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING,
						"jms connection to url " + urls[index].toString()
								+ " failed!", jmse);
				lastException = jmse;
			}
		}

		if (!atLeastOneConnected) {
			throw new RuntimeException("No connection possible!", lastException);
		}
	};
	
	/**
	 * Copied from SUN Java 1.6 API, class java.util.Arrays.
	 */
	@SuppressWarnings("unchecked")
	private static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

	/**
	 * Closes all sessions/connections. The producer can not be used afterwards.
	 * 
	 * @require !isClosed()
	 */
	public void closeAll() {
		assert !isClosed() : "Vorbedingung verletzt: !isClosed()";
		MessageProducer[] producer = null;

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

		if (producers != null) {
			Enumeration<MessageProducer[]> list = producers.elements();

			while (list.hasMoreElements()) {
				producer = list.nextElement();

				for (int i = 0; i < producer.length; i++) {
					try {
						producer[i].close();
					} catch (JMSException jmse) {
					}
				}
			}

			producer = null;
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

	/**
	 * Creates a MessageProducer with given topic-destination.
	 * 
	 * @param topicName
	 *            Name of the destination topic (could be null)
	 * @return The Id for the created Producer.
	 * @throws RuntimeException
	 *             If no producer could be created for given topic (the nested
	 *             exception will be the JMSException)!
	 * @require !isClosed()
	 */
	public ProducerId createProducer(String topicName) throws RuntimeException {
		assert !isClosed() : "Vorbedingung verletzt: !isClosed()";

		MessageProducer[] producers = new MessageProducer[CONNECTION_COUNT];

		int numberOfFailures = 0;
		Exception lastException = null;

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			try {
				Topic topic = _sessions[index].createTopic(topicName);
				producers[index] = _sessions[index].createProducer(topic);
			} catch (JMSException jmsex) {
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING,
						"MessageProducer for Topic " + topicName + " to url "
								+ urls[index].toString() + " failed!", jmsex);
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

		this.producers.put(id, producers);

		return id;
	}

	/**
	 * Determines if producers on id have an initial destination topic.
	 * 
	 * @param id
	 *            The id of the producers
	 * @return {@code true}, if the producers on the id have an initial
	 *         destination topic, {@code false} otherwise.
	 * @throws RuntimeException
	 *             If destination couldn't be retrieved on at least one producer
	 *             (the nested exception will be the JMSException)!
	 * @require !isClosed()
	 */
	public boolean hasProducerDestiantion(ProducerId id)
			throws RuntimeException {
		assert !isClosed() : "Vorbedingung verletzt: !isClosed()";

		MessageProducer[] messageProducers = producers.get(id);

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
								+ urls[index].toString(), e);
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

	/**
	 * Determines if the producer has been closed.
	 * 
	 * @return {@code true} if producers is closed, {@code false} otherwise.
	 */
	public boolean isClosed() {
		return _sessions == null;
	}

	/**
	 * Sends the given message to the producers specified by the id. Should only
	 * be called if the producers have an initial topic (see
	 * {@link #hasProducerDestiantion(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId)}).
	 * 
	 * @param id
	 *            The id of the producer
	 * @param message
	 *            The Message to send
	 * @return An array of URLs, where the message could successfully send to
	 * @throws RuntimeException
	 *             If the message couldn't be send to at least one producer (the
	 *             nested exception will be the JMSException)!
	 * @require !isClosed()
	 */
	public URL[] send(ProducerId id, Message message) throws RuntimeException {
		assert !isClosed() : "Vorbedingung verletzt: !isClosed()";

		return this.send(id, null, message);
	}

	/**
	 * Sends the given message to the producers specified by the id.
	 * 
	 * @param id
	 *            The id of the producer
	 * @param topicName
	 *            The name of the topic to send the message (could be null if
	 *            producer has initial topic (see
	 *            {@link #hasProducerDestiantion(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId)}))
	 * @param message
	 *            The Message to send
	 * @return An array of URLs, where the message could successfully send to
	 * @throws RuntimeException
	 *             If the message couldn't be send to at least one producer (the
	 *             nested exception will be the JMSException)!
	 * @require !isClosed()
	 */
	public URL[] send(ProducerId id, String topicName, Message message)
			throws RuntimeException {
		assert !isClosed() : "Vorbedingung verletzt: !isClosed()";

		MessageProducer[] messageProducers = producers.get(id);

		int numberOfFailures = 0;
		Exception lastException = null;

		List<URL> result = new ArrayList<URL>(CONNECTION_COUNT);

		for (int index = 0; index < CONNECTION_COUNT; index++) {
			try {
				if (topicName == null) {
					messageProducers[index].send(message);
				} else {
					Topic topic = _sessions[index].createTopic(topicName);
					messageProducers[index].send(topic, message);
				}
				result.add(urls[index]);
			} catch (JMSException e) {
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING,
						"Could not send Message for Topic " + topicName
								+ " to url " + urls[index].toString(), e);
				lastException = e;
				numberOfFailures++;
			}
		}

		if (numberOfFailures >= CONNECTION_COUNT) {
			throw new RuntimeException("No producers could be created!",
					lastException);
		}

		return result.toArray(new URL[result.size()]);
	}
}
