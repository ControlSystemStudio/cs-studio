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
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * View with table for all log messages from JMS. Creates the TableViewer
 * <code>JMSLogTableViewer</code>, holds the model <code>JMSMessageList</code>
 * @author jhatje
 *
 */
public class ViewLog extends ViewPart implements MessageListener {

	public static final String ID = ViewLog.class.getName();

	public Shell parentShell = null;

	public JMSMessageList _messageList = null;

	public JMSLogTableViewer _tableViewer = null;

	private MessageReceiver receiver1;
	private MessageReceiver receiver2;

	public String[] columnNames;

	public ColumnPropertyChangeListener cl;
	
	/**
	 * The Show Property View action.
	 */
	private Action _showPropertyViewAction;

	/**
	 * The ID of the property view.
	 */
	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	public void createPartControl(Composite parent) {
		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewerPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		_messageList = new JMSLogMessageList(columnNames);

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
		GregorianCalendar currentTime = new GregorianCalendar(TimeZone.getTimeZone("ECT"));
	    SimpleDateFormat formater = new SimpleDateFormat();
		Label runningSinceLabel = new Label(parent, SWT.NONE);
		runningSinceLabel.setText("Running Since: " + formater.format(currentTime.getTime()));
		
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, true));

		_tableViewer = new JMSLogTableViewer(parent, getSite(), columnNames, _messageList, 1,SWT.MULTI | SWT.FULL_SELECTION);
		_tableViewer.setAlarmSorting(false);
		parent.pack();
		
		getSite().setSelectionProvider(_tableViewer);

		makeActions();
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());

		
		cl = new ColumnPropertyChangeListener(
				LogViewerPreferenceConstants.P_STRING,
				_tableViewer);
		
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(cl);

	}
	
	/**
	 * Creates the actions offered by this view.
	 */
	void makeActions() {
		_showPropertyViewAction = new Action() {
			@Override
			public void run() {
				try {
					getSite().getPage().showView(PROPERTY_VIEW_ID);
				} catch (PartInitException e) {
					MessageDialog.openError(getSite().getShell(), "Alarm Tree",
							e.getMessage());
				}
			}
		};
		_showPropertyViewAction.setText("Properties");
		_showPropertyViewAction.setToolTipText("Show property view");
		
		IViewRegistry viewRegistry = getSite().getWorkbenchWindow().getWorkbench().getViewRegistry();
		IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
		_showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());
	}
	
	
	/**
	 * Adds the tool bar actions.
	 * @param manager the menu manager.
	 */
	void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_showPropertyViewAction);
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
//			 FIXME: This deadlocks the system if it happens during startup
			box.open();
		}

	}

	public void setFocus() {
	}

	/**
	 * MessageListener implementation
	 * 
	 * Receives the JMS message. If it is an ack 
	 */
	synchronized public void onMessage(final Message message) {
		if (message == null) {
			JmsLogsPlugin.logError("Message == null");
		}
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
				try {
					if (message instanceof TextMessage) {
						JmsLogsPlugin.logError("received message is not a map message");
					} else if (message instanceof MapMessage) {
						final MapMessage mm = (MapMessage) message;
                        CentralLogger.getInstance().debug(this, "received map message");
//	DEBUG					JmsLogsPlugin.logInfo("ViewLog message received, MsgName: " + 
//                        		mm.getString("NAME") + " Severity: " + mm.getString("SEVERITY") +
//                        		" MsgTime: " + mm.getString("EVENTTIME"));
						if(mm.getString("ACK")!=null &&  mm.getString("ACK").toUpperCase().equals("TRUE")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	                        CentralLogger.getInstance().debug(this, "received acknowledge message");
	                		Display.getDefault().syncExec(new Runnable() {
	                			public void run() {
	                				try {
	        							setAck(mm);
	                				} catch (Exception e) {
	                	                e.printStackTrace();
	                					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
	                				}
	                			}
	                			});

						} else {
                    		Display.getDefault().asyncExec(new Runnable() {
                    			public void run() {
                    				try {
                    					_messageList.addJMSMessage(mm);
                    					} catch (Exception e) {
                                        e.printStackTrace();
                    					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
                    				}
                    			}
                    			});
                        }
					} else {
						JmsLogsPlugin.logError("received message is an unknown type");
					}
				} catch (Exception e) {
                    e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
//			}
//		});
	}

	/**
     * @param message
	 * @throws JMSException 
     */
    protected void setAck(MapMessage message) {
//DEBUG       JmsLogsPlugin.logInfo("ViewLog Ack message received, MsgName: " + 
//       		message.getString("NAME") + " MsgTime: " + message.getString("EVENTTIME"));
       TableItem[] items = _tableViewer.getTable().getItems();
       	   for (TableItem item : items) {
           if (item.getData() instanceof JMSMessage) {
            JMSMessage jmsMessage = (JMSMessage) item.getData();
            try {
                if(jmsMessage.getName().equals(message.getString("NAME"))&&jmsMessage.getProperty("EVENTTIME").equals(message.getString("EVENTTIME"))){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                	jmsMessage.getHashMap().put("ACK","true"); //$NON-NLS-1$ //$NON-NLS-2$
                    _tableViewer.refresh();
                    break;
                }
                
            } catch (JMSException e) {
                JmsLogsPlugin.logException("can not set ACK", e);
            }
            
        }
       }
        
    }


    public void dispose() {
    	saveColumn();
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

	/**
	 * When dispose store the width for each column.
	 */
	public void saveColumn(){
		int[] width = _tableViewer.getColumnWidth();
		String newPreferenceColumnString=""; //$NON-NLS-1$
		String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogViewerPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		if(width.length!=columns.length){
			return;
		}
		for (int i = 0; i < columns.length; i++) {
			newPreferenceColumnString = newPreferenceColumnString.concat(columns[i].split(",")[0]+","+width[i]+";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		newPreferenceColumnString = newPreferenceColumnString.substring(0,newPreferenceColumnString.length()-1);
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setValue(LogViewerPreferenceConstants.P_STRING, newPreferenceColumnString);
		if(store.needsSaving()){
			JmsLogsPlugin.getDefault().savePluginPreferences();
		}
	}

}
