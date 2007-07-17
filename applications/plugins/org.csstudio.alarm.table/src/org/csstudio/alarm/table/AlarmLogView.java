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

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.table.dataModel.JMSAlarmMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

/**
 * Add to the base class {@code LogView}:
 * - the acknowledge button and combo box
 * - the send method for jms acknowledge messages 
 * - the rule for receiving a new acknowledge message
 * 
 * @see LogView
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class AlarmLogView extends LogView {

	public static final String ID = AlarmLogView.class.getName();

	/**
	 * Creates the view for the alarm log table.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		//in alarm table the 'ack' column must be the first one!
		String preferenceColumnString = JmsLogsPlugin.getDefault().getPluginPreferences()
		.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm);
		
		preferenceColumnString = "Ack;" + preferenceColumnString;
		
		//read the column names from the preference page
		columnNames = preferenceColumnString.split(";");

		//create the table model 
		jmsml = new JMSAlarmMessageList(columnNames);

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
		comp.setLayout(new GridLayout(4, true));

		Button ackButton = new Button(comp, SWT.PUSH);
		ackButton.setText("Acknowledge");
		ackButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		final Combo ackCombo = new Combo(comp, SWT.SINGLE);
		ackCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,
				1));
		ackCombo.add("ALL");
		ackCombo.select(0);
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

		ackButton.addSelectionListener(new SelectionListener() {

			/**
			 * Acknowledge button is pressed for all (selection 0) 
			 * messages or messages with a special severity (selection
			 * 1-3). 
			 * 
			 */
			public void widgetSelected(SelectionEvent e) {

				TableItem[] items = jlv.getTable().getItems();
				JMSMessage message = null;

				List<JMSMessage> msgList = new ArrayList<JMSMessage>();
				for (TableItem ti : items) {

					if (ti.getData() instanceof JMSMessage) {
						message = (JMSMessage) ti.getData();
						if (ackCombo.getItem(ackCombo.getSelectionIndex())
								.equals(message.getProperty("SEVERITY"))
								|| (ackCombo.getSelectionIndex() == 0)) {
							msgList.add(message);
						}

					} else {
						JmsLogsPlugin.logInfo("unknown item type in table");
					}

				}
				SendAcknowledge sendAck = new SendAcknowledge(msgList);
				sendAck.schedule();

			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		//create jface table viewer with paramter 2 for alarm table version
		jlv = new JMSLogTableViewer(parent, getSite(), columnNames, jmsml, 2,
				SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);

		jlv.setAlarmSorting(true);

		parent.pack();

		cl = new ColumnPropertyChangeListener(
				AlarmViewerPreferenceConstants.P_STRINGAlarm, jlv);

		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(cl);

	}

	/**
	 * Override the rule in the log table if a message
	 * is acknowledged.
	 * 
	 * @param message
	 * @throws JMSException 
	 */
	@Override
	protected void setAck(MapMessage message) throws JMSException {
		JmsLogsPlugin.logInfo("AlarmLogView Ack message received, MsgName: "
				+ message.getString("NAME") + " MsgTime: "
				+ message.getString("EVENTTIME"));

		TableItem[] items = jlv.getTable().getItems();
		for (TableItem item : items) {
			if (item.getData() instanceof JMSMessage) {
				JMSMessage jmsMessage = (JMSMessage) item.getData();
				try {
					if (jmsMessage.getName().equals(message.getString("NAME"))
							&& jmsMessage.getProperty("EVENTTIME").equals(
									message.getString("EVENTTIME"))) {
						if ((jmsMessage.isBackgroundColorGray() == true)
								|| (jmsMessage.getProperty("SEVERITY_KEY")
										.equalsIgnoreCase("NO_ALARM"))
								|| (jmsMessage.getProperty("SEVERITY_KEY")
										.equalsIgnoreCase("INVALID"))) {
							jmsml.removeJMSMessage(jmsMessage);
							jlv.refresh();
						} else {
							item.setChecked(true);
							jmsMessage.getHashMap().put("ACK_HIDDEN", "true");
						}
						break;
					}

				} catch (JMSException e) {
					JmsLogsPlugin.logException("can not set ACK", e);
				}
			}
		}
	}

}
