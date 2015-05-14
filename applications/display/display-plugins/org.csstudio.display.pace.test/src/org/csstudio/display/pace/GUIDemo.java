/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import java.io.FileInputStream;

import org.csstudio.display.pace.gui.GUI;
import org.csstudio.display.pace.model.Model;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

/** Standalone GUI test, run as JUnit test.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUIDemo
{
    @Before
    public void setup()
    {
        TestSettings.setup();
    }

    @Test
    public void testGUI() throws Exception
    {
        // Open display & shell ourself.
        // Real application would have Eclipse Workbench site
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setBounds(10, 100, 800, 600);

        // Exceptions are not expected and handled by JUnit
        // framework by displaying them.
        final Model model =
            new Model(new FileInputStream(TestSettings.TEST_CONFIG_FILE));
        new GUI(shell, model, null);
        model.start();

        // SWT main loop, in real application handled by workbench
        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();

        // Model shutdown
        model.stop();
    }
}
