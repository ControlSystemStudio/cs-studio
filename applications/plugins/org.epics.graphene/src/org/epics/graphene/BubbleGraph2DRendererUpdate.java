/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 * A set of parameters that can be applied to a <code>BubbleGraph2DRendererUpdate</code>
 * to update its settings.
 * 
 * <p>
 * Only the parameters that are set in the update get applied to the renderer.
 * Parameters unique to the bubble graph that can be changed are:
 * <ul>
 *   <li>focusPixel - TODO</li>
 *   <li>highlightFocusValue - TODO</li>
 * </ul>
 * 
 * @author carcassi
 */
public class BubbleGraph2DRendererUpdate extends Graph2DRendererUpdate<BubbleGraph2DRendererUpdate> {
    
    private Integer focusPixelX;
    private Integer focusPixelY;
    private Boolean highlightFocusValue;
    
    /**
     * Changes the current position of the pixel with focus.
     * 
     * @param x horizontal position of the pixel
     * @param y vertical position of the pixel
     * @return this
     */
    public BubbleGraph2DRendererUpdate focusPixel(int x, int y) {
        this.focusPixelX = x;
        this.focusPixelY = y;
        return this;
    }
    
    /**
     * Sets the state of highlightFocusValue.
     * <ul>
     *  <li>True - highlight and show the value the mouse is on.</li>
     *  <li>False - Avoid calculation involved with finding the highlighted value/ do not highlight the value.</li>
     * </ul>
     * 
     * @param highlightFocusValue true if value should be highlighted
     * @return this
     */
    public BubbleGraph2DRendererUpdate highlightFocusValue(boolean highlightFocusValue) {
        this.highlightFocusValue = highlightFocusValue;
        return this;
    }
    
    /**
     * New horizontal position of the focused pixel.
     * 
     * @return new horizontal position or null
     */
    public Integer getFocusPixelX() {
        return focusPixelX;
    }
    
    /**
     * New vertical position of the focused pixel.
     * 
     * @return new vertical position or null
     */
    public Integer getFocusPixelY() {
        return focusPixelY;
    }

    /**
     * Whether to highlight the focused value.
     * 
     * @return the new setting or null
     */
    public Boolean getHighlightFocusValue() {
        return highlightFocusValue;
    }
}
