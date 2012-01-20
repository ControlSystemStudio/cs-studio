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
import java.util.Date;

/** Helper for formatting data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DataFormatter
{
	/** Suggested time format */
	final public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	final private static DateFormat time_fmt = new SimpleDateFormat(TIME_FORMAT);

	/** Extract double from sample
	 *  @param sample ScanSample
	 *  @return Double or NaN if there is no number to extract
	 */
	public static double toDouble(final ScanSample sample)
	{
	    if (sample instanceof NumberScanSample)
	        return ((NumberScanSample) sample).getNumber().doubleValue();
	    return Double.NaN;
	}
	
	/** @param timestamp {@link Date}
	 *  @return Date in preferred text format
	 */
	public static synchronized String format(final Date timestamp)
	{
	    if (timestamp == null)
	        return "null";
    	return time_fmt.format(timestamp);
	}
}
