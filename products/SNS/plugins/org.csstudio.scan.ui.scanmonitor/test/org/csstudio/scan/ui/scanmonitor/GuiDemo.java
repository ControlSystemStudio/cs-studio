/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor;

import org.csstudio.scan.client.ScanInfoModel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit test.
 *  Run as Plug-in test to get icons in context menu.
 *
 *  @author Kay Kasemir
 */
public class GuiDemo
{
    @Test
    public void runGuiDemo() throws Exception
    {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final ScanInfoModel model = ScanInfoModel.getInstance();
        new GUI(shell, model, null);

        shell.setSize(600, 400);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }
}
