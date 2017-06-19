/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.probe;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import org.csstudio.java.time.TimestampFormats;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;

/** Info about the most recent value.
 *  <p>
 *  Since the data is updated in a protocol thread,
 *  but displayed in a GUI thread,
 *  this class holds the data and handles the synchronization.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueInfo
{
    /** The most recent numeric meta data of the PV, or <code>null</code> */
    private volatile Optional<Display> display_info = Optional.empty();

    /** The most recent numeric value of the PV. */
    private volatile double value_dbl;

    /** The most recent time stamp of the PV. */
    private volatile Instant time = null;

    /** Smoothed period in seconds between received values. */
    private SmoothedDouble value_period = new SmoothedDouble();

    /** The most recent value of the PV, as a string. */
    private volatile String value_str = "";

    private DateTimeFormatter formatter = TimestampFormats.MILLI_FORMAT;

    public String getValueString()
    {
        return value_str;
    }

    Optional<Display>  getDisplayInfo()
    {
        return display_info;
    }

    public double getDouble()
    {
        return value_dbl;
    }

    public String getTimeText()
    {
        final Instant safe_time = time;
        if (safe_time == null)
            return "";
        final LocalDateTime local = LocalDateTime.ofInstant(safe_time, ZoneId.systemDefault());
        return local.format(formatter);
    }

    double getUpdatePeriod()
    {
        return value_period.get();
    }

    void reset()
    {
        value_str = "";
        time = null;
    }

    public void update(final VType value)
    {
        display_info = Optional.ofNullable(ValueUtil.displayOf(value));
        if (display_info.isPresent())
            value_dbl = ValueUtil.numericValueOf(value).doubleValue();

        if (value instanceof VNumber)
            value_str = Double.toString(((VNumber) value).getValue().doubleValue());
        else if (value instanceof VEnum)
        {
            final VEnum ev = (VEnum) value;
            if (ev.getIndex() >= 0  &&  ev.getIndex() < ev.getLabels().size())
                value_str = ev.getLabels().get(ev.getIndex());
            else
                value_str = Integer.toString(ev.getIndex());
        }
        else if (value instanceof VString)
            value_str = ((VString) value).getValue();
        else
            value_str = Objects.toString(value);

        final Time vtime = ValueUtil.timeOf(value);
        if (vtime == null)
            return;

        final Instant new_time = vtime.getTimestamp();
        if (time != null)
        {
            final Duration duration = Duration.between(time,  new_time);
            final double period = duration.getSeconds() + duration.getNano() * 1e-9;
            value_period.add(period);
        }
        else
            value_period.reset();
        time = new_time;
    }
}
