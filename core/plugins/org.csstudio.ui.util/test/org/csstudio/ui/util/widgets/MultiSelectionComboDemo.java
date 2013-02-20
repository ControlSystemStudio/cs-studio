/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.widgets;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT Demo of the {@link MultiSelectionCombo}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MultiSelectionComboDemo
{
    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        
        shell.setLayout(new GridLayout());
        
        final MultiSelectionCombo<String> list = new MultiSelectionCombo<>(shell, 0);
        list.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        list.setItems(Arrays.asList(
           "Zero",
           "One",
           "Two",
           "Three",
           "Four",
           "Five",
           "Six"));
        list.setSelection("One, Four  , Five");
        
        list.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
        list.setToolTipText("Enter items");
        
        Button dump = new Button(shell, SWT.PUSH);
        dump.setText("Dump selected items");
        dump.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                System.out.println("Selected items: " + list.getSelection());
            }
        });
        
        shell.setSize(400, 300);
        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose();
    }
}
