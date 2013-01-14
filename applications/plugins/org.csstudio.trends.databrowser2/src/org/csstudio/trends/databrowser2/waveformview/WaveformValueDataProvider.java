/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import java.util.ArrayList;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VType;

/** Data provider for the XYGraph that shows waveform elements of an IValue
 *  @author Kay Kasemir
 */
public class WaveformValueDataProvider implements IDataProvider
{
    final private ArrayList<IDataProviderListener> listeners =
        new ArrayList<IDataProviderListener>();

    private ListNumber numbers = null;

    /** Update the waveform value.
     *  @param value New value
     *  Fires event to listeners (plot)
     */
    public void setValue(final VType value)
    {
        if (value instanceof VNumberArray)
            numbers = ((VNumberArray) value).getData();
        else
            numbers = null;
        for (IDataProviderListener listener : listeners)
            listener.dataChanged(this);
    }

     /** {@inheritDoc} */
    @Override
    public int getSize()
    {
        return numbers == null ? 0 : numbers.size();
    }

    /** {@inheritDoc} */
    @Override
    public ISample getSample(final int index)
    {
        return new Sample(index, numbers.getDouble(index));
    }

    /** {@inheritDoc} */
    @Override
    public Range getXDataMinMax()
    {
        if (numbers == null)
            return null;
        return new Range(0, getSize()-1);
    }

    /** {@inheritDoc} */
    @Override
    public Range getYDataMinMax()
    {
        if (numbers == null)
            return null;
        double min, max;
        min = max = numbers.getDouble(0);
        for (int i=getSize()-1; i>=1; --i)
        {
            final double num = numbers.getDouble(i);
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
