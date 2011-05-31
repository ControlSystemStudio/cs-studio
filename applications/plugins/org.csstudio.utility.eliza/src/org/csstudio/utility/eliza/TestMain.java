/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.eliza;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Standalone SWT Test of ElizaGUI
 *  @author Kay Kasemir
 */
public class TestMain
{
    public static void main(String[] args)
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setBounds(400, 100, 300, 350);
        
        new ElizaGUI(shell);

        shell.open();
        // Message loop left to the application
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose(); // !
    }
}
