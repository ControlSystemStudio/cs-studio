/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.util.Random;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.ValueFactory;

/**
 * Function to simulate a signal that generates Strings.
 *
 * @author carcassi
 */
public class Strings extends SimFunction<VString> {

    private StringBuffer buffer = new StringBuffer();

    /**
     * Creates a signal uniformly distributed between -5.0 and 5.0, updating
     * every 100ms (10Hz).
     */
    public Strings() {
        this(0.1);
    }

    /**
     * Creates a signal uniformly distributed between min and max, updating
     * every interval seconds.
     *
     * @param min minimum value
     * @param max maximum value
     * @param interval interval between samples in seconds
     */
    public Strings(Double interval) {
        super(interval, VString.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
    }

    @Override
    VString nextValue() {
        return ValueFactory.newVString(nextString(), AlarmSeverity.NONE, AlarmStatus.NONE, lastTime, null);
    }

    String nextString() {
        if (buffer.length() > 10) {
            buffer.setLength(0);
        }
        buffer.append("A");
        return buffer.toString();
    }
}
