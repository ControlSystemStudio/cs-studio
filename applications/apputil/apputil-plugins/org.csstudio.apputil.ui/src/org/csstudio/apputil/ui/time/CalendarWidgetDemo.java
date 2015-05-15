/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of TimestampWidget.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CalendarWidgetDemo implements CalendarWidgetListener
{
    final private DateFormat format =
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public void updatedCalendar(CalendarWidget source, Calendar calendar)
    {
        System.out.println("Time: " + format.format(calendar.getTime()));
    }

    public void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        CalendarWidget w = new CalendarWidget(shell, 0);
        w.addListener(this);

        shell.pack();
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        w.removeListener(this);
        display.dispose();
    }

    public static void main(String[] args)
    {
        new CalendarWidgetDemo().run();
    }
}
