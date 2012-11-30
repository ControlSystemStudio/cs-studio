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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.IMessageTypes;
import org.csstudio.alarm.dbaccess.archivedb.Result;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.ArchiveMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.database.AsynchronousDatabaseAccess;
import org.csstudio.alarm.table.database.IDatabaseAccessListener;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.archive.ArchiveViewPreferenceConstants;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.alarm.table.ui.messagetable.MessageTable;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for messages read from oracel DB.
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 19.07.2007
 */
public class ArchiveView extends ViewPart {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveView.class);

    /** The Id of this Object. */
    public static final String ID = ArchiveView.class.getName();

    private Shell _shell;

    /** The JMS message list. */
    private ArchiveMessageList _jmsMessageList = null;

    /** An Array whit the name of the Columns. */
    private String[] _columnNames;

    /** The count of results. */
    private Label _countLabel;

    /**
     * Current settings of the filter that they are available to delete the
     * displayed messages.
     */
    private Filter _filter;

    private final ArrayList<String> _filterSettingHistory = new ArrayList<String>();

    private boolean _canExecute = false;

    private TableViewer _tableViewer;

    private MessageTable _messageTable;

    private ColumnWidthPreferenceMapping _columnMapping;

    /**
     * The ID of the property view.
     */
    private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

    private static final String SECURITY_ID = "alarmAdministration"; //$NON-NLS-1$

    private final AsynchronousDatabaseAccess _asyncDatabaseAccess = new AsynchronousDatabaseAccess();

    private final String[][] _messageProperties;

    private Text _timeFromText;

    private Text _timeToText;

    private Button _nextButton;

    private final StoredFilters _storedFilters = new StoredFilters();

    private Combo _filterSelector;


    public ArchiveView() {
        super();
        final GregorianCalendar to = (GregorianCalendar) Calendar
                .getInstance();
        final GregorianCalendar from = (GregorianCalendar) to.clone();
        from.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR) - 1);
        _filter = new Filter(from, to);
        
        IMessageTypes messageTypeService = ServiceLocator.getService(IMessageTypes.class);
        _messageProperties = messageTypeService.getMsgTypes();
        if (_messageProperties == null) {
            throw new IllegalStateException("Cannot load configuration of message types from database - probably no database access.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void createPartControl(final Composite parent) {

        _shell = parent.getShell();

        _canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, false);

        _storedFilters.readFromPreferences();

        _storedFilters.addFilterListChangeListener(new IFilterListChangeListener() {

            /**
             * Update filter names because filter list has changed.
             */
            @Override
            public void FilterListChanged() {
                if (_filterSelector != null) {
                    _filterSelector.removeAll();
                    for (final Filter filter : _storedFilters.getFilterList()) {
                        _filterSelector.add(filter.getName());
                    }
                }
            }
        });

        _columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
                .getString(ArchiveViewPreferenceConstants.P_STRINGArch).split(
                        ";"); //$NON-NLS-1$
        _jmsMessageList = new ArchiveMessageList();

        final GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        parent.setLayout(grid);

        final Composite archiveTableManagementComposite = new Composite(parent,
                SWT.NONE);
        archiveTableManagementComposite.setLayoutData(new GridData(SWT.LEFT,
                SWT.FILL, true, false, 1, 1));
        archiveTableManagementComposite.setLayout(new GridLayout(6, false));

        addPeriodGroup(archiveTableManagementComposite);
        addCommandButtons(archiveTableManagementComposite);
        addMessageCount(archiveTableManagementComposite);
        // addManageButtons(archiveTableManagementComposite);

        _tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
        _messageTable = new MessageTable(_tableViewer, _columnNames,
                _jmsMessageList);

        _messageTable.makeContextMenu(getSite());

        _columnMapping = new ColumnWidthPreferenceMapping(_tableViewer);
        // _jmsLogTableViewer.setAlarmSorting(false);
        //
        // IActionBars bars = getViewSite().getActionBars();
        // fillLocalToolBar(bars.getToolBarManager());

        addControlListenerToColumns(ArchiveViewPreferenceConstants.P_STRINGArch);
        getSite().setSelectionProvider(_tableViewer);

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

        
        makeActions();
        parent.pack();
    }

    private void addMessageCount(final Composite archiveTableManagementComposite) {
        GridData gd;
        final Group count = new Group(archiveTableManagementComposite, SWT.LINE_SOLID);
        count.setText(Messages.LogViewArchive_count); //$NON-NLS-1$
        count.setLayout(new GridLayout(1, true));
        gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd.minimumHeight = 60;
        gd.minimumWidth = 40;
        count.setLayoutData(gd);
        _countLabel = new Label(count, SWT.RIGHT);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        _countLabel.setLayoutData(gd);
        _countLabel.setText("0"); //$NON-NLS-1$

        //Add Button to get next set of messages after the last message of the
        //current subset.
        _nextButton = new Button(count, SWT.PUSH);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        _nextButton.setLayoutData(gd);
        _nextButton.setText(Messages.LogViewArchive_nextButton); //$NON-NLS-1$
        _nextButton.setEnabled(false);
        _nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                super.widgetSelected(e);
                LOG.debug("Get next subset of messages");
                _filter.setFrom(_jmsMessageList.getLatestMessageDate());
                readDatabase();
            }
        });
    }

    private void addPeriodGroup(final Composite archiveTableManagementComposite) {
        GridData gd;
        final Group periodGroup = new Group(archiveTableManagementComposite,
                SWT.LINE_SOLID);
        periodGroup.setLayout(new GridLayout(5, false));
        periodGroup.setText(Messages.LogViewArchive_period); //$NON-NLS-1$" +

        //Set Layout for group
        gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        gd.minimumHeight = 160;
        gd.minimumWidth = 360;
        periodGroup.setLayoutData(gd);

        //Set label and current 'from time' from filter in label and text field
        new Label(periodGroup, SWT.NONE).setText(Messages.ViewArchive_fromTime);
        _timeFromText = new Text(periodGroup, SWT.SINGLE);
        _timeFromText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        _timeFromText.setText(TimestampFactory.fromCalendar(_filter.getFrom())
                              .toString());
        _timeFromText.addFocusListener(new DateTextFieldFocusListener());

        //Create Buttons for direct search
        createFixedSearchButton(periodGroup, 24, "1 day");
        createFixedSearchButton(periodGroup, 72, "3 day");
        createFixedSearchButton(periodGroup, 168, "7 day");

        //Set label and current 'from time' from filter in label and text field
        new Label(periodGroup, SWT.NONE).setText(Messages.ViewArchive_toTime);
        _timeToText = new Text(periodGroup, SWT.SINGLE);
        _timeToText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        _timeToText.setText(TimestampFactory.fromCalendar(_filter.getTo())
                .toString());
        _timeToText.addFocusListener(new DateTextFieldFocusListener());

        //Create Button for search with filter and variable time
        final Button variableSearchButton = new Button(periodGroup, SWT.PUSH);
        variableSearchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        variableSearchButton.setText(Messages.LogViewArchive_user); //$NON-NLS-1$
        variableSearchButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                super.widgetSelected(e);
                callFilterSettingDialog();
            }
        });

        _filterSelector = new Combo (periodGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        _filterSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                                                        true, false, 2, 1));
        for (final Filter filter : _storedFilters.getFilterList()) {
            _filterSelector.add(filter.getName());
        }

       _filterSelector.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                super.widgetSelected(e);
                final String filterName = _filterSelector.getText();
                for (final Filter filter : _storedFilters.getFilterList()) {
                    if (filterName.equals(filter.getName())) {
                        filter.setFrom(_filter.getFrom());
                        filter.setTo(_filter.getTo());
                        _filter = filter.copy();
                    }
                }
                callFilterSettingDialog();
            }
        });
    }

    private void callFilterSettingDialog() {
        final FilterSettingDialog filterSettingDialog = new FilterSettingDialog(_shell,
                      _filter, _storedFilters, _filterSettingHistory, _messageProperties,
                      TimestampFactory.fromCalendar(_filter.getFrom()).toString(),
                      TimestampFactory.fromCalendar(_filter.getTo()).toString());
        if (filterSettingDialog.open() == Window.OK) {
            final String lowString = filterSettingDialog
            .getStartSpecification();
            final String highString = filterSettingDialog
            .getEndSpecification();
            try {
                final StartEndTimeParser parser = new StartEndTimeParser(lowString, highString);
                _filter.setFrom((GregorianCalendar) parser.getStart());
                _filter.setTo((GregorianCalendar) parser.getEnd());
                _timeFromText.setText(TimestampFactory.fromCalendar(_filter.getFrom()).toString());
                _timeToText.setText(TimestampFactory.fromCalendar(_filter.getTo()).toString());
                _timeFromText.getParent().getParent().redraw();
                _timeToText.getParent().getParent().redraw();
                readDatabase();
            } catch (final Exception e1) {
                JmsLogsPlugin.logError("Time/Date parser error");
            }
        }
    }
    /*
     * Set new from and to dates to filter if valid otherwise set previous dates.
     */
    private class DateTextFieldFocusListener extends FocusAdapter {
        @Override
        public void focusLost(final FocusEvent e) {
            try {
                final StartEndTimeParser parser = new StartEndTimeParser(_timeFromText.getText(), _timeToText.getText());
                _filter.setFrom((GregorianCalendar) parser.getStart());
                _filter.setTo((GregorianCalendar) parser.getEnd());
            } catch (final Exception e1) {
                _timeFromText.setText(TimestampFactory.fromCalendar(_filter.getFrom()).toString());
                _timeToText.setText(TimestampFactory.fromCalendar(_filter.getTo()).toString());
                _timeFromText.getParent().getParent().redraw();
                _timeToText.getParent().getParent().redraw();
                JmsLogsPlugin.logError("Time/Date parser error");
            }
            super.focusLost(e);
        }
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
            final String buttonText) {
        final Button fixedSearchButton = new Button(comp, SWT.PUSH);
        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gridData.minimumWidth = 60;
        fixedSearchButton.setLayoutData(gridData);
        fixedSearchButton.setText(buttonText); //$NON-NLS-1$
        fixedSearchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final GregorianCalendar to = new GregorianCalendar();
                final GregorianCalendar from = (GregorianCalendar) to.clone();
                from.add(Calendar.HOUR_OF_DAY, (-1 * hours));
                _filter.setFrom(from);
                _filter.setTo(to);
                _timeFromText.setText(TimestampFactory.fromCalendar(
                        _filter.getFrom()).toString());
                _timeToText.setText(TimestampFactory.fromCalendar(
                        _filter.getTo()).toString());
                // redraw
                _timeFromText.getParent().getParent().redraw();
                _timeToText.getParent().getParent().redraw();
                readDatabase();
            }
        });
    }

    private void addCommandButtons(final Composite archiveTableManagementComposite) {
        final Group commandButtonGroup = new Group(archiveTableManagementComposite,
                SWT.LINE_SOLID);
        commandButtonGroup.setText(Messages.ViewArchiveCommandGroup); //$NON-NLS-1$
        final GridLayout layout = new GridLayout(1, true);
        commandButtonGroup.setLayout(layout);
        final GridData gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        // gd.minimumHeight = 60;
        gd.minimumWidth = 60;
        commandButtonGroup.setLayoutData(gd);

        final Button fixedSearchButton = new Button(commandButtonGroup, SWT.PUSH);
        fixedSearchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, true, 2, 1));
        fixedSearchButton.setText("Start"); //$NON-NLS-1$
        fixedSearchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                readDatabase();
            }
        });
        if (_canExecute) {
            layout.numColumns = 2;
            gd.minimumWidth = 120;
            createDeleteButton(commandButtonGroup);
        }

        createExportButton(commandButtonGroup);
    }

    /**
     * Button to delete messages currently displayed in the table. *
     *
     * @param comp
     */
    private void createDeleteButton(final Composite comp) {
        final Button delete = new Button(comp, SWT.PUSH);
        delete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
                1));
        delete.setText(Messages.ViewArchive_deleteButton);

        delete.addSelectionListener(new SelectionAdapter() {

            /**
             * Set the 'delete' flag and use the current settings of the select
             * statement to delete the messages. The command will delete all
             * messages even if the table shows just a subset!!
             */
            @Override
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
    private void createExportButton(final Composite comp) {
        final Button export = new Button(comp, SWT.PUSH);
        final GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
                1);
        layoutData.minimumWidth = 60;
        export.setLayoutData(layoutData);
        export.setText(Messages.ViewArchiveExcelExportButton);

        export.addSelectionListener(new SelectionAdapter() {

            /**
			 *
			 */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // File standard dialog
                final FileDialog fileDialog = new FileDialog(Display.getDefault()
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
                final String selected = fileDialog.open();

                final File path = new File(selected);

                exportMessagesFromDatabase(path);
            }

        });

    }

    /**
     * Creates the actions offered by this view.
     */
    private void makeActions() {
        final Action showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    getSite().getPage().showView(PROPERTY_VIEW_ID);
                } catch (final PartInitException e) {
                    MessageDialog.openError(getSite().getShell(),
                            Messages.ViewArchive_10, e.getMessage());
                }
            }
        };
        showPropertyViewAction.setText(Messages.ViewArchive_11);
        showPropertyViewAction.setToolTipText(Messages.ViewArchive_12);

        final IViewRegistry viewRegistry = getSite().getWorkbenchWindow()
                .getWorkbench().getViewRegistry();
        final IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        showPropertyViewAction
                .setImageDescriptor(viewDesc.getImageDescriptor());
        final IActionBars bars = getViewSite().getActionBars();
        bars.getToolBarManager().add(showPropertyViewAction);
    }

    public void readDBFromExternalCall(final ProcessVariable pv) {
        // GregorianCalendar _from = new GregorianCalendar();
        // GregorianCalendar _to = new GregorianCalendar();
        // _from.setTimeInMillis(_to.getTimeInMillis() - 1000 * 60 * 60 * 24);
        _filter.clearFilter();
        _filter.addFilterItem("NAME", pv.getName(), "END");
        _filterSettingHistory.add(0, pv.getName());
        readDatabase();
    }

    private void exportMessagesFromDatabase(final File path) {
        final String maxAnswerSize = JmsLogsPlugin.getDefault()
                .getPluginPreferences().getString("maximum answer size export");
        Integer maximumMessageNumber;
        try {
            maximumMessageNumber = Integer.parseInt(maxAnswerSize);
        } catch (final NumberFormatException e) {
            LOG.warn("Invalid value format in preference for maximum message number");
            maximumMessageNumber = 5000;
        }
        _filter.setMaximumMessageNumber(maximumMessageNumber);

        final IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onReadFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        final MessageBox messageBox = new MessageBox(Display
                                .getDefault().getActiveShell(), SWT.OK
                                | SWT.ICON_INFORMATION);
                        messageBox.setText(Messages.ViewArchive_26);
                        if (result.isMaxSize()) {
                            messageBox.setMessage(Messages.ViewArchive_27);
                        } else {
                            messageBox.setMessage(Messages.ViewArchive_28);
                        }
                        messageBox.open();
                    }
                });
            }
        };
        _asyncDatabaseAccess.exportMessagesInFile(listener, _filter, path,
                _columnNames);
    }

    private void countAndDeleteMessagesFromDatabase() {
        final IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onMessageCountFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        final MessageBox messageBox = new MessageBox(Display
                                .getDefault().getActiveShell(), SWT.OK
                                | SWT.CANCEL | SWT.ICON_WARNING);
                        messageBox.setText(Messages.ViewArchive_17);
                        messageBox.setMessage(Messages.ViewArchive_18
                                + Math.abs(result.getMsgNumberToDelete() / 11)
                                + Messages.ViewArchive_19
                                + System.getProperty(Messages.ViewArchive_20)
                                + Messages.ViewArchive_21);
                        final int buttonID = messageBox.open();
                        switch (buttonID) {
                        case SWT.OK:
                            deleteMessagesFromDatabase();
                            break;
                        case SWT.CANCEL:
                            LOG.debug(Messages.ViewArchive_23);
                            break;
                        }

                    }

                });
            }
        };
        _asyncDatabaseAccess.countMessages(listener, _filter);
    }

    private void deleteMessagesFromDatabase() {
        final IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onDeletionFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        final MessageBox messageBox = new MessageBox(Display
                                .getDefault().getActiveShell(), SWT.OK
                                | SWT.ICON_INFORMATION);
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
        final String maxAnswerSize = JmsLogsPlugin.getDefault()
                .getPluginPreferences().getString("maximum answer size");
        Integer maximumMessageNumber;
        try {
            maximumMessageNumber = Integer.parseInt(maxAnswerSize);
        } catch (final NumberFormatException e) {
            LOG.warn("Invalid value format in preference for maximum message number");
            maximumMessageNumber = 500;
        }
        _filter.setMaximumMessageNumber(maximumMessageNumber);
        final IDatabaseAccessListener listener = new ArchiveDatabaseAccessListener() {
            @Override
            public void onReadFinished(final Result result) {
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        _jmsMessageList.deleteAllMessages();
                        _tableViewer.refresh();
                        final int size = result.getMessagesFromDatabase().size();
                        //If there are more messages in the database (maxsize == true)
                        //set countLabel background red and enable next button to get
                        //the next message subset.
                        if (result.isMaxSize()) {
                            _countLabel.setBackground(Display.getCurrent()
                                    .getSystemColor(SWT.COLOR_RED));
                            _countLabel.setText(Messages.ViewArchive_16
                                    + Integer.toString(size));
                            _nextButton.setEnabled(true);
                        } else {
                            _countLabel.setText(Integer.toString(size));
                            _countLabel
                                    .setBackground(Display
                                            .getCurrent()
                                            .getSystemColor(
                                                    SWT.COLOR_WIDGET_BACKGROUND));
                            _nextButton.setEnabled(false);
                        }
                        _tableViewer.getTable().setEnabled(true);
                        if (size > 0) {
                            _jmsMessageList.addMessageList(result
                                    .getMessagesFromDatabase());
                        } else {
                            final String[] propertyNames = JmsLogsPlugin
                                    .getDefault()
                                    .getPluginPreferences()
                                    .getString(
                                            LogViewPreferenceConstants.P_STRING)
                                    .split(";"); //$NON-NLS-1$

                            final BasicMessage jmsMessage = new BasicMessage(propertyNames);
                            final String firstColumnName = _columnNames[0];
                            jmsMessage.setProperty(firstColumnName,
                                    Messages.LogViewArchive_NoMessageInDB);
                            _jmsMessageList.addMessage(jmsMessage);
                        }
                    }
                });
            }
        };
        _asyncDatabaseAccess.readMessages(listener, _filter);
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
        final String patternChain = memento.getString("filterPatterns");
        if (patternChain == null) {
            LOG.debug("No filter patterns from previous session"); //$NON-NLS-1$
        } else {
            LOG.debug("Get filter patterns from previous session"); //$NON-NLS-1$
            final String[] patterns = patternChain.split(";");
            for (final String pattern : patterns) {
                _filterSettingHistory.add(pattern);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
        if (memento != null && _filterSettingHistory != null) {
            LOG.debug("Save latest filter setting history in IMemento: ");
            final StringBuffer patternChain = new StringBuffer();
            for (final String pattern : _filterSettingHistory) {
                patternChain.append(pattern);
                patternChain.append(";");
            }
            memento.putString("filterPatterns", patternChain.toString()); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus() {
    }

	/**
	 * Write new Column width when a column is resized.
	 */
	void addControlListenerToColumns(final String colSetPref) {
		final TableColumn[] columns = _tableViewer.getTable().getColumns();
		for (final TableColumn tableColumn : columns) {
			tableColumn.addControlListener(new ControlAdapter() {

				@Override
				public void controlResized(final ControlEvent e) {
					_columnMapping.saveColumn(colSetPref);
				}
			});
		}
	}

    /** {@inheritDoc} */
    @Override
    public final void dispose() {
        _storedFilters.writeToPreferences();
        super.dispose();
    }
}
