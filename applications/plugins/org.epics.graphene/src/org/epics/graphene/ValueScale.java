/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;

/**
 * The scale to be used to create axis references and rescale values.
 *
 * @author carcassi
 */
public interface ValueScale {
    
    /**
     * Scales the actual value to the scale value.
     * 
     * @param value actual value to be scaled
     * @param minValue actual range min
     * @param maxValue actual range max
     * @param newMinValue scale range min
     * @param newMaxValue scale range max
     * @return new value in the scale
     */
    double scaleValue(double value, double minValue, double maxValue, double newMinValue, double newMaxValue);
    
    /**
     * Scales the scale value to the actual value.
     * 
     * @param scaleValue scale value to be scaled
     * @param minValue actual range min
     * @param maxValue actual range max
     * @param newMinValue scale range min
     * @param newMaxValue scale range max
     * @return new value in the actual range
     */
    double invScaleValue(double scaleValue, double minValue, double maxValue, double newMinValue, double newMaxValue);
    
    /**
     * Returns the reference axes for a given range
     * @param range the range of values to create a reference axis for
     * @param minRefs minimum references
     * @param maxRegs max references
     * @return reference axes
     */
    ValueAxis references(Range range, int minRefs, int maxRegs);
}
