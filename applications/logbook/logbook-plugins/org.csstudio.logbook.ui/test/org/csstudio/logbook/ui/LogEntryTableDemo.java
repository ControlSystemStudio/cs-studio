/**
 *
 */
package org.csstudio.logbook.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookBuilder;
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
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 *
 */
public class LogEntryTableDemo extends ApplicationWindow {

    public LogEntryTableDemo() {
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
    final LogEntryTable logEntryTable = new LogEntryTable(container,
        SWT.NONE);
    logEntryTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        true, 1, 1));

    Button btnNewButton = new Button(container, SWT.NONE);
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
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
    btnNewButton.setText("test logEntry set1");

    Button btnNewButton_1 = new Button(container, SWT.NONE);
    btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
        false, 1, 1));
    btnNewButton_1.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        LogEntry logEntry;
        try {
            logEntry = LogEntryBuilder.withText("SomeText")
                .owner("shroffk")
                .addLogbook(LogbookBuilder.logbook("test"))
                .addLogbook(LogbookBuilder.logbook("test2"))
                .build();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        }
    });
    btnNewButton_1.setText("testlogEntry set2");
    return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
    try {
        LogEntryTableDemo window = new LogEntryTableDemo();
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
