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

import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.csstudio.alarm.table.dataModel.JMSLogMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.csstudio.platform.libs.jms.MessageReceiver;
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
import org.eclipse.ui.part.ViewPart;
import org.exolab.jms.message.MapMessageImpl;

/**
 * Simple view more like console, used to write log messages
 */
public class LogView extends ViewPart implements MessageListener {

	public static final String ID = LogView.class.getName();

	private Shell parentShell = null;

	private JMSMessageList jmsml = null;

	private JMSLogTableViewer jlv = null;

	private MessageReceiver receiver1;
	private MessageReceiver receiver2;

	private String[] columnNames;

	//	int max;
	//	int rows;

	public void createPartControl(Composite parent) {
		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewerPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		jmsml = new JMSLogMessageList(columnNames);

		parentShell = parent.getShell();

		initializeJMSReceiver(parentShell);

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, true));

		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewerPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$

		jlv = new JMSLogTableViewer(parent, getSite(), columnNames, jmsml, 1);
		jlv.setAlarmSorting(false);
		parent.pack();
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(propertyChangeListener);
		
		//TEstData
		for (int i = 0; i < 10; i++) {
			try {
				MapMessage mm = new MapMessageImpl();
				mm.setString("NAME", "pv name");
				mm.setString("SEVERITY", "MINOR");
				jmsml.addJMSMessage(mm);
				mm.setString("NAME", "pv name X" + i);
				mm.setString("SEVERITY", "MAJOR");
				jmsml.addJMSMessage(mm);
				mm.setString("NAME", "pv name X" + i);
				mm.setString("SEVERITY", "MINOR");
				jmsml.addJMSMessage(mm);
				mm.setString("NAME", "pv name xx");
				mm.setString("SEVERITY", "MINOR");
				jmsml.addJMSMessage(mm);
				mm.setString("NAME", "pv name Xx" + i);
				mm.setString("SEVERITY", "NO_ALARM");
				jmsml.addJMSMessage(mm);
				mm.setString("NAME", "pv name Xy" + i);
				mm.setString("SEVERITY", "MAJOR");
				jmsml.addJMSMessage(mm);

			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		
	}

	private void initializeJMSReceiver(Shell ps) {
		String[] queues = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogViewerPreferenceConstants.QUEUE).split(","); //$NON-NLS-1$
		try {
			receiver1 = new MessageReceiver(
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY),
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogViewerPreferenceConstants.PRIMARY_URL),
					queues
					);
			receiver1.startListener(this);
		} catch (Exception e) {
			MessageBox box = new MessageBox(ps, SWT.ICON_ERROR);
			box.setText(Messages.getString("LogView.error.JMS.1")); //$NON-NLS-1$
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
			box.setText(Messages.getString("LogView.error.JMS.2")); //$NON-NLS-1$
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
			System.out.println("message gleich null");
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (message instanceof TextMessage) {
						System.out
								.println("received message is not a map message: "
										+ ((TextMessage) message).getText());
					} else if (message instanceof MapMessage) {
						// if(table.getItemCount() >= max)
						// table.remove(table.getItemCount() - 1 - rows,
						// table.getItemCount() - 1);
						jmsml.addJMSMessage((MapMessage) message);
					} else {
						System.out
								.println(Messages.getString("LogView.error.JMS.type") //$NON-NLS-1$
										+ message.getJMSType());
					}
				} catch (Exception e) {
					System.out.println(Messages.getString("LogView.error.error.1")); //$NON-NLS-1$
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
			System.out.println(Messages.getString("LogView.12")); //$NON-NLS-1$

			columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
					.getString(LogViewerPreferenceConstants.P_STRING)
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
			Vector<JMSMessage> jmsMessages = new Vector<JMSMessage>();
			jmsMessages.addAll(jmsml.getJMSMessageList());
			jmsml.clearList();
			jlv.refresh();
			jmsml.addJMSMessageList(jmsMessages);
			jlv.refresh();
		}
	};

}
