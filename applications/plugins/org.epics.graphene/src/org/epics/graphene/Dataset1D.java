/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public interface Dataset1D {
    
    public IteratorDouble getValues();
    
    public double getMinValue();
    
    public double getMaxValue();
    
    public void update(Dataset1DUpdate update);
}
