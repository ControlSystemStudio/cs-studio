/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class Histogram1DUpdate {

    private Dataset1D recalculateFrom;
    
    public Histogram1DUpdate recalculateFrom(Dataset1D dataset) {
        recalculateFrom = dataset;
        return this;
    }

    public Dataset1D getDataset() {
        return recalculateFrom;
    }
    
}
