/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.util.time;

/** Throttle that only permits a certain number of events.
 *  @author Kay Kasemir
 */
public class Throttle
{
    /** Minimum period between events [millis] */
    final private long period;
    
    /** Time of the last event [millis] */
    private long last;
    
    /** Construct throttle that only allows one event in every time slot.
     *  @param seconds_between_messages
     */
    public Throttle(final double seconds_between_messages)
    {
        period = (long)(seconds_between_messages * 1000);
        last = 0;
    }
    
    /** @return Event period [seconds] */
    public double getPeriod()
    {
        return period / 1000.0;
    }
    
    /** @return <code>true</code> if another event is permitted "right now" */
    synchronized public boolean isPermitted()
    {
        final long now = System.currentTimeMillis();
        if ((now - last) < period)
            return false;
        last = now;
        return true;
    }
}
