/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;

/** Factory for {@link ScanSample} instances
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanSampleFactory
{
    /** Provides the next available <code>serial</code> */
    final private static AtomicLong serials = new AtomicLong();

    /** @return Next available serial */
    public static long getNextSerial()
    {
        return serials.incrementAndGet();
    }

    /** Create ScanSample for control system value
	 *  @param device_name Name of the device that provided the sample
     *  @param value IValue
	 *  @return {@link ScanSample}
	 *  @throws IllegalArgumentException if the value type is not handled
	 */
	public static ScanSample createSample(final String device_name,
			final IValue value) throws IllegalArgumentException
	{
		return createSample(device_name, getNextSerial(), value);
	}

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
            return createSample(device_name, date, serial, ValueUtil.getString(value));
        else
            return createSample(device_name, date, serial, ValueUtil.getDouble(value));
    }

    /** @param time IValue timestamp
     *  @return Date
     */
    private static Date getDate(final ITimestamp time)
    {
        final long milli = time.seconds()*1000l + time.nanoseconds() / 1000000l;
        return new Date(milli);
    }

    /** Create ScanSample for plain number or text value
	 *  @param device_name Name of the device that provided the sample
	 *  @param timestamp Time stamp
     *  @param serial Serial to identify when the sample was taken
	 *  @param value Value (Number, String)
	 *  @return {@link ScanSample}
	 *  @throws IllegalArgumentException if the value type is not handled
	 */
	public static ScanSample createSample(final String device_name, final Date timestamp,
	        final long serial, final Object value) throws IllegalArgumentException
	{
		if (value instanceof Number)
			return new NumberScanSample(device_name, timestamp, serial, (Number)value);
		throw new IllegalArgumentException("Sample of type " + value.getClass().getName() + " is not handled");
	}
}
