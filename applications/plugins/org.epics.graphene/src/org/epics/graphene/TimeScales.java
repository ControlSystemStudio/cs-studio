/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class TimeScales {
    public static TimeScale linearAbsoluteScale() {
        return new LinearAbsoluteTimeScale();
    }
}
