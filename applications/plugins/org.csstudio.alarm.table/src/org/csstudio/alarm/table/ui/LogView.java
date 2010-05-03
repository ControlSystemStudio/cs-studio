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

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.jms.AlarmListener;
import org.csstudio.alarm.table.jms.IAlarmTableListener;
import org.csstudio.alarm.table.preferences.TopicSet;
import org.csstudio.alarm.table.preferences.TopicSetColumnService;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.alarm.table.service.ITopicsetService;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
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
    public MessageTable _messageTable = null;
    
    /**
     * Stateful service maintaining connections and message lists. There are two service
     * implementations available, one for the log views, one for the alarm views. Each is a
     * singleton, all views belonging together share the state. You have to override
     * defineTopicSetService accordingly.
     */
    private ITopicsetService _topicsetService = null;
    
    /**
     * Default topic set. Try to read state 1. From previous viewPart data 2. From default marker in
     * preferences 3. Take first set from preferences
     */
    private String _currentTopicSet = null;
    
    /**
     * Mapping of column widths from table to preferences.
     */
    ExchangeableColumnWidthPreferenceMapping _columnMapping;
    
    /**
     * The JFace {@link TableViewer}
     */
    TableViewer _tableViewer;
    
    Composite _parent;
    
    Composite _tableComposite;
    
    TopicSetColumnService _topicSetColumnService;
    
    private Label _runningSinceLabel;
    
    Button _pauseButton;
    
    private PopUpTimerTask _timerTask;
    
    private Timer _timer;
    
    /**
     * {@inheritDoc}
     */
    public void createPartControl(final Composite parent) {
        _parent = parent;
        
        // Read column names and JMS topic settings from preferences
        _topicSetColumnService = new TopicSetColumnService(LogViewPreferenceConstants.TOPIC_SET,
                                                           LogViewPreferenceConstants.P_STRING);
        setTopicSetService(JmsLogsPlugin.getDefault().getTopicsetServiceForLogViews());
        defineCurrentTopicSet();
        
        // Create UI
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);
        
        Composite logTableManagementComposite = new Composite(parent, SWT.NONE);
        RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 15;
        logTableManagementComposite.setLayout(layout);
        
        addJmsTopicItems(logTableManagementComposite);
        // addMessageUpdateControl(logTableManagementComposite);
        addRunningSinceGroup(logTableManagementComposite);
        
        initializeMessageTable();
    }
    
    /**
     * Initialization of {@link MessageTable} with {@link TableViewer}, column names etc for startup
     * of this view. If the user selects another topic set this method is also executed and the
     * previous table will be disposed. This method must be overridden by subclasses.
     * 
     * @param parent
     * @param _columnNames
     */
    protected void initializeMessageTable() {
        
        // Initialize JMS message list
        if (_columnMapping != null) {
            _columnMapping.saveColumn(LogViewPreferenceConstants.P_STRING,
                                      LogViewPreferenceConstants.TOPIC_SET);
            _columnMapping = null;
        }
        _topicSetColumnService = new TopicSetColumnService(LogViewPreferenceConstants.TOPIC_SET,
                                                           LogViewPreferenceConstants.P_STRING);
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
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableComposite.setLayoutData(gridData);
        GridLayout grid2 = new GridLayout();
        grid2.numColumns = 1;
        _tableComposite.setLayout(grid2);
        
        // setup message table with context menu etc.
        _tableViewer = new TableViewer(_tableComposite, SWT.MULTI | SWT.FULL_SELECTION);
        
        // get the font for the selected topic set. If there was no font defined
        // in preferences set no font.
        Font font = _topicSetColumnService.getFont(_currentTopicSet);
        if (font != null) {
            _tableViewer.getTable().setFont(font);
        }
        GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true);
        _tableViewer.getTable().setLayoutData(gridData2);
        
        MessageList messageList = getOrCreateCurrentMessageList();
        _messageTable = new MessageTable(_tableViewer, _topicSetColumnService
                .getColumnSet(_currentTopicSet), messageList);
        _messageTable.makeContextMenu(getSite());
        setCurrentTimeToRunningSince(messageList.getStartTime());
        
        _columnMapping = new ExchangeableColumnWidthPreferenceMapping(_tableViewer,
                                                                      _currentTopicSet);
        addControlListenerToColumns(LogViewPreferenceConstants.P_STRING,
                                    LogViewPreferenceConstants.TOPIC_SET);
        getSite().setSelectionProvider(_tableViewer);
        makeActions();
        _parent.layout();
        
    }
    
    /**
     * The log view operates on a topic set service for log views. This method must be called to set
     * the appropriate one.
     * 
     * @param topicSetService .
     */
    protected void setTopicSetService(final ITopicsetService topicSetService) {
        _topicsetService = topicSetService;
    }
    
    /**
     * Ensures that there is a proper current topic set
     */
    protected final void defineCurrentTopicSet() {
        // is there already a topicSet from previous session?
        if (_currentTopicSet == null) {
            _currentTopicSet = _topicSetColumnService.getDefaultTopicSet();
        }
    }
    
    /**
     * Provides read access for subclasses
     * 
     * @return the current topic set
     */
    protected String getCurrentTopicSet() {
        return _currentTopicSet;
    }
    
    /**
     * Factory method for creating the appropriate message list. Is used as a template method in
     * getOrCreateCurrentMessageList. This should be overridden when a different type of MessageList
     * is required by the view.
     * 
     * @return the newly created message list
     */
    protected MessageList createMessageList() {
        return new LogMessageList(getMaximumNumberOfMessages());
    }
    
    /**
     * Read access for subclasses<br>
     * The alarm table listener may only be retrieved after a connection has been established, see
     * getOrCreateCurrentMessageList.
     * 
     * @return the alarm table listener
     */
    protected IAlarmTableListener getAlarmTableListener() {
        return _topicsetService.getAlarmTableListenerForTopicSet(_topicSetColumnService
                .getJMSTopics(_currentTopicSet));
    }
    
    /**
     * Returns the existing message list for the current topic set or creates a new one. If no
     * message list was present, a connection and an alarm table listener is implicitly created too.
     * This is intended to be called in initializeMessageTable. Be sure to override
     * createMessageList.
     * 
     * @return the message list
     */
    protected final MessageList getOrCreateCurrentMessageList() {
        TopicSet topicSet = _topicSetColumnService.getJMSTopics(_currentTopicSet);
        if (!_topicsetService.hasTopicSet(topicSet)) {
            IAlarmTableListener alarmTableListener = new AlarmListener(JmsLogsPlugin.getDefault()
                    .getAlarmSoundService());
            _topicsetService.createAndConnectForTopicSet(topicSet,
                                                         createMessageList(),
                                                         alarmTableListener);
        }
        return _topicsetService.getMessageListForTopicSet(topicSet);
    }
    
    private Integer getMaximumNumberOfMessages() {
        ScopedPreferenceStore prefStore = new ScopedPreferenceStore(new InstanceScope(),
                                                                    JmsLogsPlugin.getDefault()
                                                                            .getBundle()
                                                                            .getSymbolicName());
        String maximumNumberOfMessagesPref = prefStore.getString(LogViewPreferenceConstants.MAX);
        Integer result = 200; // Default
        try {
            result = Integer.parseInt(maximumNumberOfMessagesPref);
        } catch (NumberFormatException e) {
            CentralLogger.getInstance().warn(this,
                                             "Invalid value format for maximum number"
                                                     + " of messages in preferences");
        }
        return result;
    }
    
    /**
     * Write new Column width when a column is resized.
     */
    void addControlListenerToColumns(final String colSetPref, final String topicSetPref) {
        TableColumn[] columns = _tableViewer.getTable().getColumns();
        for (TableColumn tableColumn : columns) {
            tableColumn.addControlListener(new ControlListener() {
                
                @Override
                public void controlResized(final ControlEvent e) {
                    _columnMapping.saveColumn(colSetPref, topicSetPref);
                }
                
                @Override
                public void controlMoved(final ControlEvent e) {
                    // do nothing
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
        Group runningSinceGroup = new Group(logTableManagementComposite, SWT.NONE);
        
        runningSinceGroup.setText(Messages.LogView_runningSince);
        
        RowLayout layout = new RowLayout();
        runningSinceGroup.setLayout(layout);
        _runningSinceLabel = new Label(runningSinceGroup, SWT.CENTER);
        _runningSinceLabel.setLayoutData(new RowData(90, 21));
    }
    
    void setCurrentTimeToRunningSince(final Date time) {
        SimpleDateFormat formater = new SimpleDateFormat();
        _runningSinceLabel.setText(formater.format(time));
    }
    
    /**
     * Add combo box to select set of topics to be monitored. The items in the combo box are names
     * that are mapped in the preferences to sets of topics.
     * 
     * @param logTableManagementComposite
     */
    void addJmsTopicItems(final Composite logTableManagementComposite) {
        Group jmsTopicItemsGroup = new Group(logTableManagementComposite, SWT.NONE);
        
        jmsTopicItemsGroup.setText(Messages.LogView_monitoredJmsTopics);
        
        RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 5;
        jmsTopicItemsGroup.setLayout(layout);
        
        final Combo topicSetsCombo = new Combo(jmsTopicItemsGroup, SWT.SINGLE);
        int i = 0;
        
        for (TopicSet topicSet : _topicSetColumnService.getTopicSets()) {
            topicSetsCombo.add(topicSet.getName());
            if (_currentTopicSet.equals(topicSet.getName())) {
                topicSetsCombo.select(i);
            }
            i++;
        }
        topicSetsCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                super.widgetSelected(e);
                String oldTopicSet = _currentTopicSet;
                _currentTopicSet = _topicSetColumnService.getTopicSets().get(topicSetsCombo
                        .getSelectionIndex()).getName();
                if (!oldTopicSet.equals(_currentTopicSet)) {
                    _messageTable.setMessageUpdatePause(false);
                    _pauseButton.setSelection(false);
                    initializeMessageTable();
                }
            }
        });
        
        _pauseButton = new Button(jmsTopicItemsGroup, SWT.TOGGLE);
        _pauseButton.setLayoutData(new RowData(60, 21));
        
        _pauseButton.setText("Pause");
        
        _pauseButton.addSelectionListener(new SelectionListener() {
            
            public void widgetSelected(final SelectionEvent e) {
                if (_pauseButton.getSelection()) {
                    _messageTable.setMessageUpdatePause(true);
                    if (_timer != null) {
                        if (_timerTask != null) {
                            _timerTask.cancel();
                            _timerTask = null;
                        }
                        _timer.cancel();
                        _timer = null;
                    }
                    _timer = new Timer();
                    _timerTask = new PopUpTimerTask();
                    _timerTask.addExpirationListener(new IExpirationLisener() {
                        
                        @Override
                        public void expired() {
                            _pauseButton.setSelection(false);
                            _tableViewer.refresh();
                            _messageTable.setMessageUpdatePause(false);
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
                    _timer.schedule(_timerTask, 100000, 100000);
                } else {
                    if (_timer != null) {
                        if (_timerTask != null) {
                            _timerTask.cancel();
                            _timerTask = null;
                        }
                        _timer.cancel();
                        _timer = null;
                    }
                    _tableViewer.refresh();
                    _messageTable.setMessageUpdatePause(false);
                }
            }
            
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
        });
        
    }
    
    /**
     * Creates action to call property view.
     */
    void makeActions() {
        Action showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    getSite().getPage().showView(PROPERTY_VIEW_ID);
                } catch (PartInitException e) {
                    MessageDialog.openError(getSite().getShell(), "Alarm Tree", //$NON-NLS-1$
                                            e.getMessage());
                }
            }
        };
        showPropertyViewAction.setText(Messages.LogView_properties);
        showPropertyViewAction.setToolTipText(Messages.LogView_propertiesToolTip);
        
        IViewRegistry viewRegistry = getSite().getWorkbenchWindow().getWorkbench()
                .getViewRegistry();
        IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());
        IActionBars bars = getViewSite().getActionBars();
        bars.getToolBarManager().add(showPropertyViewAction);
    }
    
    /**
     * {@inheritDoc}
     */
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
        _currentTopicSet = memento.getString("previousTopicSet"); //$NON-NLS-1$
        if (_currentTopicSet == null) {
            CentralLogger.getInstance().debug(this, "No topic set from previous session"); //$NON-NLS-1$
        } else {
            CentralLogger.getInstance()
                    .debug(this, "Get topic set from previous session: " + _currentTopicSet); //$NON-NLS-1$
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
        if ( (memento != null) && (_currentTopicSet != null)) {
            CentralLogger.getInstance().debug(this,
                                              "Save latest topic set in IMemento: "
                                                      + _currentTopicSet);
            memento.putString("previousTopicSet", _currentTopicSet); //$NON-NLS-1$
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // TODO jp disconnect is missing
        // _topicsetService.disconnectAll();
        _messageTable = null;
        super.dispose();
    }
}
