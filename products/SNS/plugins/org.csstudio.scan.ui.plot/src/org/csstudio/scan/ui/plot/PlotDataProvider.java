/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.swt.widgets.Display;

/** Data provider for XYGraph based on {@link ScanSample}
 *  @author Kay Kasemir
 */
public class PlotDataProvider implements IDataProvider
{
    final private Display display;

    final private List<IDataProviderListener> listeners =
            new CopyOnWriteArrayList<IDataProviderListener>();

    // Synchronize on access. XYPlot will also sync on 'this'.
    private List<SampleAdapter> samples = new ArrayList<SampleAdapter>();
    private Range xrange = new Range(0, 0);
    private Range yrange = new Range(0, 0);
    
    /** Initialize
     *  @param display Display to use for listener notifications
     */
    public PlotDataProvider(final Display display)
    {
        this.display = display;
    }
    
    /** Remove all samples */
    public void clear()
    {
        synchronized (this)
        {
            samples.clear();
            xrange = new Range(0, 0);
            yrange = new Range(0, 0);
        }
        notifyListeners();
    }

    /** Set samples for plot from scan data
     *  @param scan_data {@link ScanData}
     *  @param x_device Name of device for 'X' axis
     *  @param y_device Name of device for 'Y' axis
     */
    public void update(final ScanData scan_data, final String x_device, final String y_device)
    {
        // Arrange data in 'spreadsheet'
        final SpreadsheetScanDataIterator sheet =
                new SpreadsheetScanDataIterator(scan_data,
                        Arrays.asList(x_device, y_device));

        final List<SampleAdapter> new_samples = new ArrayList<SampleAdapter>();
        Range new_xrange = new Range(0, 0);
        Range new_yrange = new Range(0, 0);

        while (sheet.hasNext())
        {
            final List<ScanSample> samples = sheet.getSamples();

            // Add sample
            final SampleAdapter sample = new SampleAdapter(samples.get(0), samples.get(1));
            new_samples.add(sample);
            
            // Update ranges
            double d = sample.getXValue();
            if (!Double.isNaN(d)  &&  !new_xrange.inRange(d))
                new_xrange = new Range(Math.min(new_xrange.getLower(), d),
                                        Math.max(new_xrange.getUpper(), d));
            d = sample.getYValue();
            if (!Double.isNaN(d)  &&  !new_yrange.inRange(d))
                new_yrange = new Range(Math.min(new_yrange.getLower(), d),
                                       Math.max(new_yrange.getUpper(), d));
        }
        synchronized (this)
        {
            samples = new_samples;
            xrange = new_xrange;
            yrange = new_yrange;
        }
        notifyListeners();
    }

    /** Update listeners on Display thread */
    private void notifyListeners()
    {
        if (display != null)
            display.asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    for (IDataProviderListener listener : listeners)
                        listener.dataChanged(PlotDataProvider.this);
                }
            });
    }

    /** {@inheritDoc} */
    @Override
    public void addDataProviderListener(final IDataProviderListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeDataProviderListener(final IDataProviderListener listener)
    {
        return listeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public int getSize()
    {
        return samples.size();
    }

    /** {@inheritDoc} */
    @Override
    public ISample getSample(final int index)
    {
        return samples.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public Range getXDataMinMax()
    {
        return xrange;
    }

    /** {@inheritDoc} */
    @Override
    public Range getYDataMinMax()
    {
        return yrange;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isChronological()
    {
        return false;
    }
}
