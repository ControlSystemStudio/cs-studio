/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/** Demo of {@link RGBFactory}
 *  @author Kay Kasemir
 */
public class RGBFactoryDemo
{
    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(3, true));
        shell.setSize(800, 600);
        shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

        final RGBFactory colors = new RGBFactory();
        int index = 0;
        for (int row=0; row<20; ++row)
            for (int col=0; col<3; ++col)
            {
                Label text = new Label(shell, SWT.NONE);
                final RGB color = colors.next();
                text.setText("COLOR " + (++index) + ": " + color);
                text.setForeground(new Color(display, color));
                text.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
            }

        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }
}
