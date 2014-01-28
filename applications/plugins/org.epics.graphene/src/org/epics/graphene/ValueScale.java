/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * The scale to be used to create axis references and rescale values.
 *
 * @author carcassi
 */
public interface ValueScale {
    
    /**
     * Scales the value from range A to range B.
     * @param value original value in original range
     * @param minValue original range min
     * @param maxValue original range max
     * @param newMinValue new range min
     * @param newMaxValue new range max
     * @return new value in new range
     */
    double scaleValue(double value, double minValue, double maxValue, double newMinValue, double newMaxValue);
    
    /**
     * Returns the reference axes for a given range
     * @param range the range of values to create a reference axis for
     * @param minRefs minimum references
     * @param maxRegs max references
     * @return reference axes
     */
    ValueAxis references(Range range, int minRefs, int maxRegs);
}
