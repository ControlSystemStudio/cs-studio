package org.csstudio.nams.service.messaging.impl.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

class ConsumerFactoryServiceImpl implements ConsumerFactoryService {

//	private Map<String, ConnectionDesciptor> connections = new HashMap<String, ConnectionDesciptor>();
	
	public Consumer createConsumer(String clientId, String messageSourceName,
			PostfachArt artDesPostfaches, String[] messageServerURLs)
			throws MessagingException {
		
//		List<MessageConsumer> messageConsumerList = new ArrayList<MessageConsumer>(messageServerURLs.length);
//		
//		ConnectionDesciptor desciptor = connections.get(clientId);
//		if (desciptor == null) {
//			desciptor = new ConnectionDesciptor();
//			desciptor.setClientId(clientId);
//			connections.put(clientId, desciptor);
//		}
//		
//		for (String url : messageServerURLs) {
//			if (desciptor.containsJmsEnvFor(url)) {
//				JmsEnvironment jmsEnv = desciptor.getJmsEnvFor(url);
//				MessageConsumer consumer = createMessageConsumer(jmsEnv);
//			} else {
//				// create new Session
//				try {
//					
//				} catch (Exception e) {
//					throw new MessagingException(e);
//				}
//			}
//		}
//		
//		
//		
//		return new JMSConsumer(sessions, clientId, messageSourceName, artDesPostfaches);
		
		try {
			return new JMSConsumer(clientId, messageSourceName,
					messageServerURLs, artDesPostfaches);
		} catch (NamingException e) {
			throw new MessagingException(e);
		} catch (JMSException e) {
			throw new MessagingException(e);
		}
		// TODO Exception handling
	}

	
//	private JmsEnvironment createJmsEnv(String url, ConnectionDesciptor desciptor) throws JMSException, NamingException {
//		Hashtable<String, String> properties = new Hashtable<String, String>();
//		properties.put(Context.INITIAL_CONTEXT_FACTORY,
//				"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//		properties.put(Context.PROVIDER_URL, url);
//
//		Context context = new InitialContext(properties);
//		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
//		Connection connection = factory.createConnection();
//		connection.setClientID(desciptor.getClientId());
//		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//
//		connection.start();
//
//		return new JmsEnvironment(context, connection, session);
//	}
//
//
//	private static class ConnectionDesciptor {
//
//		private String clientId;
//		private Map<String, JmsEnvironment> urlToEnv = new HashMap<String, JmsEnvironment>();
//		private Map<JMSConsumer, JmsEnvironment> consumerToEnv = new HashMap<JMSConsumer, JmsEnvironment>();
//
//		public void setClientId(String clientId) {
//			this.clientId = clientId;
//		}
//		
//		public String getClientId() {
//			return clientId;
//		}
//
//		public JmsEnvironment getJmsEnvFor(String url) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		public boolean containsJmsEnvFor(String url) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//		
//	}
//	
//	private static class JmsEnvironment {
//		private Context context;
//		private Connection connection;
//		private Session session;
//		
//		public JmsEnvironment(Context context, Connection connection,
//				Session session) {
//			this.context = context;
//			this.connection = connection;
//			this.session = session;
//		}
//
//		public Context getContext() {
//			return context;
//		}
//		
//		public Connection getConnection() {
//			return connection;
//		}
//		
//		public Session getSession() {
//			return session;
//		}
//	}
}
