/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;

/**
 * A stateful object that computes the value range for a particular graph.
 * It includes the state to compute the range for a particular axis on a particular
 * graph.
 * <p>
 * Objects of this type are mutable, and cannot be shared
 * across different graphs, on different threads.
 *
 * @author carcassi
 */
public interface AxisRangeInstance {
    
    /**
     * Calculates the range for the axis, given the range of the data and
     * the suggested range.
     * 
     * @param dataRange the actual range of the data being displayed
     * @param displayRange the suggested range for the data
     * @return the range to use on the axis
     */
    public Range axisRange(Range dataRange, Range displayRange);
    
    /**
     * The AxisRange of which this object is an instance of.
     * 
     * @return an AxisRange; never null
     */
    public AxisRange getAxisRange();
}
