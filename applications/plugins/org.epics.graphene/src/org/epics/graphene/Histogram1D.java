/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public interface Histogram1D {
    
    public void update(Histogram1DUpdate update);
    
    /**
     * The minimum value that should be displayed in the 
     * x axis of the plot.
     * 
     * @return minimum x axis value
     */
    public double getMinValueRange();
    
    /**
     * The maximum value that should be displayed
     * in the x axis of the plot.
     * 
     * @return maximum x axis value
     */
    public double getMaxValueRange();
    

    /**
     * The maximum value that should be displayed in the
     * y axis of the plot.
     * 
     * @return maximum y axis value
     */
    public int getMaxCountRange();

    /**
     * The minimum value that should be displayed in the
     * y axis of the plot.
     * 
     * @return  minimum y axis value
     */
    public int getMinCountRange();
    
    /**
     * The number of bins.
     * 
     * @return number of bins
     */
    public int getNBins();
    
    /**
     * The value where the corresponding bin starts. The range is up to
     * {@link #getNBins() } + 1 for the right boundary of the last bin.
     * 
     * @param index between 0 and nBins + 1
     * @return the value at the boundary
     */
    public double getBinValueBoundary(int index);
    
    /**
     * The count for the given bin.
     * 
     * @param index between 0 and nBins
     * @return the count for the given bin
     */
    public int getBinCount(int index);
}
