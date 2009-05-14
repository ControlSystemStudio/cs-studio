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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.database.AsynchronousDatabaseAccess;
import org.csstudio.alarm.table.database.IDatabaseAccessListener;
import org.csstudio.alarm.table.database.Result;
import org.csstudio.alarm.table.expertSearch.ExpertSearchDialog;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.ArchiveViewPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
import org.csstudio.alarm.table.readDB.AccessDBJob;
import org.csstudio.alarm.table.readDB.DBAnswer;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * View for message read from oracel DB.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 19.07.2007
 */
public class ArchiveView extends ViewPart implements Observer {

    /** The Id of this Object. */
    public static final String ID = ArchiveView.class.getName();

    /** The JMS message list. */
    private JMSMessageList _jmsMessageList = null;

    /** An Array whit the name of the Columns. */
    private String[] _columnNames;

    /** Textfield witch contain the "from time". */
    private Text _timeFrom;
    /** Textfield witch contain the "to time". */
    private Text _timeTo;

    /** The selectet "from time". */
    private ITimestamp _fromTime;
    /** The selectet "to time". */
    private ITimestamp _toTime;

    /**
     * The Answer from the DB.
     */
    private DBAnswer _dbAnswer = null;

    /** The count of results. */
    private Label _countLabel;

    private ArrayList<FilterItem> _filterSettings;

    /**
     * Current settings of the filter that they are available to delete the
     * displayed messages.
     */
    private Filter _filter;

    private AccessDBJob _dbReader = new AccessDBJob("DBReader"); //$NON-NLS-1$

    /**
     * The Show Property View action.
     */
    private Action _showPropertyViewAction;

    private boolean _canExecute = false;

    private TableViewer _tableViewer;

    private MessageTable _messageTable;

    private ColumnWidthPreferenceMapping _columnMapping;

    /**
     * The ID of the property view.
     */
    private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

    private static final String SECURITY_ID = "alarmAdministration"; //$NON-NLS-1$

    private AsynchronousDatabaseAccess _asyncDatabaseAccess = new AsynchronousDatabaseAccess();

    public ArchiveView() {
        super();
        _dbAnswer = new DBAnswer();
        _dbAnswer.addObserver(this);
    }

    /** {@inheritDoc} */
    public final void createPartControl(final Composite parent) {
        _canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID,
                false);

        // _disp = parent.getDisplay();

        _columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
                .getString(ArchiveViewPreferenceConstants.P_STRINGArch).split(
                        ";"); //$NON-NLS-1$
        _jmsMessageList = new JMSMessageList(_columnNames);

        // _parentShell = parent.getShell();

        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);

        Composite archiveTableManagementComposite = new Composite(parent,
                SWT.NONE);
        archiveTableManagementComposite.setLayoutData(new GridData(SWT.LEFT,
                SWT.FILL, true, false, 1, 1));
        archiveTableManagementComposite.setLayout(new GridLayout(6, false));

        addSearchButtons(archiveTableManagementComposite);
        addShownPeriod(archiveTableManagementComposite);
        addMessageCount(archiveTableManagementComposite);
        addManageButtons(archiveTableManagementComposite);

        _tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
        _messageTable = new MessageTable(_tableViewer, _columnNames,
                _jmsMessageList);

        _columnMapping = new ColumnWidthPreferenceMapping(_tableViewer);
        // _jmsLogTableViewer.setAlarmSorting(false);
        //
        // IActionBars bars = getViewSite().getActionBars();
        // fillLocalToolBar(bars.getToolBarManager());

        getSite().setSelectionProvider(_tableViewer);

        makeActions();
        parent.pack();
        // _columnPropertyChangeListener = new ColumnPropertyChangeListener(
        // ArchiveViewPreferenceConstants.P_STRINGArch, _jmsLogTableViewer);
        //
        // JmsLogsPlugin.getDefault().getPluginPreferences()
        // .addPropertyChangeListener(_columnPropertyChangeListener);
    }

    private void addManageButtons(Composite archiveTableManagementComposite) {
        GridData gd;
        Group messageButtons = new Group(archiveTableManagementComposite,
                SWT.LINE_SOLID);
        messageButtons.setText(Messages.ViewArchive_messagesGroup);
        GridLayout layout = new GridLayout(1, true);
        messageButtons.setLayout(layout);
        gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        gd.minimumHeight = 60;
        gd.minimumWidth = 60;
        messageButtons.setLayoutData(gd);
        createExportButton(messageButtons);

        if (_canExecute) {
            layout.numColumns = 2;
            gd.minimumWidth = 120;
            createDeleteButton(messageButtons);
        }
    }

    private void addMessageCount(Composite archiveTableManagementComposite) {
        GridData gd;
        Group count = new Group(archiveTableManagementComposite, SWT.LINE_SOLID);
        count.setText(Messages.getString("LogViewArchive_count")); //$NON-NLS-1$
        count.setLayout(new GridLayout(1, true));
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd.minimumHeight = 60;
        gd.minimumWidth = 40;
        count.setLayoutData(gd);
        _countLabel = new Label(count, SWT.RIGHT);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        _countLabel.setLayoutData(gd);
        _countLabel.setText("0"); //$NON-NLS-1$
    }

    private void addShownPeriod(Composite archiveTableManagementComposite) {
        GridData gd;
        Group shownPeriodGroup = new Group(archiveTableManagementComposite,
                SWT.LINE_SOLID);
        shownPeriodGroup.setText(Messages.getString("LogViewArchive_from")); //$NON-NLS-1$
        gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        gd.minimumHeight = 80;
        gd.minimumWidth = 180;
        shownPeriodGroup.setLayoutData(gd);
        shownPeriodGroup.setLayout(new GridLayout(2, false));

        new Label(shownPeriodGroup, SWT.NONE)
                .setText(Messages.ViewArchive_fromTime);

        _timeFrom = new Text(shownPeriodGroup, SWT.SINGLE);
        _timeFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
                1, 1));

        _timeFrom.setEditable(false);
        _timeFrom.setText("                            "); //$NON-NLS-1$

        new Label(shownPeriodGroup, SWT.NONE)
                .setText(Messages.ViewArchive_toTime);
        _timeTo = new Text(shownPeriodGroup, SWT.SINGLE);
        _timeTo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
                1));
        _timeTo.setEditable(false);
    }

    private void addSearchButtons(Composite archiveTableManagementComposite) {
        Group searchButtonGroup = new Group(archiveTableManagementComposite,
                SWT.LINE_SOLID);
        searchButtonGroup.setText(Messages.getString("LogViewArchive_period")); //$NON-NLS-1$
        searchButtonGroup.setLayout(new GridLayout(5, true));
        GridData gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        gd.minimumHeight = 60;
        gd.minimumWidth = 300;
        searchButtonGroup.setLayoutData(gd);

        createFixedSearchButton(searchButtonGroup, 24, Messages
                .getString("LogViewArchive_day"));
        createFixedSearchButton(searchButtonGroup, 72, Messages
                .getString("LogViewArchive_3days"));
        createFixedSearchButton(searchButtonGroup, 168, Messages
                .getString("LogViewArchive_week"));
        createVariableSearchButton(searchButtonGroup);
        createVariableFilterSearchButton(searchButtonGroup);
    }

    /**
     * Button to delete messages currently displayed in the table. *
     * 
     * @param comp
     */
    private void createDeleteButton(Composite comp) {
        Button delete = new Button(comp, SWT.PUSH);
        delete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
                1));
        delete.setText(Messages.ViewArchive_deleteButton);

        delete.addSelectionListener(new SelectionAdapter() {

            /**
             * Set the 'delete' flag and use the current settings of the select
             * statement to delete the messages. The command will delete all
             * messages even if the table shows just a subset!!
             */
            public void widgetSelected(final SelectionEvent e) {
                countAndDeleteMessagesFromDatabase();
            }

        });

    }

    /**
     * Button to export messages currently displayed in the table. *
     * 
     * @param comp
     */
    private void createExportButton(Composite comp) {
        Button export = new Button(comp, SWT.PUSH);
        export.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
                1));
        export.setText(Messages.ViewArchive_6);

        export.addSelectionListener(new SelectionAdapter() {

            /**
			 * 
			 */
            public void widgetSelected(final SelectionEvent e) {
                // File standard dialog
                FileDialog fileDialog = new FileDialog(Display.getDefault()
                        .getActiveShell(), SWT.SAVE);

                // Set the text
                fileDialog.setText(Messages.ViewArchive_7);
                // Set filter on .txt files
                fileDialog
                        .setFilterExtensions(new String[] { Messages.ViewArchive_8 });
                // Put in a readable name for the filter
                fileDialog
                        .setFilterNames(new String[] { Messages.ViewArchive_9 });

                // Open Dialog and save result of selection
                String selected = fileDialog.open();

                File path = new File(selected);

                exportMessagesFromDatabase(path);
            }

        });

    }

    /**
     * Creates the actions offered by this view.
     */
    private void makeActions() {
        _showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    getSite().getPage().showView(PROPERTY_VIEW_ID);
                } catch (PartInitException e) {
                    MessageDialog.openError(getSite().getShell(),
                            Messages.ViewArchive_10, e.getMessage());
                }
            }
        };
        _showPropertyViewAction.setText(Messages.ViewArchive_11);
        _showPropertyViewAction.setToolTipText(Messages.ViewArchive_12);

        IViewRegistry viewRegistry = getSite().getWorkbenchWindow()
                .getWorkbench().getViewRegistry();
        IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        _showPropertyViewAction.setImageDescriptor(viewDesc
                .getImageDescriptor());
    }

    /**
     * Adds the tool bar actions.
     * 
     * @param manager
     *            the menu manager.
     */
    private void fillLocalToolBar(final IToolBarManager manager) {
        manager.add(_showPropertyViewAction);
    }

    /**
     * Create a button to search for a fixed period and add the selection
     * listener.
     * 
     * @param comp
     *            Composite for the new button
     * @param hours
     *            Period (hours) to search for
     * @param buttonText
     *            Text for the button.
     */
    private void createFixedSearchButton(final Composite comp, final int hours,
            String buttonText) {
        Button fixedSearchButton = new Button(comp, SWT.PUSH);
        fixedSearchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, true, 1, 1));
        fixedSearchButton.setText(buttonText); //$NON-NLS-1$
        fixedSearchButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                GregorianCalendar _to = new GregorianCalendar();
                GregorianCalendar _from = (GregorianCalendar) _to.clone();
                _from.add(GregorianCalendar.HOUR_OF_DAY, (-1 * hours));
                _filter = new Filter(null, _from, _to);
                readDatabase();
            }
        });
    }

    /**
     * Create a Button that open a dialog to select required period.
     * 
     * @param comp
     *            the parent Composite for the Button.
     */
    private void createVariableSearchButton(final Composite comp) {
        Button bFlexSearch = new Button(comp, SWT.PUSH);
        bFlexSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                true, 1, 1));
        bFlexSearch.setText(Messages.getString("LogViewArchive_user")); //$NON-NLS-1$

        bFlexSearch.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                StartEndDialog dlg;
                if (_fromTime != null && _toTime != null) {
                    dlg = new StartEndDialog(comp.getShell(),
                    // dlg = new StartEndDialog(_parentShell,
                            _fromTime.toString(), _toTime.toString());
                } else {
                    dlg = new StartEndDialog(comp.getShell());
                    // dlg = new StartEndDialog(_parentShell);
                }
                if (dlg.open() == StartEndDialog.OK) {
                    String lowString = dlg.getStartSpecification();
                    String highString = dlg.getEndSpecification();
                    try {
                        StartEndTimeParser parser = new StartEndTimeParser(
                                lowString, highString);
                        GregorianCalendar _from = (GregorianCalendar) parser
                                .getStart();
                        _fromTime = TimestampFactory.fromCalendar(_from);
                        GregorianCalendar _to = (GregorianCalendar) parser
                                .getEnd();
                        _toTime = TimestampFactory.fromCalendar(_to);
                        _filter = new Filter(null, _from, _to);
                        readDatabase();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        JmsLogsPlugin.logInfo(e1.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Create a Button that open a dialog to select required period and define
     * filters.
     * 
     * @param comp
     *            the parent Composite for the Button.
     */
    private void createVariableFilterSearchButton(final Composite comp) {
        Button bSearch = new Button(comp, SWT.PUSH);
        bSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
                1));
        bSearch.setText(Messages.getString("LogViewArchive_expert")); //$NON-NLS-1$

        bSearch.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (_fromTime == null) {
                    ITimestamp now = TimestampFactory.now();
                    _fromTime = TimestampFactory.createTimestamp(now.seconds()
                            - (24 * 60 * 60), now.nanoseconds()); // new
                    // Timestamp(fromDate.getTime()/1000);
                }
                if (_toTime == null) {
                    _toTime = TimestampFactory.now();
                }

                ExpertSearchDialog dlg = new ExpertSearchDialog(
                        comp.getShell(),
                        // ExpertSearchDialog dlg = new
                        // ExpertSearchDialog(_parentShell,
                        _fromTime, _toTime, _filterSettings);

                GregorianCalendar _to = new GregorianCalendar();
                GregorianCalendar _from = (GregorianCalendar) _to.clone();
                if (dlg.open() == ExpertSearchDialog.OK) {
                    _fromTime = dlg.getStart();
                    _toTime = dlg.getEnd();
                    double low = _fromTime.toDouble();
                    double high = _toTime.toDouble();
                    if (low < high) {
                        _from.setTimeInMillis((long) low * 1000);
                        _to.setTimeInMillis((long) high * 1000);
                    } else {
                        _from.setTimeInMillis((long) high * 1000);
                        _to.setTimeInMillis((long) low * 1000);
                    }
                    _filterSettings = dlg.get_filterConditions();
                    _filter = new Filter(_filterSettings, _from, _to);
                    readDatabase();
                }
            }

        });
    }

    public void readDBFromExternalCall(IProcessVariable pv) {
        GregorianCalendar _from = new GregorianCalendar();
        GregorianCalendar _to = new GregorianCalendar();
        _from.setTimeInMillis(_to.getTimeInMillis() - 1000 * 60 * 60 * 24);
        showNewTime(_from, _to);
        ArrayList<FilterItem> _filterSettings = new ArrayList<FilterItem>();
        _filterSettings.add(new FilterItem(Messages.ViewArchive_13, pv
                .getName(), Messages.ViewArchive_14));
        _filter = new Filter(_filterSettings, _from, _to);
        readDatabase();
    }

    private void exportMessagesFromDatabase(File path) {
        String maxAnswerSize = JmsLogsPlugin.getDefault()
        .getPluginPreferences().getString("maximum answer size export");
        Integer maximumMessageNumber;
        try {
            maximumMessageNumber = Integer.parseInt(maxAnswerSize);
        } catch (NumberFormatException e) {
            CentralLogger
                    .getInstance()
                    .warn(this,
                            "Invalid value format in preference for maximum message number");
            maximumMessageNumber = 5000;
        }
        _filter.setMaximumMessageNumber(maximumMessageNumber);

        IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onReadFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        MessageBox messageBox = new MessageBox(Display.getDefault()
                                .getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
                        messageBox.setText(Messages.ViewArchive_26);
                        if (_dbAnswer.is_maxSize()) {
                            messageBox.setMessage(Messages.ViewArchive_27);
                        } else {
                            messageBox.setMessage(Messages.ViewArchive_28);
                        }
                        messageBox.open();
                    }
                });
            }
        };
        _asyncDatabaseAccess.exportMessagesInFile(listener, _filter,
                path, _columnNames);
    }

    private void countAndDeleteMessagesFromDatabase() {
        IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onMessageCountFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        MessageBox messageBox = new MessageBox(Display.getDefault()
                                .getActiveShell(), SWT.OK | SWT.CANCEL
                                | SWT.ICON_WARNING);
                        messageBox.setText(Messages.ViewArchive_17);
                        messageBox.setMessage(Messages.ViewArchive_18
                                + Math.abs(result.getMsgNumberToDelete() / 11)
                                + Messages.ViewArchive_19
                                + System.getProperty(Messages.ViewArchive_20)
                                + Messages.ViewArchive_21);
                        int buttonID = messageBox.open();
                        switch (buttonID) {
                        case SWT.OK:
                            deleteMessagesFromDatabase();
                            break;
                        case SWT.CANCEL:
                            CentralLogger.getInstance().debug(this,
                                    Messages.ViewArchive_23);
                            break;
                        }

                    }

                });
            }
        };
        _asyncDatabaseAccess.countMessages(listener, _filter);
    }

    private void deleteMessagesFromDatabase() {
        IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onDeletionFinished(Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        MessageBox messageBox = new MessageBox(Display.getDefault()
                                .getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
                        messageBox.setText(Messages.ViewArchive_24);
                        messageBox.setMessage(Messages.ViewArchive_25);
                        messageBox.open();
                    }
                });
            }
        };
        _asyncDatabaseAccess.deleteMessages(listener, _filter);
    }

    private void readDatabase() {
        showNewTime(_filter.getFrom(), _filter.getTo());

        String maxAnswerSize = JmsLogsPlugin.getDefault()
                .getPluginPreferences().getString("maximum answer size");
        Integer maximumMessageNumber;
        try {
            maximumMessageNumber = Integer.parseInt(maxAnswerSize);
        } catch (NumberFormatException e) {
            CentralLogger
                    .getInstance()
                    .warn(this,
                            "Invalid value format in preference for maximum message number");
            maximumMessageNumber = 500;
        }
        _filter.setMaximumMessageNumber(maximumMessageNumber);
        IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onReadFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        _jmsMessageList.clearList();
                        _tableViewer.refresh();
                        int size = result.getMessagesFromDatabase().size();
                        if (result.isMaxSize()) {
                            _countLabel.setBackground(Display.getCurrent()
                                    .getSystemColor(SWT.COLOR_RED));
                            _countLabel.setText(Messages.ViewArchive_16
                                    + Integer.toString(size));
                        } else {
                            _countLabel.setText(Integer.toString(size));
                            _countLabel
                                    .setBackground(Display
                                            .getCurrent()
                                            .getSystemColor(
                                                    SWT.COLOR_WIDGET_BACKGROUND));
                        }
                        _tableViewer.getTable().setEnabled(true);
                        if (size > 0) {
                            _jmsMessageList.addJMSMessageList(result.getMessagesFromDatabase());
                        } else {
                            String[] propertyNames = JmsLogsPlugin
                                    .getDefault()
                                    .getPluginPreferences()
                                    .getString(
                                            LogViewPreferenceConstants.P_STRING)
                                    .split(";"); //$NON-NLS-1$

                            JMSMessage jmsMessage = new JMSMessage(
                                    propertyNames);
                            String firstColumnName = _columnNames[0];
                            jmsMessage.setProperty(firstColumnName,
                                    Messages.LogViewArchive_NoMessageInDB);
                            _jmsMessageList.addJMSMessage(jmsMessage);
                        }
                    }
                });
            }
        };
        _asyncDatabaseAccess.readMessages(listener, _filter);
    }

    /**
     * Set the two times from, to .
     * 
     * @param from
     * @param to
     */
    private void showNewTime(final Calendar from, final Calendar to) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        try {
            sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore()
                    .getString(ArchiveViewPreferenceConstants.DATE_FORMAT));
        } catch (Exception e) {
            sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore()
                    .getDefaultString(
                            ArchiveViewPreferenceConstants.DATE_FORMAT));
            JmsLogsPlugin.getDefault().getPreferenceStore().setToDefault(
                    ArchiveViewPreferenceConstants.DATE_FORMAT);
        }
        _timeFrom.setText(sdf.format(from.getTime()));
        _fromTime = TimestampFactory.fromCalendar(from);

        _timeTo.setText(sdf.format(to.getTime()));
        _toTime = TimestampFactory.fromCalendar(to);
        // redraw
        _timeFrom.getParent().getParent().redraw();
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus() {
    }

    /** {@inheritDoc} */
    @Override
    public final void dispose() {
        super.dispose();
        _columnMapping.saveColumn(ArchiveViewPreferenceConstants.P_STRINGArch);
        ArchiveDBAccess.getInstance().close();
        // JmsLogsPlugin.getDefault().getPluginPreferences()
        // .removePropertyChangeListener(_columnPropertyChangeListener);
    }

    public void update(Observable arg0, Object arg1) {
        Display.getDefault().syncExec(new Runnable() {

            // _disp.syncExec(new Runnable() {
            public void run() {
                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.LOG_MESSAGES) {
                    _jmsMessageList.clearList();
                    _tableViewer.refresh();
                    ArrayList<HashMap<String, String>> answer = _dbAnswer
                            .getLogMessages();
                    int size = answer.size();
                    if (_dbAnswer.is_maxSize()) {
                        _countLabel.setBackground(Display.getCurrent()
                                .getSystemColor(SWT.COLOR_RED));
                        _countLabel.setText(Messages.ViewArchive_16
                                + Integer.toString(size));
                    } else {
                        _countLabel.setText(Integer.toString(size));
                        _countLabel.setBackground(Display.getCurrent()
                                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                    }
                    _tableViewer.getTable().setEnabled(true);
                    if (size > 0) {
                        _jmsMessageList.addJMSMessageList(answer);
                    } else {
                        String[] propertyNames = JmsLogsPlugin.getDefault()
                                .getPluginPreferences().getString(
                                        LogViewPreferenceConstants.P_STRING)
                                .split(";"); //$NON-NLS-1$

                        JMSMessage jmsMessage = new JMSMessage(propertyNames);
                        String firstColumnName = _columnNames[0];
                        jmsMessage.setProperty(firstColumnName,
                                Messages.LogViewArchive_NoMessageInDB);
                        _jmsMessageList.addJMSMessage(jmsMessage);
                    }
                }
                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.MSG_NUMBER_TO_DELETE) {
                    MessageBox messageBox = new MessageBox(Display.getDefault()
                            .getActiveShell(), SWT.OK | SWT.CANCEL
                            | SWT.ICON_WARNING);
                    messageBox.setText(Messages.ViewArchive_17);
                    messageBox.setMessage(Messages.ViewArchive_18
                            + Math.abs(_dbAnswer.get_msgNumberToDelete() / 11)
                            + Messages.ViewArchive_19
                            + System.getProperty(Messages.ViewArchive_20)
                            + Messages.ViewArchive_21);
                    int buttonID = messageBox.open();
                    switch (buttonID) {
                    case SWT.OK:
                        CentralLogger.getInstance().debug(this,
                                Messages.ViewArchive_22);
                        _dbReader.setReadProperties(ArchiveView.this._dbAnswer,
                                _filter.copy());
                        _dbReader
                                .setAccessType(AccessDBJob.DBAccessType.DELETE);
                        _dbReader.schedule();
                        break;
                    case SWT.CANCEL:
                        CentralLogger.getInstance().debug(this,
                                Messages.ViewArchive_23);
                        break;
                    }
                }

                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.DELETE_RESULT) {
                    MessageBox messageBox = new MessageBox(Display.getDefault()
                            .getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
                    messageBox.setText(Messages.ViewArchive_24);
                    messageBox.setMessage(Messages.ViewArchive_25);
                    messageBox.open();
                }

                if (_dbAnswer.getDbqueryType() == DBAnswer.ResultType.EXPORT_RESULT) {
                    MessageBox messageBox = new MessageBox(Display.getDefault()
                            .getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
                    messageBox.setText(Messages.ViewArchive_26);
                    if (_dbAnswer.is_maxSize()) {
                        messageBox.setMessage(Messages.ViewArchive_27);
                    } else {
                        messageBox.setMessage(Messages.ViewArchive_28);
                    }
                    messageBox.open();
                }

            }
        });
    }
}
