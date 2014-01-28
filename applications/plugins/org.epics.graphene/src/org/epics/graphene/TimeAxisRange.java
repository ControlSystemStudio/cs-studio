/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
