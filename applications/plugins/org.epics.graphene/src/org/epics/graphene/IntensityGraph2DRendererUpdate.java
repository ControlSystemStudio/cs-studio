/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class IntensityGraph2DRendererUpdate {

    private Integer imageHeight;
    private Integer imageWidth;
    private InterpolationScheme interpolation;
    
    private Double startX;
    private Double endX;
    private Double startY;
    private Double endY;
    
    private Boolean rangeFromDataset;
    
    public IntensityGraph2DRendererUpdate startX(Double startX) {
        this.startX = startX;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate startY(Double startY) {
        this.startY = startY;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate endX(Double endX) {
        this.endX = endX;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate endY(Double endY) {
        this.endY = endY;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate rangeFromDataset(boolean rangeFromDataset) {
        this.rangeFromDataset = rangeFromDataset;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate imageHeight(int height) {
        this.imageHeight = height;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate imageWidth(int width) {
        this.imageWidth = width;
        return this;
    }
    
    public IntensityGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
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

    public Double getStartX() {
        return startX;
    }

    public Double getStartY() {
        return startY;
    }

    public Double getEndX() {
        return endX;
    }

    public Double getEndY() {
        return endY;
    }

    public Boolean isRangeFromDataset() {
        return rangeFromDataset;
    }
    
}
