/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class Histograms {
    
    public static Histogram1D newHistogram() {
        Histogram1DFromDataset1D histogram = new Histogram1DFromDataset1D();
        return histogram;
    }
    
    public static Histogram1D createHistogram(Point1DDataset dataset) {
        Histogram1DFromDataset1D histogram = new Histogram1DFromDataset1D();
        histogram.setDataset(dataset);
        return histogram;
    }
}
