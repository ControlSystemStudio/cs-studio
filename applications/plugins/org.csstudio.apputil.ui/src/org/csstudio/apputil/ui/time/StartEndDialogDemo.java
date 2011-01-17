/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import java.text.SimpleDateFormat;

import org.csstudio.apputil.time.RelativeTime;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of TimestampWidget.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StartEndDialogDemo
{
    public void updatedTime(RelativeTimeWidget source, RelativeTime time)
    {
        System.out.println("Time: " + time);
    }

    public void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        
        shell.pack();
        shell.open();

        StartEndDialog dlg = new StartEndDialog(shell);
        if (dlg.open() == Window.OK)
        {
            System.out.println("Start: '" + dlg.getStartSpecification() + "'");
            System.out.println("End: '" + dlg.getEndSpecification() + "'");
            
            final SimpleDateFormat date_format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            String start = date_format.format(dlg.getStartCalendar().getTime());
            String end = date_format.format(dlg.getEndCalendar().getTime());
            System.out.println(start + " ... " + end);
        }

        display.dispose();
    }
    
    public static void main(String[] args)
    {
        new StartEndDialogDemo().run();
    }
}
