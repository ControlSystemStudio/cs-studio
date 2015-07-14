/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.csstudio.swt.rtplot.data.SimpleDataItem;
import org.csstudio.swt.rtplot.util.RGBFactory;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/** Demo of {@link RTValuePlot}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValuePlotDemo
{
    final private static int MAX_SIZE = 10000;

    static class DemoData implements PlotDataProvider<Double>
    {
        final private ReadWriteLock lock = new ReentrantReadWriteLock();
        final private List<PlotDataItem<Double>> data = new ArrayList<>();
        private int calls = 0;

        @Override
        public Lock getLock()
        {
            return lock.readLock();
        }

        @Override
        public int size()
        {
            return data.size();
        }

        @Override
        public PlotDataItem<Double> get(final int index)
        {
            return data.get(index);
        }

        public void update()
        {
            lock.writeLock().lock();
            try
            {
                data.clear();
                final double amp = 10.0*Math.cos(2*Math.PI * (++calls) / 1000.0);
                for (int i=0; i<MAX_SIZE; ++i)
                {
                    final double value = amp*(Math.sin(2*Math.PI * i / (MAX_SIZE/3)) + Math.random()*0.1);
                    data.add(new SimpleDataItem<Double>(Double.valueOf(i), value));
                }
            }
            finally
            {
                lock.writeLock().unlock();
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.WARNING);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(logger.getLevel());

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        shell.setSize(600, 700);

        final RTValuePlot plot = new RTValuePlot(shell);
        plot.getXAxis().setValueRange(0.0, Double.valueOf(MAX_SIZE));
        plot.getXAxis().setGridVisible(true);
        plot.getYAxes().get(0).setValueRange(-12.0, 12.0);
        plot.getYAxes().get(0).setGridVisible(true);

        plot.setUpdateThrottle(20, TimeUnit.MILLISECONDS);

        final RGBFactory colors = new RGBFactory();
        final DemoData data = new DemoData();
        plot.addTrace("Fred", "socks", data, colors.next(), TraceType.AREA, 3, PointType.NONE, 0, 0);

        final AtomicBoolean run = new AtomicBoolean(true);
        // Update data at 50Hz
        Thread update_data = new Thread(() ->
        {
            while (run.get())
            {
                data.update();
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


        plot.addListener(new RTPlotListener<Double>()
        {
            @Override
            public void changedXAxis(Axis<Double> x_axis)
            {
                System.out.println("X Axis changed: " + x_axis);
            }
            @Override
            public void changedYAxis(YAxis<Double> y_axis)
            {
                System.out.println("Y Axis changed: " + y_axis);
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
