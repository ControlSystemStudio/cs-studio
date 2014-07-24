/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.xygraph.dataprovider.ISample;
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
public class PlotSample implements ISample
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
    
    /** ErrorType, determines whether min/max or std are used for errors */
    private ErrorType errorType = ErrorType.MIN_MAX;

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
    
    /** @return ErrorType 
     * @author Friederike Johlinger */
    public ErrorType getErrorType(){
    	return errorType;
    }
    
    /** @param errorType either stdDev or min_max (i.e. range) 
     * @author Friederike Johlinger */
    public void setErrorType(ErrorType errorType){
    	this.errorType = errorType;
    }

    /** @return Source of the data */
    public String getSource()
    {
        return source;
    }

    /** @return Control system value */
    public VType getValue()
    {
        return value;
    }

    /** @return Control system time stamp */
    public Timestamp getTime()
    {
        // NOT checking if time.isValid()
        // because that actually takes quite some time.
        // We just plot what we have, and that includes
        // the case where the time stamp is invalid.
        if (value instanceof Time)
            return ((Time) value).getTimestamp();
        return Timestamp.now();
    }

    /** Since the 'X' axis is used as a 'Time' axis, this
     *  returns the time stamp of the control system sample.
     *  The XYGraph expects it to be milliseconds(!) since 1970.
     *  @return Time as milliseconds since 1970
     */
    @Override
    public double getXValue()
    {
        final Timestamp time = getTime();
        return time.getSec() * 1000.0 + time.getNanoSec() / 1e6;
    }

    /** {@inheritDoc} */
    @Override
    public double getYValue()
    {
    	return VTypeHelper.toDouble(value, waveform_index);
    }

    /** Get sample's info text.
     *  If not set on construction, the value's text is used.
     *  @return Sample's info text. */
    @Override
    public String getInfo()
    {
        if (info == null)
            return toString();
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
        if (!(value instanceof VStatistics))
            return 0;
        
        // Although the behavior of getMinimum() method depends on archive
        // readers' implementation, at least, RDB and kblog archive readers
        // return the minimum value of the first element. This minimum value
        // does not make sense to plot error bars when the chart shows other
        // elements. Therefore, this method returns 0 if the waveform index
        // is not 0.
        if (waveform_index != 0)
        	return 0;

        final VStatistics minmax = (VStatistics)value;
        if (errorType == ErrorType.MIN_MAX)
        	return minmax.getAverage() - minmax.getMin();
        else 
        	return minmax.getStdDev();
    }

    /** {@inheritDoc} */
    @Override
    public double getYPlusError()
    {
        if (!(value instanceof VStatistics))
            return 0;
 
        // Although the behavior of getMaximum() method depends on archive
        // readers' implementation, at least, RDB and kblog archive readers
        // return the maximum value of the first element. This maximum value
        // does not make sense to plot error bars when the chart shows other
        // elements. Therefore, this method returns 0 if the waveform index
        // is not 0.
        if (waveform_index != 0)
        	return 0;
        
        final VStatistics minmax = (VStatistics)value;
        if (errorType == ErrorType.MIN_MAX)
        	return minmax.getMax() - minmax.getAverage();
        else 
        	return minmax.getStdDev();
        
    }

    @Override
    public String toString()
    {
        return VTypeHelper.toString(value);
    }
}
