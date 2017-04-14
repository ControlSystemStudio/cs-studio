/**
 *
 */
package org.csstudio.logbook.olog.property.fault;

import java.util.Arrays;
import java.util.List;

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

/**
 * @author shroffk
 *
 */
public class CreateFaultEntryWidgetDemo extends ApplicationWindow {

    public CreateFaultEntryWidgetDemo() {
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

        List<Integer> logIds = Arrays.asList(1234, 222, 2345);
        List<String> logbooks = Arrays.asList("Operations", "LOTO", "Commisioning");
        List<String> tags = Arrays.asList("Fault", "MASAR", "RF");

        FaultEditorWidget faultEditorWidget = new FaultEditorWidget(container, SWT.NONE,
                FaultConfigurationFactory.getConfiguration(), logbooks, tags);
        faultEditorWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));


        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Fault fault = new Fault("An example fault");
                fault.setArea("Area");
                fault.setSubsystem("System");
                fault.setDevice("Device");
                faultEditorWidget.setFault(fault);
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
                faultEditorWidget.setFault(fault);
                faultEditorWidget.setLogIds(logIds);
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
            CreateFaultEntryWidgetDemo window = new CreateFaultEntryWidgetDemo();
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
