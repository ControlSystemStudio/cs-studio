/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Short array with alarm, timestamp, display and control information.
 *
 * @author carcassi
 */
public interface VShortArray extends Array<Integer>, Alarm, Time, Display {
    @Override
    short[] getArray();
}
