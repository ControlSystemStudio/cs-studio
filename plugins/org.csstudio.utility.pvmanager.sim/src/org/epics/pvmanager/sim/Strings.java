/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import org.epics.vtype.VString;
import static org.epics.vtype.ValueFactory.*;

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
        return newVString(nextString(), alarmNone(), timeNow());
    }

    String nextString() {
        if (buffer.length() > 10) {
            buffer.setLength(0);
        }
        buffer.append("A");
        return buffer.toString();
    }
}
