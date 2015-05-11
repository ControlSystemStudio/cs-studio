/**
 * 
 */
package org.csstudio.shift.ui;

import gov.bnl.shiftClient.Shift;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.csstudio.shift.ShiftBuilder;
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


public class ShiftWidgetTest extends ApplicationWindow {

    public ShiftWidgetTest() {
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
		container.setLayout(new GridLayout(5, false));
		final ShiftWidget shiftWidget = new ShiftWidget(container, SWT.WRAP, false, false);
		shiftWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
	
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
			Shift shift;
			try {
			    shift = ShiftBuilder.withType("SomeText\nsome more text")
				    .setOwner("eschuhmacher").build();
			    shiftWidget.setShift(shift);
			} catch (IOException e1) {
			    e1.printStackTrace();
			}
		    }
		});
		btnNewButton.setText("test shift");
	
		Button btnNewButton_1 = new Button(container, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
			Shift shift;
			try {
			    shift = ShiftBuilder.withType("SomeText").setOwner("eschuhmacher").build();
			    shiftWidget.setShift(shift);
			} catch (IOException e1) {
			    // TODO Auto-generated catch block
			    e1.printStackTrace();
			}
		    }
		});
		btnNewButton_1.setText("simple Entry");
		return container;
    }

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
	try {
	    ShiftWidgetTest window = new ShiftWidgetTest();
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
	newShell.setText("New Application");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
	return new Point(473, 541);
    }

}
