/**
 * 
 */
package org.csstudio.logbook.olog.properties;

import java.io.IOException;

import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.ui.LogEntryChangeset;
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
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 * 
 */
public class FileWidgetTest extends ApplicationWindow {

    private Button btnEditable;

    public FileWidgetTest() {
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
	final LogEntryChangeset logEntrychangeset = new LogEntryChangeset();

	final FileWidget fileWidget = new FileWidget(container, SWT.NONE,
		logEntrychangeset);
	fileWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
		1, 1));

	Button btnNewButton = new Button(container, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		try {
		    logEntrychangeset.setLogEntryBuilder(LogEntryBuilder
			    .withText("test")
			    .owner("test")
			    .attach(AttachmentBuilder
				    .attachment("build.properties"))
			    .attach(AttachmentBuilder.attachment("plugin.xml"))
			    .attach(AttachmentBuilder
				    .attachment("META-INF\\MANIFEST.MF")));
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});
	btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	btnNewButton.setText("Add Test LogEntry with attachments");

	btnEditable = new Button(container, SWT.CHECK);
	btnEditable.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		fileWidget.setEditable(btnEditable.getSelection());
	    }
	});
	btnEditable.setText("Editable");

	return container;
    }

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
	try {
	    FileWidgetTest window = new FileWidgetTest();
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
	newShell.setText("Test ImageStackWidgetTest");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
	return new Point(473, 541);
    }
}
