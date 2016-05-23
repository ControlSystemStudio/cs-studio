/**
 *
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 *
 */
public class LinkTableDemo extends ApplicationWindow {

    private LinkTable linkTable;

    public LinkTableDemo() {
    super(null);
    addToolBar(SWT.FLAT | SWT.WRAP);
    addMenuBar();
    addStatusLine();
    }

    /**
     * Create contents of the application window.
     *
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new GridLayout(1, false));
    linkTable = new LinkTable(container, SWT.NONE);
    linkTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
        1));

    Button btnNewButton = new Button(container, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        try {
            List<Attachment> files = new ArrayList<Attachment>();
            files.add(AttachmentBuilder.attachment("first.txt").build());
            files.add(AttachmentBuilder.attachment("build.properties")
                .build());
            files.add(AttachmentBuilder.attachment("plugin.xml")
                .build());
            linkTable.setFiles(files);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        }
    });
    linkTable.addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        StringBuffer sb = new StringBuffer();
        for (Attachment selectedAttachment : linkTable.getSelection()) {
            sb.append(selectedAttachment.getFileName());
        }
        System.out.println("Selected Files: " + sb.toString());
        }
    });
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton.setText("Add Test Files");

    Button btnNewButton_2 = new Button(container, SWT.NONE);
    btnNewButton_2.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        try {
            List<Attachment> files = new ArrayList<Attachment>();
            final File folder = new File("icons");
            for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(AttachmentBuilder.attachment(
                    fileEntry.getName()).build());
            }
            }
            linkTable.setFiles(files);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        }
    });
    btnNewButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton_2.setText("Add Test Images");

    Button btnNewButton_1 = new Button(container, SWT.NONE);
    btnNewButton_1.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        final FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);

        final String filename = dlg.open();
        if (filename != null) {
            List<Attachment> files = new ArrayList<Attachment>(
                linkTable.getFiles());
            try {
            files.add(AttachmentBuilder.attachment(filename)
                .build());
            } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            }
            linkTable.setFiles(files);
        }
        }
    });
    btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton_1.setText("Add File");
    return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
    try {
        LinkTableDemo window = new LinkTableDemo();
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    /**
     * Configure the shell.
     *
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("LinkTableTest");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
    return new Point(473, 541);
    }
}
