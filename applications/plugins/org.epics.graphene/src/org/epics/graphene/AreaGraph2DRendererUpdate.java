/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * A set of parameters that can be applied to a <code>AreaGraph2DRenderer</code>
 * to update it's settings.
 * 
 * @author carcassi
 */
public class AreaGraph2DRendererUpdate extends Graph2DRendererUpdate<AreaGraph2DRendererUpdate> {
    
    private Integer focusPixelX;
    private Boolean highlightFocusValue;
    
    public AreaGraph2DRendererUpdate focusPixel(int x) {
        this.focusPixelX = x;
        return this;
    }
    
    public AreaGraph2DRendererUpdate highlightFocusValue(boolean highlightFocusValue) {
        this.highlightFocusValue = highlightFocusValue;
        return this;
    }
    
    public Integer getFocusPixelX() {
        return focusPixelX;
    }

    public Boolean getHighlightFocusValue() {
        return highlightFocusValue;
    }
}
