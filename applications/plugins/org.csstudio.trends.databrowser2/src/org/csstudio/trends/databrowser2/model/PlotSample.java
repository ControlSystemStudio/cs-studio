/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.time.Instant;

import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** Data Sample from control system ({@link VType})
 *  with interface for XYGraph ({@link ISample})
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed PlotSample to handle waveform index.
 */
public class PlotSample implements PlotDataItem<Instant>
{
    /** Value contained in this sample */
    final private VType value;

    /** Source of the data */
    final private String source;

    /** Info string.
     *  @see #getInfo()
     */
    private String info;

    /** Waveform index */
    private int waveform_index = 0;

    /** Initialize with valid control system value
     *  @param source Info about the source of this sample
     *  @param value
     */
    public PlotSample(final String source, final VType value)
    {
        this.value = value;
        this.source = source;
        info = null;
    }

    /** Initialize with (error) info, creating a non-plottable sample 'now'
     *  @param info Text used for info as well as error message
     */
    public PlotSample(final String source, final String info)
    {
        this(source, ValueFactory.newVString(info, ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, info), ValueFactory.timeNow()));
        this.info = info;
    }

    /** Package-level constructor, only used in unit tests */
    @SuppressWarnings("nls")
    PlotSample(final double x, final double y)
    {
        this("Test",
             ValueFactory.newVDouble(y, ValueFactory.newTime(Timestamp.of((long) x, 0))));
    }

    /** @return Waveform index */
    public int getWaveformIndex()
    {
    	return waveform_index;
    }

    /** @param index Waveform index to plot */
    public void setWaveformIndex(int index)
    {
    	this.waveform_index = index;
    }

    /** @return Source of the data */
    public String getSource()
    {
        return source;
    }

    /** @return Control system value */
    public VType getVType()
    {
        return value;
    }

    /** @return Control system time stamp */
    private Timestamp getTime()
    {
        // NOT checking if time.isValid()
        // because that actually takes quite some time.
        // We just plot what we have, and that includes
        // the case where the time stamp is invalid.
        if (value instanceof Time)
            return ((Time) value).getTimestamp();
        return Timestamp.now();
    }

    /** {@inheritDoc} */
    @Override
    public Instant getPosition()
    {
        final Timestamp time = getTime();
        return Instant.ofEpochSecond(time.getSec(), time.getNanoSec());
    }

    /** {@inheritDoc} */
    @Override
    public double getValue()
    {
        return VTypeHelper.toDouble(value, waveform_index);
    }

    /** @return {@link VStatistics} or <code>null</code> */
    private VStatistics getStats()
    {
        // Although the behavior of getMinimum() method depends on archive
        // readers' implementation, at least, RDB and kblog archive readers
        // return the minimum value of the first element. This minimum value
        // does not make sense to plot error bars when the chart shows other
        // elements. Therefore, this method returns 0 if the waveform index
        // is not 0.
        if (waveform_index != 0)
            return null;
        if (value instanceof VStatistics)
            return (VStatistics) value;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public double getStdDev()
    {
        final VStatistics stats = getStats();
        return (stats != null) ? stats.getStdDev() : Double.NaN;
    }

    /** {@inheritDoc} */
    @Override
    public double getMin()
    {
        final VStatistics stats = getStats();
        return (stats != null) ? stats.getMin() : Double.NaN;
    }

    /** {@inheritDoc} */
    @Override
    public double getMax()
    {
        final VStatistics stats = getStats();
        return (stats != null) ? stats.getMax() : Double.NaN;
    }

    /** {@inheritDoc} */
    @Override
    public String getInfo()
    {
        if (info == null)
            return toString();
        return info;
    }

    @Override
    public String toString()
    {
        return VTypeHelper.toString(value);
    }
}
