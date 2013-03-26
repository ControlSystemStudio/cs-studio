/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
