/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.csstudio.swt.rtplot.Axis;

/** {@link PlotDataProvider} based on {@link List}
 *  @param <XTYPE> Data type used for the horizontal {@link Axis}
 *  @author Kay Kasemir
 */
public class ArrayPlotDataProvider<XTYPE extends Comparable<XTYPE>> implements PlotDataProvider<XTYPE>
{
    final private ReadWriteLock lock = new ReentrantReadWriteLock();
    final private List<PlotDataItem<XTYPE>> data;

    /** Construct with existing data
     *  @param data
     */
    public ArrayPlotDataProvider(final List<PlotDataItem<XTYPE>> data)
    {
        this.data = data;
    }

    /** Construct with internal array */
    public ArrayPlotDataProvider()
    {
        this(new ArrayList<PlotDataItem<XTYPE>>());
    }

    /** @param item Item to add to the list */
    public void add(final PlotDataItem<XTYPE> item)
    {
        lock.writeLock().lock();
        try
        {
            data.add(item);
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

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
    public PlotDataItem<XTYPE> get(final int index)
    {
        return data.get(index);
    }
}
