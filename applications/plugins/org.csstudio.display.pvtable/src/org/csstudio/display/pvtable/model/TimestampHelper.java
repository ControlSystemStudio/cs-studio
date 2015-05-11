/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.text.Format;

import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelper
{
    final public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.NNNNNNNNN";

    /** Time stamp format */
    final private static Format time_format = new TimestampFormat(TimestampHelper.FORMAT_FULL);

    /** @param timestamp {@link Timestamp}, may be <code>null</code>
     *  @return Time stamp formatted as string
     */
    public static String format(final Timestamp timestamp)
    {
        if (timestamp == null)
            return "null";
        synchronized (time_format)
        {
            return time_format.format(timestamp);
        }
    }
}
