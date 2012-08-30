/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.gui;


import org.csstudio.diag.pvutil.model.PVUtilModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** Standalone "main" test of the PVUtilDataAPI and the GUI */
public class TestMain
{
    @SuppressWarnings("nls")
    @Test
	public void test() throws Exception
    {
        // Initialize SWT
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        shell.setText("PV Utility");

        PVUtilModel control = new PVUtilModel ();
        new GUI(shell, control);

        shell.pack();
        shell.open();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
