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

import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.util.LogEntrySearchUtil;
import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

/**
 * A view to search for logEntries and then display them in a Tree form
 * 
 * @author shroffk
 * 
 */
public class LogTreeView extends ViewPart {
    private Text text;
    private org.csstudio.logbook.ui.extra.LogEntryTree logEntryTree;

    private final IPreferencesService service = Platform.getPreferencesService();
    
    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.logbook.ui.LogTreeView"; //$NON-NLS-1$

    private LogbookClient logbookClient;
    private Label label;

    private List<String> logbooks = Collections.emptyList();
    private List<String> tags = Collections.emptyList();
    private ErrorBar errorBar;
    
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private String searchString;
    
    private String resultSize;
    private boolean showHistory;

    public LogTreeView() {
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

	changeSupport.addPropertyChangeListener("searchString", new PropertyChangeListener() {
	    
	    @Override
	    public void propertyChange(PropertyChangeEvent arg0) {
		text.setText(searchString);
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
		    // search();
		}
	    }
	});	
	text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Button btnNewButton = new Button(parent, SWT.NONE);
	
	try {
	    resultSize = service.getString("org.csstudio.logbook.ui","Result.size", "", null);
	    showHistory = service.getBoolean("org.csstudio.logbook.ui","Show.history", false, null);
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
				for (Logbook logbook : logbookClient.listLogbooks()) {
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
			    e.printStackTrace();
			}
		    }
		};
		BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
	    }
	});
	btnNewButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	btnNewButton.setText("Adv Search");
	
	Button configureButton = new Button(parent, SWT.NONE);
	configureButton.setImage(ResourceManager.getPluginImage(
		"org.csstudio.channel.widgets", "icons/gear-16.png"));
	configureButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
			    LogViewConfigurationDialog dialog = new LogViewConfigurationDialog(
				    parent.getShell(), logEntryTree.isExpanded(), logEntryTree.getLogEntryOrder());
			    dialog.setBlockOnOpen(true);
			    if (dialog.open() == IDialogConstants.OK_ID) {
				logEntryTree.setLogEntryOrder(dialog.getOrder());
				logEntryTree.setExpanded(dialog.isExpandable());
			    }
			}
		    });
	    }
	});

	// Add AutoComplete support, use type logEntrySearch
	new AutoCompleteWidget(text, "LogentrySearch");	
	
	label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
	label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

	logEntryTree = new org.csstudio.logbook.ui.extra.LogEntryTree(parent,
		SWT.NONE | SWT.SINGLE);
	logEntryTree.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseDoubleClick(MouseEvent evt) {
		IHandlerService handlerService = (IHandlerService) getSite()
			.getService(IHandlerService.class);
		try {
		    handlerService.executeCommand(OpenLogViewer.ID, null);
		} catch (Exception ex) {		    
		    errorBar.setException(ex);
		}
	    }
	});

	logEntryTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

	PopupMenuUtil.installPopupForView(logEntryTree, getSite(),
		logEntryTree);
	initializeClient();
    }

    private boolean initializeClient() {
	if (logbookClient == null) {
	    try {
		logbookClient = LogbookClientManager.getLogbookClientFactory().getClient();
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
	Job search = new Job("Searching") {

	    @Override
	    protected IStatus run(IProgressMonitor monitor) {
		if (initializeClient()) {
		    try {
			if(!resultSize.isEmpty() && Integer.valueOf(resultSize) >= 0){
			    searchString.append(" page:1");
			    searchString.append(" limit:" + resultSize);
			}
			if(showHistory){
			    searchString.append(" history:");
			}
			final List<LogEntry> logEntries = new ArrayList<LogEntry>(
				logbookClient.findLogEntries(searchString.toString()));
			Display.getDefault().asyncExec(new Runnable() {
			    @Override
			    public void run() {
				logEntryTree.setLogs(logEntries);
			    }
			});
		    } catch (Exception e1) {
			e1.printStackTrace();
		    }
		}
		return Status.OK_STATUS;
	    }
	};
	search.schedule();
    }


    public void setSearchString(String searchString) {
	String oldValue = this.searchString;
	this.searchString = searchString;
	changeSupport.firePropertyChange("searchString", oldValue, this.searchString);
    }
    
    @Override
    public void setFocus() {
    }
}
