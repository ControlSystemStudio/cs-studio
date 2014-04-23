/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class Histograms {
    
    /**
     *Creates a new Historgram1D object by calling Histogram1DFromDataset1D.
     * @return Histogram1D
     */
    public static Histogram1D newHistogram() {
        Histogram1DFromDataset1D histogram = new Histogram1DFromDataset1D();
        return histogram;
    }
    
    /**
     *Creates a new Historgram1D object by calling Histogram1DFromDataset1D, then updates it with the given dataset.
     * @param dataset
     * @return Histogram1D
     */
    public static Histogram1D createHistogram(Point1DDataset dataset) {
        Histogram1DFromDataset1D histogram = new Histogram1DFromDataset1D();
        histogram.update(new Histogram1DUpdate().recalculateFrom(dataset));
        return histogram;
    }
}
