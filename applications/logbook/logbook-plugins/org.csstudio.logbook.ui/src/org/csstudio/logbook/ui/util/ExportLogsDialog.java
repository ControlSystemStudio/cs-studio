package org.csstudio.logbook.ui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.Tag;
import org.csstudio.ui.util.dialogs.StringListSelectionDialog;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import com.google.common.base.Joiner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class ExportLogsDialog extends Dialog {

    private ErrorBar errorBar;
    private final Collection<LogEntry> data;
    private Button btnAddFields;
    private Button btnAddPath;
    private Text filePath;
    private MultipleSelectionCombo<String> fieldsText;
    private Map<String, Integer> fieldPositionMap = new HashMap<String,Integer>();
    private final List<String> fields = Arrays.asList("id", "date", "modifyDate", "description", "owner", "logbooks", "tags", "level");
    private final String separator = "\t";

    protected ExportLogsDialog(Shell parentShell, List<LogEntry> data) {
        super(parentShell);
        setBlockOnOpen(false);
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
        this.data = data;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText("Export Log Entries");
        final Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 2;
        errorBar = new ErrorBar(container, SWT.NONE);
        errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        container.setLayout(new GridLayout(3, false));
        final Label filePhLabel = new Label(container, SWT.NONE);
        filePhLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        filePhLabel.setText("Save As:");
        filePath =  new Text(container, SWT.BORDER);
        filePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddPath = new Button(container, SWT.PUSH);
        btnAddPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
                dlg.setFilterExtensions(new String[] {"*.csv",  "*.txt"});
                final String filename = dlg.open();
                if (filename != null) {
                    filePath.setText(filename);
                }
            }
        });
        btnAddPath.setText("...");
        btnAddPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1, 1));
        final Label fieldsLabel = new Label(container, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        fieldsLabel.setText("Select Fields:");
        fieldsText = new MultipleSelectionCombo<String>(container, SWT.NONE);
        fieldsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        fieldsText.setItems(fields);
        fieldsText.setSelection(fields);
        btnAddFields = new Button(container, SWT.PUSH);
        btnAddFields.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            // Open a dialog which allows users to select fields
                final StringListSelectionDialog dialog = new StringListSelectionDialog(getShell(), fields, fieldsText.getSelection(), "Add Fields");
                if (dialog.open() == IDialogConstants.OK_ID) {
                    fieldsText.setSelection(Joiner.on(",").join(dialog.getSelectedValues()));
                }
            }
        });

        btnAddFields.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1, 1));
        btnAddFields.setText("...");
        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, "Submit", true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        Cursor originalCursor = getShell().getCursor();
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath.getText())));
            try {
                bw.append(Joiner.on(separator).join(getHeader()));
                for (final LogEntry log : data) {
                    bw.newLine();
                    bw.append(Joiner.on(separator).join(getLine(log)));
                }
                getShell().setCursor(originalCursor);
                setReturnCode(OK);
                close();
            } finally {
                bw.close();
            }
        } catch (Exception ex) {
            getShell().setCursor(originalCursor);
            errorBar.setException(ex);
        }
    }

    private String[] getHeader() {
        int i = 0;
        final List<String> header = new LinkedList<String>();
        for (String f : fieldsText.getSelection()) {
            if (!fieldPositionMap.containsKey(f)) {
                    fieldPositionMap.put(f, i++);
                    header.add(f);
            }
        }
        return header.toArray(new String[fieldPositionMap.size()]);
    }

    private String[] getLine(final LogEntry log) {
        final String[] line = new String[fieldPositionMap.size()];
        for (final String field : fieldPositionMap.keySet()) {
            switch (field) {
                case "id" :
                    line[fieldPositionMap.get(field)] = String.valueOf(log.getId());
                     break;
                case "owner":
                    line[fieldPositionMap.get(field)] = log.getOwner();
                     break;
                case "date":
                    line[fieldPositionMap.get(field)] = log.getCreateDate().toString();
                     break;
                case "modifyDate":
                line[fieldPositionMap.get(field)] = log.getModifiedDate() != null ?  log.getModifiedDate().toString() : "";
                 break;
                case "description":
                    line[fieldPositionMap.get(field)] = log.getText().replaceAll("\\r|\\n|\\t", " ");
                     break;
                case "logbooks":
                    StringBuilder logbooks = new StringBuilder();
                    for (final Logbook logbook : log.getLogbooks()) {
                        logbooks.append(logbook.getName() + "/");
                    }
                    line[fieldPositionMap.get(field)] = logbooks.substring(0, logbooks.length() - 1);
                     break;
                case "tags":
                    StringBuilder tags = new StringBuilder();
                    for (final Tag tag : log.getTags()) {
                        tags.append(tag.getName() + "/");
                    }
                    line[fieldPositionMap.get(field)] = tags.length() == 0 ? "" : tags.substring(0, tags.length() - 1);
                     break;
                case "level":
                    line[fieldPositionMap.get(field)] = log.getLevel();
                     break;
            }
        }
        return line;
    }

}
