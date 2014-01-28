/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * The statistics of a given set of numbers.
 * <p>
 * For the purpose of statistics calculation, NaNs should be skipped. That is,
 * they should not appear as minimum, maximum, average or stdDev, and shouldn't
 * even be included in the count. The number of elements (including NaNs)
 * will be available from the number set itself.
 * <p>
 * The appropriate Statistics instance for
 * an unknown set, or for a set of NaN values, is null.
 *
 * @author carcassi
 */
public interface Statistics extends Range {
    
    /**
     * The number of values (excluding NaN) included in the set.
     * 
     * @return the number of values
     */
    public int getCount();
    
    /**
     * The average value.
     * 
     * @return the average value
     */
    public double getAverage();
    
    /**
     * The standard deviation.
     * 
     * @return the standard deviation
     */
    public double getStdDev();
}
