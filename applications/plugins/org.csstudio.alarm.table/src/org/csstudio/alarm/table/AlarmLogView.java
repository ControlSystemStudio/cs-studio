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

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.csstudio.alarm.table.dataModel.JMSAlarmMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.csstudio.platform.libs.jms.*;

import org.eclipse.ui.part.ViewPart;


/**
 * Simple view more like console, used to write log messages
 */
public class AlarmLogView extends ViewPart implements MessageListener {

	public static final String ID = AlarmLogView.class.getName();

	private Shell parentShell = null;

	private JMSMessageList jmsml = null;

	private JMSLogTableViewer jlv = null;

	private MessageReceiver receiver1;
	private MessageReceiver receiver2;

	private String[] columnNames;

	// int max;
	// int rows;

	public void createPartControl(Composite parent) {

		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm).split(
						";"); //$NON-NLS-1$
		jmsml = new JMSAlarmMessageList(columnNames);

		parentShell = parent.getShell();

		initializeJMSReceiver(parentShell);

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, true));

		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm).split(
						";"); //$NON-NLS-1$

		jlv = new JMSLogTableViewer(parent, getSite(), columnNames, jmsml, 2);
		jlv.setAlarmSorting(true);
		parent.pack();
		JmsLogsPlugin.getDefault().getPluginPreferences()
		.addPropertyChangeListener(propertyChangeListener);

		//TEstData
//		for (int i = 0; i < 10; i++) {
//			try {
//				MapMessage mm = new MapMessageImpl();
//				mm.setString("NAME", "pv name");
//				mm.setString("SEVERITY", "MINOR");
//				jmsml.addJMSMessage(mm);
//				mm.setString("NAME", "pv name X" + i);
//				mm.setString("SEVERITY", "MAJOR");
//				jmsml.addJMSMessage(mm);
//				mm.setString("NAME", "pv name X" + i);
//				mm.setString("SEVERITY", "MINOR");
//				jmsml.addJMSMessage(mm);
//				mm.setString("NAME", "pv name xx");
//				mm.setString("SEVERITY", "MINOR");
//				jmsml.addJMSMessage(mm);
//				mm.setString("NAME", "pv name Xx" + i);
//				mm.setString("SEVERITY", "NO_ALARM");
//				jmsml.addJMSMessage(mm);
//				mm.setString("NAME", "pv name Xy" + i);
//				mm.setString("SEVERITY", "MAJOR");
//				jmsml.addJMSMessage(mm);
//
//			} catch (JMSException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.out.println(e.getMessage());
//			}
//		}
		
		
	}

	/**
	 * 
	 * @param ps
	 */
	private void initializeJMSReceiver(Shell ps) {
		String[] queues = JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.QUEUE).split(",");
		try {
			receiver1 = new MessageReceiver(
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY),
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.PRIMARY_URL),
					queues);
			receiver1.startListener(this);
		} catch (Exception e) {
			MessageBox box = new MessageBox(ps, SWT.ICON_ERROR);
			box.setText("Failed to initialise primary JMS Context"); //$NON-NLS-1$
			box.setMessage(e.getMessage());
			box.open();
		}
		try{
			receiver2 = new MessageReceiver(
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY),
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.SECONDARY_URL),
					queues);
			receiver2.startListener(this);

		} catch (Exception e) {
			MessageBox box = new MessageBox(ps, SWT.ICON_ERROR);
			box.setText("Failed to initialise secondary JMS Context"); //$NON-NLS-1$
			box.setMessage(e.getMessage());
			box.open();
		}

	}

	public void setFocus() {
	}

	/**
	 * MessageListener implementation
	 */
	public void onMessage(final Message message) {
		if (message == null) {
			System.out.println("message gleich null"); //$NON-NLS-1$
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (message instanceof TextMessage) {
					} else if (message instanceof MapMessage) {
						MapMessage mm = (MapMessage) message;
						//
						// We only connect to alarm messages - so we'll display everything that shows up here
						// Matthias 05-04-2007
						//if (mm.getString("TYPE").equalsIgnoreCase("Alarm")) { //$NON-NLS-1$ //$NON-NLS-2$
						//jmsml.addJMSMessage((MapMessage) message);
						//
						//}
						jmsml.addJMSMessage((MapMessage) message);
					} else {
						System.out
								.println("received message is unhandled type: " //$NON-NLS-1$
										+ message.getJMSType());
					}
				} catch (Exception e) {
					System.out.println("error"); //$NON-NLS-1$
					System.err.println(e);
					e.printStackTrace();
				}
			}
		});
	}

	public void dispose() {
		super.dispose();
		try {
			if (receiver1 != null)
				receiver1.stopListening();
		} catch (Exception e) {
			System.err.println(e);
		}
		try {
			if (receiver2 != null)
				receiver2.stopListening();
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	private final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			System.out.println("AlarmLog  ChangeListener"); //$NON-NLS-1$

			columnNames = JmsLogsPlugin
					.getDefault()
					.getPluginPreferences()
					.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm)
					.split(";"); //$NON-NLS-1$
			jlv.setColumnNames(columnNames);

			Table t = jlv.getTable();
			TableColumn[] tc = t.getColumns();

			int diff = columnNames.length - tc.length;

			if (diff > 0) {
				for (int i = 0; i < diff; i++) {
					TableColumn tableColumn = new TableColumn(t, SWT.CENTER);
					tableColumn.setText(new Integer(i).toString());
					tableColumn.setWidth(100);
				}
			} else if (diff < 0) {
				diff = (-1) * diff;
				for (int i = 0; i < diff; i++) {
					tc[i].dispose();
				}
			}
			tc = t.getColumns();

			for (int i = 0; i < tc.length; i++) {
				tc[i].setText(columnNames[i]);
			}
			jlv.refresh();
		}
	};
}
