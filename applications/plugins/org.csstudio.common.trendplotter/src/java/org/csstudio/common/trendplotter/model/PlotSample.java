/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.eclipse.osgi.util.NLS;

/** Data Sample from control system (IValue)
 *  with interface for XYGraph (ISample)
 *  @author Kay Kasemir
 */
public class PlotSample implements ISample
{
    final public static INumericMetaData dummy_meta = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 1, "a.u."); //$NON-NLS-1$
    final public static ISeverity ok_severity = ValueFactory.createOKSeverity();

    /** Value contained in this sample */
    final private IValue value;

    /** Source of the data */
    final private String source;

    /** Info string.
     *  @see #getInfo()
     */
    private String info;
    
    /** Waveform index */
    private int waveform_index = 0;
    
    private Number deadband = null;

    boolean show_deadband = false;


    /** Initialize with valid control system value
     *  @param source Info about the source of this sample
     *  @param value
     */
    public PlotSample(final String source, final IValue value)
    {
        if (value == null) {
            throw new IllegalArgumentException("IValue is null for PlotSample");
        }
        this.value = value;
        this.source = source;
        info = null;
    }

    /** Initialize with (error) info, creating a non-plottable sample 'now'
     *  @param info Text used for info as well as error message
     */
    public PlotSample(final String source, final String info)
    {
        this(source, ValueFactory.createDoubleValue(TimestampFactory.now(),
                ValueFactory.createInvalidSeverity(), info, dummy_meta,
                IValue.Quality.Original, new double[] { Double.NaN }));
        this.info = info;
    }

    /** Package-level constructor, only used in unit tests */
    @SuppressWarnings("nls")
    PlotSample(final double x, final double y)
    {
        this("Test",
             ValueFactory.createDoubleValue(TimestampFactory.fromDouble(x),
               ok_severity, ok_severity.toString(), dummy_meta,
               IValue.Quality.Original, new double[] { y }));
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
    public IValue getValue()
    {
        return value;
    }

    /** @return Control system time stamp */
    public ITimestamp getTime()
    {
        return value.getTime();
    }

    /** Since the 'X' axis is used as a 'Time' axis, this
     *  returns the time stamp of the control system sample.
     *  The XYGraph expects it to be milliseconds(!) since 1970.
     *  @return Time as milliseconds since 1970
     */
    @Override
    public double getXValue()
    {
        return value.getTime().toDouble()*1000.0;
    }

    /** {@inheritDoc} */
    @Override
    public double getYValue()
    {
        if (value.getSeverity().hasValue() && waveform_index < ValueUtil.getSize(value)){
            return ValueUtil.getDouble(value, waveform_index);
        }
        // No numeric value. Plot shows NaN as marker.
        return Double.NaN;
    }

    /** Get sample's info text.
     *  If not set on construction, the value's text is used.
     *  @return Sample's info text. */
    @Override
    public String getInfo()
    {
        if (info == null) {
            return toString();
        }
        return info;
    }

    /** {@inheritDoc} */
    @Override
    public double getXMinusError()
    {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getXPlusError()
    {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getYMinusError()
    {
        if (!(value instanceof IMinMaxDoubleValue)) {
            return 0;
        }
        // Although the behavior of getMinimum() method depends on archive
        // readers' implementation, at least, RDB and kblog archive readers
        // return the minimum value of the first element. This minimum value
        // does not make sense to plot error bars when the chart shows other
        // elements. Therefore, this method returns 0 if the waveform index
        // is not 0.
        if (waveform_index != 0) {
            return 0;
        }

        final IMinMaxDoubleValue minmax = (IMinMaxDoubleValue)value;
        if (show_deadband) {
            return getDeadband().doubleValue();
        }
        return minmax.getValue() - minmax.getMinimum();
    }

    /** {@inheritDoc} */
    @Override
    public double getYPlusError()
    {
        if (!(value instanceof IMinMaxDoubleValue)) {
            return 0;
        }
        
        // Although the behavior of getMaximum() method depends on archive
        // readers' implementation, at least, RDB and kblog archive readers
        // return the maximum value of the first element. This maximum value
        // does not make sense to plot error bars when the chart shows other
        // elements. Therefore, this method returns 0 if the waveform index
        // is not 0.
        if (waveform_index != 0) {
            return 0;
        }
        final IMinMaxDoubleValue minmax = (IMinMaxDoubleValue)value;
        if (show_deadband) {
            return getDeadband().doubleValue();
        }
        return minmax.getMaximum() - minmax.getValue();
    }

    @Override
    public String toString()
    {
        if (hasDeadband()) {
            return NLS.bind(Messages.PlotSampleFmtWithDeadband, new Object[] { value, getDeadband(), source});
        }
        return NLS.bind(Messages.PlotSampleFmt, new Object[] { value, source});
    }

    public void setDeadband(final Number db) {
        deadband = db;
    }
    public Number getDeadband() {
        return deadband;
    }
    public void setShowDeadband(final boolean b) {
        show_deadband = b;
    }
    public boolean getShowDeadband() {
        return show_deadband;
    }
    public boolean hasDeadband() {
        return deadband != null;
    }

}
