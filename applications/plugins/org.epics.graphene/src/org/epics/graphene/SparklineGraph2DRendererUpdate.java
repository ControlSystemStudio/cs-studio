/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;

/**
 * A set of parameters that can be applied to a <code>SparklineGraph2DRenderer</code>
 * to update it's settings.
 * 
 * <p>
 * Only the parameters that are set in the update get applied to the renderer.
 * Parameters unique to the sparkline that can be changed are:
 * <ul>
 *      <li>Colors of the circles drawn at the important data points of a sparkline graph</li>
 *      <li>Size of the circles drawn at the important data points</li>
 *      <li>Whether to draw circles at the important data points</li>
 *      <li>Interpolation scheme used in drawing the sparkline line</li>
 *      <li>Aspect ratio to be maintained in the graph area</li>
 * </ul>
 * 
 * @author asbarber
 * @author jkfeng
 * @author sjdallst
 */
public class SparklineGraph2DRendererUpdate extends Graph2DRendererUpdate<SparklineGraph2DRendererUpdate>{
    
    private Color   minValueColor, maxValueColor, lastValueColor;               //Circle colors
    private Integer circleDiameter;                                             //Circle size
    private Boolean drawCircles;                                                //Option to not draw circles
    
    private InterpolationScheme interpolation;                                  //Data interpolation
    private Double aspectRatio;                                                 //x:y ratio
    
    /**
     * Sets the color of the circle drawn at the minimum value.
     * @param color color for the minimum value circle
     * @return this
     */    
    public SparklineGraph2DRendererUpdate minValueColor(Color color){
        minValueColor = color;
        return self();
    }
    
    /**
     * Sets the color of the circle drawn at the maximum value.
     * @param color color for the maximum value circle
     * @return this
     */    
    public SparklineGraph2DRendererUpdate maxValueColor(Color color){
        maxValueColor = color;
        return self();
    }
    
    /**
     * Sets the color of the circle drawn at the last value.
     * @param color color for the last value circle
     * @return this
     */
    public SparklineGraph2DRendererUpdate lastValueColor(Color color){
        lastValueColor = color;
        return self();
    }
    
    /**
     * Sets the diameter of the circles drawn.
     * @param diameter size of circle diameter in pixels
     * @return this
     */
    public SparklineGraph2DRendererUpdate circleDiameter(int diameter){
        circleDiameter = diameter;
        return self();
    }
    
    /**
     * Sets whether circles are drawn on the line at important values.
     * @param decision whether circles are drawn
     * @return this
     */
    public SparklineGraph2DRendererUpdate drawCircles(boolean decision){
        this.drawCircles = decision;
        return self();
    }
    
    /**
     * Sets the interpolation scheme of the line.
     * @param scheme new interpolation scheme
     * @return this
     */
    public SparklineGraph2DRendererUpdate interpolation(InterpolationScheme scheme) {
        if (scheme == null) {
            throw new NullPointerException("Interpolation scheme chan't be null");
        }
        if (!LineTimeGraph2DRenderer.supportedInterpolationScheme.contains(scheme)) {
            throw new IllegalArgumentException("Interpolation " + scheme + " is not supported");
        }
        this.interpolation = scheme;
        return self();
    }    
    
    /**
     * Sets the width to height aspect ratio for the graph area.
     * @param ratio ratio between width and height that should be maintained 
     * @return this
     */
    public SparklineGraph2DRendererUpdate aspectRatio(double ratio){
        aspectRatio = ratio;
        return self();
    }
    
    /**
     * Gets the color of the circle drawn for the minimum value.
     * @return color of the circle at the minimum
     */
    public Color getMinValueColor(){
        return minValueColor;
    }
    
    /**
     * Gets the color of the circle drawn for the maximum value.
     * @return color of the circle at the maximum
     */
    public Color getMaxValueColor(){
        return maxValueColor;
    }
    
    /**
     * Gets the color of the circle drawn for the last value.
     * @return color of the circle at the last index
     */
    public Color getLastValueColor(){
        return lastValueColor;
    }
    
    /**
     * Gets the diameter of the circle.
     * @return size of the diameter of the circles drawn in pixels
     */
    public Integer getCircleDiameter(){
        return circleDiameter;
    }
    
    /**
     * Gets the decision for whether circles are drawn.
     * @return whether circles are drawn
     */
    public Boolean getDrawCircles(){
        return drawCircles;
    }
    
    /**
     * Gets the interpolation scheme of the line.
     * @return interpolation scheme for the line
     */
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }    
    
    /**
     * Gets the width to height aspect ratio of the graph area
     * @return width to height aspect ratio
     */
    public Double getAspectRatio(){
        return aspectRatio;
    }
}