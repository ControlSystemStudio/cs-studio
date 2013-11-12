/**
 * 
 */
package org.csstudio.shift.ui.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.shift.Shift;
import org.csstudio.shift.ShiftBuilder;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Text;

/**
 * @author shroffk
 * 
 */
public class ShiftTableTest extends ApplicationWindow {
    private static String MEDIUM_TEXT = "this a a text that is a bit longer, but not too long. This row should have a smaller height than row #1";
    private ShiftTable shiftTable;
    private Text text;

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
	
		shiftTable = new ShiftTable(container, SWT.NONE);
	
		shiftTable.addSelectionChangedListener(new ISelectionChangedListener() {
	
			    @Override
			    public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
				    for (Object selection : ((IStructuredSelection) event.getSelection()).toList()) {
					Shift shift = (Shift) selection;
					String selectedLogEntry = shift.getId()
						+ " : " + shift.getType();
					text.setText(selectedLogEntry);
				    }
				}
			    }
			});
		shiftTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
			List<Shift> shifts = new ArrayList<Shift>();
			String eol = System.getProperty("line.separator");
			try {
			    for (int i = 0; i < 10; i++) {
				StringBuffer sb = new StringBuffer("line" + i);
				for (int j = 0; j < i; j++) {
				    sb.append(eol);
				    sb.append("line" + j);
				}
				shifts.add(ShiftBuilder.withType(sb.toString()).setOwner("eschuhmacher").build());
			    }
			    shiftTable.setShifts(shifts);
			} catch (IOException e1) {
			    // TODO Auto-generated catch block
			    e1.printStackTrace();
			}
		    }
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
			false, 1, 1));
		btnNewButton.setText("Add Test Shift");
	
		Button btnAddTestShift = new Button(container, SWT.NONE);
		btnAddTestShift.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
				List<Shift> shifts = new ArrayList<Shift>();
				String eol = System.getProperty("line.separator");
				try {
				    for (int i = 0; i < 10; i++) {
				    	ShiftBuilder shiftBuilder = ShiftBuilder.withType("").setOwner(String.valueOf(i));
				    	StringBuffer sb = new StringBuffer("line" + i);
						if (i % 2 == 0) {
						    sb.append(MEDIUM_TEXT);
						}
						
						shiftBuilder.addDescription(sb.toString());
						shifts.add(shiftBuilder.build());
				    }
				    shiftTable.setShifts(shifts);
				} catch (IOException e1) {
				    // TODO Auto-generated catch block
				    e1.printStackTrace();
				}
		    }
		});
		btnAddTestShift.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnAddTestShift.setText("Add Test Shift 2");
	
		Label lblSelectedEntry = new Label(container, SWT.NONE);
		lblSelectedEntry.setText("Selected Entry:");
	
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
