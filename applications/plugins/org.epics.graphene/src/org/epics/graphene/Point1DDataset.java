/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.CollectionNumber;

/**
 *
 * @author carcassi
 */
public interface Point1DDataset {
    
    public CollectionNumber getValues();
    
    public Number getMinValue();
    
    public Number getMaxValue();
    
    public void update(Point1DDatasetUpdate update);
}
