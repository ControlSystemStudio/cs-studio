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
public class TimeAxisRanges {
    
    private TimeAxisRanges() {
    }
    
    public static TimeAxisRange absolute(final TimeInterval timeInterval) {
        return new TimeAxisRange() {

            @Override
            public TimeInterval axisRange(TimeInterval dataRange, TimeInterval aggregatedRange) {
                return timeInterval;
            }
        };
    }
    
    public static TimeAxisRange relative() {
        return new TimeAxisRange() {

            @Override
            public TimeInterval axisRange(TimeInterval dataRange, TimeInterval aggregatedRange) {
                return dataRange;
            }
        };
    }
    
    
    // TODO horrible name
    // TODO we may need the integrated to "jump", so that the plot
    //      gets stretched fewer times, but its unclear how to do it
    //      in general
    public static TimeAxisRange integrated() {
        return new TimeAxisRange() {

            @Override
            public TimeInterval axisRange(TimeInterval dataRange, TimeInterval aggregatedRange) {
                return aggregatedRange;
            }
        };
    }
}
