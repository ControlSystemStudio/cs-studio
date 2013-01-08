/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import org.epics.util.array.ListNumber;

/**
 * Numeric array with alarm, timestamp, display and control information.
 * <p>
 * This class allows to use any numeric array (i.e. {@link VIntArray} or
 * {@link VDoubleArray}) through the same interface.
 *
 * @author carcassi
 */
public interface VNumberArray extends Array, Alarm, Time, Display, VType {
    @Override
    ListNumber getData();
}
