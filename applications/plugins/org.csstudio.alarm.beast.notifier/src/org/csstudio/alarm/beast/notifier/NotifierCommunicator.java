package org.csstudio.alarm.beast.notifier;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;

import org.csstudio.alarm.beast.JMSCommunicationWorkQueueThread;
import org.csstudio.alarm.beast.JMSNotifierMessage;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.logging.JMSLogMessage;

/**
 * JMS communicator which handles Notifier messages from GUI.
 * (automated action execution from GUI)
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class NotifierCommunicator extends JMSCommunicationWorkQueueThread
{
    /** Server for which we communicate */
    final private AlarmNotifier notifier;

    /** Alarm tree root (config) name */
    final private String root_name;

    /** Host for messages */
    final private String host = InetAddress.getLocalHost().getHostName();

    /** User for messages. Updated with authenticated user */
    final private String user = System.getProperty("user.name"); //$NON-NLS-1$
    
    /** Format of time stamps */
	final private SimpleDateFormat date_format = new SimpleDateFormat(
			JMSLogMessage.DATE_FORMAT);

    // Note on synchronization:
    // Access to the producer is within the JMSCommunicationThread
    // that also creates and closes them,
    // so there is no need to synch' on them.

    /** Consumer for listening to the 'notifier' topic */
    private MessageConsumer consumer;
    
    /** Producer for sending to the 'notifier' topic */
    private MessageProducer producer;

    /** Initialize communicator that writes to the 'notifier' topic
     *  and listens to 'client' topic messages
     *  @param notifier Alarm notifier
     *  @param work_queue
     *  @param root_name 
     */
	public NotifierCommunicator(final AlarmNotifier notifier,
			final String root_name) throws Exception {
		super(Preferences.getJMS_URL());
		this.notifier = notifier;
		this.root_name = root_name;
	}

	// JMSCommunicationThread
	@Override
	protected void createProducersAndConsumers() throws Exception {
		producer = createProducer(Preferences.getJMS_AlarmNotifierRtnTopic(root_name));
		consumer = createConsumer(Preferences.getJMS_AlarmNotifierExeTopic(root_name));
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(final Message message) {
				if (message instanceof MapMessage) {
					handleMapMessage((MapMessage) message);
				} else {
					Activator.getLogger().log(Level.WARNING,
							"Message type {0} not handled",
							message.getClass().getName());
				}
			}
		});
	}

	// JMSCommunicationThread
	@Override
	protected void closeProducersAndConsumers() throws Exception {
		producer.close();
		producer = null;
		consumer.close();
		consumer = null;
	}

	/** Start the communicator */
	@Override
	public void start() {
		super.start();
	}

	/** Stop the communicator */
	@Override
	public void stop() {
		super.stop();
	}

	/**
	 * Create message initialized with basic notifier info
	 * @param text TEXT property
	 * @return MapMessage
	 * @throws Exception on error.
	 */
	@SuppressWarnings("unused")
	private MapMessage createMessage(final String text)
			throws Exception {
		final MapMessage map = createMapMessage();
		map.setString(JMSLogMessage.TYPE, JMSNotifierMessage.TYPE_NOTIFIER_RTN);
//		map.setString(JMSNotifierMessage.ITEM_ID, String.valueOf(item.getID()));
//		map.setString(JMSNotifierMessage.ITEM_NAME, item.getName());
//		map.setString(JMSNotifierMessage.ITEM_PATH, item.getPathName());
//		map.setString(JMSNotifierMessage.AA_TITLE, auto_action.getTitle());
		map.setString(JMSNotifierMessage.EVENTTIME, date_format.format(Calendar.getInstance().getTime()));
		map.setString(JMSLogMessage.TEXT, text);
        map.setString(JMSLogMessage.APPLICATION_ID, Application.APPLICATION_NAME);
        map.setString(JMSLogMessage.HOST, host);
        map.setString(JMSLogMessage.USER, user);
		return map;
	}
	
	/** Handle messages received from alarm clients. */
	private void handleMapMessage(final MapMessage message) {
		try {
			final String type = message.getString(JMSLogMessage.TYPE);
			// Automated action returns
			if (JMSNotifierMessage.TYPE_NOTIFIER_EXE.equals(type)) {
				notifier.handleManualExecution(GUIExecInfo
						.fromMapMessage(message));
			}
		} catch (Throwable ex) {
			Activator.getLogger()
					.log(Level.SEVERE, "Message handler error", ex);
		}
	}
	
}
