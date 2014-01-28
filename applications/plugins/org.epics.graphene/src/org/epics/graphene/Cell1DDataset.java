/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.array.ListNumber;

/**
 * Dataset consisting of a value for each cell of a 1D grid.
 * <p>
 * It represents values distributed on a 1D grid and their statistical information.
 *
 * @author carcassi
 */
public interface Cell1DDataset {
    
    /**
     * Returns the value at the given coordinates.
     * 
     * @throws ArrayIndexOutOfBoundsException if x $lt; 0 or x &gt;= {@link #getXCount() }
     * @param x the x coordinate
     * @return the value
     */
    public double getValue(int x);
    
    /**
     * Returns the statistics of all values at all cells.
     * <p>
     * If the the grid has zero cells, or if all values are NaN, it returns null.
     * 
     * @return statistical information; null if no actual value is defined on the grid
     */
    public Statistics getStatistics();
    
    /**
     * Returns the boundaries of the cells along x.
     * <p>
     * The number of elements matches {@link #getXCount()} + 1. If
     * no cells are defined, it will return null. The boundaries are
     * ordered from the smallest to the greatest.
     * 
     * @return the boundaries of the cells; null if 0 cells are defined
     */
    public ListNumber getXBoundaries();
    
    /**
     * The range along x.
     * <p>
     * Effectively the first and last elements of {@link #getXBoundaries()}.
     * If no cells are defined, it will return null.
     * 
     * @return the range along x; null if 0 cells are defined
     */
    public Range getXRange();
    
    /**
     * The number of cells defined along the x direction.
     * 
     * @return the number of cells along x
     */
    public int getXCount();
}
