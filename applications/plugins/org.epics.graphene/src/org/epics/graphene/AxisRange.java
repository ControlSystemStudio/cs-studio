/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * Determines the value range to be displayed on a given axis. It represents
 * the abstract option, without any state for the actual calculation.
 * <p>
 * Objects of this type are immutable, and can be shared
 * across different graphs, on different threads.
 *
 * @author carcassi
 */
public interface AxisRange {
    
    /**
     * Creates an instance of the AxisRange to calculate the range for
     * a particular graph. The instance will contain all the state
     * relative to the particular graph. It is not immutable, threadsafe
     * and should not be shared across graphs.
     * 
     * @return a new instance
     */
    public AxisRangeInstance createInstance();
}
