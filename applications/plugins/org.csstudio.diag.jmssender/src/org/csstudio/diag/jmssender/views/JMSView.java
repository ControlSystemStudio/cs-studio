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
package org.csstudio.diag.jmssender.views;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ListIterator;
import java.util.Vector;

import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.diag.jmssender.JmsSenderPlugin;
import org.csstudio.diag.jmssender.internationalization.Messages;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.ViewPart;

public class JMSView extends ViewPart implements IWorkbenchWindowActionDelegate {

	/**
	 * Listener class for the send buttons.
	 */
	private final class SendButtonSelectionListener extends SelectionAdapter {
		private final Text _topicField;
		private final Text _countField;
		private final SimpleDateFormat _jmsDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

		/**
		 * Creates the listener.
		 * 
		 * @param alarmTopic
		 *            the text field in which the topic is entered to which this
		 *            listener will send the JMS messages.
		 * @param alarmCount
		 *            the text field in which the number of messages is entered
		 *            that this listener will send.
		 */
		private SendButtonSelectionListener(Text alarmTopic, Text alarmCount) {
			_topicField = alarmTopic;
			_countField = alarmCount;
		}

		@Override
        public void widgetSelected(SelectionEvent ev) {
			SendMapMessage sender = new SendMapMessage();
			try {
				sender.startSender();
				MapMessage message = sender.getSessionMessageObject();
				PropertyList properties = messageTable.getPropertyList();
				Vector<Property> propertyList = properties.getProperties();
				ListIterator<Property> it = propertyList.listIterator();
				while (it.hasNext()) {
					Property p = (Property) it.next();
					message.setString(p.getProperty(), p.getValue());
				}
				int count;
				try {
					count = Integer.parseInt(_countField.getText());
				} catch (NumberFormatException e) {
					count = 1;
				}
				if (count == 1) {
				    message.setString("EVENTTIME", (_jmsDateFormat.format(Calendar
				            .getInstance().getTime())));
					sender.sendMessage(_topicField.getText());
				} else {
					for (int i = 0; i < count; i++) {
						String name = message.getString("NAME");
						String[] nameParts = name.split("_");
						name = nameParts[0] + "_" + i;
						message.setString("NAME", name);
						message.setString("EVENTTIME", (_jmsDateFormat.format(Calendar
								.getInstance().getTime())));
						sender.sendMessage(_topicField.getText());
					}
				}
			} catch (Exception e) {
				MessageDialog.openError(null, "JMS Sender",
						"Error sending JMS message: " + e.getMessage());
			} finally {
				try {
					sender.stopSender();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static final String ID = JMSView.class.getName();
	private static final String SECURITY_ID = "testing";

	private MessageTable messageTable;

	public JMSView() {
	}

	@Override
    public void createPartControl(Composite parent) {
//		final boolean canExecute = SecurityFacade.getInstance().canExecute(
//				SECURITY_ID, false);
		GridLayout grid = new GridLayout();
		grid.numColumns = 5;
		parent.setLayout(grid);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		Table table = new Table(parent, style);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1);
		table.setLayoutData(gridData);
		messageTable = new MessageTable(table);

		Button butAlarm = new Button(parent, SWT.PUSH);
		butAlarm.setText(Messages.JMSView_SendButton1);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		butAlarm.setLayoutData(gridData);
		butAlarm.setEnabled(true);

		Label alarmTopicLabel = new Label(parent, SWT.NONE);
		alarmTopicLabel.setText(Messages.JMSView_Labels);
		final Text alarmTopic = new Text(parent, SWT.BORDER);
		alarmTopic.setText("ALARM"); //$NON-NLS-1$
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 160;
		alarmTopic.setLayoutData(gridData);

		Label alarmCountLabel = new Label(parent, SWT.NONE);
		alarmCountLabel.setText(Messages.JMSView_Numbers);
		final Text alarmCount = new Text(parent, SWT.BORDER);
		alarmCount.setText("1"); //$NON-NLS-1$
		alarmCount.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (e.character == SWT.DEL || e.character == SWT.BS) {
					e.doit = true;
				} else {
					try {
						Integer.parseInt(e.text);
						e.doit = true;
					} catch (NumberFormatException nfe) {
						e.doit = false;
					}
				}
			}
		});
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		alarmCount.setLayoutData(gridData);

		Button butLog = new Button(parent, SWT.PUSH);
		butLog.setText(Messages.JMSView_SendButton2);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		butLog.setLayoutData(gridData);
		butLog.setEnabled(true);

		Label logTopicLabel = new Label(parent, SWT.NONE);
		logTopicLabel.setText(Messages.JMSView_Labels);
		final Text logTopic = new Text(parent, SWT.BORDER);
		logTopic.setText("LOG"); //$NON-NLS-1$
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 160;
		logTopic.setLayoutData(gridData);

		Label logCountLabel = new Label(parent, SWT.NONE);
		logCountLabel.setText(Messages.JMSView_Numbers);
		final Text logCount = new Text(parent, SWT.BORDER);
		logCount.setText("1"); //$NON-NLS-1$
		logCount.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (e.character == SWT.DEL || e.character == SWT.BS) {
					e.doit = true;
				} else {
					try {
						Integer.parseInt(e.text);
						e.doit = true;

					} catch (NumberFormatException nfe) {
						e.doit = false;
					}
				}
			}
		});
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		logCount.setLayoutData(gridData);

		butAlarm.addSelectionListener(new SendButtonSelectionListener(alarmTopic, alarmCount));
		butLog.addSelectionListener(new SendButtonSelectionListener(logTopic, logCount));
		JmsSenderPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(propertyChangeListener);

		parent.pack();
	}
	
	
	@Override
    public void setFocus() {
	}

	public void init(IWorkbenchWindow window) {

	}

	@Override
    public void dispose() {
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.removePropertyChangeListener(propertyChangeListener);
		super.dispose();
	}

	public void run(IAction action) {
		try {
			IWorkbenchWindow window = JmsSenderPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow();
			window.getActivePage().showView(
					"org.csstudio.jmsSender.views.JMSView"); //$NON-NLS-1$
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

	private final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			messageTable.init();
		}
	};
}
