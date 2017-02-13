/**
 *
 */
package org.csstudio.logbook.olog.property.fault;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

/**
 * @author shroffk
 *
 */
public class CreateFaultViewerWidgetDemo extends ApplicationWindow {

    public CreateFaultViewerWidgetDemo() {
        super(null);

        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();
    }

    private FaultViewWidget faultViewWidget;

    /**
     * Create contents of the application window.
     *
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        List<String> logIds = Arrays.asList("1", "2", "3");
        List<String> logbooks = Arrays.asList("Operations", "LOTO", "Commisioning");
        List<String> tags = Arrays.asList("Fault", "MASAR", "RF");

        faultViewWidget = new FaultViewWidget(container, SWT.NONE);
        faultViewWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Fault fault = new Fault("An example fault");
                fault.setArea("Area");
                fault.setSubsystem("System");
                fault.setDevice("Device");
                faultViewWidget.setLogEntries(Collections.emptyList());
                faultViewWidget.setFault(fault);
            }
        });
        btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnNewButton.setText("Add a simple fault");

        Button btnNewButton_1 = new Button(container, SWT.NONE);
        btnNewButton_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Fault fault = new Fault("An example fault with log entries");
                fault.setArea("Area");
                fault.setSubsystem("System");
                fault.setDevice("Device");
                faultViewWidget.setFault(fault);
                try {
                    List<LogEntry> logs = new ArrayList<LogEntry>();
                    logs.add(LogEntryBuilder.withText("First test log entry").attach(AttachmentBuilder.attachment("resources/opi.png")
                            .inputStream(new FileInputStream("resources/opi.png"))).build());
                    logs.add(LogEntryBuilder.withText("Second test log entry").attach(AttachmentBuilder.attachment("resources/opi.png")
                            .inputStream(new FileInputStream("resources/opi.png"))).build());
                    faultViewWidget.setLogEntries(logs);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnNewButton_1.setText("Add fault with logs");

        return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            CreateFaultViewerWidgetDemo window = new CreateFaultViewerWidgetDemo();
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
        newShell.setText("Test ImageStackWidgetTest");
    }

}
