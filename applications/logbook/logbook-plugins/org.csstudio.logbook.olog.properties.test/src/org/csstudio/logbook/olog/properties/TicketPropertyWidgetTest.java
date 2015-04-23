/**
 * 
 */
package org.csstudio.logbook.olog.properties;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.PropertyBuilder;
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
public class TicketPropertyWidgetTest extends ApplicationWindow {

    private Button btnEditable;

    public TicketPropertyWidgetTest() {
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

	final TicketPropertyWidget tracPropertyWidget = new TicketPropertyWidget(
		container, SWT.NONE, logEntrychangeset, true);
	tracPropertyWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true, 1, 1));

	Button btnNewButton = new Button(container, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		logEntrychangeset.setLogEntryBuilder(LogEntryBuilder
			.withText("test")
			.owner("test")
			.addProperty(
				PropertyBuilder
					.property("Ticket")
					.attribute("TicketId", "12345")
					.attribute("TicketURL",
						"http://localhost.com")));
	    }
	});
	btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		false, 1, 1));
	btnNewButton.setText("Add Test Trac Property");

	btnEditable = new Button(container, SWT.CHECK);
	btnEditable.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		tracPropertyWidget.setEditable(btnEditable.getSelection());
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
	    TicketPropertyWidgetTest window = new TicketPropertyWidgetTest();
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
