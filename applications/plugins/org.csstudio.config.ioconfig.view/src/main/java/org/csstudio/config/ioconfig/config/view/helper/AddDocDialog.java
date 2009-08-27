package org.csstudio.config.ioconfig.config.view.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

import org.csstudio.config.ioconfig.model.Activator;
import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.preference.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddDocDialog extends Dialog {

    private Document _document;
    private GregorianCalendar _createDate;

    protected AddDocDialog(Shell parentShell) {
        super(parentShell);
        _document = new Document();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        InstanceScope instanceScope = new InstanceScope();
        IEclipsePreferences node = instanceScope.getNode(Activator.PLUGIN_ID);
        _createDate = new GregorianCalendar();
        GridData gridData = (GridData) parent.getLayoutData();
        gridData.minimumWidth = 400;
        getShell().setText("Add new Document");
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(4, false);
        dialogArea.setLayout(gridLayout);
        String[] logbooks = node.get(PreferenceConstants.DDB_LOGBOOK, "MKS-2-DOC").split(",");
        final ComboViewer logbooksViewer = new ComboViewer(dialogArea);
        logbooksViewer.setContentProvider(new ArrayContentProvider());
        logbooksViewer.setInput(logbooks);
        logbooksViewer.getCombo().select(0);
        final Text eLogbookIdLabel = new Text(dialogArea, SWT.BORDER);
        eLogbookIdLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        eLogbookIdLabel.setEditable(false);
        String generateId = generateId(logbooksViewer);
        eLogbookIdLabel.setText(generateId);
        _document.setId(generateId);
        
        // IMAGE
        Label file = new Label(dialogArea, SWT.NONE);
        file.setText("File: ");
        final Text filePathText = new Text(dialogArea, SWT.MULTI | SWT.BORDER);
        filePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // MIME_TYPE
        Composite composite = new Composite(dialogArea, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite.setLayout(new GridLayout(2, false));

        final Text mimeTypeValue = new Text(composite, SWT.NONE | SWT.BORDER);
        mimeTypeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        mimeTypeValue.setEditable(false);
        mimeTypeValue.setText("");
        Button fileButton = new Button(composite, SWT.PUSH);
        fileButton.setText("File");
        // Bedeutung
        Label label = new Label(dialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Bedeutung: ");
        String[] meaning = node.get(PreferenceConstants.DDB_LOGBOOK_MEANING, "DOCU").split(",");
        final Combo meaningCombo = new Combo(dialogArea, SWT.DROP_DOWN);
        meaningCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 3, 1));
        meaningCombo.setItems(meaning);
        meaningCombo.select(0);
        String item = meaningCombo.getItem(meaningCombo.getSelectionIndex());
        System.out.println("Debug select: "+item);
        _document.setLogseverity(item);

        // SUBJECT
        Label shortDesc = new Label(dialogArea, SWT.NONE);
        shortDesc.setText("Titel: ");
        final Text shortDescText = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        shortDescText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        // DESCLONG
        Label longDesc = new Label(dialogArea, SWT.NONE);
        longDesc.setText("Description: ");
        final Text longDescText = new Text(dialogArea, SWT.MULTI | SWT.BORDER);
        longDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 4));
        new Label(dialogArea, SWT.NONE);
        new Label(dialogArea, SWT.NONE);
        new Label(dialogArea, SWT.NONE);
        // LOCATION
        Label location = new Label(dialogArea, SWT.NONE);
        location.setText("Location: ");
        final Text locationText = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        // KEYWORDS
        Label keywords = new Label(dialogArea, SWT.NONE);
        keywords.setText("Keywords: ");
        final Text keywordsText = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
        keywordsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        // ACCOUNTNAME
        Label creater = new Label(dialogArea, SWT.NONE);
        creater.setText("Author: ");

        Text createrValue = new Text(dialogArea, SWT.BORDER);
        createrValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createrValue.setEditable(false);
        String userName = ConfigHelper.getUserName();
        createrValue.setText(userName);
        _document.setAccountname(userName);
        // ENTRYDATE
        Label createdOn = new Label(dialogArea, SWT.NONE);
        createdOn.setText("Eintrag: ");
        Text createdOnValue = new Text(dialogArea, SWT.BORDER);
        createdOnValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createdOnValue.setEditable(false);
        String format = String.format("%1$tF %1$tT", _createDate);
        createdOnValue.setText(format);
        _document.setCreatedDate(_createDate.getTime());
        _document.setEntrydate(_createDate.getTime());
        
        // LOGSEVERITY
        // ERRORIDENTIFYER
        // CREATED_DATE
        // DELETE_DATE
        // UPDATE_DATE
        
        meaningCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                select();
            }
            
            private void select() {
                String item = meaningCombo.getItem(meaningCombo.getSelectionIndex());
                System.out.println("Debug select: "+item);
                _document.setLogseverity(item);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                select();
            }
        });

        logbooksViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                String generateId = generateId(logbooksViewer);
                _document.setId(generateId);
                eLogbookIdLabel.setText(generateId);
            }

        });

        fileButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                action();
            }

            public void widgetSelected(SelectionEvent e) {
                action();
            }

            private void action() {
                FileDialog fileDialog = new FileDialog(parent.getShell());
                String fileName = fileDialog.open();
                if (fileName != null) {
                    File file = new File(fileName);
                    filePathText.setText(fileName);
                    String[] split = fileName.split("\\.");
                    String mimeType = split[split.length - 1];
                    mimeTypeValue.setText(mimeType);
                    _document.setMimeType(mimeType);
                    byte[] data = new byte[(int) file.length()];
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        fileInputStream.read(data);
                        fileInputStream.close();
                        _document.setImage(data);
                    } catch (IOException e) {
                        MessageDialog.openError(getParentShell(), "File open Error", "Can't read file!");
                        CentralLogger.getInstance().error(this, e);
                    }
                }
            }
        });
        
        shortDescText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                _document.setSubject(shortDescText.getText());
            }
        });
        
        longDescText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                _document.setDesclong(longDescText.getText());
            }
        });
        
        locationText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                _document.setLocation(locationText.getText());
            }
        });
        
        keywordsText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                _document.setKeywords(keywordsText.getText());
            }
        });
        return dialogArea;
    }

    private String generateId(Viewer logbooksViewer) {
        String eLogbookId = (String) ((StructuredSelection) logbooksViewer.getSelection())
                .getFirstElement();
        return String.format("%1$s:%2$ty%2$tm%2$td-%2$tT", eLogbookId, _createDate);
    }
    
    public Document getDocument() {
        return _document;
    }
    
    
}
