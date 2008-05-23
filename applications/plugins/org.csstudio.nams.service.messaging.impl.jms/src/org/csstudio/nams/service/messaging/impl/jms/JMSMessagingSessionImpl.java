package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class JMSMessagingSessionImpl implements MessagingSession {

	private Context[] contexts;
	private ConnectionFactory[] factorys;
	private Connection[] connections;
	private Session[] sessions;
	private boolean isClosed;
	private final String environmentUniqueClientId;

	public JMSMessagingSessionImpl(String environmentUniqueClientId,
			String[] urls) throws NamingException, JMSException {
		
		this.environmentUniqueClientId = environmentUniqueClientId;
		contexts = new Context[urls.length];
		factorys = new ConnectionFactory[urls.length];
		connections = new Connection[urls.length];
		sessions = new Session[urls.length];

		try {
			for (int i = 0; i < urls.length; i++) {
				Hashtable<String, String> properties = new Hashtable<String, String>();
				//FIXME aus dem preference store holen!
				properties.put(Context.INITIAL_CONTEXT_FACTORY,
						"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				properties.put(Context.PROVIDER_URL, urls[i]);
	
				contexts[i] = new InitialContext(properties);
				factorys[i] = (ConnectionFactory) contexts[i]
						.lookup("ConnectionFactory");
				connections[i] = factorys[i].createConnection();
				connections[i].setClientID(environmentUniqueClientId);
				sessions[i] = connections[i].createSession(false,
						Session.CLIENT_ACKNOWLEDGE);
	
				connections[i].start();
			}
		} catch (NamingException e) {
			close();
			throw e;
		} catch (JMSException e) {
			close();
			throw e;
		}
		isClosed = false;
	}

	public void close() {
		for (Session session : sessions) {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {}
			}
		}
		sessions = null;
		for (Connection connection : connections) {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {}
			}
		}
		connections = null;
		factorys = null;
		for (Context context : contexts) {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException e) {}
			}
		}
		contexts = null;
		isClosed = true;
	}

	public Consumer createConsumer(String messageSourceName,
			PostfachArt artDesPostfaches) throws MessagingException {
		
		JMSConsumer consumer;
		try {
			consumer = new JMSConsumer(environmentUniqueClientId, messageSourceName, artDesPostfaches, sessions);
		} catch (JMSException e) {
			throw new MessagingException("JMSException during creation of JMSConsumer",e);
		}
		
		return consumer;
	}

	public Producer createProducer(String messageDestinationName,
			PostfachArt artDesPostfaches) throws MessagingException {
		
		JMSProducer producer;
		try {
			producer = new JMSProducer(messageDestinationName, artDesPostfaches, sessions);
		} catch (JMSException e) {
			throw new MessagingException("JMSException during creation of JMSProducer",e);
		}
		
		return producer;
	}

	public boolean isClosed() {
		return isClosed;
	}

}
