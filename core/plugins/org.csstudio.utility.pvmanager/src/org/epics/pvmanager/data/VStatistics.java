/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Statistics for double with alarm, timestamp and display information.
 *
 * @author carcassi
 */
public interface VStatistics extends Statistics, Alarm, Time, Display {
}
