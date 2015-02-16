/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.csstudio.swt.rtplot.data.SimpleDataItem;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VType;

/** Data provider for the plot that shows waveform elements of a VNumberArray
 *  @author Kay Kasemir
 */
public class WaveformValueDataProvider implements PlotDataProvider<Double>
{
    final private ReadWriteLock lock = new ReentrantReadWriteLock();

    private ListNumber numbers = null;

    /** {@inheritDoc} */
    @Override
    public Lock getLock()
    {
        return lock.readLock();
    }

    /** Update the waveform value.
     *  @param value New value
     *  Fires event to listeners (plot)
     */
    public void setValue(final VType value)
    {
        final ListNumber new_numbers;
        if (value instanceof VNumberArray)
            new_numbers = ((VNumberArray) value).getData();
        else
            new_numbers = new ArrayDouble(VTypeHelper.toDouble(value));

        lock.writeLock().lock();
        try
        {
            numbers = new_numbers;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

     /** {@inheritDoc} */
    @Override
    public int size()
    {
        return numbers == null ? 0 : numbers.size();
    }

    /** {@inheritDoc} */
    @Override
    public PlotDataItem<Double> get(final int index)
    {
        return new SimpleDataItem<Double>((double)index, numbers.getDouble(index));
    }
}
