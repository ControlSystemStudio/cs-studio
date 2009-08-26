package org.csstudio.config.ioconfig.config.view.helper;

import java.awt.datatransfer.StringSelection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.Workbench;

public class AddDocDialog extends Dialog {

    protected AddDocDialog(Shell parentShell) {
        super(parentShell);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        GregorianCalendar createDate = new GregorianCalendar();
        GridData gridData = (GridData) parent.getLayoutData();
        gridData.minimumWidth = 400;
        getShell().setText("Add new Document");
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(3, false);
        dialogArea.setLayout(gridLayout);
        String[] logbooks = new String[] { "MKS-2-DOC", "MKS-2" };
        ComboViewer logbooksViewer = new ComboViewer(dialogArea);
        logbooksViewer.setContentProvider(new ArrayContentProvider());
        logbooksViewer.setInput(logbooks);
        logbooksViewer.getCombo().select(0);
        String eLogbookId = (String) ((StructuredSelection)logbooksViewer.getSelection()).getFirstElement();
        eLogbookId = String.format("%1$s:%2$ty%2$tm%2$td-%2$tT", eLogbookId, createDate);
        Label label = new Label(dialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText(eLogbookId);

        // IMAGE
        Label file = new Label(dialogArea, SWT.NONE);
        file.setText("File: ");
        final Text filePathText = new Text(dialogArea, SWT.MULTI | SWT.BORDER);
        filePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Button fileButton = new Button(dialogArea, SWT.PUSH);
        fileButton.setText("File");
        // MIME_TYPE
        Label mimeType = new Label(dialogArea, SWT.NONE);
        mimeType.setText("MIME Type: ");
        final Label mimeTypeValue = new Label(dialogArea, SWT.NONE | SWT.BORDER);
        mimeTypeValue.setText("");
        mimeTypeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(dialogArea, SWT.NONE);
        // SUBJECT
        Label shortDesc = new Label(dialogArea, SWT.NONE);
        shortDesc.setText("Subject: ");
        Text shortDescText = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        shortDescText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // DESCLONG
        Label longDesc = new Label(dialogArea, SWT.NONE);
        longDesc.setText("Description: ");
        Text longDescText = new Text(dialogArea, SWT.MULTI | SWT.BORDER);
        longDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 4));
        new Label(dialogArea, SWT.NONE);
        new Label(dialogArea, SWT.NONE);
        new Label(dialogArea, SWT.NONE);
        // LOCATION
        Label location = new Label(dialogArea, SWT.NONE);
        location.setText("Location: ");
        Text locationText = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // KEYWORDS
        Label keywords = new Label(dialogArea, SWT.NONE);
        keywords.setText("Keywords: ");
        Text keywordsText = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        keywordsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // ACCOUNTNAME
        Label creater = new Label(dialogArea, SWT.NONE);
        creater.setText("Created by: ");
        Label createrValue = new Label(dialogArea, SWT.NONE);
        createrValue.setText("");
        // ENTRYDATE
        // LOGSEVERITY
        // ERRORIDENTIFYER
        // CREATED_DATE
        // DELETE_DATE
        // UPDATE_DATE

        fileButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                action();
            }

            public void widgetSelected(SelectionEvent e) {
                action();
            }

            private void action() {
                FileDialog fileDialog = new FileDialog(parent.getShell());
                String file = fileDialog.open();
                if (file != null) {
                    filePathText.setText(file);
                    String[] split = file.split("\\.");
                    mimeTypeValue.setText(split[split.length - 1]);
                }
            }
        });

        return dialogArea;
    }

}
