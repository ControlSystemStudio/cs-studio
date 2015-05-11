/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * SWT Demo of the {@link MultiSelectionCombo}
 *
 * @author Kay Kasemir, Kunal Shroff
 */
@SuppressWarnings("nls")
public class MultipleSelectionComboDemo
{
    /** Custom item to demonstrate that
     *  {@link MultipleSelectionCombo}
     *  can handle any type of Object as long
     *  as it defines <code>toString()</code>.
     */
    static class MyItem
    {
        final private String name;

        public MyItem(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    };

    private static Text text;
    private final static List<MyItem> itemSet1 = Arrays.asList(
        new MyItem("Zero"), new MyItem("One"),
        new MyItem("Two"), new MyItem("Three"),
        new MyItem("Four"), new MyItem("Five"),
        new MyItem("Six"), new MyItem("Sixty"));
    private final static List<MyItem> itemSet2 = Arrays.asList(
        new MyItem("1"), new MyItem("2"), new MyItem("3"),
        new MyItem("4"), new MyItem("5"), new MyItem("6"));

    public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);

    shell.setLayout(new GridLayout());

    Label lblNewLabel = new Label(shell, SWT.NONE);
    lblNewLabel.setText("Basic");

    final MultipleSelectionCombo<MyItem> list = new MultipleSelectionCombo<>(
        shell, 0);
    list.addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selection")) {
            StringBuilder sb = new StringBuilder();
            String loopDelim = "";
            for (MyItem s : list.getSelection()) {
            sb.append(loopDelim);
            sb.append(s);
            loopDelim = ", ";
            }
            text.setText(sb.toString());
        }
        }
    });
    list.setLayoutData(new GridData(SWT.FILL, 0, true, false));
    list.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
    list.setToolTipText("Enter items");

    Button dump = new Button(shell, SWT.PUSH);
    dump.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    dump.setText("Dump selected items");
    dump.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        System.out.println("Selected items: " + list.getSelection());
        }
    });

    shell.setSize(400, 500);

    Button btnNewButton = new Button(shell, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        list.setItems(itemSet1);
        }
    });
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton.setText("Items set1");

    Button btnNewButton_1 = new Button(shell, SWT.NONE);
    btnNewButton_1.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        list.setItems(itemSet2);
        }
    });
    btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton_1.setText("Item set2");

    Label lblSelectedValues = new Label(shell, SWT.NONE);
    lblSelectedValues.setText("Selected Values:");

    text = new Text(shell, SWT.BORDER);
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    // Set initial Data

    list.setItems(itemSet1);
    list.setSelection("One, Four  , Five");

    Label lblUserSpecifiedDisplay = new Label(shell, SWT.NONE);
    lblUserSpecifiedDisplay.setText("User Specified display");

    MultipleSelectionCombo<MyItem> list2 = new MultipleSelectionCombo<MyItem>(
        shell, SWT.NONE) {
        @Override
        public String stringRepresention(MyItem object) {
        return "~" + object.toString() + "~";
        }
    };
    list2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
        1));

    list2.setToolTipText("Enter items");
    list2.setItems(itemSet2);

    shell.open();
    while (!shell.isDisposed())
        if (!display.readAndDispatch())
        display.sleep();
    display.dispose();
    }
}
