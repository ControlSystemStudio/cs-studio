/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the AddPVDialog
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AddPVDialogDemo
{
    final String names[] = new String[] { "Fred", "Jane" };
    final String axes[] = new String[] { "Value", "Value 2" };

    @Test
    public void pvTest()
    {
        final Shell shell = new Shell();
        final AddPVDialog dlg = new AddPVDialog(shell, names, axes, false);
        if (dlg.open() == Window.OK)
        {
            System.out.println("Name  : " + dlg.getName());
            System.out.println("Period: " + dlg.getScanPeriod());
            System.out.println("Axis  : " + dlg.getAxisIndex());
        }
    }


    @Test
    public void formulaTest()
    {
        final Shell shell = new Shell();
        final AddPVDialog dlg = new AddPVDialog(shell, names, axes, true);
        if (dlg.open() == Window.OK)
        {
            System.out.println("Name  : " + dlg.getName());
            System.out.println("Axis  : " + dlg.getAxisIndex());
        }
    }
}
