package org.csstudio.alarm.table;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;


/**
 * Simple view more like console, used to write log messages
 */
public class LogView extends ViewPart implements MessageListener {

	public static final String ID = LogView.class.getName();

	private Shell parentShell = null;

	private JMSMessageList jmsml = null;
	
	private MessageReceiver receiver;

	private String[] columnNames;

//	int max;
//	int rows;


	public void createPartControl(Composite parent) {
		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewerPreferenceConstants.P_STRING).split(";");
		jmsml = new JMSMessageList(columnNames);

		parentShell = parent.getShell();

		initializeJMSReceiver(parentShell);

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, true));

		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewerPreferenceConstants.P_STRING).split(";");

		JMSLogTableViewer jlv = new JMSLogTableViewer(parent, getSite(),
				columnNames, jmsml);
		jlv.setAlarmSorting(false);
		parent.pack();

	}

	private void initializeJMSReceiver(Shell ps) {
		try {
			receiver = new MessageReceiver();
			receiver.startListener(this);
		} catch (Exception e) {
			MessageBox box = new MessageBox(ps, SWT.ICON_ERROR);
			box.setText("Failed to initialise JMS Context");
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
		System.out.println("in on message");
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					System.out.println("in runmethod");
					if (message instanceof TextMessage) {
						System.out
								.println("received message is not a map message: "
										+ ((TextMessage) message).getText());
					} else if (message instanceof MapMessage) {
						// if(table.getItemCount() >= max)
						// table.remove(table.getItemCount() - 1 - rows,
						// table.getItemCount() - 1);
						System.out.println("message received");
						jmsml.addJMSMessage((MapMessage) message);
					} else {
						System.out
								.println("received message is unhandled type: "
										+ message.getJMSType());
					}
				} catch (Exception e) {
					System.out.println("error");
					System.err.println(e);
					e.printStackTrace();
				}
			}
		});
	}

	public void dispose() {
		super.dispose();
		try {
			if (receiver != null)
				receiver.stopListening();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
