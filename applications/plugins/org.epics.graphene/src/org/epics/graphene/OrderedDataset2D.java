/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public interface OrderedDataset2D {
    
    public double getXValue(int index);
    
    public double getYValue(int index);
    
    public double getXMinValue();
    
    public double getXMaxValue();
    
    public double getYMinValue();
    
    public double getYMaxValue();
    
    public int getCount();
    
}
