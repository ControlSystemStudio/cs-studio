/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.device;

import java.util.Date;

import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;

/** Convert {@link IValue} into {@link ScanSample}
 *  @author Kay Kasemir
 */
public class ValueConverter
{
    /** Create ScanSample for control system value
     *  @param device_name Name of the device that provided the sample
     *  @param serial Serial to identify when the sample was taken
     *  @param value IValue
     *  @return {@link ScanSample}
     *  @throws IllegalArgumentException if the value type is not handled
     */
    public static ScanSample createSample(final String device_name,
            final long serial, final IValue value) throws IllegalArgumentException
    {
        final Date date = getDate(value.getTime());
        // Log strings as text, rest as double
        if (value instanceof IStringValue)
            return ScanSampleFactory.createSample(device_name, date, serial, ValueUtil.getString(value));
        else
            return ScanSampleFactory.createSample(device_name, date, serial, ValueUtil.getDouble(value));
    }

    /** @param time IValue timestamp
     *  @return Date
     */
    private static Date getDate(final ITimestamp time)
    {
        final long milli = time.seconds()*1000l + time.nanoseconds() / 1000000l;
        return new Date(milli);
    }
}
