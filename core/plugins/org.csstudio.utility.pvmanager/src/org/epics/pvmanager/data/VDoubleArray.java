/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

/**
 * Double array with alarm, timestamp, display and control information.
 *
 * @author carcassi
 */
public interface VDoubleArray extends Array<Double>, Alarm, Time, Display {
    @Override
    double[] getArray();
}
