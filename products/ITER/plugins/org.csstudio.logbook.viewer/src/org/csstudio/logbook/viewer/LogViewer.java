/**
 * 
 */
package org.csstudio.logbook.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.ui.LogEntryTable;
import org.csstudio.logbook.ui.LogEntryWidget;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author shroffk
 * 
 */
public class LogViewer extends ViewPart {
    private Text text;
    private LogEntryTable logEntryTable;

    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.logbook.viewer.LogViewer"; //$NON-NLS-1$

    private LogbookClient logbookClient;
    private LogEntryWidget logEntryWidget;
    private Label label;

    public LogViewer() {
    }

    @Override
    public void createPartControl(final Composite parent) {
	parent.setLayout(new FormLayout());

	Label lblLogQuery = new Label(parent, SWT.NONE);
	FormData fd_lblLogQuery = new FormData();
	fd_lblLogQuery.top = new FormAttachment(0, 5);
	lblLogQuery.setLayoutData(fd_lblLogQuery);
	lblLogQuery.setText("Log Query:");

	text = new Text(parent, SWT.BORDER);
	text.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
		    if (logbookClient == null) {
			try {
			    logbookClient = LogbookClientManager
				    .getLogbookClientFactory().getClient();
			} catch (Exception e1) {
			    e1.printStackTrace();
			}
		    } else {
			try {
			    final Collection<LogEntry> logEntries = logbookClient
				    .findLogEntries(text.getText());
			    Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
				    logEntryTable.setLogs(logEntries);
				    parent.layout();
				}
			    });
			} catch (Exception e1) {
			    e1.printStackTrace();
			}
		    }
		}
	    }
	});
	FormData fd_text = new FormData();
	fd_text.left = new FormAttachment(lblLogQuery, 5);
	fd_text.top = new FormAttachment(0, 5);
	fd_text.right = new FormAttachment(100, -3);
	text.setLayoutData(fd_text);

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

	logEntryTable = new LogEntryTable(parent, SWT.NONE);
	fd_lblLogQuery.left = new FormAttachment(logEntryTable, 0, SWT.LEFT);
	FormData fd_logEntryTable = new FormData();
	fd_logEntryTable.top = new FormAttachment(text, 5);
	fd_logEntryTable.right = new FormAttachment(100, -3);
	fd_logEntryTable.left = new FormAttachment(0, 3);
	fd_logEntryTable.bottom = new FormAttachment(label, -5);

	logEntryTable.setLayoutData(fd_logEntryTable);
	logEntryTable
		.addSelectionChangedListener(new ISelectionChangedListener() {

		    @Override
		    public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
			    IStructuredSelection selection = (IStructuredSelection) event
				    .getSelection();
			    if (selection != null && !selection.isEmpty()) {
				FormData fd = (FormData) label.getLayoutData();
				fd.top = new FormAttachment(60);
				label.setLayoutData(fd);
				logEntryWidget.setLogEntry((LogEntry) selection
					.getFirstElement());
			    } else {
				FormData fd = (FormData) label.getLayoutData();
				fd.top = new FormAttachment(100);
				label.setLayoutData(fd);
				logEntryWidget.setLogEntry(null);
			    }
			    parent.layout();
			}
		    }
		});
	logEntryWidget = new LogEntryWidget(parent, SWT.NONE, false, false);
	FormData fd_logEntryWidget = new FormData();
	fd_logEntryWidget.right = new FormAttachment(100, -3);
	fd_logEntryWidget.top = new FormAttachment(label, -5);
	fd_logEntryWidget.left = new FormAttachment(0, 3);
	fd_logEntryWidget.bottom = new FormAttachment(100, -5);
	logEntryWidget.setLayoutData(fd_logEntryWidget);

	PopupMenuUtil.installPopupForView(logEntryTable, getSite(),
		logEntryTable);
    }

    @Override
    public void setFocus() {
	// TODO Auto-generated method stub

    }

}
