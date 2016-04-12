/**
 *
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
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
public class PropertyTreeTest extends ApplicationWindow {

    private PropertyTree propertyTree;

    private Property processProperty = PropertyBuilder.property("process")
        .attribute("Name", "").attribute("Description", "")
        .attribute("Type").attribute("Id").attribute("Attachments").build();

    private Property ticketProperty = PropertyBuilder.property("Ticket")
        .attribute("Id", "3645").attribute("URL", "www.bnl.gov").build();

    public PropertyTreeTest() {
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
    propertyTree = new PropertyTree(container, SWT.NONE);
    GridData gd_propertyTree = new GridData(SWT.FILL, SWT.FILL, true, true,
        1, 1);
    propertyTree.setLayoutData(gd_propertyTree);

    Button btnNewButton = new Button(container, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        List<Property> properties = new ArrayList<Property>();
        properties.add(processProperty);
        propertyTree.setProperties(properties);
        }
    });
    propertyTree.addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        StringBuffer sb = new StringBuffer();

        System.out.println("Selected Files: " + sb.toString());
        }
    });
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton.setText("Add Multiple Properties");

    Button btnNewButton_2 = new Button(container, SWT.NONE);
    btnNewButton_2.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
        List<Property> properties = new ArrayList<Property>();
        properties.add(processProperty);
        properties.add(ticketProperty);
        propertyTree.setProperties(properties);
        }
    });
    btnNewButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton_2.setText("Add Test Images");

    return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
    try {
        PropertyTreeTest window = new PropertyTreeTest();
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
