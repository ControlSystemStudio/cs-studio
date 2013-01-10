/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.util.List;

/**
 *
 * @author carcassi
 */
public interface Statistics2DDataset {
    
    public List<Statistics> getXValues();
    
    public List<Statistics> getYValues();
    
    public double getXMinValue();
    
    public double getXMaxValue();
    
    public double getYMinValue();
    
    public double getYMaxValue();
    
    public int getCount();
    
}
