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
    
    public LineGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
        this.interpolation = scheme;
        return this;
    }
    
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }
    
}
