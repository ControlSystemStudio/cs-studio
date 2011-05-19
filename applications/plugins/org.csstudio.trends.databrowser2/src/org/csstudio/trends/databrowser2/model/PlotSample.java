/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.trends.databrowser2.Messages;
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


    /** Initialize with valid control system value
     *  @param source Info about the source of this sample
     *  @param value
     */
    public PlotSample(final String source, final IValue value)
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
        if (value.getSeverity().hasValue())
            return ValueUtil.getDouble(value);
        // No numeric value. Plot shows NaN as marker.
        return Double.NaN;
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
        if (!(value instanceof IMinMaxDoubleValue))
            return 0;
        final IMinMaxDoubleValue minmax = (IMinMaxDoubleValue)value;
        return minmax.getValue() - minmax.getMinimum();
    }

    /** {@inheritDoc} */
    @Override
    public double getYPlusError()
    {
        if (!(value instanceof IMinMaxDoubleValue))
            return 0;
        final IMinMaxDoubleValue minmax = (IMinMaxDoubleValue)value;
        return minmax.getMaximum() - minmax.getValue();
    }

    @Override
    public String toString()
    {
        return NLS.bind(Messages.PlotSampleFmt, new Object[] { value, source, value.getQuality().toString() });
    }
}
