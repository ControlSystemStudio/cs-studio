/////*
//// * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
//// * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
//// *
//// * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
//// * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
//// * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
//// * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
//// * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
//// * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
//// * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
//// * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
//// * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
//// * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
//// * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
//// * OR MODIFICATIONS.
//// * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
//// * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
//// * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
//// * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
//// */
////
////package org.csstudio.alarm.table.ui;
////
////import java.text.SimpleDateFormat;
////import java.util.GregorianCalendar;
////import java.util.HashMap;
////import java.util.TimeZone;
////
////import org.csstudio.alarm.table.ColumnPropertyChangeListener;
////import org.csstudio.alarm.table.JmsLogsPlugin;
////import org.csstudio.alarm.table.dataModel.JMSLogMessageList;
////import org.csstudio.alarm.table.dataModel.JMSMessageList;
////import org.csstudio.alarm.table.internal.localization.Messages;
////import org.csstudio.alarm.table.jms.JmsMessageReceiver;
////import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
////import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
////import org.csstudio.platform.logging.CentralLogger;
////import org.eclipse.jface.action.Action;
////import org.eclipse.jface.action.IToolBarManager;
////import org.eclipse.jface.dialogs.MessageDialog;
////import org.eclipse.jface.preference.IPreferenceStore;
////import org.eclipse.swt.SWT;
////import org.eclipse.swt.events.SelectionAdapter;
////import org.eclipse.swt.events.SelectionEvent;
////import org.eclipse.swt.layout.GridLayout;
////import org.eclipse.swt.layout.RowData;
////import org.eclipse.swt.layout.RowLayout;
////import org.eclipse.swt.widgets.Combo;
////import org.eclipse.swt.widgets.Composite;
////import org.eclipse.swt.widgets.Group;
////import org.eclipse.swt.widgets.Label;
////import org.eclipse.ui.IActionBars;
////import org.eclipse.ui.IMemento;
////import org.eclipse.ui.IViewSite;
////import org.eclipse.ui.PartInitException;
////import org.eclipse.ui.part.ViewPart;
////import org.eclipse.ui.views.IViewDescriptor;
////import org.eclipse.ui.views.IViewRegistry;
////
/////**
//// * View with table for all log messages from JMS. Creates the TableViewer
//// * <code>JMSLogTableViewer</code>, holds the model <code>JMSMessageList</code>
//// * 
//// * @author jhatje
//// * 
//// */
////public class LogViewOLD extends ViewPart {
////
////	public static final String ID = LogViewOLD.class.getName();
////
////	public JMSMessageList _messageList = null;
////
////	public JMSLogTableViewer _tableViewer = null;
////
////	public String[] _columnNames;
////
////	public ColumnPropertyChangeListener _propertyChangeListener;
////
////	/**
////	 * Action to call property view.
////	 */
////	private Action _showPropertyViewAction;
////
////	/**
////	 * The ID of the property view.
////	 */
////	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
////
////	/**
////	 * List of topic sets and names from preferences. Displayed in combo box.
////	 */
////	private HashMap<String, String> _topicListAndName;
////
////	/**
////	 * Default topic set. Try to read state 1. From previous viewPart data 2.
////	 * From default marker in preferences 3. Take first set from preferences
////	 */
////	String _defaultTopicSet;
////
////	JmsMessageReceiver _jmsMessageReceiver;
////
////	public void createPartControl(Composite parent) {
////		_columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
////				.getString(LogViewPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
////		_messageList = new JMSLogMessageList(_columnNames);
////
////		readPreferenceTopics(JmsLogsPlugin.getDefault().getPluginPreferences()
////				.getString(LogViewPreferenceConstants.TOPIC_SET));
////
////		GridLayout grid = new GridLayout();
////		grid.numColumns = 1;
////		parent.setLayout(grid);
////
////		Composite logTableManagementComposite = new Composite(parent, SWT.NONE);
////		RowLayout layout = new RowLayout();
////		layout.type = SWT.HORIZONTAL;
////		layout.spacing = 8;
////		logTableManagementComposite.setLayout(layout);
////
////		addJmsTopicItems(logTableManagementComposite);
////
////		addRunningSinceGroup(logTableManagementComposite);
////
////		_tableViewer = new JMSLogTableViewer(parent, getSite(), _columnNames,
////				_messageList, 1, SWT.MULTI | SWT.FULL_SELECTION);
////		_tableViewer.setAlarmSorting(false);
////		parent.pack();
////
////		getSite().setSelectionProvider(_tableViewer);
////
////		makeActions();
////		IActionBars bars = getViewSite().getActionBars();
////		fillLocalToolBar(bars.getToolBarManager());
////
////		_propertyChangeListener = new ColumnPropertyChangeListener(
////				LogViewPreferenceConstants.P_STRING, _tableViewer);
////
////		JmsLogsPlugin.getDefault().getPluginPreferences()
////				.addPropertyChangeListener(_propertyChangeListener);
////		
////		_jmsMessageReceiver = new JmsMessageReceiver(_messageList);
////
////		_jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
////	}
////
////	/**
////	 * Parse string from properties with jms topics settings and store items in
////	 * a HashMap for combo box. Each set of topic items is separated with ';'. A
////	 * topic item consists of three parts separated by '?';
////	 * 'default?jmsTopic1,jmsTopic2?nameOfThisTopicSet' If the topic item is not
////	 * default the first part is empty: '?jmsTopic1jms...'. If there is no
////	 * default the first item is taken. The default tag from the preferences is
////	 * overwritten if there is a topic set from a previous session.
////	 * 
////	 * @param topics
////	 *            raw topic string from preferences
////	 * @return set of topics for initialization
////	 */
////	void readPreferenceTopics(String topics) {
////		_topicListAndName = new HashMap<String, String>();
////		String[] topicSetsAndNames = topics.split(";"); //$NON-NLS-1$
////		for (String topicSet : topicSetsAndNames) {
////			String[] topicSetItems = topicSet.split("\\?"); //$NON-NLS-1$
////			//preference string for topic set is invalid -> next one
////			if (topicSetItems.length < 3) {
////				continue;
////			}
////			_topicListAndName.put(topicSetItems[2], topicSetItems[1]);
////			if ((_defaultTopicSet == null)
////					|| (topicSetItems[0].equals("default")) //$NON-NLS-1$
////					|| (_defaultTopicSet.equals(topicSetItems[1]))) {
////				_defaultTopicSet = topicSetItems[1];
////			}
////		}
////		return;
////	}
////
////	/**
////	 * Add label with date and time the table is started.
////	 * 
////	 * @param logTableManagementComposite
////	 */
////	void addRunningSinceGroup(Composite logTableManagementComposite) {
////		Group runningSinceGroup = new Group(logTableManagementComposite,
////				SWT.NONE);
////
////		runningSinceGroup.setText(Messages.LogView_runningSince);
////
////		RowLayout layout = new RowLayout();
////		runningSinceGroup.setLayout(layout);
////
////		GregorianCalendar currentTime = new GregorianCalendar(TimeZone
////				.getTimeZone("ECT")); //$NON-NLS-1$
////		SimpleDateFormat formater = new SimpleDateFormat();
////		Label runningSinceLabel = new Label(runningSinceGroup, SWT.CENTER);
////		runningSinceLabel.setLayoutData(new RowData(90, 21));
////		runningSinceLabel.setText(formater.format(currentTime.getTime()));
////	}
////
////	/**
////	 * Add combo box to select set of topics to be monitored. The items in the
////	 * combo box are names that are mapped in the preferences to sets of topics.
////	 * 
////	 * @param logTableManagementComposite
////	 */
////	void addJmsTopicItems(Composite logTableManagementComposite) {
////		Group jmsTopicItemsGroup = new Group(logTableManagementComposite,
////				SWT.NONE);
////
////		jmsTopicItemsGroup.setText(Messages.LogView_monitoredJmsTopics);
////
////		RowLayout layout = new RowLayout();
////		layout.type = SWT.HORIZONTAL;
////		layout.spacing = 5;
////		jmsTopicItemsGroup.setLayout(layout);
////
////		final Combo topicSetsCombo = new Combo(jmsTopicItemsGroup, SWT.SINGLE);
////		int i = 0;
////
////		for (String topicSetName : _topicListAndName.keySet()) {
////			topicSetsCombo.add(topicSetName);
////			if (_defaultTopicSet.equals(_topicListAndName.get(topicSetName))) {
////				topicSetsCombo.select(i);
////			}
////			i++;
////		}
////		topicSetsCombo.addSelectionListener(new SelectionAdapter() {
////			@Override
////			public void widgetSelected(SelectionEvent e) {
////				super.widgetSelected(e);
////				_defaultTopicSet = _topicListAndName.get(topicSetsCombo
////						.getItem(topicSetsCombo.getSelectionIndex()));
////				_jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
////			}
////		});
////	}
////
////
////
////	/**
////	 * Creates action to call property view.
////	 */
////	void makeActions() {
////		_showPropertyViewAction = new Action() {
////			@Override
////			public void run() {
////				try {
////					getSite().getPage().showView(PROPERTY_VIEW_ID);
////				} catch (PartInitException e) {
////					MessageDialog.openError(getSite().getShell(), "Alarm Tree", //$NON-NLS-1$
////							e.getMessage());
////				}
////			}
////		};
////		_showPropertyViewAction.setText(Messages.LogView_properties);
////		_showPropertyViewAction.setToolTipText(Messages.LogView_propertiesToolTip);
////
////		IViewRegistry viewRegistry = getSite().getWorkbenchWindow()
////				.getWorkbench().getViewRegistry();
////		IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
////		_showPropertyViewAction.setImageDescriptor(viewDesc
////				.getImageDescriptor());
////	}
////
////	/**
////	 * Add 'show property action' to tool.
////	 * 
////	 * @param manager
////	 *            the menu manager.
////	 */
////	void fillLocalToolBar(final IToolBarManager manager) {
////		manager.add(_showPropertyViewAction);
////	}
////
////	public void setFocus() {
////	}
////
////	@Override
////	public void init(IViewSite site, IMemento memento) throws PartInitException {
////		super.init(site, memento);
////		if (memento == null) {
////			return;
////		}
////			_defaultTopicSet = memento.getString("previousTopicSet"); //$NON-NLS-1$
////		if (_defaultTopicSet == null) {
////			CentralLogger.getInstance().debug(this,
////					"No topic set from previous session"); //$NON-NLS-1$
////		} else {
////			CentralLogger.getInstance().debug(this,
////					"Get topic set from previous session: " + _defaultTopicSet); //$NON-NLS-1$
////		}
////	}
////
////	@Override
////	public void saveState(IMemento memento) {
////		super.saveState(memento);
////		if ((memento != null) && (_defaultTopicSet != null)) {
////			memento.putString("previousTopicSet", _defaultTopicSet); //$NON-NLS-1$
////		}
////	}
////
////	public void dispose() {
////		saveColumn();
////		_tableViewer = null;
////		JmsLogsPlugin.getDefault().getPluginPreferences()
////				.removePropertyChangeListener(_propertyChangeListener);
////		super.dispose();
////	}
////
////	/**
////	 * When dispose store the width for each column.
////	 */
////	public void saveColumn() {
////		int[] width = _tableViewer.getColumnWidth();
////		String newPreferenceColumnString = ""; //$NON-NLS-1$
////		String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
////				.getString(LogViewPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
////		if (width.length != columns.length) {
////			return;
////		}
////		for (int i = 0; i < columns.length; i++) {
////			newPreferenceColumnString = newPreferenceColumnString
////					.concat(columns[i].split(",")[0] + "," + width[i] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
////		}
////		newPreferenceColumnString = newPreferenceColumnString.substring(0,
////				newPreferenceColumnString.length() - 1);
////		IPreferenceStore store = JmsLogsPlugin.getDefault()
////				.getPreferenceStore();
////		store.setValue(LogViewPreferenceConstants.P_STRING,
////				newPreferenceColumnString);
////		if (store.needsSaving()) {
////			JmsLogsPlugin.getDefault().savePluginPreferences();
////		}
////	}
////}
//
//
//
//
//
//
//_____________________________________________________________________
//
//
///*
// * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
// * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
// *
// * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
// * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
// * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
// * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
// * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
// * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
// * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
// * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
// * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
// * OR MODIFICATIONS.
// * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
// * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
// * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
// * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
// */
//
//package org.csstudio.alarm.table.ui;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Observable;
//import java.util.Observer;
//
//import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
//import org.csstudio.alarm.dbaccess.archivedb.Filter;
//import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
//import org.csstudio.alarm.table.ColumnPropertyChangeListener;
//import org.csstudio.alarm.table.JmsLogsPlugin;
//import org.csstudio.alarm.table.dataModel.JMSMessage;
//import org.csstudio.alarm.table.dataModel.JMSMessageList;
//import org.csstudio.alarm.table.expertSearch.ExpertSearchDialog;
//import org.csstudio.alarm.table.internal.localization.Messages;
//import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
//import org.csstudio.alarm.table.preferences.ArchiveViewPreferenceConstants;
//import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
//import org.csstudio.alarm.table.readDB.AccessDBJob;
//import org.csstudio.alarm.table.readDB.DBAnswer;
//import org.csstudio.apputil.time.StartEndTimeParser;
//import org.csstudio.apputil.ui.time.StartEndDialog;
//import org.csstudio.platform.data.ITimestamp;
//import org.csstudio.platform.data.TimestampFactory;
//import org.csstudio.platform.logging.CentralLogger;
//import org.csstudio.platform.model.IProcessVariable;
//import org.csstudio.platform.security.SecurityFacade;
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.IToolBarManager;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.preference.IPreferenceStore;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.layout.RowData;
//import org.eclipse.swt.layout.RowLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.FileDialog;
//import org.eclipse.swt.widgets.Group;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.ui.IActionBars;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.handlers.HandlerUtil;
//import org.eclipse.ui.part.ViewPart;
//import org.eclipse.ui.views.IViewDescriptor;
//import org.eclipse.ui.views.IViewRegistry;
//
///**
// * View for message read from oracel DB.
// * 
// * @author jhatje
// * @author $Author$
// * @version $Revision$
// * @since 19.07.2007
// */
//public class ArchiveView extends ViewPart implements Observer {
//
//    /** The Id of this Object. */
//    public static final String ID = ArchiveView.class.getName();
//
//    /** The Parent Shell. */
//    private Shell _parentShell = null;
//
//    /** The JMS message list. */
//    private JMSMessageList _jmsMessageList = null;
//
//    /** The JMS Logtable Viewer. */
//    private JMSLogTableViewer _jmsLogTableViewer = null;
//
//    /** An Array whit the name of the Columns. */
//    private String[] _columnNames;
//
//    /** Textfield witch contain the "from time". */
//    private Text _timeFrom;
//    /** Textfield witch contain the "to time". */
//    private Text _timeTo;
//
//    /** The selectet "from time". */
//    private ITimestamp _fromTime;
//    /** The selectet "to time". */
//    private ITimestamp _toTime;
//
//    /** The column property change listener. */
//    private ColumnPropertyChangeListener _columnPropertyChangeListener;
//
//    /** The default / last filter. */
//    //  private String _filter = ""; //$NON-NLS-1$
//    /**
//     * The Answer from the DB.
//     */
//    private DBAnswer _dbAnswer = null;
//
//    /** The Display. */
//    private Display _disp;
//
//    /** The count of results. */
//    private Label _countLabel;
//
//    private ArrayList<FilterItem> _filterSettings;
//
//    /**
//     * Current settings of the filter that they are available to delete the
//     * displayed messages.
//     */
//    private Filter _filter;
//    // private GregorianCalendar _from;
//    // private GregorianCalendar _to;
//    // private ArrayList<FilterItem> _filterSettings;
//
//    private AccessDBJob _dbReader = new AccessDBJob("DBReader"); //$NON-NLS-1$
//
//    /**
//     * The Show Property View action.
//     */
//    private Action _showPropertyViewAction;
//
//    private boolean _canExecute = false;
//
//    /**
//     * The ID of the property view.
//     */
//    private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
//
//    private static final String SECURITY_ID = "alarmAdministration"; //$NON-NLS-1$
//
//    public ArchiveView() {
//        super();
//        _dbAnswer = new DBAnswer();
//        _dbAnswer.addObserver(this);
//    }
//
//    /** {@inheritDoc} */
//    public final void createPartControl(final Composite parent) {
//        _canExecute = SecurityFacade.getInstance()
//                .canExecute(SECURITY_ID, true);
//
//        _disp = parent.getDisplay();
//
//        _columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
//                .getString(ArchiveViewPreferenceConstants.P_STRINGArch).split(
//                        ";"); //$NON-NLS-1$
//        _jmsMessageList = new JMSMessageList(_columnNames);
//
//        _parentShell = parent.getShell();
//
//        // Create UI
//        GridLayout grid = new GridLayout();
//        grid.numColumns = 1;
//        parent.setLayout(grid);
//
//        Composite archiveTableManagementComposite = new Composite(parent,
//                SWT.NONE);
//        RowLayout layout = new RowLayout();
//        layout.type = SWT.HORIZONTAL;
//        layout.type = SWT.WRAP;
//        layout.spacing = 8;
//        archiveTableManagementComposite.setLayout(layout);
//
//        addSearchButtons(archiveTableManagementComposite);
//        addShownPeriod(archiveTableManagementComposite);
//
//        Composite comp = parent;
//
//        GridData gd;
//        Group count = new Group(comp, SWT.LINE_SOLID);
//        count.setText(Messages.getString("LogViewArchive_count")); //$NON-NLS-1$
//        count.setLayout(new GridLayout(1, true));
//        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
//        gd.minimumHeight = 60;
//        gd.minimumWidth = 40;
//        count.setLayoutData(gd);
//
//        Group messageButtons = new Group(comp, SWT.LINE_SOLID);
//        messageButtons.setText(Messages.ViewArchive_messagesGroup);
//        GridLayout layoutXX = new GridLayout(1, true);
//        messageButtons.setLayout(layoutXX);
//        gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
//        gd.minimumHeight = 60;
//        gd.minimumWidth = 60;
//        messageButtons.setLayoutData(gd);
//        createExportButton(messageButtons);
//
//        if (_canExecute) {
//            layoutXX.numColumns = 2;
//            gd.minimumWidth = 120;
//            createDeleteButton(messageButtons);
//        }
//
//        _countLabel = new Label(count, SWT.RIGHT);
//        gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
//        _countLabel.setLayoutData(gd);
//        _countLabel.setText("0"); //$NON-NLS-1$
//
//        _jmsLogTableViewer = new JMSLogTableViewer(parent, getSite(),
//                _columnNames, _jmsMessageList, 3, SWT.SINGLE
//                        | SWT.FULL_SELECTION);
//        _jmsLogTableViewer.setAlarmSorting(false);
//
//        getSite().setSelectionProvider(_jmsLogTableViewer);
//        makeActions();
//        IActionBars bars = getViewSite().getActionBars();
//        fillLocalToolBar(bars.getToolBarManager());
//
//        parent.pack();
//
//        _columnPropertyChangeListener = new ColumnPropertyChangeListener(
//                ArchiveViewPreferenceConstants.P_STRINGArch, _jmsLogTableViewer);
//
//        JmsLogsPlugin.getDefault().getPluginPreferences()
//                .addPropertyChangeListener(_columnPropertyChangeListener);
//
//    }
//
//    private void addShownPeriod(final Composite archiveTableManagementComposite) {
//
//        Group shownPeriodGroup = new Group(archiveTableManagementComposite,
//                SWT.LINE_SOLID);
//        shownPeriodGroup.setText(Messages.getString("LogViewArchive_period")); //$NON-NLS-1$
//        RowLayout layout = new RowLayout();
//        shownPeriodGroup.setLayout(layout);
//
//        
//
//        new Label(shownPeriodGroup, SWT.NONE).setText(Messages.ViewArchive_fromTime);
//
//        _timeFrom = new Text(shownPeriodGroup, SWT.SINGLE);
//        _timeFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
//                1, 1));
//
//        _timeFrom.setEditable(false);
//        _timeFrom.setText("                            "); //$NON-NLS-1$
//
//        new Label(shownPeriodGroup, SWT.NONE).setText(Messages.ViewArchive_toTime);
//        _timeTo = new Text(shownPeriodGroup, SWT.SINGLE);
//        _timeTo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
//                1));
//        _timeTo.setEditable(false);
//    }
//
//    /**
//     * Button to delete messages currently displayed in the table. *
//     * 
//     * @param comp
//     */
//    private void createDeleteButton(Composite comp) {
//        Button delete = new Button(comp, SWT.PUSH);
//        delete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
//                1));
//        delete.setText(Messages.ViewArchive_deleteButton);
//
//        delete.addSelectionListener(new SelectionAdapter() {
//
//            /**
//             * Set the 'delete' flag and use the current settings of the select
//             * statement to delete the messages. The command will delete all
//             * messages even if the table shows just a subset!!
//             */
//            public void widgetSelected(final SelectionEvent e) {
//                _dbReader.setReadProperties(ArchiveView.this._dbAnswer, _filter
//                        .copy());
//                _dbReader
//                        .setAccessType(AccessDBJob.DBAccessType.READ_MSG_NUMBER_TO_DELETE);
//                _dbReader.schedule();
//            }
//        });
//
//    }
//
//    /**
//     * Button to export messages currently displayed in the table. *
//     * 
//     * @param comp
//     */
//    private void createExportButton(Composite comp) {
//        Button export = new Button(comp, SWT.PUSH);
//        export.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
//                1));
//        export.setText(Messages.ViewArchive_6);
//
//        export.addSelectionListener(new SelectionAdapter() {
//
//            /**
//             * 
//             */
//            public void widgetSelected(final SelectionEvent e) {
//                // File standard dialog
//                FileDialog fileDialog = new FileDialog(Display.getDefault()
//                        .getActiveShell(), SWT.SAVE);
//
//                // Set the text
//                fileDialog.setText(Messages.ViewArchive_7);
//                // Set filter on .txt files
//                fileDialog
//                        .setFilterExtensions(new String[] { Messages.ViewArchive_8 });
//                // Put in a readable name for the filter
//                fileDialog
//                        .setFilterNames(new String[] { Messages.ViewArchive_9 });
//
//                // Open Dialog and save result of selection
//                String selected = fileDialog.open();
//
//                File path = new File(selected);
//
//                _dbReader.setReadProperties(ArchiveView.this._dbAnswer, _filter
//                        .copy(), path, _columnNames);
//                _dbReader.setAccessType(AccessDBJob.DBAccessType.EXPORT);
//                _dbReader.schedule();
//            }
//        });
//
//    }
//
//    /**
//     * Creates the actions offered by this view.
//     */
//    private void makeActions() {
//        _showPropertyViewAction = new Action() {
//            @Override
//            public void run() {
//                try {
//                    getSite().getPage().showView(PROPERTY_VIEW_ID);
//                } catch (PartInitException e) {
//                    MessageDialog.openError(getSite().getShell(),
//                            Messages.ViewArchive_10, e.getMessage());
//                }
//            }
//        };
//        _showPropertyViewAction.setText(Messages.ViewArchive_11);
//        _showPropertyViewAction.setToolTipText(Messages.ViewArchive_12);
//
//        IViewRegistry viewRegistry = getSite().getWorkbenchWindow()
//                .getWorkbench().getViewRegistry();
//        IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
//        _showPropertyViewAction.setImageDescriptor(viewDesc
//                .getImageDescriptor());
//    }
//
//    /**
//     * Adds the tool bar actions.
//     * 
//     * @param manager
//     *            the menu manager.
//     */
//    private void fillLocalToolBar(final IToolBarManager manager) {
//        manager.add(_showPropertyViewAction);
//    }
//
//    private void addSearchButtons(Composite archiveTableManagementComposite) {
//        Group searchButtonsGroup = new Group(archiveTableManagementComposite,
//                SWT.LINE_SOLID);
//        searchButtonsGroup.setText(Messages.getString("LogViewArchive_period")); //$NON-NLS-1$
//        RowLayout layout = new RowLayout();
//        searchButtonsGroup.setLayout(layout);
//
//        createFixedSearchButton(searchButtonsGroup, 24, "LogViewArchive_day");
//        createFixedSearchButton(searchButtonsGroup, 72, "LogViewArchive_3days");
//        createFixedSearchButton(searchButtonsGroup, 168, "LogViewArchive_week");
//        createVariableSearchButton(searchButtonsGroup);
//        createVariableFilterSearchButton(searchButtonsGroup);
//
//    }
//
//    /**
//     * Create a button to search for a fixed period and add the selection
//     * listener.
//     * 
//     * @param comp
//     *            Composite for the new button
//     * @param hours
//     *            Period (hours) to search for
//     * @param textTag
//     *            Tag for text on the button. The tag identifies the text set in
//     *            the message class for internationalization.
//     */
//    private void createFixedSearchButton(final Composite comp, final int hours,
//            String textTag) {
//        Button fixedSearchButton = new Button(comp, SWT.PUSH);
//        fixedSearchButton.setLayoutData(new RowData(50, 21));
//        fixedSearchButton.setText(Messages.getString(textTag)); //$NON-NLS-1$
//
//        fixedSearchButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(final SelectionEvent e) {
//                GregorianCalendar _to = new GregorianCalendar();
//                GregorianCalendar _from = (GregorianCalendar) _to.clone();
//                _from.add(GregorianCalendar.HOUR_OF_DAY, (-1 * hours));
//                _filter = new Filter(null, _from, _to);
//                callDBReadJob();
//            }
//        });
//
//    }
//
//    /**
//     * Create a button that opens a dialog to enter individual period.
//     * 
//     * @param comp
//     *            Parent composite for new button.
//     */
//    private void createVariableSearchButton(final Composite comp) {
//        Button searchButton = new Button(comp, SWT.PUSH);
//        searchButton.setLayoutData(new RowData(50, 21));
//        searchButton.setText(Messages.getString("LogViewArchive_user")); //$NON-NLS-1$
//
//        searchButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(final SelectionEvent e) {
//                StartEndDialog dlg;
//                if (_fromTime != null && _toTime != null) {
//                    dlg = new StartEndDialog(_parentShell,
//                            _fromTime.toString(), _toTime.toString());
//                } else {
//                    dlg = new StartEndDialog(_parentShell);
//                }
//                if (dlg.open() == StartEndDialog.OK) {
//                    String lowString = dlg.getStartSpecification();
//                    String highString = dlg.getEndSpecification();
//                    try {
//                        StartEndTimeParser parser = new StartEndTimeParser(
//                                lowString, highString);
//                        GregorianCalendar _from = (GregorianCalendar) parser
//                                .getStart();
//                        _fromTime = TimestampFactory.fromCalendar(_from);
//                        GregorianCalendar _to = (GregorianCalendar) parser
//                                .getEnd();
//                        _toTime = TimestampFactory.fromCalendar(_to);
//                        _filter = new Filter(null, _from, _to);
//                        callDBReadJob();
//                    } catch (Exception e1) {
//                        // TODO Auto-generated catch block
//                        JmsLogsPlugin.logInfo(e1.getMessage());
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * Create a button that opens a dialog to enter individual period
//     * and set filter conditions.
//     * 
//     * @param comp
//     *            Parent composite for new button.
//     */
//    private void createVariableFilterSearchButton(final Composite comp) {
//        Button bSearch = new Button(comp, SWT.PUSH);
//        bSearch.setLayoutData(new RowData(50, 21));
//        bSearch.setText(Messages.getString("LogViewArchive_expert")); //$NON-NLS-1$
//
//        bSearch.addSelectionListener(new SelectionAdapter() {
//
//            public void widgetSelected(final SelectionEvent e) {
//                if (_fromTime == null) {
//                    ITimestamp now = TimestampFactory.now();
//                    _fromTime = TimestampFactory.createTimestamp(now.seconds()
//                            - (24 * 60 * 60), now.nanoseconds()); // new
//                    // Timestamp(fromDate.getTime()/1000);
//                }
//                if (_toTime == null) {
//                    _toTime = TimestampFactory.now();
//                }
//
//                ExpertSearchDialog dlg = new ExpertSearchDialog(_parentShell,
//                        _fromTime, _toTime, _filterSettings);
//
//                GregorianCalendar _to = new GregorianCalendar();
//                GregorianCalendar _from = (GregorianCalendar) _to.clone();
//                if (dlg.open() == ExpertSearchDialog.OK) {
//                    _fromTime = dlg.getStart();
//                    _toTime = dlg.getEnd();
//                    double low = _fromTime.toDouble();
//                    double high = _toTime.toDouble();
//                    if (low < high) {
//                        _from.setTimeInMillis((long) low * 1000);
//                        _to.setTimeInMillis((long) high * 1000);
//                    } else {
//                        _from.setTimeInMillis((long) high * 1000);
//                        _to.setTimeInMillis((long) low * 1000);
//                    }
//                    _filterSettings = dlg.get_filterConditions();
//                    _filter = new Filter(_filterSettings, _from, _to);
//                    callDBReadJob();
//                }
//            }
//
//        });
//    }
//
//    public void readDBFromExternalCall(IProcessVariable pv) {
//        GregorianCalendar _from = new GregorianCalendar();
//        GregorianCalendar _to = new GregorianCalendar();
//        _from.setTimeInMillis(_to.getTimeInMillis() - 1000 * 60 * 60 * 24);
//        showNewTime(_from, _to);
//        ArrayList<FilterItem> _filterSettings = new ArrayList<FilterItem>();
//        _filterSettings.add(new FilterItem(Messages.ViewArchive_13, pv
//                .getName(), Messages.ViewArchive_14));
//        _filter = new Filter(_filterSettings, _from, _to);
//        callDBReadJob();
//    }
//
//    private void callDBReadJob() {
//        showNewTime(_filter.getFrom(), _filter.getTo());
//        _dbReader.setReadProperties(ArchiveView.this._dbAnswer, _filter.copy());
//        _countLabel.setText(Messages.ViewArchive_15);
//        _jmsLogTableViewer.getTable().setEnabled(false);
//        _dbReader.setAccessType(AccessDBJob.DBAccessType.READ_MESSAGES);
//        _dbReader.schedule();
//    }
//
//    /**
//     * Set the two times from, to .
//     * 
//     * @param from
//     * @param to
//     */
//    private void showNewTime(final Calendar from, final Calendar to) {
//        SimpleDateFormat sdf = new SimpleDateFormat();
//        try {
//            sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore()
//                    .getString(ArchiveViewPreferenceConstants.DATE_FORMAT));
//        } catch (Exception e) {
//            sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore()
//                    .getDefaultString(
//                            ArchiveViewPreferenceConstants.DATE_FORMAT));
//            JmsLogsPlugin.getDefault().getPreferenceStore().setToDefault(
//                    ArchiveViewPreferenceConstants.DATE_FORMAT);
//        }
//        _timeFrom.setText(sdf.format(from.getTime()));
//        _fromTime = TimestampFactory.fromCalendar(from);
//
//        _timeTo.setText(sdf.format(to.getTime()));
//        _toTime = TimestampFactory.fromCalendar(to);
//        // redraw
//        _timeFrom.getParent().getParent().redraw();
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void setFocus() {
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public final void dispose() {
//        super.dispose();
//        ArchiveDBAccess.getInstance().close();
//        JmsLogsPlugin.getDefault().getPluginPreferences()
//                .removePropertyChangeListener(_columnPropertyChangeListener);
//    }
//
//    /** @return get the from Time. */
//    public final Date getFromTime() {
//        return _fromTime.toCalendar().getTime();
//
//    }
//
//    /** @return get the to Time. */
//    public final Date getToTime() {
//        return _toTime.toCalendar().getTime();
//
//    }
//
//    /**
//     * When dispose store the width for each column.
//     */
//    public void saveColumn() {
//        int[] width = _jmsLogTableViewer.getColumnWidth();
//        String newPreferenceColumnString = ""; //$NON-NLS-1$
//        String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
//                .getString(ArchiveViewPreferenceConstants.P_STRINGArch).split(
//                        ";"); //$NON-NLS-1$
//        if (width.length != columns.length) {
//            return;
//        }
//        for (int i = 0; i < columns.length; i++) {
//            newPreferenceColumnString = newPreferenceColumnString
//                    .concat(columns[i].split(",")[0] + "," + width[i] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        }
//        newPreferenceColumnString = newPreferenceColumnString.substring(0,
//                newPreferenceColumnString.length() - 1);
//        IPreferenceStore store = JmsLogsPlugin.getDefault()
//                .getPreferenceStore();
//        store.setValue(ArchiveViewPreferenceConstants.P_STRINGArch,
//                newPreferenceColumnString);
//        if (store.needsSaving()) {
//            JmsLogsPlugin.getDefault().savePluginPreferences();
//        }
//    }
//
//    public void update(Observable arg0, Object arg1) {
//        _disp.syncExec(new Runnable() {
//            public void run() {
//                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.LOG_MESSAGES) {
//                    _jmsMessageList.clearList();
//                    _jmsLogTableViewer.refresh();
//                    ArrayList<HashMap<String, String>> answer = _dbAnswer
//                            .getLogMessages();
//                    int size = answer.size();
//                    if (_dbAnswer.is_maxSize()) {
//                        _countLabel.setBackground(Display.getCurrent()
//                                .getSystemColor(SWT.COLOR_RED));
//                        _countLabel.setText(Messages.ViewArchive_16
//                                + Integer.toString(size));
//                    } else {
//                        _countLabel.setText(Integer.toString(size));
//                        _countLabel.setBackground(Display.getCurrent()
//                                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//                    }
//                    _jmsLogTableViewer.getTable().setEnabled(true);
//                    if (size > 0) {
//                        _jmsMessageList.addJMSMessageList(answer);
//                    } else {
//                        String[] propertyNames = JmsLogsPlugin.getDefault()
//                                .getPluginPreferences().getString(
//                                        LogViewPreferenceConstants.P_STRING)
//                                .split(";"); //$NON-NLS-1$
//
//                        JMSMessage jmsMessage = new JMSMessage(propertyNames);
//                        String firstColumnName = _columnNames[0];
//                        jmsMessage.setProperty(firstColumnName,
//                                Messages.LogViewArchive_NoMessageInDB);
//                        _jmsMessageList.addJMSMessage(jmsMessage);
//                    }
//                }
//                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.MSG_NUMBER_TO_DELETE) {
//                    MessageBox messageBox = new MessageBox(Display.getDefault()
//                            .getActiveShell(), SWT.OK | SWT.CANCEL
//                            | SWT.ICON_WARNING);
//                    messageBox.setText(Messages.ViewArchive_17);
//                    messageBox.setMessage(Messages.ViewArchive_18
//                            + Math.abs(_dbAnswer.get_msgNumberToDelete() / 11)
//                            + Messages.ViewArchive_19
//                            + System.getProperty(Messages.ViewArchive_20)
//                            + Messages.ViewArchive_21);
//                    int buttonID = messageBox.open();
//                    switch (buttonID) {
//                    case SWT.OK:
//                        CentralLogger.getInstance().debug(this,
//                                Messages.ViewArchive_22);
//                        _dbReader.setReadProperties(ArchiveView.this._dbAnswer,
//                                _filter.copy());
//                        _dbReader
//                                .setAccessType(AccessDBJob.DBAccessType.DELETE);
//                        _dbReader.schedule();
//                        break;
//                    case SWT.CANCEL:
//                        CentralLogger.getInstance().debug(this,
//                                Messages.ViewArchive_23);
//                        break;
//                    }
//                }
//
//                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.DELETE_RESULT) {
//                    MessageBox messageBox = new MessageBox(Display.getDefault()
//                            .getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
//                    messageBox.setText(Messages.ViewArchive_24);
//                    messageBox.setMessage(Messages.ViewArchive_25);
//                    messageBox.open();
//                }
//
//                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.EXPORT_RESULT) {
//                    MessageBox messageBox = new MessageBox(Display.getDefault()
//                            .getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
//                    messageBox.setText(Messages.ViewArchive_26);
//                    if (_dbAnswer.is_maxSize()) {
//                        messageBox.setMessage(Messages.ViewArchive_27);
//                    } else {
//                        messageBox.setMessage(Messages.ViewArchive_28);
//                    }
//                    messageBox.open();
//                }
//
//            }
//        });
//    }
//}
//
