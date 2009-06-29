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

import java.text.SimpleDateFormat;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.jms.JmsMessageReceiver;
import org.csstudio.alarm.table.jms.SendMapMessage;
import org.csstudio.alarm.table.preferences.AmsVerifyViewPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * View to verify functionality of AMS. Add to {@link LogView} some buttons to
 * send test messages.
 * 
 * @see LogView
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class AmsVerifyView extends LogView {

    public static final String ID = AmsVerifyView.class.getName();

    private String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"; //$NON-NLS-1$

    /**
     * Creates the view for the alarm log table.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {

        // in alarm table the 'ack' column must be the first one!
        _preferenceColumnString = AmsVerifyViewPreferenceConstants.P_STRING;
        String[] _columnNames = JmsLogsPlugin.getDefault()
                .getPluginPreferences().getString(
                        _preferenceColumnString).split(";");
        readPreferenceTopics(JmsLogsPlugin.getDefault().getPluginPreferences()
                .getString(AmsVerifyViewPreferenceConstants.TOPIC_SET));

        // create the table model
        _messageList = new LogMessageList(100);

        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);
        Composite logTableManagementComposite = new Composite(parent, SWT.NONE);

        RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 15;
        logTableManagementComposite.setLayout(layout);

        addJmsTopicItems(logTableManagementComposite);
        addVerifyItems(logTableManagementComposite);
        addRunningSinceGroup(logTableManagementComposite);

        // setup message table with context menu etc.
        _tableViewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION);

        _messageTable = new MessageTable(_tableViewer, _columnNames,
                _messageList);
        _messageTable.makeContextMenu(getSite());

        _columnMapping = new ColumnWidthPreferenceMapping(_tableViewer);

        getSite().setSelectionProvider(_tableViewer);

        makeActions();

//        
//        // create jface table viewer with paramter 1 for log table version
//        _tableViewer = new JMSLogTableViewer(parent, getSite(), _columnNames,
//                _messageList, 1, SWT.MULTI | SWT.FULL_SELECTION);
//
//        _tableViewer.setAlarmSorting(false);
//        makeActions();
//        IActionBars bars = getViewSite().getActionBars();
//        fillLocalToolBar(bars.getToolBarManager());
//        getSite().setSelectionProvider(_tableViewer);
//
//        parent.pack();
//
//        _propertyChangeListener = new ColumnPropertyChangeListener(
//                AmsVerifyViewPreferenceConstants.P_STRING, _tableViewer);
//
//        JmsLogsPlugin.getDefault().getPluginPreferences()
//                .addPropertyChangeListener(_propertyChangeListener);
//
        _jmsMessageReceiver = new JmsMessageReceiver(_messageList);

        _jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
    }

    private void addVerifyItems(Composite logTableManagementComposite) {

        Group verifyItemGroup = new Group(logTableManagementComposite, SWT.NONE);

        verifyItemGroup.setText(Messages.AmsVerifyView_AmsActionsPruefen);

        RowLayout layout = new RowLayout();
        verifyItemGroup.setLayout(layout);

        Button verify1Button = new Button(verifyItemGroup, SWT.PUSH);
        verify1Button.setLayoutData(new RowData(60, 21));
        verify1Button.setText("SMS"); //$NON-NLS-1$

        verify1Button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                sendVerifyMessage("#MODEMTEST#"); //$NON-NLS-1$
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button verify2Button = new Button(verifyItemGroup, SWT.PUSH);
        verify2Button.setLayoutData(new RowData(60, 21));
        verify2Button.setText("Voice Mail"); //$NON-NLS-1$

        verify2Button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                sendVerifyMessage("#VOICEMAILTEST#"); //$NON-NLS-1$
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button verify3Button = new Button(verifyItemGroup, SWT.PUSH);
        verify3Button.setLayoutData(new RowData(60, 21));
        verify3Button.setText("E-Mail"); //$NON-NLS-1$

        verify3Button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                sendVerifyMessage("#MAILTEST#"); //$NON-NLS-1$
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button verify4Button = new Button(verifyItemGroup, SWT.PUSH);
        verify4Button.setLayoutData(new RowData(60, 21));
        verify4Button.setText("JMS Topic"); //$NON-NLS-1$

        verify4Button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                sendVerifyMessage("#JMSCONNECTORTEST#"); //$NON-NLS-1$
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void sendVerifyMessage(String textPropertyValue) {
        try {
            SendMapMessage sender = SendMapMessage.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
            java.util.Date currentDate = new java.util.Date();
            String time = sdf.format(currentDate);

            MapMessage mapMessage = sender.getSessionMessageObject("ALARM"); //$NON-NLS-1$

            // Add username and host to acknowledge message.
            User user = SecurityFacade.getInstance().getCurrentUser();
            if (user != null) {
                mapMessage.setString("USER", user.getUsername()); //$NON-NLS-1$
            } else {
                mapMessage.setString("USER", "NULL"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            String host = CSSPlatformInfo.getInstance().getQualifiedHostname();
            if (host != null) {
                mapMessage.setString("HOST", host); //$NON-NLS-1$
            } else {
                mapMessage.setString("HOST", "NULL"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            mapMessage.setString("EVENTTIME", time); //$NON-NLS-1$
            mapMessage.setString("TEXT", textPropertyValue); //$NON-NLS-1$
            mapMessage.setString("NAME", "AMSCOMMONTEST"); //$NON-NLS-1$ //$NON-NLS-2$

            JmsLogsPlugin.logInfo("Verify Ams system with " //$NON-NLS-1$
                    + textPropertyValue); //$NON-NLS-2$
            sender.sendMessage("ALARM"); //$NON-NLS-1$
        } catch (JMSException e) {
            CentralLogger.getInstance().error(this,
                    "JMS error: " + e.toString()); //$NON-NLS-1$
        } catch (Exception e) {
            CentralLogger.getInstance().error(this,
                    "Send message error: " + e.toString()); //$NON-NLS-1$
        }
    }
}
