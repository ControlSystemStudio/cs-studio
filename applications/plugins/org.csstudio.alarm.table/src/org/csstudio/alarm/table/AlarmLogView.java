/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.alarm.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.table.dataModel.JMSAlarmMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;

/**
 * Add to the base class {@code LogView}: - the acknowledge button and combo
 * box - the send method for jms acknowledge messages - the rule for receiving a
 * new acknowledge message
 * 
 * @see LogView
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class AlarmLogView extends LogView {

	public static final String ID = AlarmLogView.class.getName();

	private Timer timer = new Timer();

	private final Vector<JMSMessage> _jmsMessagesToRemove = new Vector<JMSMessage>();

	private boolean working = false;

	private RemoveAcknowledgedMessagesTask _removeMessageTask;

	/**
	 * Creates the view for the alarm log table.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		// in alarm table the 'ack' column must be the first one!
		String preferenceColumnString = JmsLogsPlugin.getDefault()
				.getPluginPreferences().getString(
						AlarmViewerPreferenceConstants.P_STRINGAlarm);

		preferenceColumnString = "Ack,25;" + preferenceColumnString; //$NON-NLS-1$

		// read the column names from the preference page
		columnNames = preferenceColumnString.split(";"); //$NON-NLS-1$

		// create the table model
		_messageList = new JMSAlarmMessageList(columnNames);

		parentShell = parent.getShell();

		initializeJMSReceiver(
				parentShell,
				AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY,
				AlarmViewerPreferenceConstants.PRIMARY_URL,
				AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY,
				AlarmViewerPreferenceConstants.SECONDARY_URL,
				AlarmViewerPreferenceConstants.QUEUE);

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);

		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, false));

		Button ackButton = new Button(comp, SWT.PUSH);
		ackButton.setText("Acknowledge");
		ackButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		final Combo ackCombo = new Combo(comp, SWT.SINGLE);
		ackCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				2, 1));
		ackCombo.add("ALL");
		IPreferenceStore prefs = JmsLogsPlugin.getDefault()
				.getPreferenceStore();
		if (prefs.getString(JmsLogPreferenceConstants.VALUE0).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE0));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE1).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE1));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE2).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE2));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE3).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE3));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE4).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE4));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE5).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE5));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE6).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE6));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE7).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE7));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE8).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE8));
		if (prefs.getString(JmsLogPreferenceConstants.VALUE9).trim().length() > 0)
			ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE9));
		ackCombo.select(4);

		GregorianCalendar currentTime = new GregorianCalendar(TimeZone
				.getTimeZone("ECT"));
		SimpleDateFormat formater = new SimpleDateFormat();
		Label runningSinceLabel = new Label(comp, SWT.NONE);
		runningSinceLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		runningSinceLabel.setText("Running Since: "
				+ formater.format(currentTime.getTime()));

		ackButton.addSelectionListener(new SelectionListener() {

			/**
			 * Acknowledge button is pressed for all (selection 0) messages or
			 * messages with a special severity (selection 1-3).
			 * 
			 */
			public void widgetSelected(SelectionEvent e) {

				TableItem[] items = _tableViewer.getTable().getItems();
				JMSMessage message = null;

				List<JMSMessage> msgList = new ArrayList<JMSMessage>();
				for (TableItem ti : items) {

					if (ti.getData() instanceof JMSMessage) {
						message = (JMSMessage) ti.getData();
						// ComboBox selection for all messages or for a special
						// severity
						if (ackCombo.getItem(ackCombo.getSelectionIndex())
								.equals(message.getProperty("SEVERITY"))
								|| (ackCombo.getSelectionIndex() == 0)) {
							// add the message only if it is not yet
							// acknowledged.
							if (message.is_ackknowledgement() == false) {
								msgList.add(message);
							}
						}

					} else {
						JmsLogsPlugin.logInfo("unknown item type in table");
					}

				}
				SendAcknowledge sendAck = SendAcknowledge
						.newFromJMSMessage(msgList);
				sendAck.schedule();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// create jface table viewer with paramter 2 for alarm table version
		_tableViewer = new JMSLogTableViewer(parent, getSite(), columnNames, _messageList, 2,
				SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);

		_tableViewer.setAlarmSorting(true);
		makeActions();
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
		getSite().setSelectionProvider(_tableViewer);

		parent.pack();

		cl = new ColumnPropertyChangeListener(
				AlarmViewerPreferenceConstants.P_STRINGAlarm, _tableViewer);

		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(cl);

	}

	/**
	 * Override the rule in the log table if a message is acknowledged.
	 * 
	 * @param message
	 * @throws JMSException
	 */
	@Override
	protected void setAck(final MapMessage message) {

//		Display.getDefault().asyncExec(new Runnable() {
//		public void run() {
			try {
				JmsLogsPlugin.logInfo("AlarmLogView Ack message received, MsgName: "
						+ message.getString("NAME") + " MsgTime: "
						+ message.getString("EVENTTIME"));
			
			} catch (Exception e) {
                e.printStackTrace();
				JmsLogsPlugin.logException("", e); //$NON-NLS-1$
			}
//		}
//		});
//		
					TableItem[] items = _tableViewer.getTable().getItems();

					for (final TableItem item : items) {
			if (item.getData() instanceof JMSMessage) {
				final JMSMessage jmsMessage = (JMSMessage) item.getData();
				try {
					if (jmsMessage.getName().equals(message.getString("NAME"))
							&& jmsMessage.getProperty("EVENTTIME").equals(
									message.getString("EVENTTIME"))) {
						if ((jmsMessage.isBackgroundColorGray() == true)
								|| (jmsMessage.getProperty("SEVERITY_KEY")
										.equalsIgnoreCase("NO_ALARM"))
								|| (jmsMessage.getProperty("SEVERITY_KEY")
										.equalsIgnoreCase("INVALID"))) {
							_jmsMessagesToRemove.add(jmsMessage);
							CentralLogger.getInstance().debug(
									this,
									"add message, removelist size: "
											+ _jmsMessagesToRemove.size());
							if ((_removeMessageTask == null)
									|| (_removeMessageTask.getState() == Job.NONE)) {
								CentralLogger.getInstance().debug(this, "Create new 'RemoveAckMessage Task'");
								_removeMessageTask = null;
								_removeMessageTask = new RemoveAcknowledgedMessagesTask(
										_messageList, _jmsMessagesToRemove);
						        _removeMessageTask.schedule();
							}
						} else {
//							Display.getDefault().asyncExec(new Runnable() {
//								public void run() {
//									try {
//
							item.setChecked(true);
							jmsMessage.getHashMap().put("ACK_HIDDEN", "true");
							jmsMessage.set_ackknowledgement(true);
//									} catch (Exception e) {
//					                    e.printStackTrace();
//										JmsLogsPlugin.logException("", e); //$NON-NLS-1$
//									}
//								}
//								});

						}
						break;
					}

				} catch (JMSException e) {
					JmsLogsPlugin.logException("can not set ACK", e);
				}
			}
		}

	}

	// private synchronized void removeMessages() {
	// CentralLogger.getInstance().debug(this, "XXXXXXXXXXXXX in removeMessage
	// start, removelist size: " + jmsMessagesToRemove.size());
	// JMSMessage[] a = new JMSMessage[1];
	// a = jmsMessagesToRemove.toArray(a);
	// jmsml.removeJMSMessage(a);
	// for (JMSMessage message : a) {
	// jmsMessagesToRemove.remove(message);
	// }
	// // jmsMessagesToRemove.clear();
	// CentralLogger.getInstance().debug(this, "XXXXXXXXXXXXX in removeMessage
	// end, removelist size: " + jmsMessagesToRemove.size());
	// }

	public void saveColumn() {
		/**
		 * When dispose store the width for each column, excepting the first
		 * column (ACK).
		 */
		int[] width = _tableViewer.getColumnWidth();
		String newPreferenceColumnString = ""; //$NON-NLS-1$
		String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm).split(
						";"); //$NON-NLS-1$
		/**
		 * The "+1" is need for the column Ack. The column Ack is not at the
		 * preferences and the ackcolumn is ever the first column.
		 */
		if (width.length != columns.length + 1) {
			return;
		}
		for (int i = 0; i < columns.length; i++) {
			/** +width[i+1]: see above */
			newPreferenceColumnString = newPreferenceColumnString
					.concat(columns[i].split(",")[0] + "," + width[i + 1] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		newPreferenceColumnString = newPreferenceColumnString.substring(0,
				newPreferenceColumnString.length() - 1);
		IPreferenceStore store = JmsLogsPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(AlarmViewerPreferenceConstants.P_STRINGAlarm,
				newPreferenceColumnString);
		if (store.needsSaving()) {
			JmsLogsPlugin.getDefault().savePluginPreferences();
		}

	}
}
