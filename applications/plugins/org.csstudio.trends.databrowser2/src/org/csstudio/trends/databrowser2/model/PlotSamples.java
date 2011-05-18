/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.trends.databrowser2.ui.Controller;

/** Base for classes that hold plot samples
 *  in a way accessible to the XYGraph (IDataProvider).
 *  <p>
 *  Note that this IDataProvider never fires IDataProviderListener
 *  events: The Data Browser does not trigger a plot refresh
 *  for each new sample. Instead, the {@link Controller}
 *  handles the scrolling and refresh.
 *  <p>
 *  <b>Important Note on Synchronization:</b>
 *  Synchronize on the PlotSamples whenever accessing them because
 *  they can change dynamically (new 'live' sample, updated archive data).
 *  While getSize() and getSample() might already be synchronized methods,
 *  that still leaves a possibility for the overall sample count to change
 *  from the time getSize() was called to the time getSample() is used to
 *  access the samples.
 *  @author Kay Kasemir
 */
abstract public class PlotSamples implements IDataProvider
{
    /** To be set when samples change
     *  @see #testAndClearNewSamplesFlag()
     */
    protected boolean have_new_samples = false;

    /** {@inheritDoc} */
    @Override
    public void addDataProviderListener(IDataProviderListener listener)
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeDataProviderListener(IDataProviderListener listener)
    {
        // NOP
        return false;
    }

    /** @return <code>true</code> because Data Browser samples are always ordered by time */
    @Override
    public boolean isChronological()
    {
        return true;
    }

    /** @see IDataProvider#getSize() */
    @Override
    abstract public int getSize();

    /** @see IDataProvider#getSample() */
    @Override
    abstract public PlotSample getSample(int index);

    /** @return Time range */
    @Override
    synchronized public Range getXDataMinMax()
    {
        final int n = getSize();
        if (n <= 0)
            return null;
        // X data is really time data. Min/max = start/end.
        final double min = getSample(0).getXValue();
        final double max = getSample(n-1).getXValue();
        return new Range(min, max);
    }

    /** @return Value range */
    @Override
    synchronized public Range getYDataMinMax()
    {
        // Is there a way to optimize this?
        // Keep last range until new samples are added?
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i=getSize()-1; i>=0; --i)
        {
            final PlotSample sample = getSample(i);
            final double val = sample.getYValue();
            if (Double.isNaN(val) || Double.isInfinite(val))
                continue;
            // Include min/max error in range
            if (val-sample.getYMinusError() < min)
                min = val-sample.getYMinusError();
            if (val+sample.getYPlusError() > max)
                max = val+sample.getYPlusError();
        }
        // No valid range because no samples, all NaN, or with outrageous values?
        if (min == Double.MAX_VALUE  ||  max == -Double.MAX_VALUE)
            return null;
        return new Range(min, max);
    }

    /** Test if samples changed since the last time
     *  <code>testAndClearNewSamplesFlag</code> was called.
     *  @return <code>true</code> if there were new samples
     */
    synchronized public boolean hasNewSamples()
    {
        return have_new_samples;
    }

    /** Test if samples changed since the last time this method was called.
     *  @return <code>true</code> if there were new samples
     */
    synchronized public boolean testAndClearNewSamplesFlag()
    {
        if (have_new_samples)
        {
            have_new_samples = false;
            return true;
        }
        return false;
    }

    /** @return Info text about PlotSamples for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final int n = getSize();
        final StringBuilder buf = new StringBuilder(n + " Plot Samples");
        if (n < 100)
        {
            for (int i=0; i<n; ++i)
                buf.append(String.format("\n%3d: ", i) + getSample(i).getValue());
        }
        return buf.toString();
    }
}
