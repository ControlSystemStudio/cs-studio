/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.swt.rtplot.util.RGBFactory;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/** Demo of {@link RTTimePlot}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimePlotDemo
{
    final private static int MAX_SIZE = 10000;

    public static void main(String[] args) throws Exception
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.WARNING);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(logger.getLevel());

        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setText("Time Plot Demo");
        shell.setLayout(new FillLayout());
        shell.setSize(600, 700);

        final RTTimePlot plot = new RTTimePlot(shell);

        final String font_name = shell.getFont().getFontData()[0].getName();
        plot.setTitle(Optional.of("Title of Time Demo"));
        plot.setTitleFont(new FontData(font_name, 20, SWT.BOLD));
        plot.setLabelFont(new FontData(font_name, 12, SWT.BOLD));
        plot.setScaleFont(new FontData(font_name, 8, SWT.ITALIC));

        plot.setUpdateThrottle(200, TimeUnit.MILLISECONDS);
        plot.setScrollStep(Duration.ofSeconds(30));

        plot.getXAxis().setGridVisible(true);

        plot.addYAxis("y2");
        plot.getYAxes().get(0).setValueRange(-2.2, 3.2);
        plot.getYAxes().get(0).setGridVisible(true);
        plot.getYAxes().get(1).setValueRange(1.2, 6.2);
        plot.getYAxes().get(1).setLogarithmic(true);

        plot.addYAxis("Right").setOnRight(true);

        final RGBFactory colors = new RGBFactory();
        final DynamicDemoData[] data = new DynamicDemoData[]
        { new DynamicDemoData(MAX_SIZE, 5.0), new DynamicDemoData(MAX_SIZE, 10.0), new DynamicDemoData(MAX_SIZE, 20.0) };
        plot.addTrace("Fred", "socks", data[0], colors.next(), TraceType.AREA_DIRECT, 3, PointType.NONE, 3, 0);
        plot.addTrace("Jane", "handbags", data[1], colors.next(), TraceType.AREA, 5, PointType.NONE, 5, 1);
        plot.addTrace("Another", "mA", data[2], colors.next(), TraceType.LINES_DIRECT, 1, PointType.TRIANGLES, 15, 2);

        final AtomicBoolean run = new AtomicBoolean(true);
        // Update data at 50Hz
        final Thread update_data = new Thread(() ->
        {
            while (run.get())
            {
                for (DynamicDemoData trace : data)
                    trace.add();
                plot.requestUpdate();
                try
                {
                    Thread.sleep(1000/50);
                }
                catch (Exception e)
                {
                    // NOP
                }
            }
        }, "DemoDataUpdate");
        update_data.start();

        final Control menu_holder = plot.getPlotControl();
        final MenuManager mm = new MenuManager();
        mm.add(plot.getToolbarAction());
        mm.add(plot.getLegendAction());
        final Menu menu = mm.createContextMenu(menu_holder);
        menu_holder.setMenu(menu);

        plot.addListener(new RTPlotListener<Instant>()
        {
            @Override
            public void changedXAxis(Axis<Instant> x_axis)
            {
                System.out.println("X Axis changed: " + x_axis);
            }
            @Override
            public void changedYAxis(YAxis<Instant> y_axis)
            {
                System.out.println("Y Axis changed: " + y_axis);
            }
            @Override
            public void changedAnnotations()
            {
                System.out.println("Annotations changed");
            }
            @Override
            public void changedCursors()
            {
                // System.out.println("Cursors changed");
            }
        });

        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        run.set(false);
        update_data.join();
        display.dispose();
    }
}
