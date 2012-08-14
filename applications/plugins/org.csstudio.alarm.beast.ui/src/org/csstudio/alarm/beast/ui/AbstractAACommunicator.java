package org.csstudio.alarm.beast.ui;

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
import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.logging.JMSLogMessage;

public abstract class AbstractAACommunicator extends JMSCommunicationWorkQueueThread
{
	/** Alarm tree root (config) name */
    final private String root_name;
	
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
     *  and listens to 'notifier' topic messages
     *  @param notifier Alarm notifier
     *  @param work_queue
     *  @param root_name 
     */
	public AbstractAACommunicator(final String root_name) {
		super(Preferences.getJMS_URL());
		this.root_name = root_name;
	}

	// JMSCommunicationThread
	@Override
	protected void createProducersAndConsumers() throws Exception {
		producer = createProducer(Preferences.getJMS_AlarmNotifierExeTopic(root_name));
		consumer = createConsumer(Preferences.getJMS_AlarmNotifierRtnTopic(root_name));
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
	
	public void sendAutomatedAction(AlarmTreeItem item, AADataStructure auto_action) {
		MapMessage msg = null;
		try {
			msg = createMessage(item, auto_action);
			producer.send(msg);
		} catch (Exception e) {
			handleError(e);
		}
	}
	
	protected abstract void handleError(Exception e); 

	/**
	 * Create message initialized to execute an automated action
	 * @return MapMessage
	 * @throws Exception on error.
	 */
	private MapMessage createMessage(AlarmTreeItem item, AADataStructure auto_action)
			throws Exception {
		final MapMessage map = createMapMessage();
		map.setString(JMSLogMessage.TYPE, JMSNotifierMessage.TYPE_NOTIFIER_EXE);
		map.setString(JMSNotifierMessage.ITEM_ID, String.valueOf(item.getID()));
		map.setString(JMSNotifierMessage.ITEM_NAME, item.getName());
		map.setString(JMSNotifierMessage.ITEM_PATH, item.getPathName());
		map.setString(JMSNotifierMessage.AA_TITLE, auto_action.getTitle());
		map.setString(JMSNotifierMessage.EVENTTIME, date_format.format(Calendar.getInstance().getTime()));
        map.setString(JMSLogMessage.APPLICATION_ID, Activator.ID);
		return map;
	}
	
	/** Handle messages received from alarm clients. */
	private void handleMapMessage(final MapMessage message) {
		try {
			final String type = message.getString(JMSLogMessage.TYPE);
			// Automated action returns
			if (JMSNotifierMessage.TYPE_NOTIFIER_RTN.equals(type)) {
				// handleNotifierResponse("");
			}
		} catch (Throwable ex) {
			Activator.getLogger()
					.log(Level.SEVERE, "Message handler error", ex);
		}
	}
	
	protected abstract void handleNotifierResponse(String txt);
	
}
