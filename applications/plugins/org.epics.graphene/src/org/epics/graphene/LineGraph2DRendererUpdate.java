/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class LineGraph2DRendererUpdate extends Graph2DRendererUpdate<LineGraph2DRendererUpdate> {

    private InterpolationScheme interpolation;
    private ReductionScheme reduction;
    private Integer focusPixelX;
    private Boolean highlightFocusValue;
    
    /**
     *Sets this object's interpolation to the given scheme.
     * @param scheme can not be null, must be a supported scheme. Supported schemes:NEAREST_NEIGHBOUR,LINEAR,CUBIC.
     * @return this
     */
    public LineGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Interpolation scheme can't be null");
        }
        if (!LineGraph2DRenderer.supportedInterpolationScheme.contains(scheme)) {
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
    public LineGraph2DRendererUpdate dataReduction(ReductionScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Data reduction scheme can't be null");
        }
        if (!LineGraph2DRenderer.supportedReductionScheme.contains(scheme)) {
            throw new IllegalArgumentException("Data reduction " + scheme + " is not supported");
        }
        this.reduction = scheme;
        return this;
    }
    
    /**
     *Sets the current value of the focused pixel to x.
     * @param x value of focused pixel.
     * @return this
     */
    public LineGraph2DRendererUpdate focusPixel(int x) {
        this.focusPixelX = x;
        return this;
    }
    
    /**
     *Sets the state of highlightFocusValue.
     * <ul>
     *  <li>True - highlight and show the value the mouse is on.</li>
     *  <li>False - Avoid calculation involved with finding the highlighted value/ do not highlight the value.</li>
     * </ul>
     * @param highlightFocusValue
     * @return this
     */
    public LineGraph2DRendererUpdate highlightFocusValue(boolean highlightFocusValue) {
        this.highlightFocusValue = highlightFocusValue;
        return this;
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

    /**
     *Current x-value of the focused pixel
     * @return the current x-value of the focused pixel.
     */
    public Integer getFocusPixelX() {
        return focusPixelX;
    }

    /**
     *Current state of highlightFocusValue.
     * <ul>
     *  <li>True - highlight and show the value the mouse is on.</li>
     *  <li>False - Avoid calculation involved with finding the highlighted value/ do not highlight the value.</li>
     * </ul>
     * @return true or false
     */
    public Boolean getHighlightFocusValue() {
        return highlightFocusValue;
    }
    
}
