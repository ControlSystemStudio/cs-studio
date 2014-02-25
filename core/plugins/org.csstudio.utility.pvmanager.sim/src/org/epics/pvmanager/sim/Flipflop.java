/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import org.epics.vtype.VBoolean;
import org.epics.vtype.VString;
import static org.epics.vtype.ValueFactory.*;

/**
 * Function to simulate a boolean signal that turns on and off.
 *
 * @author carcassi
 */
public class Flipflop extends SimFunction<VBoolean> {

    private boolean value = true;

    /**
     * Creates a flipflop that changes every 500 ms.
     */
    public Flipflop() {
        this(0.5);
    }

    /**
     * Creates a signal that turns on and off every interval.
     *
     * @param interval interval between samples in seconds
     */
    public Flipflop(Double interval) {
        super(interval, VBoolean.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
    }

    @Override
    VBoolean nextValue() {
        value = !value;
        return newVBoolean(value, alarmNone(), timeNow());
    }
}
