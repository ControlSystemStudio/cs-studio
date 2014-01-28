/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class AxisRanges {
    
    private AxisRanges() {
    }
    
    public static AxisRange absolute(final double min, final double max) {
        final Range axisRange = RangeUtil.range(min, max);
        return new AxisRange() {

            @Override
            public Range axisRange(Range dataRange, Range aggregatedRange) {
                return axisRange;
            }
        };
    }
    
    public static AxisRange relative() {
        return new AxisRange() {

            @Override
            public Range axisRange(Range dataRange, Range aggregatedRange) {
                return dataRange;
            }
        };
    }
    
    
    // TODO horrible name
    // TODO we may need the integrated to "jump", so that the plot
    //      gets stretched fewer times, but its unclear how to do it
    //      in general
    public static AxisRange integrated() {
        return new AxisRange() {

            @Override
            public Range axisRange(Range dataRange, Range aggregatedRange) {
                return aggregatedRange;
            }
        };
    }
}
