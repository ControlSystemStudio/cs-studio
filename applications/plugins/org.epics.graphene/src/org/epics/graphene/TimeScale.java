/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 * The scale to be used to create axis references and rescale time.
 *
 * @author carcassi
 */
public interface TimeScale {
    double scaleNormalizedTime(double value, double newMinValue, double newMaxValue);
    double scaleTimestamp(Timestamp value, TimeInterval timeInterval, double newMinValue, double newMaxValue);
    TimeAxis references(TimeInterval range, int minRefs, int maxRefs);
}
