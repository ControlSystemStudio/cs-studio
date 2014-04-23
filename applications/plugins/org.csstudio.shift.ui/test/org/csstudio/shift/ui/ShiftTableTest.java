/**
 * 
 */
package org.csstudio.shift.ui;

import gov.bnl.shiftClient.Shift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.swt.widgets.Label;


public class ShiftTableTest extends ApplicationWindow {

    public ShiftTableTest() {
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
	final ShiftTable shiftTable = new ShiftTable(container, SWT.NONE);
	shiftTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	Button btnNewButton = new Button(container, SWT.NONE);
	btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	btnNewButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		List<Shift> shits = new ArrayList<Shift>();
		String eol = System.getProperty("line.separator");
		try {
		    for (int i = 0; i < 10; i++) {
				StringBuffer sb = new StringBuffer("line" + i);
				for (int j = 0; j < i; j++) {
				    sb.append(eol);
				    sb.append("line" + j);
				}
				shits.add(ShiftBuilder.withType(sb.toString()).setOwner("eschuhmacher").build());
				shits.add(ShiftBuilder.withType("test").setOwner("eschuhmacher").build());
				shits.add(ShiftBuilder.withType("test2").setOwner("eschuhmacher").build());
		    }
		    shiftTable.setShifts(shits);
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});
	btnNewButton.setText("test shift set1");

	Button btnNewButton_1 = new Button(container, SWT.NONE);
	btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		false, 1, 1));
	btnNewButton_1.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		Shift shift;
		try {
		    shift = ShiftBuilder.withType("SomeText").setOwner("eschuhmacher").build();
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});
	btnNewButton_1.setText("testlogEntry set2");
	return container;
    }

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
	try {
	    ShiftTableTest window = new ShiftTableTest();
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
