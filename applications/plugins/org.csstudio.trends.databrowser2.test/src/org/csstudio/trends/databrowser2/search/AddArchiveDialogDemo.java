/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.search;

import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit Plug-in demo of the AddArchiveDialog
 *  @author Kay Kasemir
 */
public class AddArchiveDialogDemo
{
    @Test
    public void testArchiveGUI() throws Exception
    {
        final Shell shell = new Shell();

        final AddArchiveDialog dlg = new AddArchiveDialog(shell);
        if (dlg.open() == Window.OK)
            for (ArchiveDataSource arch : dlg.getArchives())
                System.out.println(arch);
    }
}
