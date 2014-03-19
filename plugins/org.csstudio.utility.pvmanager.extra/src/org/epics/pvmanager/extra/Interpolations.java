/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import static java.lang.Math.*;

/**
 *
 * @author carcassi
 */
public class Interpolations {
    
    private static Interpolation noInterpolation = new Interpolation() {

            @Override
            public double[] interpolate(double[] x, double[] y, int nSamples) {
                double[] result = new double[nSamples];
                int currentData = 0;
                double size = x[x.length - 1] - x[0];
                double step = size / (nSamples - 1);
                double currentX = x[0];
                for (int i = 0; i < nSamples; i++) {
                    // Get to the closest value
                    while (currentData != x.length - 1 && abs(x[currentData] - currentX) >= abs(x[currentData + 1] - currentX)) {
                        currentData++;
                    }
                    result[i] = y[currentData];
                    currentX += step;
                }
                return result;
            }
        };

    public static Interpolation noInterpolation() {
        return noInterpolation;
    }
}
