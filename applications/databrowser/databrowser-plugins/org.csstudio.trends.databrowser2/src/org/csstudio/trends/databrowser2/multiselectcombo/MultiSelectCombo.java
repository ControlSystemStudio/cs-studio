package org.csstudio.trends.databrowser2.multiselectcombo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/** An implementation of a Combo box for the selection of multiple items
 *
 */

public class MultiSelectCombo extends Composite {

    Shell selectionShell;
    Text selectionSummary;
    String[] allItems;
    List<Integer> selectedIndices;

    private Button[] itemButtons;
    private SelectionAdapter selectionAdapter;

    public MultiSelectCombo(Composite parent, String[] items, int style) {
        super(parent, style);

        allItems = items;
        selectedIndices = new ArrayList<Integer>();
        setLayout(new GridLayout());
        selectionSummary = new Text(this, SWT.READ_ONLY);
        selectionSummary.setText("Select Item(s) for Waveform inspection ...");
        selectionSummary.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event) {
                super.mouseDown(event);
                showSelectionShell();
            }

        });

    }

    public void setItems(String[] names) {
        allItems = names;
    }

    private void showSelectionShell() {

        Point p = selectionSummary.getParent().toDisplay(selectionSummary.getLocation());
        Point size = selectionSummary.getSize();
        Rectangle shellRect = new Rectangle(p.x, p.y + size.y, size.x, 0);

        final int numItems = allItems.length;
        selectionShell = new Shell(Display.getDefault(), SWT.NO_TRIM);
        selectionShell.setLayout(new GridLayout());
        this.setLayoutData(new GridData());
        selectionShell.setSize(shellRect.width, 30*numItems);
        selectionShell.setLocation(shellRect.x, shellRect.y);
        itemButtons = new Button[numItems];
        for (int i = 0; i < numItems; i++) {
            Button button = new Button(selectionShell, SWT.CHECK);
            button.setText(allItems[i]);
            if (selectedIndices.contains(i))
                button.setSelection(true);
            button.pack();
            itemButtons[i] = button;
            itemButtons[i].addSelectionListener(selectionAdapter);
        }

        selectionShell.addShellListener(new ShellAdapter() {
            @Override
            public void shellDeactivated(ShellEvent event) {
                if (selectionShell != null && !selectionShell.isDisposed())
                    selectedIndices.clear();
                    for (int i = 0; i < itemButtons.length; i++) {
                        if (itemButtons[i].getSelection())
                            selectedIndices.add(i);
                    }
                   selectionShell.dispose();
                }

        });

        selectionShell.open();

    }

    public List<Integer> getSelectedIndices() {
        if (selectionShell == null || selectionShell.isDisposed())
            return selectedIndices;
        selectedIndices.clear();
        for (int i = 0; i < itemButtons.length; i++) {
            if (itemButtons[i].getSelection())
                selectedIndices.add(i);
        }
        return selectedIndices;
    }

    public void addSelectionListener(SelectionAdapter selAdapter) {
        selectionAdapter = selAdapter;
    }

}
