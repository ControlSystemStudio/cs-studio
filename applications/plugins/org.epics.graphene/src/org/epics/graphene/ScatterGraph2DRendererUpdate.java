/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class ScatterGraph2DRendererUpdate extends Graph2DRendererUpdate<ScatterGraph2DRendererUpdate> {

    private InterpolationScheme interpolation;
    
    public ScatterGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Interpolation scheme chan't be null");
        }
        if (!ScatterGraph2DRenderer.supportedInterpolationScheme.contains(scheme)) {
            throw new IllegalArgumentException("Interpolation " + scheme + " is not supported");
        }
        this.interpolation = scheme;
        return this;
    }
    
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }
    
}
