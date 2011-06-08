package org.csstudio.config.ioconfig.config.view.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.IOConifgActivator;
import org.csstudio.config.ioconfig.model.preference.PreferenceConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rickens Helge
 * @author $Author: $
 * @since 14.12.2010
 */
public class AddDocDialog extends Dialog {
    
    private static final Logger LOG = LoggerFactory.getLogger(AddDocDialog.class);

    private DocumentDBO _document;
    private GregorianCalendar _date;

    protected AddDocDialog(@Nullable final Shell parentShell,@CheckForNull final IDocument document) {
        super(parentShell);
        if(document==null) {
            _document = new DocumentDBO();
        }else {
            _document = (DocumentDBO) document;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override @Nonnull
    protected Control createDialogArea(@Nonnull final Composite parent) {
        InstanceScope instanceScope = new InstanceScope();
        IEclipsePreferences node = instanceScope.getNode(IOConifgActivator.PLUGIN_ID);
        _date = new GregorianCalendar();
        GridData gridData = (GridData) parent.getLayoutData();
        gridData.minimumWidth = 400;
        getShell().setText("Add new Document");
        Composite dialogComposite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(4, false);
        dialogComposite.setLayout(gridLayout);
        String[] logbooks = node.get(PreferenceConstants.DDB_LOGBOOK, "MKS-2-DOC").split(",");

        final ComboViewer logbooksViewer = new ComboViewer(dialogComposite);
        logbooksViewer.setContentProvider(new ArrayContentProvider());
        logbooksViewer.setInput(logbooks);
        if((_document!=null)&&(_document.getId()!=null)&&!_document.getId().isEmpty()) {
            String element = _document.getId().split(":")[0];
            logbooksViewer.setSelection(new StructuredSelection(element));
            logbooksViewer.getCombo().setEnabled(false);
        }else {
            logbooksViewer.getCombo().select(0);
        }
        final Text eLogbookIdLabel = new Text(dialogComposite, SWT.BORDER);
        eLogbookIdLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        eLogbookIdLabel.setEditable(false);
        String generateId = generateId(logbooksViewer);
        eLogbookIdLabel.setText(generateId);

        // IMAGE
        Label file = new Label(dialogComposite, SWT.NONE);
        file.setText("File: ");
        final Text filePathText = new Text(dialogComposite, SWT.MULTI | SWT.BORDER);
        filePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // MIME_TYPE
        Composite composite = new Composite(dialogComposite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite.setLayout(new GridLayout(2, false));

        final Text mimeTypeValue = new Text(composite, SWT.NONE | SWT.BORDER);
        mimeTypeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        mimeTypeValue.setEditable(false);
        if((_document!=null)&&(_document.getMimeType()!=null)&&!_document.getMimeType().isEmpty()) {
            mimeTypeValue.setText(_document.getMimeType());
        }else {
            mimeTypeValue.setText("");
        }

        Button fileButton = new Button(composite, SWT.PUSH);
        fileButton.setText("File");
        // Bedeutung
        Label label = new Label(dialogComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Bedeutung: ");
        String[] meaning = node.get(PreferenceConstants.DDB_LOGBOOK_MEANING, "DOCU").split(",");
        final Combo meaningCombo = new Combo(dialogComposite, SWT.DROP_DOWN);
        meaningCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 3, 1));
        meaningCombo.setItems(meaning);
        if((_document!=null)&&(_document.getLogseverity()!=null)&&!_document.getLogseverity().isEmpty()) {
            meaningCombo.select(meaningCombo.indexOf(_document.getLogseverity()));
        }else {
            meaningCombo.select(0);
        }
        String item = meaningCombo.getItem(meaningCombo.getSelectionIndex());

        // SUBJECT
        Label shortDesc = new Label(dialogComposite, SWT.NONE);
        shortDesc.setText("Titel: ");
        final Text shortDescText = new Text(dialogComposite, SWT.SINGLE | SWT.BORDER);
        shortDescText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        if((_document!=null)&&(_document.getSubject()!=null)&&!_document.getSubject().isEmpty()) {
            shortDescText.setText(_document.getSubject());
        }else {
            shortDescText.setText("");
        }

        // DESCLONG
        Label longDesc = new Label(dialogComposite, SWT.NONE);
        longDesc.setText("Description: ");
        final Text longDescText = new Text(dialogComposite, SWT.MULTI | SWT.BORDER);
        longDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 4));
        if((_document!=null)&&(_document.getDesclong()!=null)&&!_document.getDesclong().isEmpty()) {
            longDescText.setText(_document.getDesclong());
        }else {
            longDescText.setText("");
        }
        setEmptyLabel(dialogComposite);
        setEmptyLabel(dialogComposite);
        setEmptyLabel(dialogComposite);
        // LOCATION
        Label location = new Label(dialogComposite, SWT.NONE);
        location.setText("Location: ");
        final Text locationText = new Text(dialogComposite, SWT.SINGLE | SWT.BORDER);
        locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        if((_document!=null)&&(_document.getLocation()!=null)&&!_document.getLocation().isEmpty()) {
            locationText.setText(_document.getLocation());
        }else {
            locationText.setText("");
        }
        // KEYWORDS
        Label keywords = new Label(dialogComposite, SWT.NONE);
        keywords.setText("Keywords: ");
        final Text keywordsText = new Text(dialogComposite, SWT.SINGLE | SWT.BORDER);
        keywordsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        if((_document!=null)&&(_document.getKeywords()!=null)&&!_document.getKeywords().isEmpty()) {
            keywordsText.setText(_document.getKeywords());
        }else {
            keywordsText.setText("");
        }
        // ACCOUNTNAME
        Label creater = new Label(dialogComposite, SWT.NONE);
        creater.setText("Author: ");

        Text createrValue = new Text(dialogComposite, SWT.BORDER);
        createrValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createrValue.setEditable(false);
        String userName;
        if((_document!=null)&&(_document.getKeywords()!=null)&&!_document.getKeywords().isEmpty()) {
            userName= _document.getAccountname();
        }else {
            userName = ConfigHelper.getUserName();
        }
        createrValue.setText(userName);
        // ENTRYDATE
        Label createdOn = new Label(dialogComposite, SWT.NONE);
        createdOn.setText("Eintrag: ");
        Text createdOnValue = new Text(dialogComposite, SWT.BORDER);
        createdOnValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createdOnValue.setEditable(false);
        String format = String.format("%1$tF %1$tT", _date);
        createdOnValue.setText(format);

        // LOGSEVERITY
        // ERRORIDENTIFYER
        // CREATED_DATE
        // DELETE_DATE
        // UPDATE_DATE

        if (_document != null) {
            if ( (_document.getId() == null) || _document.getId().isEmpty()) {
                _document.setId(generateId);
                _document.setLogseverity(item);
                _document.setAccountname(userName);
                if (_document.getCreatedDate() == null) {
                    _document.setCreatedDate(_date.getTime());
                }
            }
            _document.setEntrydate(_date.getTime());
        }
        meaningCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                select();
            }

            private void select() {
                String meaningComboItem = meaningCombo.getItem(meaningCombo.getSelectionIndex());
                getDocument().setLogseverity(meaningComboItem);
            }

            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                select();
            }
        });

        logbooksViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                String changedGenerateId = generateId(logbooksViewer);
                getDocument().setId(changedGenerateId);
                eLogbookIdLabel.setText(changedGenerateId);
            }

        });

        fileButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                action();
            }

            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                action();
            }

            private void action() {
                FileDialog fileDialog = new FileDialog(parent.getShell());
                String fileName = fileDialog.open();
                if (fileName != null) {
                    File file2add = new File(fileName);
                    filePathText.setText(fileName);
                    String[] split = fileName.split("\\.");
                    String mimeType = split[split.length - 1];
                    mimeTypeValue.setText(mimeType);
                    getDocument().setMimeType(mimeType);
                    byte[] data = new byte[(int) file2add.length()];
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file2add);
                        fileInputStream.read(data);
                        fileInputStream.close();
                        getDocument().setImage(data);
                    } catch (IOException e) {
                        MessageDialog.openError(getParentShell(),
                                                "File open Error",
                                                "Can't read file!");
                        LOG.error("File open Error. Can't read file!", e);
                    }
                }
            }
        });

        shortDescText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setSubject(shortDescText.getText());
            }
        });

        longDescText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setDesclong(longDescText.getText());
            }
        });

        locationText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setLocation(locationText.getText());
            }
        });

        keywordsText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setKeywords(keywordsText.getText());
            }
        });
        return dialogComposite;
    }

    /**
     * @param dialogComposite
     */
    @Nonnull
    private Label setEmptyLabel(@Nonnull Composite dialogComposite) {
        return new Label(dialogComposite, SWT.NONE);
    }

    @Nonnull
    protected String generateId(@Nonnull final Viewer logbooksViewer) {
        String eLogbookId = (String) ((StructuredSelection) logbooksViewer.getSelection())
                .getFirstElement();
        return String.format("%1$s:%2$ty%2$tm%2$td-%2$tT", eLogbookId, _date);
    }

    @Nonnull
    public DocumentDBO getDocument() {
        return _document;
    }

}
