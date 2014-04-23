/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.ui.scantree.gui.ScanTreeGUI;
import org.csstudio.scan.ui.scantree.model.ScanTreeModel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in Demo of scan tree GUI
 *
 *  Can run in plain JUnit test,
 *  but then without icons.
 *  On OS X, add JVM param -XstartOnFirstThread
 *
 *  @author Kay Kasemir
 */
public class ScanTreeGUIDemo
{
    @Test
    public void runScanTreeDemo() throws Exception
    {
        // SWT setup
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);

        // Scan GUI
        final ScanTreeModel model = new ScanTreeModel();
        shell.setLayout(new FillLayout());
        new ScanTreeGUI(shell, model, null);

        model.setCommands(DemoScan.createCommands());

        // SWT main loop
        shell.setSize(800, 600);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }
}
