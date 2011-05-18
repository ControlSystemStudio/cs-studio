/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import java.util.ArrayList;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.csstudio.swt.xygraph.linearscale.Range;

/** Data provider for the XYGraph that shows waveform elements of an IValue
 *  @author Kay Kasemir
 */
public class WaveformValueDataProvider implements IDataProvider
{
    final private ArrayList<IDataProviderListener> listeners =
        new ArrayList<IDataProviderListener>();

    private IValue value = null;

    /** Update the waveform value.
     *  @param value New value
     *  Fires event to listeners (plot)
     */
    public void setValue(final IValue value)
    {
        this.value = value;
        for (IDataProviderListener listener : listeners)
            listener.dataChanged(this);
    }

     /** {@inheritDoc} */
    @Override
    public int getSize()
    {
        if (value == null)
            return 0;
        return ValueUtil.getSize(value);
    }

    /** {@inheritDoc} */
    @Override
    public ISample getSample(final int index)
    {
        if (value.getSeverity().hasValue())
            return new Sample(index, ValueUtil.getDouble(value, index));
        return new Sample(index, Double.NaN);
    }

    /** {@inheritDoc} */
    @Override
    public Range getXDataMinMax()
    {
        if (value == null)
            return null;
        return new Range(0, getSize()-1);
    }

    /** {@inheritDoc} */
    @Override
    public Range getYDataMinMax()
    {
        if (value == null)
            return null;
        double min, max;
        min = max = ValueUtil.getDouble(value);
        for (int i=getSize()-1; i>=1; --i)
        {
            final double num = ValueUtil.getDouble(value, i);
            if (num < min)
                min = num;
            if (num > max)
                max = num;
        }
        return new Range(min, max);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isChronological()
    {   // x range is [0..waveform size]
        return true;
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
}
