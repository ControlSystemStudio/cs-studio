package org.csstudio.nams.service.messaging.impl.jms;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;

class JMSConsumer implements Consumer {

	private boolean isClosed = false;
	private WorkThread[] workers;
	private LinkedBlockingQueue<Message> messageQueue;

	public JMSConsumer(String clientId, String messageSourceName, PostfachArt art, Session[] sessions) throws JMSException {

		// TODO Schaufeln in BlockingQueue : Maximum size auf 1 oder 2,
		// damit nicht hunderte Nachrichten während eines updates gepufert
		// werden, das ablegen in der Queue blockiert, wenn diese voll ist.
		// Siehe java.util.concurrent.BlockingQueue.
		messageQueue = new LinkedBlockingQueue<Message>(1);

		workers = new WorkThread[sessions.length];
		
		try {
			for (int i = 0; i < sessions.length; i++) {
				workers[i] = new WorkThread(messageQueue, sessions[i], messageSourceName, clientId, art);
			}
		} catch (JMSException e) {
			close();
			throw e;
		}
	}

	public void close() {
		for (WorkThread worker : workers) {
			if (worker != null) {
				try {
					worker.close();
				} catch (JMSException e) {}
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
				Session session, String source, String clientId, PostfachArt art)
				throws JMSException {
			this.messageQueue = messageQueue;

			switch (art) {
			case QUEUE:
				Queue queue = session.createQueue(source);
				consumer = session.createConsumer(queue);

				break;
			case TOPIC:
				Topic topic = session.createTopic(source);
				// TODO ist durableSubscriber ok?
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
			this.interrupt(); // Keine Sorge, unbehandelte Nachricht wird
								// nicht acknowledged und kommt daher wieder.
		}

		@Override
		public void run() {
			while (arbeitFortsetzen) {
				try {
					Message message = consumer.receive();
					if (message != null) {
						messageQueue.put(message);
					}
					// Beachten das die connection geschlossen sein könnte
					// und auch auf das failover protokoll achten
				} catch (JMSException e) {
					// TODO exception handling
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO exception handling
					e.printStackTrace();
				}
				Thread.yield();
			}
		}
	}
}
