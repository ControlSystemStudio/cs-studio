/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * The range for points or cells.
 * <p>
 * For the purpose of range calculation, NaNs should be skipped. That is,
 * they should not appear as minimum or maximum.
 * <p>
 * The appropriate Range instance for
 * an unknown range, or for a range of NaN values, is null.
 *
 * @author carcassi
 */
public interface Range {
    /**
     * The minimum value.
     * 
     * @return a value; never null
     */
    public Number getMinimum();
    
    /**
     * The maximum value.
     * 
     * @return a value; never null
     */
    public Number getMaximum();
}
