/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class Histogram1DRendererUpdate {

    private Integer imageHeight;
    private Integer imageWidth;
    
    public Histogram1DRendererUpdate imageHeight(int height) {
        this.imageHeight = height;
        return this;
    }
    
    public Histogram1DRendererUpdate imageWidth(int width) {
        this.imageWidth = width;
        return this;
    }
    
    public Integer getImageHeight() {
        return imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }
    
}
