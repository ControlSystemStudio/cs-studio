/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.search;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit Plug-In test of the ArchiveListGUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveListGUIDemo
{
    @Test
    public void testArchiveGUI() throws Exception
    {
        final Display display = Display.getDefault();

        final Shell shell = new Shell();
        shell.setSize(600, 500);

        new ArchiveListGUI(shell)
        {
            @Override
            protected void handleArchiveUpdate()
            {
                System.out.println("Received archive list");
            }

            @Override
            protected void handleServerError(final String url, final Exception ex)
            {
                ex.printStackTrace();
            }
        };

        shell.open();
        // Message loop left to the application
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
    }
}
