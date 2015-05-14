/**
 *
 */
package org.csstudio.ui.util.widgets;

import java.util.ArrayList;

import org.csstudio.ui.util.dialogs.StringListSelectionDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
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
public class StringListSelectionWidgetTest extends ApplicationWindow {

    private ArrayList<String> possibleValues;
    private ArrayList<String> selectedValues;

    public StringListSelectionWidgetTest() {
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

        possibleValues = new ArrayList<String>();
        selectedValues = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            possibleValues.add("possible" + i);
            possibleValues.add("selected" + i);
            selectedValues.add("selected" + i);
        }

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(6, false));
        final StringListSelectionWidget stringListSelectionWidget = new StringListSelectionWidget(
                container, SWT.WRAP);
        stringListSelectionWidget.setLayoutData(new GridData(SWT.FILL,
                SWT.FILL, true, true, 6, 1));

        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stringListSelectionWidget.setPossibleValues(possibleValues);
                stringListSelectionWidget.setSelectedValues(selectedValues);
            }
        });
        btnNewButton.setText("add test data");

        Button btnOpenDialog = new Button(container, SWT.NONE);
        btnOpenDialog.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StringListSelectionDialog dialog = new StringListSelectionDialog(
                        getShell(), possibleValues, selectedValues,
                        "Add Test Data");
                if (dialog.open() == Window.OK) {
                    System.out.println(dialog.getSelectedValues());
                }
            }
        });
        btnOpenDialog.setText("open Dialog");
        return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            StringListSelectionWidgetTest window = new StringListSelectionWidgetTest();
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
        newShell.setText("Test StringListSelectionWidget");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(473, 541);
    }

}
