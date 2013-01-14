/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT,
 * THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF
 * WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED
 * HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE
 * REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.csstudio.alarm.table.dataModel.AlarmMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreference;
import org.csstudio.alarm.table.ui.actions.LogTableViewActionFactory;
import org.csstudio.alarm.table.ui.messagetable.AlarmMessageTable;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add to the base class {@link LogView}: acknowledge button and combo box,
 * send method for jms acknowledge messages.
 *
 * @see LogView
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class AlarmView extends LogView {
    
    private static final Logger LOG = LoggerFactory.getLogger(AlarmView.class);
    
    public static final String ALARM_VIEW_ID = AlarmView.class.getCanonicalName();
    
    private static final String SECURITY_ID = "operating"; //$NON-NLS-1$
    
    private Button _ackButton;
    
    private IAlarmService.IListener _configurationUpdateListener;
    
    /**
     * Creates the view for the alarm log table.
     *
     * @param parent
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        _parent = parent;
        
        setTopicSetColumnService(JmsLogsPlugin.getDefault().getTopicSetColumnServiceForAlarmViews());
        setTopicSetService(JmsLogsPlugin.getDefault().getTopicsetServiceForAlarmViews());
        defineCurrentTopicSetName();
        
        // Create UI
        final GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        _parent.setLayout(grid);
        
        createMessageArea(_parent);
        
        final Composite logTableManagementComposite = new Composite(_parent, SWT.NONE);
        
        final RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 15;
        logTableManagementComposite.setLayout(layout);
        
        addJmsTopicItems(logTableManagementComposite);
        addAcknowledgeItems(logTableManagementComposite);
        addSoundButton(logTableManagementComposite);
        addRunningSinceGroup(logTableManagementComposite);
        
        initializeMessageTable();
        
        new ControlSystemDragSource(_tableViewer.getTable()) {
        	
        	@Override
        	public Object getSelection() {
        		final Object[] o = ((IStructuredSelection) _tableViewer.getSelection()).toArray();
        		final ProcessVariable[] pv = new ProcessVariable[o.length];
        		for (int i=0; i<pv.length; ++i) {
        			pv[i] = new ProcessVariable(((BasicMessage) o[i]).getName());
        		}
        		return pv;
        	}
        };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        tryToDeregisterReloadListener();
        super.dispose();
    }
    
    private void tryToDeregisterReloadListener() {
        IAlarmService service = ServiceLocator.getService(IAlarmService.class);
        if (service != null) {
            service.deregister(_configurationUpdateListener);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeMessageTable() {
        // Initialize JMS message list
        if (_columnMapping != null) {
            _columnMapping
                    .saveColumn(AlarmViewPreference.ALARMVIEW_P_STRING_ALARM.getKeyAsString(),
                                AlarmViewPreference.ALARMVIEW_TOPIC_SET.getKeyAsString());
            _columnMapping = null;
        }
        // is there already a MessageTable delete it and the message list.
        if (_messageTable != null) {
            _messageTable.disposeMessageTable();
            _tableViewer = null;
            _messageTable = null;
            // _messageList = null;
        }
        if (_tableComposite != null) {
            _tableComposite.dispose();
            _tableComposite = null;
        }
        
        _tableComposite = new Composite(_parent, SWT.NONE);
        final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableComposite.setLayoutData(gridData);
        final GridLayout grid2 = new GridLayout();
        grid2.numColumns = 1;
        _tableComposite.setLayout(grid2);
        _tableViewer = new TableViewer(_tableComposite, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);
        
        // get the font for the selected topic set. If there was no font defined
        // in preferences set no font.
        final Font font = getTopicSetColumnService().getFont(getCurrentTopicSetName());
        if (font != null) {
            _tableViewer.getTable().setFont(font);
        }
        
        final GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableViewer.getTable().setLayoutData(gridData2);
        
        final String[] columnSet = getTopicSetColumnService()
                .getColumnSet(getCurrentTopicSetName());
        final String[] columnSetWithAck = new String[columnSet.length + 1];
        columnSetWithAck[0] = "ACK,25";
        for (int i = 0; i < columnSet.length; i++) {
            columnSetWithAck[i + 1] = columnSet[i];
        }
        
        final AbstractMessageList messageList = getOrCreateCurrentMessageList();
        _messageTable = new AlarmMessageTable(_tableViewer, columnSetWithAck, messageList);
        _messageTable.makeContextMenu(getSite());
        setCurrentTimeToRunningSince(messageList.getStartTime());
        
        _columnMapping = new AlarmExchangeableColumnWidthPreferenceMapping(_tableViewer,
                                                                           getCurrentTopicSetName());
        addControlListenerToColumns(AlarmViewPreference.ALARMVIEW_P_STRING_ALARM.getKeyAsString(),
                                    AlarmViewPreference.ALARMVIEW_TOPIC_SET.getKeyAsString());
        getSite().setSelectionProvider(_tableViewer);
        createAndRegisterActions();
        createAndRegisterReloadCommand();
        
        _parent.layout();
        
        setInitialStateOfSoundHandler();
    }
    
    private void createAndRegisterReloadCommand() {
        _configurationUpdateListener = new MyAlarmListener();
        ServiceLocator.getService(IAlarmService.class).register(_configurationUpdateListener);
    }
    
    @Override
    protected final void retrieveInitialState(@Nonnull final AbstractMessageList messageList) {
        final InitialStateRetriever retriever = new InitialStateRetriever(messageList);
        final Job job = retriever.newRetrieveInitialStateJob();
        
        // Start the job.
        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);
        
        progressService.schedule(job, 0, true);
    }
    
    public void retrieveInitialStateForAllPvs() {
        AbstractMessageList messageList = getOrCreateCurrentMessageList();
        // messageList.removeAllMessages();
        retrieveInitialState(messageList);
    }
    
    @Override
    @Nonnull
    protected final AbstractMessageList createMessageList() {
        // There is no maximum number of messages. The message list will not overflow, because
        // eventually all messages are contained within and will simply be exchanged.
        return new AlarmMessageList();
    }
    
    @Override
    protected void createAndRegisterActions() {
        final IActionBars bars = getViewSite().getActionBars();
        
        Action propertyViewAction = LogTableViewActionFactory
                .createAndRegisterRetrieveInitialStateAction(getSite(), this);
        bars.getToolBarManager().add(propertyViewAction);

        super.createAndRegisterActions();
    }

    @Override
    protected void doStartPause() {
        _ackButton.setEnabled(false);
    }
    
    @Override
    protected void doEndPause() {
        enableAckButtonIfPermitted();
    }
    
    private void enableAckButtonIfPermitted() {
        _ackButton.setEnabled(SecurityFacade.getInstance().canExecute(SECURITY_ID, false));
    }
    
    // CHECKSTYLE:OFF
    private void addAcknowledgeItems(final Composite logTableManagementComposite) {
        
        final Group acknowledgeItemGroup = new Group(logTableManagementComposite, SWT.NONE);
        
        acknowledgeItemGroup.setText(Messages.AlarmView_acknowledgeTitle);
        
        final RowLayout layout = new RowLayout();
        acknowledgeItemGroup.setLayout(layout);
        
        _ackButton = new Button(acknowledgeItemGroup, SWT.PUSH);
        _ackButton.setLayoutData(new RowData(60, 21));
        _ackButton.setText(Messages.AlarmView_acknowledgeButton);
        enableAckButtonIfPermitted();
        final Combo ackCombo = new Combo(acknowledgeItemGroup, SWT.SINGLE);
        ackCombo.add(Messages.AlarmView_acknowledgeAllDropDown);
        final IPreferenceStore prefs = JmsLogsPlugin.getDefault().getPreferenceStore();
        if (prefs.getString(JmsLogPreferenceConstants.VALUE0).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE0));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE1).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE1));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE2).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE2));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE3).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE3));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE4).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE4));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE5).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE5));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE6).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE6));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE7).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE7));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE8).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE8));
        }
        if (prefs.getString(JmsLogPreferenceConstants.VALUE9).trim().length() > 0) {
            ackCombo.add(prefs.getString(JmsLogPreferenceConstants.VALUE9));
        }
        ackCombo.select(4);
        
        _ackButton.addSelectionListener(newSelectionListenerForAckButton(ackCombo));
    }
    
    // CHECKSTYLE:ON
    @Nonnull
    private SelectionListener newSelectionListenerForAckButton(@Nonnull final Combo ackCombo) {
        return new SelectionListener() {
            
            /**
             * Acknowledge button is pressed for all (selection 0) messages or messages with a
             * special severity (selection 1-3).
             */
            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                final List<AlarmMessage> msgList = new ArrayList<AlarmMessage>();
                for (final TableItem ti : _tableViewer.getTable().getItems()) {
                    
                    if (ti.getData() instanceof AlarmMessage) {
                        final AlarmMessage message = (AlarmMessage) ti.getData();
                        // ComboBox selection for all messages or for a special
                        // severity
                        final String sevProp = message.getProperty(AlarmMessageKey.SEVERITY
                                .getDefiningName());
                        if (ackCombo.getItem(ackCombo.getSelectionIndex()).equals(sevProp) //$NON-NLS-1$
                                || (ackCombo.getItem(ackCombo.getSelectionIndex())
                                        .equals(Messages.AlarmView_acknowledgeAllDropDown))) {
                            // add the message only if it is not yet
                            // acknowledged.
                            if (!message.isAcknowledged()) {
                                final AlarmMessage copy = message.copy();
                                msgList.add(copy);
                            }
                        }
                        
                    } else {
                        JmsLogsPlugin.logInfo("unknown item type in table"); //$NON-NLS-1$
                    }
                    
                }
                LOG.debug("Number of msg in list to send: {}", msgList.size());
                LOG.debug("Number of msg in table: {}", _tableViewer.getTable().getItemCount());
                
                final SendAcknowledge sendAck = SendAcknowledge.newFromJMSMessage(msgList);
                sendAck.schedule();
            }
            
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                // Nothing to do
            }
        };
    }
    
    /**
     * handles callbacks from remote commands
     */
    private class MyAlarmListener implements IAlarmService.IListener {
        
        public MyAlarmListener() {
            // nothing to do
        }
        
        @Override
        public void configurationUpdated() {
            showMessage(SWT.ICON_INFORMATION, Messages.LogView_AlarmServer_ConfigurationUpdated);
        }
        
        @Override
        public void alarmServerReloaded() {
            if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()) {
                showMessage(SWT.ICON_INFORMATION, Messages.LogView_AlarmServer_Reloaded);
            }
        }
        
        // this is the proper way to retrieveInitialState if ever wanted
        //        private void retrieveInitialState() {
        //            // this is called from a non-ui-thread so we have to enqueue it
        //            Display.getDefault().asyncExec(new Runnable() {
        //                
        //                @Override
        //                public void run() {
        //                    _messageArea.showMessage(SWT.ICON_INFORMATION,
        //                                             Messages.LogView_reloadTitle,
        //                                             Messages.LogView_reloadMessage);
        //                    AbstractMessageList messageList = getOrCreateCurrentMessageList();
        //                    // messageList.removeAllMessages();
        // there may be a more clever way to delete all without permanent model fire
        //                    AlarmView.this.retrieveInitialState(messageList);
        //                }
        //            });
        //        }

        @Override
        public void alarmServerStarted() {
            showMessage(SWT.ICON_WARNING, Messages.LogView_AlarmServer_Started);
        }

        @Override
        public void alarmServerWillStop() {
            showMessage(SWT.ICON_ERROR, Messages.LogView_AlarmServer_WillStop);
        }
        
        private void showMessage(final int icon, @Nonnull final String message) {
            // this is called from a non-ui-thread so we have to enqueue it
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    _messageArea.showMessage(icon, Messages.LogView_AlarmServer_Title, message);
                }
            });
        }
    }
}
