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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/** Helper for formatting {@link ScanSample}s
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanSampleFormatter
{
	/** Suggested time format */
	final public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	final private static DateFormat date_format = new SimpleDateFormat(DATE_FORMAT);

	final public static String TIME_FORMAT = "HH:mm:ss";
	final private static DateFormat time_format = new SimpleDateFormat(TIME_FORMAT);

	
	/** Extract double from sample
	 *  @param sample ScanSample
	 *  @return Double or NaN if there is no number to extract
	 */
	public static double asDouble(final ScanSample sample)
	{
	    if (sample instanceof NumberScanSample)
	        return ((NumberScanSample) sample).getNumber(0).doubleValue();
	    return Double.NaN;
	}

	/** Extract String from sample
	 *  @param sample ScanSample
	 *  @return String for scan sample
	 */
	public static String asString(final ScanSample sample)
	{
	    if (sample == null)
	    	return "";
	    final Object[] values = sample.getValues();
	    if (values == null)
	        return "";
	    if (values.length == 1)
	        return values[0].toString();
        return Arrays.toString(values);
	}

	/** @param timestamp {@link Date}
	 *  @return Date in preferred text format
	 */
	public static String format(final Date timestamp)
	{
	    if (timestamp == null)
	        return "?";
	    synchronized (date_format)
	    {
	        return date_format.format(timestamp);
	    }
	}

    /** @param timestamp {@link Date}
     *  @return Time of the data in preferred text format
     */
    public static String formatTime(final Date timestamp)
    {
        if (timestamp == null)
            return "?";
        synchronized (time_format)
        {
            return time_format.format(timestamp);
        }
    }
}
