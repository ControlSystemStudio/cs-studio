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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.jms.AlarmListener;
import org.csstudio.alarm.table.jms.IAlarmTableListener;
import org.csstudio.alarm.table.preferences.ITopicSetColumnService;
import org.csstudio.alarm.table.preferences.TopicSet;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreference;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.alarm.table.service.IAlarmSoundService;
import org.csstudio.alarm.table.service.ITopicsetService;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * View with table for all log messages from JMS.
 *
 * @author jhatje
 */
public class LogView extends ViewPart {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(LogView.class);

    /**
     * The ID of this view.
     */
    public static final String ID = LogView.class.getName();

    /**
     * The ID of the property view.
     */
    private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

    /**
     * {@link MessageTable} holding a {@link TableViewer} for messages.
     */
    protected MessageTable _messageTable = null;

    /**
     * Stateful service maintaining connections and message lists. There are two service
     * implementations available, one for the log views, one for the alarm views. Each is a
     * singleton, all views belonging together share the state.
     * You have to register the appropriate service in createPartControl.
     */
    private ITopicsetService _topicsetService = null;

    /**
     * Default topic set name. Try to read state 1. From previous viewPart data 2. From default marker in
     * preferences 3. Take first set from preferences
     */
    private String _currentTopicSetName = null;

    /**
     * Service allowing to reload the pvs which are tracked
     */
    private final IAlarmConfigurationService _configService = JmsLogsPlugin.getDefault().getAlarmConfigurationService();
    
    /**
     * Mapping of column widths from table to preferences.
     */
    ExchangeableColumnWidthPreferenceMapping _columnMapping;

    /**
     * The JFace {@link TableViewer}
     */
    TableViewer _tableViewer;

    /**
     * The message area at the top of the table view
     */
    private MessageArea _messageArea;

    Composite _parent;

    Composite _tableComposite;

    private Button _soundEnableButton;
    private final SoundHandler _soundHandler = new SoundHandler();

    /**
     * Stateful service maintaining the topic set preferences. There are different service
     * implementations available. Each is a singleton, all views belonging together share the state.
     * You have to register the appropriate service in createPartControl.
     */
    private ITopicSetColumnService _topicSetColumnService;

    private Label _runningSinceLabel;

    private PopUpTimerTask _timerTask;

    private Timer _timer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        _parent = parent;

        setTopicSetColumnService(JmsLogsPlugin.getDefault().getTopicSetColumnServiceForLogViews());
        setTopicSetService(JmsLogsPlugin.getDefault().getTopicsetServiceForLogViews());
        defineCurrentTopicSetName();

        // Create UI
        final GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);

        createMessageArea(_parent);

        final Composite logTableManagementComposite = new Composite(parent, SWT.NONE);
        final RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 15;
        logTableManagementComposite.setLayout(layout);

        addJmsTopicItems(logTableManagementComposite);
        // addMessageUpdateControl(logTableManagementComposite);
        addSoundButton(logTableManagementComposite);
        addRunningSinceGroup(logTableManagementComposite);

        initializeMessageTable();
    }

    /**
     * Initialization of {@link MessageTable} with {@link TableViewer}, column names etc for startup
     * of this view. If the user selects another topic set this method is also executed and the
     * previous table will be disposed. This method must be overridden by subclasses.
     *
     * @param parent
     * @param data._columnNames
     */
    protected void initializeMessageTable() {

        // Initialize JMS message list
        if (_columnMapping != null) {
            _columnMapping.saveColumn(LogViewPreferenceConstants.P_STRING,
                                      LogViewPreferenceConstants.TOPIC_SET);
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

        // setup message table with context menu etc.
        _tableViewer = new TableViewer(_tableComposite, SWT.MULTI | SWT.FULL_SELECTION);

        // get the font for the selected topic set. If there was no font defined
        // in preferences set no font.
        final Font font = _topicSetColumnService.getFont(_currentTopicSetName);
        if (font != null) {
            _tableViewer.getTable().setFont(font);
        }
        final GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableViewer.getTable().setLayoutData(gridData2);

        final AbstractMessageList messageList = getOrCreateCurrentMessageList();
        _messageTable = new MessageTable(_tableViewer, _topicSetColumnService
                .getColumnSet(_currentTopicSetName), messageList);
        _messageTable.makeContextMenu(getSite());
        setCurrentTimeToRunningSince(messageList.getStartTime());

        _columnMapping = new ExchangeableColumnWidthPreferenceMapping(_tableViewer,
                                                                      _currentTopicSetName);
        addControlListenerToColumns(LogViewPreferenceConstants.P_STRING,
                                    LogViewPreferenceConstants.TOPIC_SET);
        getSite().setSelectionProvider(_tableViewer);
        createAndRegisterActions();
        _parent.layout();
        
        setInitialStateOfSoundHandler();
    }

    // Set initial state for playing sounds based on the state of the sound enable button
    protected void setInitialStateOfSoundHandler() {
		_soundHandler.enableSound(_soundEnableButton.getSelection());
	}

    /**
     * A view operates on a topic set column service specific to the view. This method must be called to set
     * the appropriate one.
     */
    protected void setTopicSetColumnService(final ITopicSetColumnService topicSetColumnService) {
        _topicSetColumnService = topicSetColumnService;
    }

    /**
     * A view operates on a topic set service specific to the view. This method must be called to set
     * the appropriate one.
     */
    protected void setTopicSetService(final ITopicsetService topicSetService) {
        _topicsetService = topicSetService;
    }

    /**
     * Ensures that there is a proper current topic set
     */
    protected final void defineCurrentTopicSetName() {
        // is there already a topicSet from previous session?
        if (_currentTopicSetName == null) {
            _currentTopicSetName = _topicSetColumnService.getDefaultTopicSet();
        }
    }

    /**
     * Provides read access for subclasses
     *
     * @return the current topic set
     */
    protected String getCurrentTopicSetName() {
        return _currentTopicSetName;
    }

    /**
     * Creation of the message area. This must be called by each subclass in createPartControl.
     *
     * @param parent
     */
    protected final void createMessageArea(final Composite parent) {
        _messageArea = new MessageArea(parent);
    }

    /**
     * Factory method for creating the appropriate message list. Is used as a template method in
     * getOrCreateCurrentMessageList. This should be overridden when a different type of MessageList
     * is required by the view.
     *
     * @return the newly created message list
     */
    @Nonnull
    protected AbstractMessageList createMessageList() {
        return new LogMessageList(getMaximumNumberOfMessages());
    }

    /**
     * Read access for subclasses<br>
     * The alarm table listener may only be retrieved after a connection has been established, see
     * getOrCreateCurrentMessageList.
     *
     * @return the alarm table listener
     */
    @CheckForNull
    protected IAlarmTableListener getAlarmTableListener() {
        IAlarmTableListener result = null;
        TopicSet topicSet = _topicSetColumnService.getJMSTopics(_currentTopicSetName);
        if (_topicsetService.hasTopicSet(topicSet)) {
            result = _topicsetService.getAlarmTableListenerForTopicSet(topicSet);
        }
        return result;
    }

    /**
     * Returns the existing message list for the current topic set or creates a new one. If no
     * message list was present, a connection and an alarm table listener is implicitly created too.
     * This is intended to be called in initializeMessageTable. Be sure to override
     * createMessageList.<br>
     * In case of error a message is displayed and a default list is returned.
     *
     * @return the message list
     */
    @Nonnull
    protected final AbstractMessageList getOrCreateCurrentMessageList() {
        final TopicSet topicSet = _topicSetColumnService.getJMSTopics(_currentTopicSetName);
        if (!_topicsetService.hasTopicSet(topicSet)) {
            final IAlarmTableListener alarmTableListener = new AlarmListener();
            try {
                final AbstractMessageList messageList = createMessageList();
                messageList.showOutdatedMessages(AlarmViewPreference.ALARMVIEW_SHOW_OUTDATED_MESSAGES.getValue());
                _topicsetService.createAndConnectForTopicSet(topicSet,
                                                             messageList,
                                                             alarmTableListener);
                if (topicSet.isRetrieveInitialState()) {
                    retrieveInitialState(messageList);
                }
                _messageArea.hide();
            } catch (final AlarmConnectionException e) {
                LOG.error("Connecting for topicSet " + topicSet.getName() + " failed", e);
                _messageArea
                        .showMessage(SWT.ICON_WARNING,
                                     Messages.LogView_connectionErrorTitle,
                                     Messages.LogView_connectionErrorHint
                                             + "\n" + e.getMessage());
                // Returns a dummy message list which will never get any input
                return createMessageList();
            }
        }
        return _topicsetService.getMessageListForTopicSet(topicSet);
    }

    @Nonnull
    protected ITopicSetColumnService getTopicSetColumnService() {
        return _topicSetColumnService;
    }

    /**
     * This is called on each creation of a message list so it can be filled with the initial states of the PVs.
     *
     * @param messageList
     */
    protected void retrieveInitialState(@Nonnull final AbstractMessageList messageList) {
        // LogView does no initialization
    }

    
    /**
     * Hook to track state of the pause button (template method).
     * 
     * subclasses are told that the pause has just begun, i.e. the pause button has been pressed.
     */
    protected void doStartPause() {
        // LogView has nothing do to
    }

    /**
     * Hook to track state of the pause button (template method).
     * 
     * subclasses are told that the pause is over. You have to get back to work.
     */
    protected void doEndPause() {
        // LogView has nothing do to
    }
    
    @Nonnull
    private Integer getMaximumNumberOfMessages() {
        final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(new InstanceScope(),
                                                                    JmsLogsPlugin.getDefault()
                                                                            .getBundle()
                                                                            .getSymbolicName());
        final String maximumNumberOfMessagesPref = prefStore.getString(LogViewPreferenceConstants.MAX);
        Integer result = 200; // Default
        try {
            result = Integer.parseInt(maximumNumberOfMessagesPref);
        } catch (final NumberFormatException e) {
            LOG.warn("Invalid value format for maximum number" + " of messages in preferences");
        }
        return result;
    }

    /**
     * Write new Column width when a column is resized.
     */
    void addControlListenerToColumns(final String colSetPref, final String topicSetPref) {
        final TableColumn[] columns = _tableViewer.getTable().getColumns();
        for (final TableColumn tableColumn : columns) {
            tableColumn.addControlListener(new ControlAdapter() {
                @Override
                public void controlResized(final ControlEvent e) {
                    _columnMapping.saveColumn(colSetPref, topicSetPref);
                }
            });
        }
    }

    /**
     * Add label with date and time the table is started.
     *
     * @param logTableManagementComposite
     */
    void addRunningSinceGroup(final Composite logTableManagementComposite) {
        final Group runningSinceGroup = new Group(logTableManagementComposite, SWT.NONE);

        runningSinceGroup.setText(Messages.LogView_runningSince);

        final RowLayout layout = new RowLayout();
        runningSinceGroup.setLayout(layout);
        _runningSinceLabel = new Label(runningSinceGroup, SWT.CENTER);
        _runningSinceLabel.setLayoutData(new RowData(90, 21));
    }

    void setCurrentTimeToRunningSince(final Date time) {
        final SimpleDateFormat formater = new SimpleDateFormat();
        _runningSinceLabel.setText(formater.format(time));
    }

    /**
     * Must be called from createPartControl
     */
    protected void addJmsTopicItems(@Nonnull final Composite logTableManagementComposite) {
        if (!AlarmPreference.ALARMSERVICE_IS_DAL_IMPL.getValue()) {
            addJmsTopicItemsLocal(logTableManagementComposite);
        }
    }

    /**
     * Add combo box to select set of topics to be monitored. The items in the combo box are names
     * that are mapped in the preferences to sets of topics.
     *
     * @param logTableManagementComposite
     */
    private void addJmsTopicItemsLocal(@Nonnull final Composite logTableManagementComposite) {
        final Group jmsTopicItemsGroup = new Group(logTableManagementComposite, SWT.NONE);
    
        jmsTopicItemsGroup.setText(Messages.LogView_monitoredJmsTopics);
    
        final RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 5;
        jmsTopicItemsGroup.setLayout(layout);
    
        final Combo topicSetsCombo = new Combo(jmsTopicItemsGroup, SWT.SINGLE);
        int i = 0;
    
        for (final TopicSet topicSet : _topicSetColumnService.getTopicSets()) {
            topicSetsCombo.add(topicSet.getName());
            if (_currentTopicSetName.equals(topicSet.getName())) {
                topicSetsCombo.select(i);
            }
            i++;
        }
        final Button pauseButton = new Button(jmsTopicItemsGroup, SWT.TOGGLE);
        pauseButton.setLayoutData(new RowData(60, 21));
        pauseButton.setText("Pause");

        topicSetsCombo.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetSelected(@Nullable final SelectionEvent e) {
                super.widgetSelected(e);
                final String oldTopicSet = _currentTopicSetName;
                _currentTopicSetName = _topicSetColumnService.getTopicSets().get(topicSetsCombo
                        .getSelectionIndex()).getName();
                if (!oldTopicSet.equals(_currentTopicSetName)) {
                    _messageTable.setMessageUpdatePause(false);
                    pauseButton.setSelection(false);
                    initializeMessageTable();
                }
            }
        });
    
        pauseButton.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetSelected(@Nullable final SelectionEvent e) {
                if (pauseButton.getSelection()) {
                    _messageTable.setMessageUpdatePause(true);
                    cancelTimerTask();
                    _timer = new Timer();
                    _timerTask = new PopUpTimerTask();
                    _timerTask.addExpirationListener(new IExpirationLisener() {
                        @Override
                        public void expired() {
                            pauseButton.setSelection(false);
                            _tableViewer.refresh();
                            _messageTable.setMessageUpdatePause(false);
                            cancelTimerTask();
                        }
                    });
                    _timer.schedule(_timerTask, 100000, 100000);
                    doStartPause();
                } else {
                    cancelTimerTask();
                    _tableViewer.refresh();
                    _messageTable.setMessageUpdatePause(false);
                    doEndPause();
                }
            }
    
            @SuppressWarnings("synthetic-access")
            private void cancelTimerTask() {
                if (_timer != null) {
                    if (_timerTask != null) {
                        _timerTask.cancel();
                        _timerTask = null;
                    }
                    _timer.cancel();
                    _timer = null;
                }
            }
        });
    }

    /**
     * Must be called from createPartControl from this class or the subclasses resp.
     */
    protected void createAndRegisterActions() {
        final IActionBars bars = getViewSite().getActionBars();

        if (AlarmPreference.ALARMSERVICE_IS_DAL_IMPL.getValue()) {
            createAndRegisterReloadAction(bars);
        }
        createAndRegisterPropertyViewAction(bars);
        createAndRegisterMessageAreaToggleAction(bars);
    }

    private void createAndRegisterReloadAction(@Nonnull final IActionBars bars) {
        final Action reloadAction = new Action() {
            @Override
            public void run() {
                try {
                    IAlarmConnection connection = getConnection();
                    if (connection != null) {
                        connection.reloadPVsFromResource();
                    } else {
                        MessageDialog.openError(getSite().getShell(),
                                                Messages.LogView_reloadErrorTitle, //$NON-NLS-1$
                                                Messages.LogView_reloadErrorHint);
                    }
                } catch (AlarmConnectionException e) {
                    MessageDialog.openError(getSite().getShell(), Messages.LogView_reloadErrorTitle, //$NON-NLS-1$
                                            e.getMessage() + "\n" + Messages.LogView_reloadErrorHint);
                }
            }
            @SuppressWarnings("synthetic-access")
            @CheckForNull
            private IAlarmConnection getConnection() {
                IAlarmConnection connection = null;
                final TopicSet topicSet = _topicSetColumnService.getJMSTopics(_currentTopicSetName);
                if (_topicsetService.hasTopicSet(topicSet)) {
                    connection = _topicsetService.getAlarmConnectionForTopicSet(topicSet);
                }
                return connection;
            }
        };
        
        final String source = AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue() ? " LDAP" : " XML";
        reloadAction.setText(Messages.LogView_reloadText + source);
        reloadAction.setToolTipText(Messages.LogView_reloadToolTip + source);
        reloadAction.setImageDescriptor(JmsLogsPlugin.getImageDescriptor("icons/refresh.gif"));

        bars.getToolBarManager().add(reloadAction);
    }
    
    private void createAndRegisterPropertyViewAction(@Nonnull final IActionBars bars) {
        final Action showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    getSite().getPage().showView(PROPERTY_VIEW_ID);
                } catch (final PartInitException e) {
                    MessageDialog.openError(getSite().getShell(), "Alarm Table", //$NON-NLS-1$
                                            e.getMessage());
                }
            }
        };
        showPropertyViewAction.setText(Messages.LogView_properties);
        showPropertyViewAction.setToolTipText(Messages.LogView_propertiesToolTip);

        final IViewRegistry viewRegistry = getSite().getWorkbenchWindow().getWorkbench()
                .getViewRegistry();
        final IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());

        bars.getToolBarManager().add(showPropertyViewAction);
    }

    private void createAndRegisterMessageAreaToggleAction(@Nonnull final IActionBars bars) {
        final Action displayMessageAreaAction = new Action() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                if (_messageArea.isVisible()) {
                    _messageArea.hide();
                } else {
                    _messageArea.show();
                }
            }
        };
        displayMessageAreaAction.setText(Messages.LogView_messageArea);
        displayMessageAreaAction.setToolTipText(Messages.LogView_messageAreaToolTip);
        
        displayMessageAreaAction.setImageDescriptor(JmsLogsPlugin.getImageDescriptor("icons/details_view.gif"));
        bars.getToolBarManager().add(displayMessageAreaAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        _tableViewer.getTable().setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException {
        super.init(site, memento);
        if (memento == null) {
            return;
        }
        _currentTopicSetName = memento.getString("previousTopicSet"); //$NON-NLS-1$
        if (_currentTopicSetName == null) {
            LOG.debug("No topic set from previous session"); //$NON-NLS-1$
        } else {
            LOG.debug("Get topic set from previous session: " + _currentTopicSetName); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
        if ( (memento != null) && (_currentTopicSetName != null)) {
            LOG.debug("Save latest topic set in IMemento: " + _currentTopicSetName);
            memento.putString("previousTopicSet", _currentTopicSetName); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        _soundHandler.enableSound(false);
        _messageTable = null;
    }

    /**
     * Encapsulation of the message area. It is located below the tree view.<br>
     * FIXME (jpenning) This is a copy of the inner class of the AlarmTreeView.
     */
    private static final class MessageArea {
        /**
         * The message area which can display error messages inside the view part.
         */
        private final Composite _messageAreaComposite;

        /**
         * The icon displayed in the message area.
         */
        private final Label _messageAreaIcon;

        /**
         * The message displayed in the message area.
         */
        private final Label _messageAreaMessage;

        /**
         * The description displayed in the message area.
         */
        private final Label _messageAreaDescription;

        public MessageArea(final Composite parent) {
            _messageAreaComposite = new Composite(parent, SWT.NONE);
            final GridData messageAreaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
            messageAreaLayoutData.exclude = true;
            _messageAreaComposite.setVisible(false);
            _messageAreaComposite.setLayoutData(messageAreaLayoutData);
            _messageAreaComposite.setLayout(new GridLayout(2, false));

            _messageAreaIcon = new Label(_messageAreaComposite, SWT.NONE);
            _messageAreaIcon.setLayoutData(new GridData(SWT.BEGINNING,
                                                        SWT.BEGINNING,
                                                        false,
                                                        false,
                                                        1,
                                                        2));
            _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

            _messageAreaMessage = new Label(_messageAreaComposite, SWT.WRAP);
            _messageAreaMessage.setText(Messages.LogView_defaultMessageText);
            // Be careful if changing the GridData below! The label will not wrap
            // correctly for some settings.
            _messageAreaMessage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

            _messageAreaDescription = new Label(_messageAreaComposite, SWT.WRAP);
            _messageAreaDescription.setText(Messages.LogView_defaultMessageDescription);
            // Be careful if changing the GridData below! The label will not wrap
            // correctly for some settings.
            _messageAreaDescription
                    .setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        }

        /**
         * Sets the message displayed in the message area of this view part.
         *
         * @param icon the icon to be displayed next to the message. Must be one of
         *            <code>SWT.ICON_ERROR</code>, <code>SWT.ICON_INFORMATION</code>,
         *            <code>SWT.ICON_WARNING</code>, <code>SWT.ICON_QUESTION</code>.
         * @param message the message.
         * @param description a descriptive text.
         */
        public void showMessage(final int icon, final String message, final String description) {
            _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(icon));
            _messageAreaMessage.setText(message);
            _messageAreaDescription.setText(description);
            _messageAreaComposite.layout();

            show();
        }

        public void show() {
            _messageAreaComposite.setVisible(true);
            ((GridData) _messageAreaComposite.getLayoutData()).exclude = false;
            _messageAreaComposite.getParent().layout();
        }

        /**
         * Hides the message displayed in this view part.
         */
        public void hide() {
            _messageAreaComposite.setVisible(false);
            ((GridData) _messageAreaComposite.getLayoutData()).exclude = true;
            _messageAreaComposite.getParent().layout();
        }

        public boolean isVisible() {
            return _messageAreaComposite.isVisible();
        }
    }
    
    /**
     * Sound is played dependent of the state of the sound enable button. If it is enabled, the
     * so-called sound playing listener (encapsulated here) is registered at the alarm table
     * listener for the current topic set. If it gets disabled, the sound playing listener is
     * deregistered. If the current topic set is changed, the sound playing listener gets
     * deregistered and then registered at the now-current alarm table listener.<br>
     * Because we have to know where we are currently registered, the current alarm table listener
     * is recorded in here.
     */
    protected final class SoundHandler {

        /**
         * Service for playing sounds
         */
        private final IAlarmSoundService _alarmSoundService = JmsLogsPlugin.getDefault()
                .getAlarmSoundService();

        /**
         * This listener listens to incoming messages for playing sounds. Each sound handler uses
         * only one sound playing listener and registers it at the appropriate alarm table listener.
         */
        private IAlarmListener _soundPlayingListener = null;

        /**
         * Keep track where the sound playing listener is registered
         */
        private IAlarmTableListener _currentAlarmTableListener = null;

        public SoundHandler() {
            // Nothing to do
        }

        @Nonnull
        private IAlarmListener getSoundPlayingListener() {
            if (_soundPlayingListener == null) {
                _soundPlayingListener = new IAlarmListener() {

                    @Override
                    public void stop() {
                        // Nothing to do
                    }

                    @SuppressWarnings("synthetic-access")
                    @Override
                    public void onMessage(@Nonnull final IAlarmMessage message) {
                        _alarmSoundService.playAlarmSound(message.getString(AlarmMessageKey.SEVERITY));
                    }
                };
            }
            return _soundPlayingListener;
        }

        @SuppressWarnings("synthetic-access")
        public void enableSound(final boolean yes) {
            // Built in a robust way: Deregister always, register at the current alarm table
            // listener.
            if (_currentAlarmTableListener != null) {
                _currentAlarmTableListener.deRegisterAlarmListener(getSoundPlayingListener());
                _currentAlarmTableListener = null;
                LOG.debug("Sound deregistered");
            }

            if (yes) {
                _currentAlarmTableListener = getAlarmTableListener();
                if (_currentAlarmTableListener != null) {
                    _currentAlarmTableListener.registerAlarmListener(getSoundPlayingListener());
                    LOG.debug("Sound registered");
                }
            }
        }
    }

    protected void addSoundButton(@Nonnull final Composite logTableManagementComposite) {
        final Group soundButtonGroup = new Group(logTableManagementComposite, SWT.NONE);

        soundButtonGroup.setText(Messages.AlarmView_soundButtonTitle);

        final RowLayout layout = new RowLayout();
        soundButtonGroup.setLayout(layout);

        _soundEnableButton = new Button(soundButtonGroup, SWT.TOGGLE);
        _soundEnableButton.setLayoutData(new RowData(60, 21));
        _soundEnableButton.setText(Messages.AlarmView_soundButtonEnable);

        // Initial state for playing sounds is always activated on startup, operator must manually turn it off.
        _soundEnableButton.setSelection(true);

        _soundEnableButton.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                _soundEnableButton.setText(_soundEnableButton.getSelection() ? Messages.AlarmView_soundButtonEnable
                        : Messages.AlarmView_soundButtonDisable);
                _soundHandler.enableSound(_soundEnableButton.getSelection());
            }
        });
    }
}
