package org.csstudio.alarm.treeView.jms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.csstudio.platform.libs.jms.MessageReceiver;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


/**
 * Subscribes to the alarm queue via JMS and updates the state of the alarm
 * tree.
 * 
 * @author Joerg Rathlev
 */
public class AlarmQueueSubscriber implements MessageListener {
	
	private static final String ALARM_TYPE = "ALARM";
	
	private static final CentralLogger log = CentralLogger.getInstance();

	private MessageReceiver receiver1;
	private MessageReceiver receiver2;
	private SubtreeNode tree;
	
	
	/**
	 * Creates a new alarm queue subscriber.
	 * @param tree the root node of the tree to which updates will be written.
	 */
	public AlarmQueueSubscriber(SubtreeNode tree) {
		if (tree == null)
			throw new NullPointerException("tree must not be null");
		
		this.tree = tree;
	}
	
	/**
	 * Sets the tree wo which updates will be applied.
	 * @param tree the root node of the tree.
	 */
	public void setTree(SubtreeNode tree) {
		this.tree = tree;
	}

	/**
	 * Connects to the JMS server.
	 */
	public void openConnection() {
		Preferences prefs = AlarmTreePlugin.getDefault().getPluginPreferences();
		String[] queues = prefs.getString(PreferenceConstants.JMS_QUEUE).split(",");
		try {
			receiver1 = new MessageReceiver(
					prefs.getString(PreferenceConstants.JMS_CONTEXT_FACTORY_PRIMARY),
					prefs.getString(PreferenceConstants.JMS_URL_PRIMARY),
					queues);
			receiver1.startListener(this);
		} catch (Exception e) {
			log.error(this, "Error initializing JMS listener for primary server.", e);
		}
		
		try {
			receiver2 = new MessageReceiver(
					prefs.getString(PreferenceConstants.JMS_CONTEXT_FACTORY_SECONDARY),
					prefs.getString(PreferenceConstants.JMS_URL_SECONDARY),
					queues);
			receiver2.startListener(this);
		} catch (Exception e) {
			log.error(this, "Error initializing JMS listener for secondary server.", e);
		}
	}

	/**
	 * Disconnects from the JMS server.
	 */
	public void closeConnection() {
		try {
			if (receiver1 != null)
				receiver1.stopListening();
		} catch (Exception e) {
			log.warn(this, "Error stopping primary JMS listener", e);
		}
		
		try {
			if (receiver2 != null)
				receiver2.stopListening();
		} catch (Exception e) {
			log.warn(this, "Error stopping secondary JMS listener", e);
		}
	}

	/**
	 * Called when a message is received via JMS.
	 * @param message the message.
	 */
	public void onMessage(final Message message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (message instanceof MapMessage) {
					processMessage((MapMessage) message);
				}
			}
		});
	}
	
	/**
	 * Updates the alarm tree based on the specified message.
	 * @param message an alarm message.
	 */
	private synchronized void processMessage(MapMessage message) {
		try {
			if (isAlarmMessage(message)) {
				String name = message.getString("NAME");
				Severity severity = new Severity(message.getString("SEVERITY"));
				ProcessVariableNode node = tree.findProcessVariableNode(name);
				if (node != null) {
					if (severity.isAlarm()) {
						Alarm alarm = new Alarm(name, severity);
						node.triggerAlarm(alarm);
					} else {
						node.cancelAlarm();
					}
				}
			}
			message.acknowledge();
		} catch (JMSException e) {
			log.error(this, "Error processing JMS message", e);
		}
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(AlarmTreeView.getID());
		if (view instanceof AlarmTreeView){
			((AlarmTreeView)view).refresh();
		}
	}

	/**
	 * Returns whether the specified message is an alarm message.
	 * @param msg the message.
	 */
	private boolean isAlarmMessage(MapMessage msg) {
		try {
			return ALARM_TYPE.equalsIgnoreCase(msg.getString("TYPE"));
		} catch (JMSException e) {
			return false;
		}
	}
}
