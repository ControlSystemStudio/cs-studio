/**
 * 
 */
package org.csstudio.logbook.ui.extra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.TagBuilder;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author shroffk
 * 
 */
public class LogEntryTableTest extends ApplicationWindow {
    private static String MEDIUM_TEXT = "this a a text that is a bit longer, but not too long. This row should have a smaller height than row #1";
    private LogEntryTable logEntryTable;
    private Text text;

    public LogEntryTableTest() {
	super(null);
	addToolBar(SWT.FLAT | SWT.WRAP);
	addMenuBar();
	addStatusLine();
    }

    /**
     * Create contents of the application window.
     * 
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
	Composite container = new Composite(parent, SWT.NONE);
	container.setLayout(new GridLayout(1, false));

	logEntryTable = new LogEntryTable(container, SWT.NONE);

	logEntryTable
		.addSelectionChangedListener(new ISelectionChangedListener() {

		    @Override
		    public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
			    for (Object selection : ((IStructuredSelection) event
				    .getSelection()).toList()) {
				LogEntry logEntry = (LogEntry) selection;
				String selectedLogEntry = logEntry.getId()
					+ " : " + logEntry.getText();
				text.setText(selectedLogEntry);
			    }
			}
		    }
		});
	logEntryTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true, 1, 1));

	Button btnNewButton = new Button(container, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		String eol = System.getProperty("line.separator");
		try {
		    for (int i = 0; i < 10; i++) {
			StringBuffer sb = new StringBuffer("line" + i);
			for (int j = 0; j < i; j++) {
			    sb.append(eol);
			    sb.append("line" + j);
			}
			logEntries.add(LogEntryBuilder.withText(sb.toString())
				.owner("shroffk")
				.addLogbook(LogbookBuilder.logbook("test"))
				.addLogbook(LogbookBuilder.logbook("test2"))
				.build());
		    }
		    logEntryTable.setLogs(logEntries);
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});
	btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	btnNewButton.setText("Add Test Log Entries");

	Button btnAddTestLog = new Button(container, SWT.NONE);
	btnAddTestLog.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		String eol = System.getProperty("line.separator");
		try {
		    for (int i = 0; i < 10; i++) {
			LogEntryBuilder logEntryBuilder = LogEntryBuilder
				.withText("").owner(String.valueOf(i))
				.addLogbook(LogbookBuilder.logbook("test"));
			StringBuffer sb = new StringBuffer("line" + i);
			if (i % 2 == 0) {
			    sb.append(MEDIUM_TEXT);
			    logEntryBuilder.addTag(TagBuilder.tag("evenTag"));
			    logEntryBuilder.addLogbook(LogbookBuilder
				    .logbook("Operations"));
			} else {
			    logEntryBuilder.addTag(TagBuilder.tag("oddTag"));
			    logEntryBuilder.addLogbook(LogbookBuilder
				    .logbook("Mechanical Systems"));
			}
			for (int j = 0; j < i; j++) {
			    sb.append(eol);
			    sb.append("line" + j);
			    logEntryBuilder.attach(AttachmentBuilder
				    .attachment("file" + j));
			}
			logEntryBuilder.addText(sb.toString());
			logEntries.add(logEntryBuilder.build());
		    }
		    logEntryTable.setLogs(logEntries);
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});
	btnAddTestLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	btnAddTestLog.setText("Add Test Log Entries2");

	Label lblSelectedEntry = new Label(container, SWT.NONE);
	lblSelectedEntry.setText("Selected Entry:");

	text = new Text(container, SWT.BORDER);
	text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	return container;
    }

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
	try {
	    LogEntryTableTest window = new LogEntryTableTest();
	    window.setBlockOnOpen(true);
	    window.open();
	    Display.getCurrent().dispose();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Configure the shell.
     * 
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("New Application");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
	return new Point(473, 541);
    }
}
