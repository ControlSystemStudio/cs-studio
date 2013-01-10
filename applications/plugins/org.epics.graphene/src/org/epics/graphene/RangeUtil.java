/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class RangeUtil {
    
    public static double[] createBins(double min, double max, int nBins) {
        double increment = (max - min) / nBins;
        double[] boundary = new double[nBins+1];
        boundary[0] = min;
        for (int i = 1; i < boundary.length; i++) {
            boundary[i] = min + ( (max - min) * i / nBins );
        }
        return boundary;
    }
}
