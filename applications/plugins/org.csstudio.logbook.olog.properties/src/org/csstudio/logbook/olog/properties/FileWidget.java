/**
 * 
 */
package org.csstudio.logbook.olog.properties;

import java.io.IOException;
import java.util.ArrayList;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.ui.extra.LinkTable;
import org.csstudio.logbook.ui.util.IFileUtil;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author shroffk
 * 
 */
public class FileWidget extends AbstractPropertyWidget {

    private ErrorBar errorBar;
    private Button btnNewButton;
    private LinkTable linkTable;

    public FileWidget(Composite parent, int style,
	    LogEntryChangeset logEntryChangeset) {
	super(parent, style, logEntryChangeset);
	setLayout(new FormLayout());

	errorBar = new ErrorBar(this, SWT.NONE);
	FormData fd_errorBar = new FormData();
	fd_errorBar.right = new FormAttachment(100, -2);
	fd_errorBar.top = new FormAttachment(0, 2);
	fd_errorBar.left = new FormAttachment(0, 2);
	errorBar.setLayoutData(fd_errorBar);

	btnNewButton = new Button(this, SWT.NONE);
	FormData fd_btnNewButton = new FormData();
	fd_btnNewButton.bottom = new FormAttachment(100, -5);
	fd_btnNewButton.right = new FormAttachment(100, -5);
	btnNewButton.setLayoutData(fd_btnNewButton);
	btnNewButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// Button behaviour various based on if the logEntry is being
		// displayed in view mode or edit mode
		try {
		    LogEntryBuilder logEntryBuilder = LogEntryBuilder
			    .logEntry(getLogEntryChangeset().getLogEntry());

		    if (isEditable()) {
			// remove the selected attachments
			for (Attachment attachment : linkTable.getSelection()) {
			    logEntryBuilder.removeAttachment(attachment
				    .getFileName());
			}
		    } else {
			// Open the selected files

		    }
		} catch (IOException e1) {
		    errorBar.setException(e1);
		}

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	linkTable = new LinkTable(this, SWT.NONE) {

	    @Override
	    public void linkAction(Attachment attachment) {

		try {
		    IWorkbenchPage page = PlatformUI.getWorkbench()
			    .getActiveWorkbenchWindow().getActivePage();
		    IFile ifile = IFileUtil.getInstance().createFileResource(
			    attachment.getFileName(),
			    attachment.getInputStream());
		    IEditorDescriptor desc = PlatformUI.getWorkbench()
			    .getEditorRegistry()
			    .getDefaultEditor(attachment.getFileName());
		    IEditorPart part = page.openEditor(new FileEditorInput(
			    ifile), desc.getId());
		    IFileUtil.getInstance().registerPart(part, ifile);
		} catch (IOException | PartInitException e) {
		    errorBar.setException(e);
		}
	    }
	};
	FormData fd_linkTable = new FormData();
	fd_linkTable.top = new FormAttachment(errorBar, 2);
	fd_linkTable.bottom = new FormAttachment(btnNewButton);
	fd_linkTable.right = new FormAttachment(100, -2);
	fd_linkTable.left = new FormAttachment(0, 2);
	linkTable.setLayoutData(fd_linkTable);

	updateUI();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.logbook.ui.AbstractPropertyWidget#updateUI()
     */
    @Override
    public void updateUI() {
	try {
	    linkTable.setFiles(new ArrayList<Attachment>(getLogEntryChangeset()
		    .getLogEntry().getAttachment()));
	    if (isEditable()) {
		btnNewButton.setText("Open Selected files");
	    } else {
		btnNewButton.setText("Remove Selected files");
	    }
	} catch (IOException e) {
	    errorBar.setException(e);
	}

    }
}
