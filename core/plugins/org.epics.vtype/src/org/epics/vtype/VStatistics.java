/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

/**
 * Statistics for double with alarm, timestamp and display information.
 *
 * @author carcassi
 */
public interface VStatistics extends Statistics, Alarm, Time, Display, VType {
}
