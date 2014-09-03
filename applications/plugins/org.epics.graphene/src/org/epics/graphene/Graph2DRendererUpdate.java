/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author carcassi
 */
public class Graph2DRendererUpdate<T extends Graph2DRendererUpdate<T>> {
    
    private Integer imageHeight;
    private Integer imageWidth;
    private AxisRange xAxisRange;
    private AxisRange yAxisRange;
    private ValueScale xValueScale;
    private ValueScale yValueScale;
    
    private Color backgroundColor;
    private Color labelColor;
    private Color referenceLineColor;
    private Font  labelFont;
    
    private Integer bottomMargin;
    private Integer topMargin;
    private Integer leftMargin;
    private Integer rightMargin;
    
    private Integer bottomAreaMargin;
    private Integer topAreaMargin;
    private Integer leftAreaMargin;
    private Integer rightAreaMargin;
    
    private Integer xLabelMargin;
    private Integer yLabelMargin;
    
    /**
     * Gets the update. Casts this appropriately.
     * For all subclasses, casts the objects as the subclass object (not Graph2DRendererUpdate).
     * @return this, casted as the appropriate object type
     */
    protected T self() {
        return (T) this;
    }
    
    /**
     * Updates the parameter for the height of the image that is rendered.
     * The height must be greater than zero.
     * @param height size of image height in pixels
     * @return this
     */
    public T imageHeight(int height) {
        if (height <= 0){
            throw new IllegalArgumentException("Image height must be a postive non-zero integer.");
        }
        
        this.imageHeight = height;
        return self();
    }
    
    /**
     * Updates the parameter for the width of the image that is rendered.
     * The width must be greater than zero.
     * @param width size of image width in pixels
     * @return this
     */   
    public T imageWidth(int width) {
        if (width <= 0){
            throw new IllegalArgumentException("Image width must be a postive non-zero integer.");
        }
        
        this.imageWidth = width;
        return self();
    }
    
    /**
     * Updates the parameter for the range (max - min) of the values on the x-axis.
     * @param xAxisRange specifies the x-axis range (composed of the data range and the aggregated data range)
     * @return this
     */    
    public T xAxisRange(AxisRange xAxisRange) {
        this.xAxisRange = xAxisRange;
        return self();
    }
    
     /**
     * Updates the parameter for the range (max - min) of the values on the y-axis.
     * @param yAxisRange specifies the y-axis range (composed of the data range and the aggregated data range)
     * @return this
     */      
    public T yAxisRange(AxisRange yAxisRange) {
        this.yAxisRange = yAxisRange;
        return self();
    }
    
    /**
     * Updates the parameter for the scaling on the x-axis.
     * This will enable actual data to be scaled to a specified min and max.
     * @param xValueScale tool used for changing actual data to scaled data on the x-axis.
     * @return this
     */
    public T xValueScale(ValueScale xValueScale) {
        this.xValueScale = xValueScale;
        return self();
    }
    
    /**
     * Updates the parameter for the scaling on the y-axis.
     * This will enable actual data to be scaled to a specified min and max.
     * @param yValueScale tool used for changing actual data to scaled data on the y-axis.
     * @return this
     */
    public T yValueScale(ValueScale yValueScale) {
        this.yValueScale = yValueScale;
        return self();
    }
    
    /**
     * Updates the parameter for the color of the image background.
     * @param backgroundColor color of image background
     * @return this
     */
    public T backgroundColor(Color backgroundColor){
        this.backgroundColor = backgroundColor;
        return self();
    }
    
    /**
     * Updates the parameter for the color of the labels for axes.
     * @param labelColor color of labels
     * @return this
     */
    public T labelColor(Color labelColor){
        this.labelColor = labelColor;
        return self();        
    }
    
    /**
     * Updates the parameter for the color of the reference lines on the background.
     * @param referenceLineColor color of reference lines
     * @return this
     */
    public T referenceLineColor(Color referenceLineColor){
        this.referenceLineColor = referenceLineColor;
        return self();        
    }
    
    /**
     * Updates the parameter for the font of the labels for axes.
     * @param labelFont font used for labels
     * @return this
     */
    public T labelFont(Font labelFont){
        this.labelFont = labelFont;
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the bottom.
     * This margin splits the bottom of the image from the bottom of the graph area.
     * The margin size must be a non-negative integer.
     * @param bottomMargin margin at bottom of image in pixels
     * @return this
     */
    public T bottomMargin(int bottomMargin){
        if (bottomMargin < 0){
            throw new IllegalArgumentException("The bottom margin must be a non-negative integer.");
        }
        
        this.bottomMargin = bottomMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the top.
     * This margin splits the top of the image from the top of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param topMargin margin at top of image in pixels
     * @return this
     */
    public T topMargin(int topMargin){
        if (topMargin < 0){
            throw new IllegalArgumentException("The top margin must be a non-negative integer.");
        }
        
        this.topMargin = topMargin;        
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the left.
     * This margin splits the left of the image from the left of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param leftMargin margin at top of image in pixels
     * @return this
     */
    public T leftMargin(int leftMargin){
        if (leftMargin < 0){
            throw new IllegalArgumentException("The left margin must be a non-negative integer.");
        }
        
        this.leftMargin = leftMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the right.
     * This margin splits the right of the image from the right of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param rightMargin margin at right of image in pixels
     * @return this
     */
    public T rightMargin(int rightMargin){
        if (rightMargin < 0){
            throw new IllegalArgumentException("The right margin must be a non-negative integer.");
        }
        
        this.rightMargin = rightMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for all margins (top, bottom, left, right).
     * All margins are set to the same size.
     * This margin region splits the edge of the image from the edge of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param marginSize margin on all sides of image in pixels
     * @return this
     */    
    public T allMargins(int marginSize){
        if (marginSize < 0){
            throw new IllegalArgumentException("The margin size must be a non-negative integer.");
        }
       
        this.bottomMargin = marginSize;
        this.topMargin = marginSize;
        this.leftMargin = marginSize;
        this.rightMargin = marginSize;
        return self();
    }
    
    /**
     * Updates the parameter for margin at the bottom of the graph area.
     * This margin splits the bottom of the graph area (where the line is drawn) from the bottom of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param bottomAreaMargin margin at bottom of the graph area in pixels
     * @return this
     */   
    public T bottomAreaMargin(int bottomAreaMargin){
        if (bottomAreaMargin < 0){
            throw new IllegalArgumentException("The bottomArea margin must be a non-negative integer.");
        }
        
        this.bottomAreaMargin = bottomAreaMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the top of the graph area.
     * This margin splits the top of the graph area (where the line is drawn) from the top of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param topAreaMargin margin at top of the graph area in pixels
     * @return this
     */      
    public T topAreaMargin(int topAreaMargin){
        if (topAreaMargin < 0){
            throw new IllegalArgumentException("The topArea margin must be a non-negative integer.");
        }
        
        this.topAreaMargin = topAreaMargin;        
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the left of the graph area.
     * This margin splits the left of the graph area (where the line is drawn) from the left of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param leftAreaMargin margin at left of the graph area in pixels
     * @return this
     */      
    public T leftAreaMargin(int leftAreaMargin){
        if (leftAreaMargin < 0){
            throw new IllegalArgumentException("The leftArea margin must be a non-negative integer.");
        }
        
        this.leftAreaMargin = leftAreaMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for margin at the right of the graph area.
     * This margin splits the right of the graph area (where the line is drawn) from the right of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param rightAreaMargin margin at right of the graph area in pixels
     * @return this
     */    
    public T rightAreaMargin(int rightAreaMargin){
        if (rightAreaMargin < 0){
            throw new IllegalArgumentException("The rightArea margin must be a non-negative integer.");
        }
        
        this.rightAreaMargin = rightAreaMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for all margins (top, bottom, left, right) of the graph area.
     * All margins are set to the same size.
     * This margin splits the all edges of the graph area (where the line is drawn) from all edges of the label/axis area.
     * The margin size must be a non-negative integer.
     * @param areaMarginSize margin on all sides of graph area in pixels
     * @return this
     */      
    public T allAreaMargins(int areaMarginSize){
        if (areaMarginSize < 0){
            throw new IllegalArgumentException("The area margin size must be a non-negative integer.");
        }
        
        this.topAreaMargin = areaMarginSize;
        this.bottomAreaMargin = areaMarginSize;
        this.leftAreaMargin = areaMarginSize;
        this.rightAreaMargin = areaMarginSize;
        return self();
    }
    
    /**
     * Updates the parameter for the margin for the labels on the x-axis.
     * This margin splits the area for the x-axis labels from the graph area.
     * The margin size must be a non-negative integer.
     * @param xLabelMargin margin for the x-axis labels in pixels
     * @return this
     */
    public T xLabelMargin(int xLabelMargin){
        if (xLabelMargin < 0){
            throw new IllegalArgumentException("The xLabel margin must be a non-negative integer.");
        }
        
        this.xLabelMargin = xLabelMargin;
        return self();        
    }
    
    /**
     * Updates the parameter for the margin for the labels on the y-axis.
     * This margin splits the area for the y-axis labels from the graph area.
     * The margin size must be a non-negative integer.
     * @param yLabelMargin margin for the y-axis labels in pixels
     * @return this
     */   
    public T yLabelMargin(int yLabelMargin){
        if (yLabelMargin < 0){
            throw new IllegalArgumentException("The yLabel margin must be a non-negative integer.");
        }     
        
        this.yLabelMargin = yLabelMargin;
        return self();        
    }    
    
    /**
     * Updates the parameter for the margins for the labels on both axes.
     * This margin splits the area for the labels on both axes from the graph area.
     * The margin size must be a non-negative integer.
     * @param labelMarginSize margin for the x-axis and y-axis labels in pixels
     * @return this
     */      
    public T allLabelMargins(int labelMarginSize){
        if (labelMarginSize < 0){
            throw new IllegalArgumentException("The label margin size must be a non-negative integer.");
        }  
        
        this.xLabelMargin = labelMarginSize;
        this.yLabelMargin = labelMarginSize;
        return self();
    }
    
    /**
     * Gets height of image.
     * Ensured as a positive non-zero integer.
     * @return the height of the image in pixels
     */
    public Integer getImageHeight() {
        return imageHeight;
    }

    /**
     * Gets width of image.
     * Ensured as a positive non-zero integer.
     * @return the width of the image in pixels
     */
    public Integer getImageWidth() {
        return imageWidth;
    }

    /**
     * Gets x-axis range.
     * @return range (of data and of aggregated data) of the values on the x-axis
     */
    public AxisRange getXAxisRange() {
        return xAxisRange;
    }

    /**
     * Gets y-axis range.
     * @return range (of data and of aggregated data) of the values on the y-axis
     */
    public AxisRange getYAxisRange() {
        return yAxisRange;
    }

    /**
     * Gets the scaling tool to scale down the values on the x-axis.
     * @return the scaling on the x-axis
     */
    public ValueScale getXValueScale() {
        return xValueScale;
    }

    /**
     * Gets the scaling tool to scale down the values on the y-axis.
     * @return the scaling on the y-axis
     */
    public ValueScale getYValueScale() {
        return yValueScale;
    }
  
    /**
     * Gets background color of the image.
     * @return the color of the background of the image
     */
    public Color getBackgroundColor(){
        return this.backgroundColor;
    }
    
    /**
     * Gets color of labels for the axes.
     * @return the color of the label of the axes
     */
    public Color getLabelColor(){
        return this.labelColor;
    }
    
    /**
     * Gets color of reference lines on the graph area.
     * @return the color of the reference lines on the graph area
     */
    public Color getReferenceLineColor(){
        return this.referenceLineColor;
    }
    
    /**
     * Gets font of labels for the axes.
     * @return the font for the labels on the axes
     */
    public Font getLabelFont(){
        return this.labelFont;
    }
    
    /**
     * Gets the margin between the bottom of the image and the bottom of the axes/labels area.
     * Ensured as a non-negative integer.
     * @return the bottom margin for the image and the axes area
     */
    public Integer getBottomMargin(){
        return this.bottomMargin;
    }
    
    /**
     * Gets the margin between the top of the image and the top of the axes/labels area.
     * Ensured as a non-negative integer.
     * @return the bottom margin for the image and the axes area
     */
    public Integer getTopMargin(){
        return this.topMargin;
    }
    
    /**
     * Gets the margin between the left of the image and the left of the axes/labels area.
     * Ensured as a non-negative integer.
     * @return the left margin for the image and the axes area
     */
    public Integer getLeftMargin(){
        return this.leftMargin;
    }
    
    /**
     * Gets the margin between the right of the image and the right of the axes/labels area.
     * Ensured as a non-negative integer.
     * @return the right margin for the image and the axes area
     */
    public Integer getRightMargin(){
        return this.rightMargin;
    }
    
    /**
     * Gets the margin between the bottom of the graph area and the axes/labels area.
     * @return the bottom margin for the image and the axes area
     */
    public Integer getBottomAreaMargin(){
        return this.bottomAreaMargin;
    }
    
    /**
     * Gets the margin between the top of the graph area and the axes/labels area.
     * @return the top margin for the image and the axes area
     */
    public Integer getTopAreaMargin(){
        return this.topAreaMargin;
    }
    
    /**
     * Gets the margin between the left of the graph area and the axes/labels area.
     * @return the left margin for the image and the axes area
     */
    public Integer getLeftAreaMargin(){
        return this.leftAreaMargin;
    }
    
    /**
     * Gets the margin between the right of the graph area and the axes/labels area.
     * @return the right margin for the image and the axes area
     */
    public Integer getRightAreaMargin(){
        return this.rightAreaMargin;
    }
    
    /**
     * Gets the margin separating x-axis labels from the graph area.
     * @return the margin for the x-axis labels from the graph area
     */
    public Integer getXLabelMargin(){
        return this.xLabelMargin;
    }
    
    /**
     * Gets the margin separating y-axis labels from the graph area.
     * @return the margin for the y-axis labels from the graph area
     */
    public Integer getYLabelMargin(){
        return this.yLabelMargin;
    }
}