/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
public class TimestampUtil
{
    /** Round time to next multiple of given seconds */
    public static ITimestamp roundUp(final ITimestamp time, long seconds)
    {
        if (seconds <= 0)
            return time;
        long secs = time.seconds();
        if (time.nanoseconds() > 0)
            ++secs;
        final long periods = secs / seconds;
        secs = (periods + 1) * seconds;
        return TimestampFactory.createTimestamp(secs, 0);
    }

    /** Add seconds to given time stamp.
     *  @param time Original time stamp
     *  @param seconds Seconds to add; may be negative
     *  @return new time stamp
     */
    public static ITimestamp add(final ITimestamp time, long seconds)
    {
        return TimestampFactory.createTimestamp(time.seconds() + seconds, 0);
    }

}
