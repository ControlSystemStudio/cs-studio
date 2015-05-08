package org.csstudio.graphene.sample;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.csstudio.graphene.AxisRangeEditorComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.epics.graphene.AxisRanges;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;

public class AxisRangeEditorSample {

    protected Shell shell;
    private AxisRangeEditorComposite axisRangeEditorComposite;
    private Text txtEvents;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            AxisRangeEditorSample window = new AxisRangeEditorSample();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(636, 552);
        shell.setText("SWT Application");
        shell.setLayout(new FormLayout());

        axisRangeEditorComposite = new AxisRangeEditorComposite(shell, SWT.NONE);
        FormData fd_axisRangeEditorComposite = new FormData();
        fd_axisRangeEditorComposite.top = new FormAttachment(0);
        fd_axisRangeEditorComposite.left = new FormAttachment(0);
        axisRangeEditorComposite.setLayoutData(fd_axisRangeEditorComposite);
        axisRangeEditorComposite.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                String text = txtEvents.getText();
                if (text.isEmpty()) {
                    text += event.getSelection().toString();
                } else {
                    text += System.lineSeparator() + event.getSelection().toString();
                }
                txtEvents.setText(text);
            }
        });

        txtEvents = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.CANCEL);
        FormData fd_txtEvents = new FormData();
        fd_txtEvents.top = new FormAttachment(0, 10);
        fd_txtEvents.bottom = new FormAttachment(100, -10);
        fd_txtEvents.left = new FormAttachment(axisRangeEditorComposite, 6);
        fd_txtEvents.right = new FormAttachment(100, -10);
        txtEvents.setLayoutData(fd_txtEvents);

        Composite composite = new Composite(shell, SWT.NONE);
        FormData fd_composite = new FormData();
        fd_composite.right = new FormAttachment(axisRangeEditorComposite, 0, SWT.RIGHT);
        fd_composite.bottom = new FormAttachment(100, -10);
        fd_composite.left = new FormAttachment(0, 10);
        fd_composite.top = new FormAttachment(100, -125);
        composite.setLayoutData(fd_composite);

        Button btnNull = new Button(composite, SWT.NONE);
        btnNull.setBounds(0, 85, 90, 30);
        btnNull.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(null);
            }
        });
        btnNull.setText("Null");

        Button btnDisplay = new Button(composite, SWT.NONE);
        btnDisplay.setBounds(0, 49, 90, 30);
        btnDisplay.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(AxisRanges.display());
            }
        });
        btnDisplay.setText("Display");

        Button btnData = new Button(composite, SWT.NONE);
        btnData.setBounds(0, 13, 90, 30);
        btnData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(AxisRanges.data());
            }
        });
        btnData.setText("Data");

        Button btnAbsolute1 = new Button(composite, SWT.NONE);
        btnAbsolute1.setBounds(96, 85, 128, 30);
        btnAbsolute1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(AxisRanges.fixed(-10, 10));
            }
        });
        btnAbsolute1.setText("Fixed -10/10");

        Button btnIntegrated = new Button(composite, SWT.NONE);
        btnIntegrated.setBounds(230, 85, 105, 30);
        btnIntegrated.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(AxisRanges.auto());
            }
        });
        btnIntegrated.setText("Auto");

        Button btnAbsolute2 = new Button(composite, SWT.NONE);
        btnAbsolute2.setBounds(96, 49, 128, 30);
        btnAbsolute2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(AxisRanges.fixed(0, 25));
            }
        });
        btnAbsolute2.setText("Fixed 0/25");

        Button btnIntegrated50 = new Button(composite, SWT.NONE);
        btnIntegrated50.setBounds(230, 49, 105, 30);
        btnIntegrated50.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                axisRangeEditorComposite.setAxisRange(AxisRanges.auto(0.5));
            }
        });
        btnIntegrated50.setText("Auto 50");

    }
}
