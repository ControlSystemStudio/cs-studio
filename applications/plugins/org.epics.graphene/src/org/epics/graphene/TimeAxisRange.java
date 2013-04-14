/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.time.TimeInterval;

/**
 *
 * @author carcassi
 */
public interface TimeAxisRange {
    public TimeInterval axisRange(TimeInterval dataRange, TimeInterval aggregatedRange);
}
