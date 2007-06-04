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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.jms.MapMessage;

import org.csstudio.alarm.table.dataModel.JMSAlarmMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.jms.SendMapMessage;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
 * Simple view more like console, used to write log messages
 */
public class AlarmLogView extends LogView {

	public static final String ID = AlarmLogView.class.getName();

	public void createPartControl(Composite parent) {

		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm).split(
						";"); //$NON-NLS-1$
		jmsml = new JMSAlarmMessageList(columnNames);

		parentShell = parent.getShell();

		initializeJMSReceiver(parentShell,
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

        Button ackButton = new Button(comp,SWT.PUSH);
        ackButton.setText("Acknowledge");
        ackButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true, false,1,1));
        final Combo ackCombo = new Combo(comp,SWT.SINGLE);
        ackCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true, false,2,1));
        ackCombo.add("ALL");
        ackCombo.select(0);
        IPreferenceStore prefs = JmsLogsPlugin.getDefault().getPreferenceStore();
        if(prefs.getString(JmsLogPreferenceConstants.VALUE0).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE0));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE1).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE1));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE2).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE2));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE3).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE3));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE4).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE4));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE5).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE5));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE6).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE6));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE7).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE7));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE8).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE8));
        if(prefs.getString(JmsLogPreferenceConstants.VALUE9).trim().length()>0)
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE9));
        ackButton.addSelectionListener(new SelectionListener(){


            public void widgetSelected(SelectionEvent e) {
                if(ackCombo.getSelectionIndex()==0){
                    TableItem[] items = jlv.getTable().getItems();
                    JMSMessage message;
                    for (TableItem ti : items) {
                        
                        SendMapMessage sender = new SendMapMessage();
                        String time = TimestampFactory.now().toString();
                        try{
                            sender.startSender();
                            MapMessage mapMessage = sender.getSessionMessageObject();
                            System.out.println("Start Sender");
                            
                            if (ti.getData() instanceof JMSMessage) {
                                message = (JMSMessage) ti.getData();
                                System.out.println("name: "+message.getName());
                                
                            }else return;
                            HashMap<String, String> hm = message.getHashMap();
                            Iterator<String> it = hm.keySet().iterator();
                            
                            while(it.hasNext()) {
                                String key = it.next();
                                String value = hm.get(key);
                                mapMessage.setString(key, value);
                            }
                            message.setProperty("ACK", "1");
                            mapMessage.setString("ACK", "TRUE");
                            mapMessage.setString("ACK_TIME", time);
                            Engine.getInstance().addLdapWriteRequest("epicsAlarmAckn", message.getName(), "ack");
                            Engine.getInstance().addLdapWriteRequest("epicsAlarmAcknTimeStamp", message.getName(), time);
    
                            sender.sendMessage();
                        }catch(Exception ex){
                            JmsLogsPlugin.logException("ACK not set", ex);
                            ex.printStackTrace();
                        }
                    }
                }else{
                    TableItem[] items = jlv.getTable().getItems();
                    JMSMessage message;
                    for (TableItem ti : items) {

                        SendMapMessage sender = new SendMapMessage();
                        String time = TimestampFactory.now().toString();
                        try{
                            System.out.println("Start Sender");
                            
                            if (ti.getData() instanceof JMSMessage) {
                                message = (JMSMessage) ti.getData();
                                System.out.println("name: "+message.getName());
                                
                            }else return;
                            if(ackCombo.getItem(ackCombo.getSelectionIndex()).equals(message.getProperty("SEVERITY"))){
                                sender.startSender();
                                MapMessage mapMessage = sender.getSessionMessageObject();

                                HashMap<String, String> hm = message.getHashMap();
                                Iterator<String> it = hm.keySet().iterator();
                                
                                while(it.hasNext()) {
                                    String key = it.next();
                                    String value = hm.get(key);
                                    mapMessage.setString(key, value);
                                }
                                message.setProperty("ACK", "1");
                                mapMessage.setString("ACK", "TRUE");
                                mapMessage.setString("ACK_TIME", time);
                                Engine.getInstance().addLdapWriteRequest("epicsAlarmAckn", message.getName(), "ack");
                                Engine.getInstance().addLdapWriteRequest("epicsAlarmAcknTimeStamp", message.getName(), time);
        
                                sender.sendMessage();
                            }
                        }catch(Exception ex){
                            JmsLogsPlugin.logException("ACK not set", ex);
                            ex.printStackTrace();
                        }
                    }                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {}
            
        });
        
        
		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(AlarmViewerPreferenceConstants.P_STRINGAlarm).split(
						";"); //$NON-NLS-1$
		
		jlv = new JMSLogTableViewer(parent, getSite(), columnNames, jmsml, 2,SWT.SINGLE | SWT.FULL_SELECTION|SWT.CHECK);
		jlv.setAlarmSorting(true);
		parent.pack();

		cl = new ColumnPropertyChangeListener(
				AlarmViewerPreferenceConstants.P_STRINGAlarm,
				jlv);

		JmsLogsPlugin.getDefault().getPluginPreferences()
		.addPropertyChangeListener(cl);
		
	}

    /* (non-Javadoc)
     * @see org.csstudio.alarm.table.LogView#setAckTrue(org.csstudio.alarm.table.dataModel.JMSMessage)
     */
    @Override
    void setAckTrue(JMSMessage jmsMessage) {
        jmsml.removeJMSMessage(jmsMessage);
        jlv.refresh();
    }
}
