/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.array.ListNumber;

/**
 * A dataset consisting on a set of 1D points.
 * <p>
 * It represents a list of ordered values, and their statistical information.
 * The order may not be meaningful, but can be used to identify the points.
 *
 * @author carcassi
 */
public interface Point1DDataset {
    
    /**
     * The values of the points.
     * <p>
     * If the dataset is empty, it returns an empty list.
     * 
     * @return the values; never null
     */
    public ListNumber getValues();
    
    /**
     * The statistical information of the values.
     * <p>
     * If the dataset is empty, or if it contains only NaN values, it returns null.
     * 
     * @return statistical information; null if no actual values in the dataset
     */
    public Statistics getStatistics();

    /**
     * The number of points in the dataset.
     * <p>
     * This number matches the size of the list returned by {@link #getValues() }.
     * 
     * @return the number of values in this dataset
     */
    public int getCount();
}
