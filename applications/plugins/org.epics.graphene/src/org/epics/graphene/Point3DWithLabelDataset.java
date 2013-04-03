/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.util.List;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public interface Point3DWithLabelDataset {
    
    public ListNumber getXValues();
    
    public ListNumber getYValues();
    
    public ListNumber getZValues();
    
    public List<String> getLabels();
    
    public Statistics getXStatistics();
    
    public Statistics getYStatistics();
    
    public Statistics getZStatistics();
    
    public int getCount();
    
}
