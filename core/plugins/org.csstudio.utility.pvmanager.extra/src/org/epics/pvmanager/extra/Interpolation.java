/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

/**
 * Defines a way to interpolate some scattered points to an equally distanced
 * grid.
 * 
 * @author carcassi
 */
public interface Interpolation {
    
    /**
     * Returns a set of interpolated y, equally spaced.
     * 
     * @param x an array of position value, assumed to be monotonically increasing
     * @param y an array of values, assumed to be the same length of x
     * @param nSamples the number of samples to be returned
     * @return a new array with nSamples interpolated values
     */
    public double[] interpolate(double[] x, double[] y, int nSamples);
}
