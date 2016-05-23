/**
 *
 */
package org.csstudio.logbook.ui;

import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.logbook.AttachmentBuilder;
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
public class LogEntryWidgetDemo extends ApplicationWindow {

    public LogEntryWidgetDemo() {
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
    container.setLayout(new GridLayout(5, false));
    final LogEntryWidget logEntryWidget = new LogEntryWidget(container,
        SWT.WRAP, true, false);
    logEntryWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        true, 5, 1));

    Button btnNewButton = new Button(container, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        LogEntry logEntry;
        try {
            logEntry = LogEntryBuilder
                .withText("SomeText\nsome more text")
                .owner("shroffk")
                .addLogbook(LogbookBuilder.logbook("test"))
                .addLogbook(LogbookBuilder.logbook("test2"))
                .addLogbook(LogbookBuilder.logbook("test3"))
                .addLogbook(LogbookBuilder.logbook("test4"))
                .addLogbook(LogbookBuilder.logbook("test5"))
                .attach(AttachmentBuilder.attachment(
                    "plugin.properties").inputStream(
                    new FileInputStream("plugin.properties")))
                .build();
            logEntryWidget.setLogEntry(logEntry);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        }
    });
    btnNewButton.setText("test logEntry");

    Button btnNewButton_1 = new Button(container, SWT.NONE);
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
            logEntryWidget.setLogEntry(logEntry);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        }
    });
    btnNewButton_1.setText("simple Entry");
    return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
    try {
        LogEntryWidgetDemo window = new LogEntryWidgetDemo();
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
