/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Time;
import org.diirt.vtype.VStatistics;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** Data Sample from control system ({@link VType})
 *  with interface for XYGraph ({@link ISample})
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed PlotSample to handle waveform index.
 */
@SuppressWarnings("nls")
public class PlotSample implements PlotDataItem<Instant>
{
    final private static AtomicInteger default_waveform_index = new AtomicInteger(0);

    /** Value contained in this sample */
    final private VType value;

    /** Source of the data */
    final private String source;

    /** Info string.
     *  @see #getInfo()
     */
    private Optional<String> info;

    /** Waveform index */
    private AtomicInteger waveform_index;

    /** Initialize with valid control system value
     *  @param waveform_index Waveform index
     *  @param source Info about the source of this sample
     *  @param value
     *  @param info Info text
     */
    PlotSample(final AtomicInteger waveform_index, final  String source, final VType value, final String info)
    {
        this.waveform_index = waveform_index;
        this.value = value;
        this.source = source;
        this.info = Optional.ofNullable(info);
    }

    /** Initialize with valid control system value
     *  @param waveform_index Waveform index
     *  @param source Info about the source of this sample
     *  @param value
     */
    PlotSample(final AtomicInteger waveform_index, final  String source, final VType value)
    {
        this(waveform_index, source, value, null);
    }

    /** Initialize with valid control system value
     *  @param source Info about the source of this sample
     *  @param value
     */
    public PlotSample(final String source, final VType value)
    {
        this(default_waveform_index, source, value);
    }

    /** Initialize with (error) info, creating a non-plottable sample 'now'
     *  @param info Text used for info as well as error message
     */
    public PlotSample(final String source, final String info)
    {
        this(default_waveform_index, source,
             ValueFactory.newVString(info, ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, info), ValueFactory.timeNow()),
             info);
    }

    /** Package-level constructor, only used in unit tests */
    PlotSample(final double x, final double y)
    {
        this("Test",
             ValueFactory.newVDouble(y, ValueFactory.newTime(Instant.ofEpochSecond((int) x, 0))));
    }

    /** @param index Waveform index to plot */
    void setWaveformIndex(final AtomicInteger index)
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
    private Instant getTime()
    {
        // NOT checking if time.isValid()
        // because that actually takes quite some time.
        // We just plot what we have, and that includes
        // the case where the time stamp is invalid.
        if (value instanceof Time)
            return ((Time) value).getTimestamp();
        return Instant.now();
    }

    /** {@inheritDoc} */
    @Override
    public Instant getPosition()
    {
    	return getTime();
    }

    /** {@inheritDoc} */
    @Override
    public double getValue()
    {
        return VTypeHelper.toDouble(value, waveform_index.get());
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
        if (waveform_index.get() != 0)
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
        return info.orElseGet(this::toString);
    }

    @Override
    public String toString()
    {
        return VTypeHelper.toString(value);
    }
}
