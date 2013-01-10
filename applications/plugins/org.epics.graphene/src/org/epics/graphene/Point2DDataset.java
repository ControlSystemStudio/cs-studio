/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public interface Point2DDataset {
    
    public ListNumber getXValues();
    
    public ListNumber getYValues();
    
    public double getXMinValue();
    
    public double getXMaxValue();
    
    public double getYMinValue();
    
    public double getYMaxValue();
    
    public int getCount();
    
}
