/**
 *
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.ui.PeriodicLogQuery.LogResult;
import org.csstudio.logbook.util.LogEntrySearchUtil;
import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

/**
 * A view to search for logEntries and then display them in a table form
 *
 * @author shroffk
 *
 */
public class LogTableView extends ViewPart {
    private Text text;
    private org.csstudio.logbook.ui.extra.LogEntryTable logEntryTable;

    private final IPreferencesService preferenceService = Platform.getPreferencesService();

    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.logbook.ui.LogTableView"; //$NON-NLS-1$

    private LogbookClient logbookClient;

    private List<String> logbooks = Collections.emptyList();
    private List<String> tags = Collections.emptyList();

    // GUI
    private Label label;
    private ErrorBar errorBar;
    private Button btnNewButton;
    private Composite navigator;
    private Link previousPage;
    private Label labelPage;
    private Link nextPage;
    private Button configureButton;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    // Model
    private PeriodicLogQuery logQuery;
    private int resultSize;
    private int page = 1;
    private String searchString;

    // Model listener
    private LogQueryListener listener = new LogQueryListener() {

    @Override
    public void queryExecuted(final LogResult result) {
        Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run() {
            ISelection selection = logEntryTable.getSelection();
            if (result.lastException != null) {
            errorBar.setException(result.lastException);
            } else {
            errorBar.setException(null);
            }
            if (result.logs.isEmpty() || result.logs.size() < resultSize) {
            nextPage.setEnabled(false);
            } else {
            nextPage.setEnabled(true);
            }
            logEntryTable.setLogs(result.logs);
            logEntryTable.setSelection(selection);
        }
        });
    }

    };

    private IMemento memento;

    public LogTableView() {
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site);
        this.memento = memento;
    }

    @Override
    public void createPartControl(final Composite parent) {

    GridLayout gridLayout = new GridLayout(4, false);
    gridLayout.verticalSpacing = 1;
    gridLayout.horizontalSpacing = 1;
    gridLayout.marginHeight = 1;
    gridLayout.marginWidth = 1;
    parent.setLayout(gridLayout);

    errorBar = new ErrorBar(parent, SWT.NONE);
    errorBar.setLayoutData(new  GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));
    errorBar.setMarginBottom(5);

    changeSupport.addPropertyChangeListener("searchString",
        new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
            text.setText(searchString);
            page = 1;
            labelPage.setText(String.valueOf(page));
            search();
            }
        });

    changeSupport.addPropertyChangeListener("page",
        new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
            labelPage.setText(String.valueOf(page));
            if (page > 1) {
                previousPage.setEnabled(true);
            } else {
                previousPage.setEnabled(false);
            }
            search();
            }
        });

    Label lblLogQuery = new Label(parent, SWT.NONE);
    lblLogQuery.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lblLogQuery.setText("Log Query:");

    text = new Text(parent, SWT.BORDER);
    text.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
            setSearchString(text.getText());
        }
        }
    });
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnNewButton = new Button(parent, SWT.NONE);
    try {
        resultSize = preferenceService.getInt("org.csstudio.logbook.ui","Result.size", 100, null);
    } catch (Exception ex) {
        errorBar.setException(ex);
    }

    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        Runnable openSearchDialog = new Runnable() {

            @Override
            public void run() {
            try {
                if (logbooks.isEmpty() && initializeClient()) {
                logbooks = new ArrayList<String>();
                for (Logbook logbook : logbookClient
                    .listLogbooks()) {
                    logbooks.add(logbook.getName());
                }
                }
                if (tags.isEmpty() && initializeClient()) {
                tags = new ArrayList<String>();
                for (Tag tag : logbookClient.listTags()) {
                    tags.add(tag.getName());
                }
                }
                Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    LogEntrySearchDialog dialog = new LogEntrySearchDialog(
                        parent.getShell(), logbooks, tags,
                        LogEntrySearchUtil
                            .parseSearchString(text
                                .getText()));
                    dialog.setBlockOnOpen(true);
                    if (dialog.open() == IDialogConstants.OK_ID) {
                    text.setText(dialog.getSearchString());
                    text.getParent().update();
                    search();
                    }
                }
                });
            } catch (Exception e) {
                errorBar.setException(e);
            }
            }
        };
        BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
        }
    });
    btnNewButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    btnNewButton.setText("Adv Search");

    configureButton = new Button(parent, SWT.NONE);
    configureButton.setImage(ResourceManager.getPluginImage(
        "org.csstudio.channel.widgets", "icons/gear-16.png"));
    configureButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
            LogViewConfigurationDialog dialog = new LogViewConfigurationDialog(
                                        parent.getShell(),
                                        logEntryTable.isExpanded(),
                                        -1,
                                        logEntryTable.getRowSize());

            dialog.setBlockOnOpen(true);
            if (dialog.open() == IDialogConstants.OK_ID) {
                logEntryTable.setExpanded(dialog.isExpandable());
                logEntryTable.setRowSize(dialog.getRowSize());
            }
            }
        });
        }
    });

    // Add AutoComplete support, use type logEntrySearch
    new AutoCompleteWidget(text, "LogentrySearch");

    label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

    logEntryTable = new org.csstudio.logbook.ui.extra.LogEntryTable(parent, SWT.NONE | SWT.SINGLE);
    boolean expanded = preferenceService.getBoolean("org.csstudio.logbook.viewer", "expanded.view", false, null);
    logEntryTable.setExpanded(expanded);
    int rowSize = preferenceService.getInt("org.csstudio.logbook.viewer", "row.size", 1, null);
    logEntryTable.setRowSize(rowSize);

    logEntryTable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseDoubleClick(MouseEvent evt) {
        IHandlerService handlerService = (IHandlerService) getSite()
            .getService(IHandlerService.class);
        try {
            handlerService.executeCommand(OpenLogViewer.ID, null);
        } catch (Exception ex) {
            throw new RuntimeException("add.command not found");
            // Give message
        }
        }
    });
    logEntryTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

    navigator = new Composite(parent, SWT.NONE);
    navigator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
    navigator.setLayout(new GridLayout(3, false));

    previousPage = new Link(navigator, SWT.NONE);
    previousPage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
    previousPage.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        if(page > 1){
            setPage(page - 1);
        }
        }
    });
    previousPage.setEnabled(false);
    previousPage.setText("<a>Previous page</a>");

    labelPage = new Label(navigator, SWT.NONE);
    labelPage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
    labelPage.setText(String.valueOf(page));

    nextPage = new Link(navigator, SWT.NONE);
    nextPage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
    nextPage.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            setPage(page+1);
        }
    });
    nextPage.setEnabled(false);
    nextPage.setText("<a>Next page</a>");

    PopupMenuUtil.installPopupForView(logEntryTable, getSite(), logEntryTable);
    initializeClient();
    }

    @Override
    public void dispose() {
    if (logQuery != null) {
        logQuery.removeLogQueryListener(listener);
        logQuery.stop();
    }
    }

    private boolean initializeClient() {
    if (logbookClient == null) {
        try {
        logbookClient = LogbookClientManager.getLogbookClientFactory()
            .getClient();
        StringBuilder query = new StringBuilder();
        if (memento != null
            && memento.getString("searchString") != null) {
            query.append(memento.getString("searchString"));
        }
        if (resultSize >= 0) {
            query.append(" page:" + page);
            query.append(" limit:" + resultSize);
        }
        int delay = preferenceService.getInt("org.csstudio.logbook.viewer", "auto.refresh.rate", 3, null);
        logQuery = new PeriodicLogQuery(query.toString(), logbookClient, delay>0?delay:1, TimeUnit.MINUTES);
        logQuery.addLogQueryListener(listener);
        logQuery.start();
        return true;
        } catch (Exception ex) {
        errorBar.setException(ex);
        return false;
        }
    } else {
        return true;
    }
    }

    private void search() {
    final StringBuilder searchString = new StringBuilder(text.getText());
    if (initializeClient()) {
        try {
        if (resultSize >= 0) {
            searchString.append(" page:" + page);
            searchString.append(" limit:" + resultSize);
        }
        logQuery.setQuery(searchString.toString());
        } catch (final Exception e1) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
            errorBar.setException(e1);
            }
        });
        }
    }
    }

    private void setPage(int page){
    this.page = page;
    changeSupport.firePropertyChange("page", null, this.page);
    }

    public void setSearchString(String searchString) {
    // Do not ignore events where the search string is the same, we need to re-execute the query
    // setting the old value to null
    this.searchString = searchString;
    changeSupport.firePropertyChange("searchString", null, this.searchString);
    }

    public String getSearchString(){
    return this.searchString;
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void saveState(IMemento memento) {
    super.saveState(memento);
    memento.putString("searchString", searchString);
    }
}
