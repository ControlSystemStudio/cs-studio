/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.util.List;

/**
 *
 * @author asbarber
 * @author jkfeng
 * @author sjdallst
 */
public class MultilineGraph2DRendererUpdate extends Graph2DRendererUpdate<MultilineGraph2DRendererUpdate>{
    private ValueColorScheme valueColorScheme;
    private ReductionScheme reduction;
    private InterpolationScheme interpolation;
    
    /**
     *Set this object's ColorScheme "valueColorScheme" to the given ColorScheme.
     * To be used in combination with the MultilineGraph2DRenderer class and update function.
     * 
     * TODO: Color scheme for the lines should be a LabelColorScheme
     * 
     * @param scheme Possible schemes:GRAY_SCALE, JET, HOT, COOL, SPRING, BONE, COPPER, PINK
     * @return MultilineGraph2DRendererUpdate
     */
    public MultilineGraph2DRendererUpdate valueColorScheme(ValueColorScheme scheme) {
        this.valueColorScheme = scheme;
        return self();
    }
    
    /**
     *Sets this object's interpolation to the given scheme.
     * @param scheme can not be null, must be a supported scheme. Supported schemes:NEAREST_NEIGHBOUR,LINEAR,CUBIC.
     * @return this
     */
    public MultilineGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Interpolation scheme can't be null");
        }
        if (!MultilineGraph2DRenderer.supportedInterpolationScheme.contains(scheme)) {
            throw new IllegalArgumentException("Interpolation " + scheme + " is not supported");
        }
        this.interpolation = scheme;
        return this;
    }
    
    /**
     *Sets this object's data reduction scheme to the given scheme.
     * @param scheme can not be null, must be a supported scheme. Supported schemes:FIRST_MAX_MIN_LAST,NONE
     * @return this
     */
    public MultilineGraph2DRendererUpdate dataReduction(ReductionScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Data reduction scheme can't be null");
        }
        if (!MultilineGraph2DRenderer.supportedReductionScheme.contains(scheme)) {
            throw new IllegalArgumentException("Data reduction " + scheme + " is not supported");
        }
        this.reduction = scheme;
        return this;
    }
    
    /**
     *The current value of this object's ColorScheme variable. Can be null. 
     * @return ColorScheme
     */
    public ValueColorScheme getValueColorScheme() {
        return valueColorScheme;
    }
    
    /**
     *Current interpolation scheme
     * @return the current interpolation scheme.
     */
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }

    /**
     *Current reduction scheme
     * @return the current reduction scheme.
     */
    public ReductionScheme getDataReduction() {
        return reduction;
    }
}
