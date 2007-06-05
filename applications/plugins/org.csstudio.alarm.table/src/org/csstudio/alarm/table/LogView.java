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

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.csstudio.alarm.table.dataModel.JMSLogMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.csstudio.platform.libs.jms.MessageReceiver;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * View with table for all log messages from JMS. Creates the TableViewer
 * <code>JMSLogTableViewer</code>, hilds the model <code>JMSMessageList</code>
 * @author jhatje
 *
 */
public class LogView extends ViewPart implements MessageListener {

	public static final String ID = LogView.class.getName();

	public Shell parentShell = null;

	public JMSMessageList jmsml = null;

	public JMSLogTableViewer jlv = null;

	private MessageReceiver receiver1;
	private MessageReceiver receiver2;

	public String[] columnNames;

	public ColumnPropertyChangeListener cl;

	public void createPartControl(Composite parent) {
		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewerPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		jmsml = new JMSLogMessageList(columnNames);

		parentShell = parent.getShell();

		initializeJMSReceiver(parentShell,
				LogViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY,
				LogViewerPreferenceConstants.PRIMARY_URL,
				LogViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY,
				LogViewerPreferenceConstants.SECONDARY_URL,
				LogViewerPreferenceConstants.QUEUE);

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, true));

		jlv = new JMSLogTableViewer(parent, getSite(), columnNames, jmsml, 1,SWT.SINGLE | SWT.FULL_SELECTION);
		jlv.setAlarmSorting(false);
		parent.pack();
		
		cl = new ColumnPropertyChangeListener(
				LogViewerPreferenceConstants.P_STRING,
				jlv);
		
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(cl);
		
	}

	public void initializeJMSReceiver(Shell ps, String primCtxFactory, String primURL,
			String secCtxFactory, String secURL, String queue) {
		
		String[] queues = JmsLogsPlugin.getDefault().getPluginPreferences().getString(queue).split(","); //$NON-NLS-1$
		try {
			receiver1 = new MessageReceiver(
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(primCtxFactory),
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(primURL),
					queues
					);
			receiver1.startListener(this);
		} catch (Exception e) {
			JmsLogsPlugin.logException("can not create receiver", e);
			MessageBox box = new MessageBox(ps, SWT.ICON_ERROR);
			box.setText("Failed to initialise primary JMS Context"); //$NON-NLS-1$
			box.setMessage(e.getMessage());
			// FIXME: This deadlocks the system if it happens during startup
//			box.open();
		}
		try{
			receiver2 = new MessageReceiver(
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(secCtxFactory),
					JmsLogsPlugin.getDefault().getPluginPreferences().getString(secURL),
					queues);
			receiver2.startListener(this);
		} catch (Exception e) {
			JmsLogsPlugin.logException("can not create receiver", e);
			MessageBox box = new MessageBox(ps, SWT.ICON_ERROR);
			box.setText("Failed to initialise secondary JMS Context"); //$NON-NLS-1$
			box.setMessage(e.getMessage());
			// FIXME: This deadlocks the system if it happens during startup
//			box.open();
		}

	}

	public void setFocus() {
	}

	/**
	 * MessageListener implementation
	 */
	public void onMessage(final Message message) {
		if (message == null) {
			JmsLogsPlugin.logError("Message == null");
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (message instanceof TextMessage) {
						JmsLogsPlugin.logError("received message is not a map message");
					} else if (message instanceof MapMessage) {
                        MapMessage mm = (MapMessage) message;
                        JmsLogsPlugin.logInfo("message received");
                        if(mm.getString("ACK")!=null &&  mm.getString("ACK").toUpperCase().equals("TRUE")){
                            setAck(mm);
                        } else {
                            jmsml.addJMSMessage(mm);
                        }
					} else {
						JmsLogsPlugin.logError("received message is an unknown type");
					}
				} catch (Exception e) {
                    e.printStackTrace();
					JmsLogsPlugin.logException("", e);
				}
			}
		});
	}

	/**
     * @param message
     */
    protected void setAck(MapMessage message) {
       TableItem[] items = jlv.getTable().getItems();
       for (TableItem item : items) {
           if (item.getData() instanceof JMSMessage) {
            JMSMessage jmsMessage = (JMSMessage) item.getData();
            try {
                if(jmsMessage.getName().equals(message.getString("NAME"))&&jmsMessage.getProperty("EVENTTIME").equals(message.getString("EVENTTIME"))){
                	jmsMessage.getHashMap().put("ACK","true");
                    jlv.refresh();
                    break;
                }
                
            } catch (JMSException e) {
                JmsLogsPlugin.logException("can not set ACK", e);
            }
            
        }
       }
        
    }

    /**
     * @param jmsMessage
     */
    void setAckTrue(JMSMessage jmsMessage) {
        jmsMessage.getHashMap().put("ACK","true");
        jlv.refresh();
    }

    public void dispose() {
		super.dispose();
		try {
			if (receiver1 != null)
				receiver1.stopListening();
		} catch (Exception e) {
			JmsLogsPlugin.logException("can not stop receiver", e);
		}
		try {
			if (receiver2 != null)
				receiver2.stopListening();
		} catch (Exception e) {
			JmsLogsPlugin.logException("can not stop receiver", e);
		}
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.removePropertyChangeListener(cl);
	}
}
