/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public interface Histogram1D extends Cell1DDataset {
    
    public void update(Histogram1DUpdate update);
}
