/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class LineGraph2DRendererUpdate extends Graph2DRendererUpdate<LineGraph2DRendererUpdate> {

    private InterpolationScheme interpolation;
    private ReductionScheme reduction;
    
    public LineGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Interpolation scheme chan't be null");
        }
        if (!LineGraph2DRenderer.supportedInterpolationScheme.contains(scheme)) {
            throw new IllegalArgumentException("Interpolation " + scheme + " is not supported");
        }
        this.interpolation = scheme;
        return this;
    }
    
    public LineGraph2DRendererUpdate dataReduction(ReductionScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Data reduction scheme chan't be null");
        }
        if (!LineGraph2DRenderer.supportedReductionScheme.contains(scheme)) {
            throw new IllegalArgumentException("Data reduction " + scheme + " is not supported");
        }
        this.reduction = scheme;
        return this;
    }
    
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }

    public ReductionScheme getDataReduction() {
        return reduction;
    }
    
}
