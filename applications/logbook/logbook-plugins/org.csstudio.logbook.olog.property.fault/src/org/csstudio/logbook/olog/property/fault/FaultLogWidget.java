package org.csstudio.logbook.olog.property.fault;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.ui.LinkTable;
import org.csstudio.logbook.ui.PropertyTree;
import org.csstudio.logbook.ui.util.IFileUtil;
import org.csstudio.ui.util.widgets.ImageStackWidget;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * A stripped down version of the LogEntryWidget - it is simply for viewing and
 * has no edit support.
 *
 * @author Kunal Shroff
 *
 */
public class FaultLogWidget extends Composite {

    // Model
    LogEntry logEntry;

    // GUI control
    private Label lblDate;
    private Label lblDateDisplay;
    private Label lblText;
    private Composite tbtmImgAttachmentsComposite;
    private ImageStackWidget imageStackWidget;
    private Composite tbtmFileAttachmentsComposite;
    private Composite tbtmPropertyTreeComposite;
    private PropertyTree propertyTree;
    private LinkTable linkTable;

    private final String[] supportedImageTypes = new String[] { "*.png", "*.jpg", "*.jpeg", "*.tiff", "*.gif" };

    public FaultLogWidget(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));

        lblDate = new Label(this, SWT.NONE);
        lblDate.setText("Date:");

        lblDateDisplay = new Label(this, SWT.NONE);

        lblText = new Label(this, SWT.NONE);
        lblText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tbtmFileAttachmentsComposite = new Composite(this, SWT.NONE);
        tbtmFileAttachmentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        tbtmFileAttachmentsComposite.setLayout(new FormLayout());
        linkTable = new LinkTable(tbtmFileAttachmentsComposite, SWT.NONE) {

            @Override
            public void linkAction(Attachment attachment) {

                try {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    IFile ifile = IFileUtil.getInstance().createFileResource(attachment.getFileName(),
                            attachment.getInputStream());
                    IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
                            .getDefaultEditor(attachment.getFileName());
                    if (desc != null) {
                        IEditorPart part = page.openEditor(new FileEditorInput(ifile), desc.getId());
                        IFileUtil.getInstance().registerPart(part, ifile);
                    } else {
                        super.linkAction(attachment);
                    }
                } catch (IOException | PartInitException e) {
                    // errorBar.setException(e);
                }
            }
        };
        FormData fd_linkTable = new FormData();
        fd_linkTable.top = new FormAttachment(0, 2);
        fd_linkTable.bottom = new FormAttachment(100, -2);
        fd_linkTable.right = new FormAttachment(100, -2);
        fd_linkTable.left = new FormAttachment(0, 2);
        linkTable.setLayoutData(fd_linkTable);

        tbtmPropertyTreeComposite = new Composite(this, SWT.NONE);
        tbtmPropertyTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        tbtmPropertyTreeComposite.setLayout(new GridLayout());
        updateUI();
    }

    private void updateUI() {
        // TODO Auto-generated method stub
        cleanup();
        if (logEntry != null) {

            lblDateDisplay.setText(logEntry.getCreateDate() != null ? logEntry.getCreateDate().toString() : "");
            lblText.setText(logEntry.getText());
            if (!logEntry.getAttachment().isEmpty()) {
                Map<String, InputStream> imageInputStreamsMap = new HashMap<String, InputStream>();
                tbtmImgAttachmentsComposite = new Composite(this, SWT.NONE);
                tbtmImgAttachmentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
                tbtmImgAttachmentsComposite.setLayout(new FormLayout());
                imageStackWidget = new ImageStackWidget(tbtmImgAttachmentsComposite, SWT.NONE);
                FormData fd_imageStackWidget = new FormData();
                fd_imageStackWidget.bottom = new FormAttachment(100, -2);
                fd_imageStackWidget.right = new FormAttachment(100, -2);
                fd_imageStackWidget.top = new FormAttachment(0, 2);
                fd_imageStackWidget.left = new FormAttachment(0, 2);
                imageStackWidget.setLayoutData(fd_imageStackWidget);

                for (Attachment attachment : logEntry.getAttachment()) {
                    if (Arrays.asList(supportedImageTypes).contains("*" + attachment.getFileName()
                            .substring(attachment.getFileName().lastIndexOf("."), attachment.getFileName().length()))) {
                        try {
                            if (attachment.getInputStream().available() > 0) {
                                imageInputStreamsMap.put(attachment.getFileName(), attachment.getInputStream());
                            }
                        } catch (IOException e) {
                            // setLastException(e);
                        }
                    }
                }
                try {
                    imageStackWidget.setImageInputStreamsMap(imageInputStreamsMap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                }
                linkTable.setFiles(Collections.<Attachment> emptyList());
                linkTable.setFiles(new ArrayList<Attachment>(logEntry.getAttachment()));
            }

            if (!logEntry.getProperties().isEmpty()) {
                propertyTree = new PropertyTree(tbtmPropertyTreeComposite, SWT.NONE);
                propertyTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                propertyTree.setVisible(true);
                propertyTree.setProperties(new ArrayList<Property>(logEntry.getProperties()));
            }
            this.getParent().layout();
        }
    }

    private void cleanup() {
        if (tbtmImgAttachmentsComposite != null)
            tbtmImgAttachmentsComposite.dispose();
        if (linkTable != null)
            linkTable.setFiles(Collections.emptyList());
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
        updateUI();
    }

}
