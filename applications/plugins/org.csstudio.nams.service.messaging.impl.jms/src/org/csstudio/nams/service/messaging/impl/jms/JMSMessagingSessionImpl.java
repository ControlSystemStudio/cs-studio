
package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;

public class JMSMessagingSessionImpl implements MessagingSession {

	private static PreferenceService preferenceService;

	public static void staticInject(final PreferenceService service) {
		JMSMessagingSessionImpl.preferenceService = service;
	}

	private Context[] contexts;
	private ConnectionFactory[] factorys;
	private Connection[] connections;
	private Session[] sessions;
	private boolean isClosed;

	private final String environmentUniqueClientId;

	public JMSMessagingSessionImpl(final String environmentUniqueClientId,
			final String[] urls) throws NamingException, JMSException {

		this.environmentUniqueClientId = environmentUniqueClientId;
		this.contexts = new Context[urls.length];
		this.factorys = new ConnectionFactory[urls.length];
		this.connections = new Connection[urls.length];
		this.sessions = new Session[urls.length];

		try {
			for (int i = 0; i < urls.length; i++) {
				final Hashtable<String, String> properties = new Hashtable<String, String>();
				properties
						.put(
								Context.INITIAL_CONTEXT_FACTORY,
								JMSMessagingSessionImpl.preferenceService
										.getString(PreferenceServiceJMSKeys.P_JMS_AMS_CONNECTION_FACTORY_CLASS));
				properties.put(Context.PROVIDER_URL, urls[i]);

				this.contexts[i] = new InitialContext(properties);
				this.factorys[i] = (ConnectionFactory) this.contexts[i]
						.lookup("ConnectionFactory");
				this.connections[i] = this.factorys[i].createConnection();
				this.connections[i].setClientID(environmentUniqueClientId);
				this.sessions[i] = this.connections[i].createSession(false,
						Session.CLIENT_ACKNOWLEDGE);

				this.connections[i].start();
			}
		} catch (final NamingException e) {
			this.close();
			throw e;
		} catch (final JMSException e) {
			this.close();
			throw e;
		}
		this.isClosed = false;
	}

	@Override
    public void close() {
		Contract.require(!this.isClosed(), "!this.isClosed()");
		for (final Session session : this.sessions) {
			if (session != null) {
				try {
					session.close();
				} catch (final JMSException e) {
				    // Can be ignored
				}
			}
		}
		this.sessions = null;
		for (final Connection connection : this.connections) {
			if (connection != null) {
				try {
					connection.close();
				} catch (final JMSException e) {
				    // Can be ignored
				}
			}
		}
		this.connections = null;
		this.factorys = null;
		for (final Context context : this.contexts) {
			if (context != null) {
				try {
					context.close();
				} catch (final NamingException e) {
				    // Can be ignored
				}
			}
		}
		this.contexts = null;
		this.isClosed = true;
	}

	@Override
    public Consumer createConsumer(final String messageSourceName,
			final PostfachArt artDesPostfaches) throws MessagingException {

		JMSConsumer consumer;
		try {
			consumer = new JMSConsumer(this.environmentUniqueClientId,
					messageSourceName, artDesPostfaches, this.sessions);
		} catch (final JMSException e) {
			throw new MessagingException(
					"JMSException during creation of JMSConsumer", e);
		}

		return consumer;
	}

	@Override
    public Producer createProducer(final String messageDestinationName,
			final PostfachArt artDesPostfaches) throws MessagingException {

		JMSProducer producer;
		try {
			producer = new JMSProducer(messageDestinationName,
					artDesPostfaches, this.sessions);
		} catch (final JMSException e) {
			throw new MessagingException(
					"JMSException during creation of JMSProducer", e);
		}

		return producer;
	}

	@Override
    public boolean isClosed() {
		return this.isClosed;
	}
}
