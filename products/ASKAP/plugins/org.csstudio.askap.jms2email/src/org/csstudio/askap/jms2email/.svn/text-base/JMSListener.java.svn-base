package org.csstudio.askap.jms2email;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ExceptionListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;

public class JMSListener extends Thread implements MessageListener,
		ExceptionListener {

	/**
	 * On JMS errors, thread will disconnect, wait, then re-connect using this
	 * delay.
	 */
	private static final int RETRY_DELAY_MS = 20000;

	private MessageHandler messageHandler = null;
	private boolean keepRunning = true;
	private boolean doWait = true;

	private String jmsUrl = "tcp://localhost:61616";
	private String jmsTopic;
	private String jmsFilters;

	
    /** Counter for received JMS messages */
    private int messageCount = 0;

    /** Last JMS Message */
    private MapMessage lastMessage = null;

    /** Last error message or <code>null</code> */
    private String lastError = "";

	
	public JMSListener(MessageHandler messageHandler, String jmsUrl,
			String jmsTopic, String jmsFilters) {
		this.messageHandler = messageHandler;

		this.jmsUrl = jmsUrl;
		this.jmsTopic = jmsTopic;
		this.jmsFilters = jmsFilters;
	}

	@Override
	public void onMessage(Message msg) {
		try {
			messageHandler.handleMessage(msg);
			messageCount++;
			lastMessage = (MapMessage) msg;
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING,
					"Could not email alarm message", e);
		}
	}

	/** Connect to JMS, handle messages */
	@Override
	public void run() {
		while (keepRunning) {
			Connection jmsConnection = null;
			try {
				jmsConnection = connectJMS();

				// Incoming JMS messages are handled in onMessage,
				// so nothing to do here but wait...
				synchronized (this) {
					doWait = true;
					// Check some condition to please FindBugs
					while (doWait)
						wait();
				}
			} catch (Exception ex) {
				Activator.getLogger()
						.log(Level.WARNING, "Log thread error", ex);
			} finally {
				// Stop JMS...
				if (jmsConnection != null) {
					try {
						jmsConnection.close();
					} catch (JMSException e) {
						Activator.getLogger().log(Level.WARNING,
								"JMS disconnect error", e);
					}
				}
			}
			// Did we wake up & close connections because of error
			// or because of requested shutdown?
			if (keepRunning) { // Error. Wait a little before trying again
				try {
					synchronized (this) {
						wait(RETRY_DELAY_MS);
					}
				} catch (InterruptedException ex) {
					// Ignore
					ex = null;
				}
			}
		}
	}

	/**
	 * Connect to JMS server
	 * 
	 * @return JMS Connection
	 * @throws JMSException
	 *             on error
	 */
	private Connection connectJMS() throws JMSException {
		final Connection connection = JMSConnectionFactory.connect(jmsUrl);
		connection.setExceptionListener(this);
		connection.start();
		final Session session = connection.createSession(/* transacted */false,
				Session.AUTO_ACKNOWLEDGE);
		// Subscribe to list of topics
		final String[] topicNames = jmsTopic.split(", *");
		for (String topicName : topicNames) {
			final Topic topic = session.createTopic(topicName);
			final MessageConsumer consumer = session.createConsumer(topic);
			consumer.setMessageListener(this);

			Activator.getLogger().log(Level.CONFIG,
					"Accepting messages for {0} at {1}",
					new Object[] { topicName, jmsUrl });
		}
		return connection;
	}

	/** Ask thread to stop. Does not block for thread to actually exit */
	public void cancel() {
		keepRunning = false;
		synchronized (this) {
			doWait = false;
			notifyAll();
		}
	}

	@Override
	public void onException(JMSException e) {
		Activator.getLogger().log(Level.WARNING, "JMS Exception", e);
	}

	public int getMessageCount() {
		return messageCount;
	}

	public MapMessage getLastMessage() {
		return lastMessage;
	}

	public String getLastError() {
		return lastError;
	}

}
