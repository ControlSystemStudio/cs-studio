/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.scan.data.ScanSample;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.linearscale.Range;

/** Data provider for XYGraph based on {@link ScanSample}
 *  @author Kay Kasemir
 */
public class ScanDataProvider implements IDataProvider
{
    final private List<IDataProviderListener> listeners =
            new CopyOnWriteArrayList<IDataProviderListener>();
    
    final private List<SampleAdapter> samples = new ArrayList<SampleAdapter>();
    private Range xrange = new Range(0, 0);
    private Range yrange = new Range(0, 0);
    
    /** Add an X/Y sample
     *  @param x X
     *  @param y Y
     */
    public void addSample(final ScanSample x, final ScanSample y)
    {
        // Add sample
        final SampleAdapter sample = new SampleAdapter(x, y);
        samples.add(sample);
        
        // Update ranges
        double d = sample.getXValue();
        if (! xrange.inRange(d))
            xrange = new Range(Math.min(xrange.getLower(), d),
                               Math.max(xrange.getUpper(), d));
        d = sample.getYValue();
        if (! yrange.inRange(d))
            yrange = new Range(Math.min(yrange.getLower(), d),
                               Math.max(yrange.getUpper(), d));
        
        // TODO Update listeners, or remove the listener support
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
