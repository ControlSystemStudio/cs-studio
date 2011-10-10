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
import org.csstudio.config.ioconfig.model.IOConfigActivator;
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

    /**
     * @author hrickens
     * @since 07.10.2011
     */
    private final class AddFileSelectionListener implements SelectionListener {
        private final Text _mimeTypeValue;
        private final Text _filePathText;
        private final Composite _parent;

        /**
         * Constructor.
         */
        protected AddFileSelectionListener(@Nonnull final Text mimeTypeValue,
                                         @Nonnull final Text filePathText,
                                         @Nonnull final Composite parent) {
            _mimeTypeValue = mimeTypeValue;
            _filePathText = filePathText;
            _parent = parent;
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            action();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            action();
        }

        private void action() {
            final FileDialog fileDialog = new FileDialog(_parent.getShell());
            final String fileName = fileDialog.open();
            if (fileName != null) {
                final File file2add = new File(fileName);
                _filePathText.setText(fileName);
                final String[] split = fileName.split("\\.");
                final String mimeType = split[split.length - 1];
                _mimeTypeValue.setText(mimeType);
                getDocument().setMimeType(mimeType);
                final byte[] data = new byte[(int) file2add.length()];
                try {
                    final FileInputStream fileInputStream = new FileInputStream(file2add);
                    fileInputStream.read(data);
                    fileInputStream.close();
                    getDocument().setImage(data);
                } catch (final IOException e) {
                    MessageDialog.openError(getParentShell(),
                                            "File open Error",
                    "Can't read file!");
                    LOG.error("File open Error. Can't read file!", e);
                }
            }
        }
    }

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

    @Nonnull
    public DocumentDBO getDocument() {
        return _document;
    }

    /**
     * @param dialogComposite
     */
    @Nonnull
    private Label addEmptyLabel(@Nonnull final Composite dialogComposite) {
        return new Label(dialogComposite, SWT.NONE);
    }

    @Override @Nonnull
    protected Control createDialogArea(@Nonnull final Composite parent) {
        final InstanceScope instanceScope = new InstanceScope();
        final IEclipsePreferences node = instanceScope.getNode(IOConfigActivator.PLUGIN_ID);
        _date = new GregorianCalendar();
        final GridData gridData = (GridData) parent.getLayoutData();
        gridData.minimumWidth = 400;
        getShell().setText("Add new Document");
        final Composite dialogComposite = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout(4, false);
        dialogComposite.setLayout(gridLayout);
        final String generateId = buildLogbooksRow(node, dialogComposite);

        buildFileRow(parent, dialogComposite);

        final String item = buildMeaningRow(node, dialogComposite);

        buildSubjectRow(dialogComposite);
        buildDescRow(dialogComposite);
        buildLocationRow(dialogComposite);
        buildKeywordsRow(dialogComposite);
        final String userName = buildAuthorAndDateRow(dialogComposite);

        if (_document != null) {
            final String id = _document.getId();
            if ( id == null || id.isEmpty()) {
                _document.setId(generateId);
                _document.setLogseverity(item);
                _document.setAccountname(userName);
                if (_document.getCreatedDate() == null) {
                    _document.setCreatedDate(_date.getTime());
                }
            }
            _document.setEntrydate(_date.getTime());
        }
        return dialogComposite;
    }

    @Nonnull
    private String buildLogbooksRow(@Nonnull final IEclipsePreferences node, @Nonnull final Composite dialogComposite) {
        final String[] logbooks = node.get(PreferenceConstants.DDB_LOGBOOK, "MKS-2-DOC").split(",");

        final ComboViewer logbooksViewer = new ComboViewer(dialogComposite);
        logbooksViewer.setContentProvider(new ArrayContentProvider());
        logbooksViewer.setInput(logbooks);
        Object seletionId = logbooksViewer.getCombo().getItem(0);
        if (_document != null) {
            final String id = _document.getId();
            if (id != null && !id.isEmpty()) {
                seletionId = id.split(":")[0];
            }
            logbooksViewer.getCombo().setEnabled(false);
        }
        logbooksViewer.setSelection(new StructuredSelection(seletionId));

        final Text eLogbookIdLabel = new Text(dialogComposite, SWT.BORDER);
        eLogbookIdLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        eLogbookIdLabel.setEditable(false);
        final String generateId = generateId(logbooksViewer);
        eLogbookIdLabel.setText(generateId);
        logbooksViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                final String changedGenerateId = generateId(logbooksViewer);
                getDocument().setId(changedGenerateId);
                eLogbookIdLabel.setText(changedGenerateId);
            }

        });
        return generateId;
    }

    @Nonnull
    private String buildMeaningRow(@Nonnull final IEclipsePreferences node, @Nonnull final Composite dialogComposite) {
        final Label label = new Label(dialogComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Bedeutung: ");
        final String[] meaning = node.get(PreferenceConstants.DDB_LOGBOOK_MEANING, "DOCU").split(",");
        final Combo meaningCombo = new Combo(dialogComposite, SWT.DROP_DOWN);
        meaningCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 3, 1));
        meaningCombo.setItems(meaning);
        final int selection = haveDocumentWithLogseverity()?meaningCombo.indexOf(_document.getLogseverity()):0;
        meaningCombo.select(selection);
        final String item = meaningCombo.getItem(meaningCombo.getSelectionIndex());
        meaningCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                select();
            }

            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                select();
            }

            private void select() {
                final String meaningComboItem = meaningCombo.getItem(meaningCombo.getSelectionIndex());
                getDocument().setLogseverity(meaningComboItem);
            }
        });
        return item;
    }

    private void buildFileRow(@Nonnull final Composite parent, @Nonnull final Composite dialogComposite) {
        final Label file = new Label(dialogComposite, SWT.NONE);
        file.setText("File: ");
        final Text filePathText = new Text(dialogComposite, SWT.MULTI | SWT.BORDER);
        filePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // MIME_TYPE
        final Composite composite = new Composite(dialogComposite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite.setLayout(new GridLayout(2, false));

        final Text mimeTypeValue = new Text(composite, SWT.NONE | SWT.BORDER);
        mimeTypeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        mimeTypeValue.setEditable(false);
        if(haveDocumentWithMimeType()) {
            mimeTypeValue.setText(_document.getMimeType());
        }else {
            mimeTypeValue.setText("");
        }
        final Button fileButton = new Button(composite, SWT.PUSH);
        fileButton.setText("File");
        fileButton.addSelectionListener(new AddFileSelectionListener(mimeTypeValue, filePathText, parent));
    }

    @Nonnull
    private void buildSubjectRow(@Nonnull final Composite dialogComposite) {
        // SUBJECT
        final Label shortDesc = new Label(dialogComposite, SWT.NONE);
        shortDesc.setText("Titel: ");
        final Text shortDescText = new Text(dialogComposite, SWT.SINGLE | SWT.BORDER);
        shortDescText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        if(haveDocumentWithSubject()) {
            shortDescText.setText(_document.getSubject());
        }else {
            shortDescText.setText("");
        }
        shortDescText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setSubject(shortDescText.getText());
            }
        });
    }

    private boolean haveDocumentWithDocumentLocation() {
        if(_document==null) {
            return false;
        }
        final String location = _document.getLocation();
        return location!=null&&!location.isEmpty();
    }

    private boolean haveDocumentWithLogseverity() {
        if(_document==null) {
            return false;
        }
        final String logseverity = _document.getLogseverity();
        return logseverity!=null&&!logseverity.isEmpty();
    }

    private boolean haveDocumentWithMimeType() {
        if(_document==null) {
            return false;
        }
        final String mimeType = _document.getMimeType();
        return mimeType!=null&&!mimeType.isEmpty();
    }

    private boolean haveDocumentWithSubject() {
        if(_document==null) {
            return false;
        }
        final String subject = _document.getSubject();
        return subject!=null&&!subject.isEmpty();
    }

    @Nonnull
    private void buildDescRow(@Nonnull final Composite dialogComposite) {
        final Label longDesc = new Label(dialogComposite, SWT.NONE);
        longDesc.setText("Description: ");
        final Text longDescText = new Text(dialogComposite, SWT.MULTI | SWT.BORDER);
        longDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 4));
        if(_document!=null&&_document.getDesclong()!=null&&!_document.getDesclong().isEmpty()) {
            longDescText.setText(_document.getDesclong());
        }else {
            longDescText.setText("");
        }
        addEmptyLabel(dialogComposite);
        addEmptyLabel(dialogComposite);
        addEmptyLabel(dialogComposite);
        longDescText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setDesclong(longDescText.getText());
            }
        });
    }

    @Nonnull
    private void buildLocationRow(@Nonnull final Composite dialogComposite) {
        final Label locationLabel = new Label(dialogComposite, SWT.NONE);
        locationLabel.setText("Location: ");
        final Text locationText = new Text(dialogComposite, SWT.SINGLE | SWT.BORDER);
        locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        final String location = haveDocumentWithDocumentLocation()?_document.getLocation():"";
        locationText.setText(location);
        locationText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setLocation(locationText.getText());
            }
        });
    }

    @Nonnull
    private void buildKeywordsRow(@Nonnull final Composite dialogComposite) {
        final Label keywords = new Label(dialogComposite, SWT.NONE);
        keywords.setText("Keywords: ");
        final Text keywordsText = new Text(dialogComposite, SWT.SINGLE | SWT.BORDER);
        keywordsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        if(haveDocumentWithKeywords()) {
            keywordsText.setText(_document.getKeywords());
        }else {
            keywordsText.setText("");
        }
        keywordsText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nullable final ModifyEvent e) {
                getDocument().setKeywords(keywordsText.getText());
            }
        });
    }

    @Nonnull
    private String buildAuthorAndDateRow(@Nonnull final Composite dialogComposite) {
        // ACCOUNTNAME
        final Label creater = new Label(dialogComposite, SWT.NONE);
        creater.setText("Author: ");

        final Text createrValue = new Text(dialogComposite, SWT.BORDER);
        createrValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createrValue.setEditable(false);
        String userName = null;
        if(haveDocumentWithKeywords()) {
            userName= _document.getAccountname();
        }
        if(userName== null) {
            userName = ConfigHelper.getUserName();
        }
        createrValue.setText(userName);
        // ENTRYDATE
        final Label createdOn = new Label(dialogComposite, SWT.NONE);
        createdOn.setText("Eintrag: ");
        final Text createdOnValue = new Text(dialogComposite, SWT.BORDER);
        createdOnValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createdOnValue.setEditable(false);
        final String format = String.format("%1$tF %1$tT", _date);
        createdOnValue.setText(format);
        return userName;
    }

    /**
     * @return
     */
    private boolean haveDocumentWithKeywords() {
        return _document!=null&&_document.getKeywords()!=null&&!_document.getKeywords().isEmpty();
    }

    @Nonnull
    protected String generateId(@Nonnull final Viewer logbooksViewer) {
        final String eLogbookId = (String) ((StructuredSelection) logbooksViewer.getSelection())
        .getFirstElement();
        return String.format("%1$s:%2$ty%2$tm%2$td-%2$tT", eLogbookId, _date);
    }

}
