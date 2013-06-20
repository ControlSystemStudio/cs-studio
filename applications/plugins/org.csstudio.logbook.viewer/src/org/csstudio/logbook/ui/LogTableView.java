/**
 * 
 */
package org.csstudio.logbook.ui;

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
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.swt.events.MouseAdapter;

/**
 * @author shroffk
 * 
 */
public class LogTableView extends ViewPart {
    private Text text;
    private org.csstudio.logbook.ui.extra.LogEntryTable logEntryTable;

    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.logbook.ui.LogTableView"; //$NON-NLS-1$

    private LogbookClient logbookClient;
    private Label label;

    private List<String> logbooks = Collections.emptyList();
    private List<String> tags = Collections.emptyList();

    public LogTableView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
	parent.setLayout(new FormLayout());

	Label lblLogQuery = new Label(parent, SWT.NONE);
	FormData fd_lblLogQuery = new FormData();
	fd_lblLogQuery.top = new FormAttachment(0, 5);
	lblLogQuery.setLayoutData(fd_lblLogQuery);
	lblLogQuery.setText("Log Query:");

	Button btnNewButton = new Button(parent, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
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
		    LogEntrySearchDialog dialog = new LogEntrySearchDialog(
			    parent.getShell(),
			    logbooks,
			    tags,
			    LogEntrySearchUtil.parseSearchString(text.getText()));
		    dialog.setBlockOnOpen(true);
		    if (dialog.open() == IDialogConstants.OK_ID) {
			text.setText(dialog.getSearchString());
			text.getParent().update();
			search();
		    }
		} catch (Exception e2) {
		    e2.printStackTrace();
		}
	    }
	});
	FormData fd_btnNewButton = new FormData();
	fd_btnNewButton.top = new FormAttachment(0, 3);
	fd_btnNewButton.right = new FormAttachment(100, -5);
	btnNewButton.setLayoutData(fd_btnNewButton);
	btnNewButton.setText("Adv Search");

	text = new Text(parent, SWT.BORDER);
	text.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
		    search();
		}
	    }
	});
	FormData fd_text = new FormData();
	fd_text.right = new FormAttachment(btnNewButton, -5);
	fd_text.left = new FormAttachment(lblLogQuery, 5);
	fd_text.top = new FormAttachment(0, 5);
	text.setLayoutData(fd_text);

	// Add AutoComplete support, use type logEntrySearch
	new AutoCompleteWidget(text, "LogentrySearch");

	label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
	label.addMouseMoveListener(new MouseMoveListener() {
	    public void mouseMove(MouseEvent e) {
		FormData fd = (FormData) label.getLayoutData();
		long calNumerator = fd.top.numerator + (e.y * 100)
			/ e.display.getActiveShell().getClientArea().height;
		fd.top = new FormAttachment((int) calNumerator);
		label.setLayoutData(fd);
		label.getParent().layout();
		logEntryTable.layout();
	    }
	});
	label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));
	FormData fd_label = new FormData();
	fd_label.top = new FormAttachment(100);
	fd_label.right = new FormAttachment(100, -2);
	fd_label.left = new FormAttachment(0, 2);
	label.setLayoutData(fd_label);

	logEntryTable = new org.csstudio.logbook.ui.extra.LogEntryTable(parent,
		SWT.NONE | SWT.SINGLE);
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
	fd_lblLogQuery.left = new FormAttachment(logEntryTable, 0, SWT.LEFT);
	FormData fd_logEntryTable = new FormData();
	fd_logEntryTable.top = new FormAttachment(text, 5);
	fd_logEntryTable.right = new FormAttachment(100, -3);
	fd_logEntryTable.left = new FormAttachment(0, 3);
	fd_logEntryTable.bottom = new FormAttachment(label, -5);

	logEntryTable.setLayoutData(fd_logEntryTable);

	PopupMenuUtil.installPopupForView(logEntryTable, getSite(),
		logEntryTable);
	initializeClient();
    }

    private boolean initializeClient() {
	if (logbookClient == null) {
	    try {
		logbookClient = LogbookClientManager.getLogbookClientFactory()
			.getClient();
		return true;
	    } catch (Exception e1) {
		e1.printStackTrace();
		return false;
	    }
	} else {
	    return true;
	}
    }

    private void search() {
	if (initializeClient()) {
	    try {
		final List<LogEntry> logEntries = new ArrayList<LogEntry>(
			logbookClient.findLogEntries(text.getText()));
		Display.getDefault().asyncExec(new Runnable() {

		    @Override
		    public void run() {
			logEntryTable.setLogs(logEntries);
		    }
		});
	    } catch (Exception e1) {
		e1.printStackTrace();
	    }
	}
    }

    @Override
    public void setFocus() {
    }
}
