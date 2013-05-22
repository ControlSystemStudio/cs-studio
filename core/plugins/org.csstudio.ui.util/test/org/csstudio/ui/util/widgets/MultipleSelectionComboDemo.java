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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * SWT Demo of the {@link MultiSelectionCombo}
 * 
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MultipleSelectionComboDemo {
    private static Text text;
    private final static List<String> itemSet1 = Arrays.asList("Zero", "One",
	    "Two", "Three", "Four", "Five", "Six");
    private final static List<String> itemSet2 = Arrays.asList("1", "2", "3",
	    "4", "5", "6");

    public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display);

	shell.setLayout(new GridLayout());

	Label lblNewLabel = new Label(shell, SWT.NONE);
	lblNewLabel.setText("Basic");

	final MultipleSelectionCombo<String> list = new MultipleSelectionCombo<>(
		shell, 0);
	list.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("selection")) {
		    StringBuilder sb = new StringBuilder();
		    String loopDelim = "";
		    for (String s : list.getSelection()) {
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

	MultipleSelectionCombo<String> list2 = new MultipleSelectionCombo<String>(
		shell, SWT.NONE) {
	    @Override
	    public String stringRepresention(String object) {
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
