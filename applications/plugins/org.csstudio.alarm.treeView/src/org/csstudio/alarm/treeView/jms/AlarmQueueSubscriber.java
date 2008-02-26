/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
	
//	private static final String ALARM_TYPE = "ALARM";
	
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
			if (isAlarmAcknowledgement(message)) {
				String name = message.getString("NAME");
				ProcessVariableNode node = tree.findProcessVariableNode(name);
				if (node != null) {
					node.removeHighestUnacknowledgedAlarm();
				}
			} else if (isAlarmMessage(message)) {
				String name = message.getString("NAME");
				Severity severity = Severity.parseSeverity(message.getString("SEVERITY"));
				ProcessVariableNode node = tree.findProcessVariableNode(name);
				if (node != null) {
					if (severity.isAlarm()) {
						Alarm alarm = new Alarm(name, severity);
						node.setActiveAlarm(alarm);
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
		if (view instanceof AlarmTreeView) {
			((AlarmTreeView)view).refresh();
		}
	}

	private boolean isAlarmAcknowledgement(MapMessage message) {
		try {
			String ack = message.getString("ACK");
			return ack != null && ack.equals("TRUE");
		} catch (JMSException e) {
			return false;
		}
	}

	/**
	 * Returns whether the specified message is an alarm message.
	 * @param msg the message.
	 */
	private boolean isAlarmMessage(MapMessage msg) {
		// We currently assume that all received mesages are alarm messages
		return true;
//		try {
//			return ALARM_TYPE.equalsIgnoreCase(msg.getString("TYPE"));
//		} catch (JMSException e) {
//			return false;
//		}
	}
}
