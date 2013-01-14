/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

/**
 * Statistics for double with alarm, timestamp and display information.
 *
 * @author carcassi
 */
public interface VStatistics extends Statistics, Alarm, Time, Display, VType {
}
