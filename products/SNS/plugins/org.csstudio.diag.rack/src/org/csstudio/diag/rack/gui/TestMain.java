/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.gui;

import org.csstudio.diag.rack.model.RackModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** Standalone "main" test of the Model and the GUI */
@SuppressWarnings("nls")
public class TestMain
{
    @Test
    public void test() throws Exception
    {
        // Initialize SWT
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        shell.setText("Real Rack Profile Utility");


        RackModel control = new RackModel();
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
