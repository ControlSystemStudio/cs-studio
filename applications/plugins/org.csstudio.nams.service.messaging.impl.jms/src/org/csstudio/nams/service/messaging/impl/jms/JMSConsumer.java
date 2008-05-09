package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;

class JMSConsumer implements Consumer {

	private Context[] contexts;
	private ConnectionFactory[] factorys;
	private Connection[] connections;
	private Session[] sessions;
	private boolean isClosed = false;
	private WorkThread[] workers;
	private LinkedBlockingQueue<Message> messageQueue;

	public JMSConsumer(String clientId, String messageSourceName,
			String[] messageServerURLs, PostfachArt art) throws NamingException, JMSException {

		messageQueue = new LinkedBlockingQueue<Message>();

		contexts = new Context[messageServerURLs.length];
		factorys = new ConnectionFactory[messageServerURLs.length];
		connections = new Connection[messageServerURLs.length];
		sessions = new Session[messageServerURLs.length];
		workers = new WorkThread[messageServerURLs.length];

		for (int i = 0; i < messageServerURLs.length; i++) {
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			properties.put(Context.PROVIDER_URL, messageServerURLs[i]);

			contexts[i] = new InitialContext(properties);
			factorys[i] = (ConnectionFactory) contexts[i]
					.lookup("ConnectionFactory");
			connections[i] = factorys[i].createConnection();
			connections[i].setClientID(clientId);
			sessions[i] = connections[i].createSession(false,
					Session.CLIENT_ACKNOWLEDGE);

			connections[i].start();

			workers[i] = new WorkThread(messageQueue, sessions[i],
					messageSourceName, clientId, art);
			workers[i].start();
		}
	}

	public void close() {
		for (WorkThread worker : workers) {
			try {
				worker.close();
			} catch (JMSException e) {
			}
		}

		for (Session session : sessions) {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException jmse) {
				}
			}

		}

		for (Connection connection : connections) {
			if (connection != null) {
				try {
					connection.stop();
				} catch (JMSException jmse) {
				}
			}
		}

		for (Context context : contexts) {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException ne) {
				}
			}
		}
		isClosed = true;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public NAMSMessage receiveMessage() {
		NAMSMessageJMSImpl namsMessage = null;
		try {
			Message message = messageQueue.take();
			
			// FIXME das sollte erst später gemacht werden.
			// am besten erst wenn die Nachricht fertig bearbeitet 
			// und im ausgangs Korb liegt
			message.acknowledge();
			namsMessage = new NAMSMessageJMSImpl(message);
		} catch (InterruptedException e) {
			// TODO exception handling
			// e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return namsMessage;
	}

	private static class WorkThread extends Thread {
		private final LinkedBlockingQueue<Message> messageQueue;
		private volatile boolean arbeitFortsetzen = true;
		private MessageConsumer consumer;

		public WorkThread(LinkedBlockingQueue<Message> messageQueue,
				Session session, String source, String clientId, PostfachArt art) throws JMSException {
			this.messageQueue = messageQueue;
			
			switch (art) {
			case QUEUE:
				Queue queue = session.createQueue(source);
				consumer = session.createConsumer(queue);
				
				break;
			case TOPIC:
				Topic topic = session.createTopic(source);
				consumer = session.createDurableSubscriber(topic, clientId);
				
				break;
			default:
				// TODO exception handling
				break;
			}
		}

		public void close() throws JMSException {
			arbeitFortsetzen = false;
			consumer.close();
		}

		@Override
		public void run() {
			while (arbeitFortsetzen) {
				try {
					Message message = consumer.receive();
					if (message != null) {
						messageQueue.add(message);
					}
				} catch (JMSException e) {
					// TODO exception handling
					e.printStackTrace();
				}
				Thread.yield();
			}
		}
	}
}
