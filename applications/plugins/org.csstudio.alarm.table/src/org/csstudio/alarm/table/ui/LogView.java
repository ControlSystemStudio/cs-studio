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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.JMSLogMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.jms.JmsMessageReceiver;
import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * View with table for all log messages from JMS.
 * 
 * @author jhatje
 * 
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
     * List of messages displayed by the table on this view.
     */
    public JMSMessageList _messageList = null;

    /**
     * {@link MessageTable} holding a {@link TableViewer} for messages.
     */
    public MessageTable _messageTable = null;

    /**
     * List of topic sets and names from preferences. Displayed in combo box.
     */
    private HashMap<String, String> _topicListAndName;

    /**
     * Default topic set. Try to read state 1. From previous viewPart data 2.
     * From default marker in preferences 3. Take first set from preferences
     */
    String _defaultTopicSet;

    /**
     * The receiver for JMS messages.
     */
    JmsMessageReceiver _jmsMessageReceiver;

    /**
     * Mapping of column widths from table to preferences.
     */
    ColumnWidthPreferenceMapping _columnMapping;

    /**
     * The JFace {@link TableViewer}
     */
    TableViewer _tableViewer;

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {

        // Read column names and JMS topic settings from preferences
        String[] _columnNames = JmsLogsPlugin.getDefault()
                .getPluginPreferences().getString(
                        LogViewPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
        readPreferenceTopics(JmsLogsPlugin.getDefault().getPluginPreferences()
                .getString(LogViewPreferenceConstants.TOPIC_SET));

        // Initialize JMS message list
        String maximumNumberOfMessagesPref = JmsLogsPlugin.getDefault()
                .getPluginPreferences().getString(
                        LogViewPreferenceConstants.MAX);
        Integer maximumNumberOfMessages;
        try {
            maximumNumberOfMessages = Integer.parseInt(maximumNumberOfMessagesPref);
        } catch (NumberFormatException e) {
            CentralLogger
                    .getInstance()
                    .warn(this,
                            "Invalid value format for maximum number" +
                            " of messages in preferences");
            maximumNumberOfMessages = 200; 
        }
        _messageList = new JMSLogMessageList(_columnNames, maximumNumberOfMessages);

        // Create UI
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);

        Composite logTableManagementComposite = new Composite(parent, SWT.NONE);
        RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 8;
        logTableManagementComposite.setLayout(layout);

        addJmsTopicItems(logTableManagementComposite);
        addRunningSinceGroup(logTableManagementComposite);

        // setup message table with context menu etc.
        _tableViewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION);

        _messageTable = new MessageTable(_tableViewer, _columnNames,
                _messageList);
        _messageTable.makeContextMenu(getSite());

        _columnMapping = new ColumnWidthPreferenceMapping(_tableViewer);

        getSite().setSelectionProvider(_tableViewer);

        makeActions();

        parent.pack();
        // _propertyChangeListener = new ColumnPropertyChangeListener(
        // LogViewPreferenceConstants.P_STRING, _messageTable);

        // JmsLogsPlugin.getDefault().getPluginPreferences()
        // .addPropertyChangeListener(_propertyChangeListener);

        // Setup JMS connection
        _jmsMessageReceiver = new JmsMessageReceiver(_messageList);
        _jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
    }

    /**
     * Parse string from properties with jms topics settings and store items in
     * a HashMap for combo box. Each set of topic items is separated with ';'. A
     * topic item consists of three parts separated by '?';
     * 'default?jmsTopic1,jmsTopic2?nameOfThisTopicSet' If the topic item is not
     * default the first part is empty: '?jmsTopic1jms...'. If there is no
     * default the first item is taken. The default tag from the preferences is
     * overwritten if there is a topic set from a previous session.
     * 
     * @param topics
     *            raw topic string from preferences
     * @return set of topics for initialization
     */
    void readPreferenceTopics(String topics) {
        _topicListAndName = new HashMap<String, String>();
        String[] topicSetsAndNames = topics.split(";"); //$NON-NLS-1$
        for (String topicSet : topicSetsAndNames) {
            String[] topicSetItems = topicSet.split("\\?"); //$NON-NLS-1$
            // preference string for topic set is invalid -> next one
            if (topicSetItems.length < 3) {
                continue;
            }
            _topicListAndName.put(topicSetItems[2], topicSetItems[1]);
            if (_defaultTopicSet == null) {
                _defaultTopicSet = topicSetItems[1];
            }
            if (topicSetItems[0].equals("default")) { //$NON-NLS-1$
                _defaultTopicSet = topicSetItems[1];
            }
        }
        return;
    }

    /**
     * Add label with date and time the table is started.
     * 
     * @param logTableManagementComposite
     */
    void addRunningSinceGroup(Composite logTableManagementComposite) {
        Group runningSinceGroup = new Group(logTableManagementComposite,
                SWT.NONE);

        runningSinceGroup.setText(Messages.LogView_runningSince);

        RowLayout layout = new RowLayout();
        runningSinceGroup.setLayout(layout);

        GregorianCalendar currentTime = new GregorianCalendar(TimeZone
                .getTimeZone("ECT")); //$NON-NLS-1$
        SimpleDateFormat formater = new SimpleDateFormat();
        Label runningSinceLabel = new Label(runningSinceGroup, SWT.CENTER);
        runningSinceLabel.setLayoutData(new RowData(90, 21));
        runningSinceLabel.setText(formater.format(currentTime.getTime()));
    }

    /**
     * Add combo box to select set of topics to be monitored. The items in the
     * combo box are names that are mapped in the preferences to sets of topics.
     * 
     * @param logTableManagementComposite
     */
    void addJmsTopicItems(Composite logTableManagementComposite) {
        Group jmsTopicItemsGroup = new Group(logTableManagementComposite,
                SWT.NONE);

        jmsTopicItemsGroup.setText(Messages.LogView_monitoredJmsTopics);

        RowLayout layout = new RowLayout();
        layout.type = SWT.HORIZONTAL;
        layout.spacing = 5;
        jmsTopicItemsGroup.setLayout(layout);

        final Combo topicSetsCombo = new Combo(jmsTopicItemsGroup, SWT.SINGLE);
        int i = 0;

        for (String topicSetName : _topicListAndName.keySet()) {
            topicSetsCombo.add(topicSetName);
            if (_defaultTopicSet.equals(_topicListAndName.get(topicSetName))) {
                topicSetsCombo.select(i);
            }
            i++;
        }
        topicSetsCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                _defaultTopicSet = _topicListAndName.get(topicSetsCombo
                        .getItem(topicSetsCombo.getSelectionIndex()));
                _jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
            }
        });
    }

    /**
     * Creates action to call property view.
     */
    void makeActions() {
        Action _showPropertyViewAction = new Action() {
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
        _showPropertyViewAction.setText(Messages.LogView_properties);
        _showPropertyViewAction
                .setToolTipText(Messages.LogView_propertiesToolTip);

        IViewRegistry viewRegistry = getSite().getWorkbenchWindow()
                .getWorkbench().getViewRegistry();
        IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        _showPropertyViewAction.setImageDescriptor(viewDesc
                .getImageDescriptor());
        IActionBars bars = getViewSite().getActionBars();
        bars.getToolBarManager().add(_showPropertyViewAction);
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
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        if (memento == null) {
            return;
        }
        _defaultTopicSet = memento.getString("previousTopicSet"); //$NON-NLS-1$
        if (_defaultTopicSet == null) {
            CentralLogger.getInstance().debug(this,
                    "No topic set from previous session"); //$NON-NLS-1$
        } else {
            CentralLogger.getInstance().debug(this,
                    "Get topic set from previous session: " + _defaultTopicSet); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        if ((memento != null) && (_defaultTopicSet != null)) {
            CentralLogger.getInstance().debug(this,
                    "Save latest topic set in IMemento: " + _defaultTopicSet);
            memento.putString("previousTopicSet", _defaultTopicSet); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        _columnMapping.saveColumn(LogViewPreferenceConstants.P_STRING);
        _jmsMessageReceiver.stopJMSConnection();
        _messageTable = null;
        // JmsLogsPlugin.getDefault().getPluginPreferences()
        // .removePropertyChangeListener(_propertyChangeListener);
        super.dispose();
    }
}
