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

package org.csstudio.alarm.table.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.csstudio.alarm.table.dataModel.AlarmMessageList;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.jms.JmsAlarmMessageReceiver;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.TopicSetColumnService;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreferenceConstants;
import org.csstudio.alarm.table.ui.messagetable.AlarmMessageTable;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.alarm.table.utility.Functions;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

/**
 * Add to the base class {@link LogView}: acknowledge button and combo box, send
 * method for jms acknowledge messages.
 * 
 * @see LogView
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class AlarmView extends LogView {

	public static final String ID = AlarmView.class.getName();

	private static final String SECURITY_ID = "operating"; //$NON-NLS-1$

	private Button soundEnableButton;

	/**
	 * Creates the view for the alarm log table.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		boolean canExecute = SecurityFacade.getInstance().canExecute(
				SECURITY_ID, true);
		_parent = parent;

		// Read column names and JMS topic settings from preferences
		_topicSetColumnService = new TopicSetColumnService(
				AlarmViewPreferenceConstants.TOPIC_SET,
				AlarmViewPreferenceConstants.P_STRINGAlarm);
		// is there already a topicSet from previous session?
		if (_currentTopicSet == null) {
			_currentTopicSet = _topicSetColumnService.getDefaultTopicSet();
		}

		// Create UI
		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		_parent.setLayout(grid);
		Composite logTableManagementComposite = new Composite(_parent, SWT.NONE);

		RowLayout layout = new RowLayout();
		layout.type = SWT.HORIZONTAL;
		layout.spacing = 15;
		logTableManagementComposite.setLayout(layout);

		addJmsTopicItems(logTableManagementComposite);
		addAcknowledgeItems(canExecute, logTableManagementComposite);
		addSoundButton(logTableManagementComposite);
		addRunningSinceGroup(logTableManagementComposite);
		_topicSetColumnService = new TopicSetColumnService(
				AlarmViewPreferenceConstants.TOPIC_SET,
				AlarmViewPreferenceConstants.P_STRINGAlarm);

		_jmsMessageReceiver = new JmsAlarmMessageReceiver();
		initializeMessageTable();

		// makeActions();
		//
		// parent.pack();

		// _propertyChangeListener = new ColumnPropertyChangeListener(
		// AlarmViewPreferenceConstants.P_STRINGAlarm, _tableViewer);
		//
		// JmsLogsPlugin.getDefault().getPluginPreferences()
		// .addPropertyChangeListener(_propertyChangeListener);

	}

	/**
	 * Initialization of {@link MessageTable} with {@link TableViewer}, column
	 * names etc for startup of this view. If the user selects another topic set
	 * this method is also executed and the previous table will be disposed.
	 * 
	 * @param parent
	 * @param _columnNames
	 */
	void initializeMessageTable() {

		// Initialize JMS message list
		if (_columnMapping != null) {
			_columnMapping.saveColumn(
					AlarmViewPreferenceConstants.P_STRINGAlarm,
					AlarmViewPreferenceConstants.TOPIC_SET);
			_columnMapping = null;
		}
		_topicSetColumnService = new TopicSetColumnService(
				AlarmViewPreferenceConstants.TOPIC_SET,
				AlarmViewPreferenceConstants.P_STRINGAlarm);
		// is there already a MessageTable delete it and the message list.
		if (_messageTable != null) {
			_messageTable.disposeMessageTable();
			_tableViewer = null;
			_messageTable = null;
			_messageList = null;
		}
		if (_tableComposite != null) {
			_tableComposite.dispose();
			_tableComposite = null;
		}

		_tableComposite = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		_tableComposite.setLayoutData(gridData);
		GridLayout grid2 = new GridLayout();
		grid2.numColumns = 1;
		_tableComposite.setLayout(grid2);
		_tableViewer = new TableViewer(_tableComposite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.CHECK);

		// get the font for the selected topic set. If there was no font defined
		// in preferences set no font.
		Font font = _topicSetColumnService.getFont(_currentTopicSet);
		if (font != null) {
			_tableViewer.getTable().setFont(font);
		}

	
		GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		_tableViewer.getTable().setLayoutData(gridData2);
		_messageList = new AlarmMessageList();
		// setup message table with context menu etc.

		String[] columnSet = _topicSetColumnService
				.getColumnSet(_currentTopicSet);
		String[] columnSetWithAck = new String[columnSet.length + 1];
		columnSetWithAck[0] = "ACK,25";
		for (int i = 0; i < columnSet.length; i++) {
			columnSetWithAck[i + 1] = columnSet[i];
		}
		_messageTable = new AlarmMessageTable(_tableViewer, columnSetWithAck,
				_messageList);
		_jmsMessageReceiver.initializeJMSConnection(_topicSetColumnService
				.getJMSTopics(_currentTopicSet), _messageList);
		_messageTable.makeContextMenu(getSite());
		setCurrentTimeToRunningSince();

		_columnMapping = new AlarmExchangeableColumnWidthPreferenceMapping(_tableViewer,
				_currentTopicSet);
		addControlListenerToColumns(AlarmViewPreferenceConstants.P_STRINGAlarm,
				AlarmViewPreferenceConstants.TOPIC_SET);
		getSite().setSelectionProvider(_tableViewer);
		makeActions();

		_parent.layout();

	}

	private void addAcknowledgeItems(boolean canExecute,
			Composite logTableManagementComposite) {

		Group acknowledgeItemGroup = new Group(logTableManagementComposite,
				SWT.NONE);

		acknowledgeItemGroup.setText(Messages.AlarmView_acknowledgeTitle);

		RowLayout layout = new RowLayout();
		acknowledgeItemGroup.setLayout(layout);

		Button ackButton = new Button(acknowledgeItemGroup, SWT.PUSH);
		ackButton.setLayoutData(new RowData(60, 21));
		ackButton.setText(Messages.AlarmView_acknowledgeButton);
		ackButton.setEnabled(canExecute);
		final Combo ackCombo = new Combo(acknowledgeItemGroup, SWT.SINGLE);
		ackCombo.add(Messages.AlarmView_acknowledgeAllDropDown);
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

		ackButton.addSelectionListener(new SelectionListener() {

			/**
			 * Acknowledge button is pressed for all (selection 0) messages or
			 * messages with a special severity (selection 1-3).
			 * 
			 */
			public void widgetSelected(SelectionEvent e) {

				TableItem[] items = _tableViewer.getTable().getItems();
				AlarmMessage message = null;

				List<AlarmMessage> msgList = new ArrayList<AlarmMessage>();
				for (TableItem ti : items) {

					if (ti.getData() instanceof AlarmMessage) {
						message = (AlarmMessage) ti.getData();
						// ComboBox selection for all messages or for a special
						// severity
						if (ackCombo.getItem(ackCombo.getSelectionIndex())
								.equals(message.getProperty("SEVERITY")) //$NON-NLS-1$
								|| (ackCombo.getItem(ackCombo
										.getSelectionIndex())
										.equals(Messages.AlarmView_acknowledgeAllDropDown))) {
							// add the message only if it is not yet
							// acknowledged.
							if (message.isAcknowledged() == false) {
								msgList.add(message.copy(new AlarmMessage()));
							}
						}

					} else {
						JmsLogsPlugin.logInfo("unknown item type in table"); //$NON-NLS-1$
					}

				}
				CentralLogger.getInstance().debug(this,
						"Number of msg in list to send: " + msgList.size());
				CentralLogger.getInstance().debug(
						this,
						"Number of msg in table: "
								+ _tableViewer.getTable().getItemCount());

				SendAcknowledge sendAck = SendAcknowledge
						.newFromJMSMessage(msgList);
				sendAck.schedule();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void addSoundButton(Composite logTableManagementComposite) {
		Group soundButtonGroup = new Group(logTableManagementComposite,
				SWT.NONE);

		soundButtonGroup.setText(Messages.AlarmView_soundButtonTitle);

		RowLayout layout = new RowLayout();
		soundButtonGroup.setLayout(layout);

		soundEnableButton = new Button(soundButtonGroup, SWT.TOGGLE);
		soundEnableButton.setLayoutData(new RowData(60, 21));
		if (Functions.is_sound()) {
			soundEnableButton.setText(Messages.AlarmView_soundButtonEnable);
			soundEnableButton.setSelection(true);
		} else {
			soundEnableButton.setText(Messages.AlarmView_soundButtonDisable);
		}

		soundEnableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (Functions.is_sound()) {
					Functions.set_sound(false);
					((JmsAlarmMessageReceiver) _jmsMessageReceiver)
							.setPlayAlarmSound(false);
					AlarmView.this.soundEnableButton
							.setText(Messages.AlarmView_soundButtonDisable);
				} else {
					Functions.set_sound(true);
					((JmsAlarmMessageReceiver) _jmsMessageReceiver)
							.setPlayAlarmSound(true);
					AlarmView.this.soundEnableButton
							.setText(Messages.AlarmView_soundButtonEnable);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		_jmsMessageReceiver.stopJMSConnection();
		_jmsMessageReceiver = null;
//		_columnMapping.saveColumn(AlarmViewPreferenceConstants.P_STRINGAlarm,
//				AlarmViewPreferenceConstants.TOPIC_SET);
		_messageTable = null;
		// JmsLogsPlugin.getDefault().getPluginPreferences()
		// .removePropertyChangeListener(_propertyChangeListener);
		super.dispose();
	}
}
