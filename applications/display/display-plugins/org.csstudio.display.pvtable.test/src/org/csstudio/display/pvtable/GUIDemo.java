/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import java.io.FileInputStream;

import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.persistence.PVTablePersistence;
import org.csstudio.display.pvtable.persistence.PVTableXMLPersistence;
import org.csstudio.display.pvtable.ui.PVTable;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of {@link PVTableXMLPersistence}
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
    public void demoGUI() throws Exception
    {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final PVTablePersistence persistence = new PVTableXMLPersistence();
        final PVTableModel model = persistence.read(new FileInputStream("lib/example.pvs"));
        final PVTable table = new PVTable(shell, null);
        table.setModel(model);

        shell.setSize(800, 600);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}
