package org.csstudio.logbook.olog.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.ui.util.IFileUtil;
import org.csstudio.logbook.util.LogEntryUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

class ContextPropertyWidget extends AbstractPropertyWidget {
    private Button btnRestore;
    private Text textFileDescription;
    private Text textFileName;
    private Label lblNewLabel_1;
    private Label lblNewLabel_2;
    private Button btnCurrentContext;

    private File file;
    private final static Property property = PropertyBuilder
	    .property("Context").attribute("FileName")
	    .attribute("FileDescription").build();

    public ContextPropertyWidget(Composite parent, int style,
	    LogEntryChangeset logEntryChangeset, boolean editable) {
	super(parent, style, logEntryChangeset, editable);
	setLayout(new FormLayout());

	Label lblNewLabel = new Label(this, SWT.NONE);
	FormData fd_lblNewLabel = new FormData();
	fd_lblNewLabel.right = new FormAttachment(100, -5);
	fd_lblNewLabel.top = new FormAttachment(0, 5);
	fd_lblNewLabel.left = new FormAttachment(0, 5);
	lblNewLabel.setLayoutData(fd_lblNewLabel);
	lblNewLabel.setText("Context Files:");

	btnRestore = new Button(this, SWT.NONE);
	btnRestore.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (isEditable()) {
		    // Attach Action
		    if (file != null) {
			try {
			    LogEntryBuilder logEntryBuilder = LogEntryBuilder
				    .logEntry(getLogEntryChangeset()
					    .getLogEntry());
			    logEntryBuilder.addProperty(PropertyBuilder
				    .property(property)
				    .attribute("FileName", file.getName())
				    .attribute("FileDescription",
					    textFileDescription.getText()));
			    FileInputStream fileInputStream = new FileInputStream(
				    file);
			    logEntryBuilder.attach(AttachmentBuilder
				    .attachment(file.getName()).inputStream(
					    fileInputStream));
			    getLogEntryChangeset().setLogEntryBuilder(
				    logEntryBuilder);
			} catch (FileNotFoundException e1) {
			    // TODO Auto-generated catch block
			} catch (IOException e1) {
			    // TODO Auto-generated catch block
			}
		    }
		} else {
		    // Restore Action
		    try {
			LogEntry logEntry = getLogEntryChangeset()
				.getLogEntry();
			for (Attachment attachment : logEntry.getAttachment()) {
			    if (attachment.getFileName().equals(
				    textFileName.getText())) {

				IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage();
				IFile ifile = IFileUtil.getInstance()
					.createFileResource(
						attachment.getFileName(),
						attachment.getInputStream());
				IEditorDescriptor desc = PlatformUI
					.getWorkbench()
					.getEditorRegistry()
					.getDefaultEditor(
						attachment.getFileName());
				IEditorPart part = page.openEditor(
					new FileEditorInput(ifile),
					desc.getId());
				IFileUtil.getInstance().registerPart(part,
					ifile);
			    }
			}
		    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    } catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }

		}
	    }
	});
	FormData fd_btnRestore = new FormData();
	fd_btnRestore.bottom = new FormAttachment(100, -5);
	fd_btnRestore.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
	btnRestore.setLayoutData(fd_btnRestore);
	btnRestore.setText("Restore");

	btnCurrentContext = new Button(this, SWT.NONE);
	btnCurrentContext.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
			IEditorInput input = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput();
			IFile editorFile = (IFile) input.getAdapter(IFile.class);
			if (editorFile != null) {
				file = new File(editorFile.getLocationURI());
				if (file != null) {
					textFileName.setText(file.getName());
				}
			}
	    }
	});
	FormData fd_btnCurrentContext = new FormData();
	fd_btnCurrentContext.top = new FormAttachment(btnRestore, 0, SWT.TOP);
	fd_btnCurrentContext.left = new FormAttachment(0, 5);
	btnCurrentContext.setLayoutData(fd_btnCurrentContext);
	btnCurrentContext.setText("Add Current Context");

	lblNewLabel_1 = new Label(this, SWT.NONE);
	FormData fd_lblNewLabel_1 = new FormData();
	fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 6);
	fd_lblNewLabel_1.left = new FormAttachment(0, 5);
	lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
	lblNewLabel_1.setText("File Description:");

	textFileName = new Text(this, SWT.BORDER);
	FormData fd_textFileName = new FormData();
	fd_textFileName.bottom = new FormAttachment(btnRestore, -6);
	fd_textFileName.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
	fd_textFileName.left = new FormAttachment(0, 5);
	textFileName.setLayoutData(fd_textFileName);

	lblNewLabel_2 = new Label(this, SWT.NONE);
	FormData fd_lblNewLabel_2 = new FormData();
	fd_lblNewLabel_2.bottom = new FormAttachment(textFileName, -6);
	fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
	lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
	lblNewLabel_2.setText("FileName:");

	textFileDescription = new Text(this, SWT.BORDER | SWT.WRAP
			| SWT.V_SCROLL);
	FormData fd_textFileDescription = new FormData();
	fd_textFileDescription.bottom = new FormAttachment(lblNewLabel_2, -6);
	fd_textFileDescription.left = new FormAttachment(0, 5);
	fd_textFileDescription.top = new FormAttachment(lblNewLabel_1, 6);
	fd_textFileDescription.right = new FormAttachment(100, -5);
	textFileDescription.setLayoutData(fd_textFileDescription);
    }

    @Override
    public void updateUI() {
	textFileName.setEditable(isEditable());
	textFileDescription.setEditable(isEditable());
	btnCurrentContext.setVisible(isEditable());
	if (isEditable()) {
	    btnRestore.setText("Attach");
	} else {
	    btnRestore.setText("Restore");
	}
	Property property = null;
	try {
	    property = LogEntryUtil.getProperty(getLogEntryChangeset()
	    	.getLogEntry(), ContextPropertyWidget.property.getName());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (property != null) {
	    String fileName = property.getAttributeValue("FileName");
	    this.textFileName.setText(fileName);
	    this.textFileDescription.setText(property
		    .getAttributeValue("FileDescription") == null ? ""
		    : property.getAttributeValue("FileDescription"));
	}
    }
}