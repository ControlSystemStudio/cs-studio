/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class LineGraphRendererUpdate {

    private Integer imageHeight;
    private Integer imageWidth;
    private InterpolationScheme interpolation;
    
    public LineGraphRendererUpdate imageHeight(int height) {
        this.imageHeight = height;
        return this;
    }
    
    public LineGraphRendererUpdate imageWidth(int width) {
        this.imageWidth = width;
        return this;
    }
    
    public LineGraphRendererUpdate interpolation(InterpolationScheme scheme) {
        this.interpolation = scheme;
        return this;
    }
    
    public Integer getImageHeight() {
        return imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }
    
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }
    
}
