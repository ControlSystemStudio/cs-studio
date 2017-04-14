package org.csstudio.logbook.olog.property.fault;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FaultExportDialog extends Dialog {

    private static final Logger log = Logger.getLogger(ExportFault.class.getCanonicalName());

    private Text text;
    private Combo combo;

    private String filePath;
    private final List<Fault> faults;
    private String delimiter = ";";


    protected FaultExportDialog(Shell parentShell, List<Fault> faults) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
        this.faults = faults;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(3, false));

        Label lblNewLabel = new Label(container, SWT.CENTER);
        lblNewLabel.setText("File:");

        text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                String[] filterExt = {"*.txt", "*.csv"};
                fileDialog.setFilterExtensions(filterExt);
                filePath = fileDialog.open();
                if (filePath != null) {
                    Display.getDefault().asyncExec(() -> {
                        text.setText(filePath);
                    });
                }
            }
        });
        btnNewButton.setText("Browse");

        Label lblNewLabel_1 = new Label(container, SWT.NONE);
        lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel_1.setText("Delimiter:");

        combo = new Combo(container, SWT.NONE);
        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                delimiter = combo.getText();
            }
        });
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        combo.setText(delimiter);
        new Label(container, SWT.NONE);
        return container;
    }


    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, "Export", true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        // Export the log entries
        new ExportJob(faults, filePath, delimiter).schedule();
        super.okPressed();
    }

    private static class ExportJob extends Job {

        private final List<Fault> faults;
        private final String filePath;
        private final String delimiter;

        public ExportJob(List<Fault> faults, String filePath, String delimiter) {
            super("Export Faults");
            this.faults = faults;
            this.filePath = filePath;
            this.delimiter = delimiter;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                File file = new File(filePath);
                BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                for (Fault fault : faults) {
                    writer.write(FaultAdapter.createFaultExportText(fault, delimiter));
                    writer.newLine();
                    if (monitor.isCanceled()) {
                        writer.close();
                        return Status.CANCEL_STATUS;
                    }
                }
                writer.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
            return Status.OK_STATUS;
        }

    }
}
