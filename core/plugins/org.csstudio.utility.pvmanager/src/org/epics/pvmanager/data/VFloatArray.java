/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Float array with alarm, timestamp, display and control information.
 *
 * @author carcassi
 */
public interface VFloatArray extends Array<Double>, Alarm, Time, Display {
    @Override
    float[] getArray();
}
