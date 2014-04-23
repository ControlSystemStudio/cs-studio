/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import org.epics.util.array.ListNumber;
import org.epics.util.array.SortedListView;

/**
 * Creates a renderer that is capable of drawing a sparkline graph.
 * 
 * <p>
 * A sparkline graph
 * <ul>
 *      <li>Displays a line</li>
 *      <li>Does not have axes labels</li>
 *      <li>Does not have scales display on the axes</li>
 *      <li>Often draws small circles at important values on the line</li>
 *      <li>Important values are:</li>
 *          <ul>
 *              <li>First Value</li>
 *              <li>Last Value</li>
 *              <li>Maximum Value</li>
 *              <li>Minimum Value</li>
 *          </ul>
 *      <li>Often maintains a high width to height aspect ratio.
 *          This can be manually setting the appropriate image width and height,
 *          or can be applied in the code through the <code>aspectRatio</code>.
 *          The aspect ratio should be set so that the line of the sparkline
 *          never has a slope greater than 45-degrees.</li>
 * </ul>
 * 
 * @author asbarber
 * @author jkfeng
 * @author sjdallst
 */
public class SparklineGraph2DRenderer extends Graph2DRenderer<SparklineGraph2DRendererUpdate>{

    /**
     * Creates a new sparkline graph renderer.
     * Will draw a circle at the max value, min value, and last value.
     * 
     * @param imageWidth the graph width in pixels
     * @param imageHeight the graph height in pixels
     */    
    public SparklineGraph2DRenderer(int imageWidth, int imageHeight){
        super(imageWidth, imageHeight);
        super.xLabelMargin = 0;
        super.yLabelMargin = 0; 
        
        super.rightMargin = 0;
        super.leftMargin = 0;
        super.bottomMargin = 0;
        super.topMargin = 0;
        
        //Set all area matgins to 1 to account for the drawing of circles.
        super.leftAreaMargin   = 1;
        super.rightAreaMargin  = 1;
        super.bottomAreaMargin = 1;
        super.topAreaMargin    = 1;        
    }
    
    //Parameters
    private int     circleDiameter = 3;
    private Color   minValueColor = new Color(28, 160, 232),    //Blue
                    maxValueColor = new Color(28, 160, 232),    
                    firstValueColor = new Color(223, 59, 73),   //Red         
                    lastValueColor = new Color(223, 59, 73);
    private boolean drawCircles = true;
    
    //Min, Max, Last Values and Indices
    private int     maxIndex, 
                    minIndex,
                    firstIndex,
                    lastIndex;
    private double  maxValueY = -1, 
                    minValueY = -1,
                    firstValueY = -1,
                    lastValueY = -1;
    private Double  aspectRatio = null;

    //Scaling Schemes    
    /**
     * The set of interpolation schemes that are supported by the <code>SparklineGraph2DRenderer</code>.
     * The interpolation schemes supported are <code>NEAREST_NEIGHBOUR</code>, <code>LINEAR</code>, and <code>CUBIC</code>.
     */
    public static java.util.List<InterpolationScheme> supportedInterpolationScheme = Arrays.asList(InterpolationScheme.NEAREST_NEIGHBOUR, InterpolationScheme.LINEAR, InterpolationScheme.CUBIC);

    private InterpolationScheme interpolation = InterpolationScheme.LINEAR;

    
    //DRAWING FUNCTIONS
    
    /**
     * Draws the graph on the given graphics context.
     * The render process is:
     * <ul>
     *      <li>Set aspect ratio</li>
     *      <li>Calculate data and plot ranges</li>
     *      <li>Calculates the region for the graph</li>
     *      <li>Draws the background</li>
     *      <li>Draws the line</li>
     *      <li>Draw circles if necessary</li>
     * </ul>
     * 
     * A circle is drawn at the max value and min value.
     * A circle is drawn at the first value and last value.
     * Each circle is drawn at 70% transparency.
     * If two circles are set to draw at the same value, only one circle is drawn.
     * If there is overlap, first/last values are drawn instead of max/min values.
     * 
     * @param g the graphics on which to display the data
     * @param data the data to display
     */
    public void draw(Graphics2D g, Point2DDataset data) {
        this.g = g;
        
        /*If we want to use the aspect ratio, we change the start and end of the coordinate plot,
        so that the total height is equal to the width of the xplot divided by the aspect ratio.*/  
        if(aspectRatio != null){
            adjustGraphToAspectRatio();
        }
        
        //General Rendering
        calculateRanges(data.getXStatistics(), data.getYStatistics());
        calculateGraphArea();

        drawBackground();
        g.setColor(Color.BLACK);        
  
        //Calculates data values
        SortedListView xValues = org.epics.util.array.ListNumbers.sortedView(data.getXValues());
        ListNumber yValues = org.epics.util.array.ListNumbers.sortedView(data.getYValues(), xValues.getIndexes());        
        setClip(g);
        
        //Draws Line  
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);        
        drawValueExplicitLine(xValues, yValues, interpolation, ReductionScheme.FIRST_MAX_MIN_LAST);
        
        //Draws a circle at the max, min, and last value
        if(drawCircles){
            //Hints: pure stroke, no antialias
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            //Set transparency
            AlphaComposite ac = java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F);
            g.setComposite(ac);        
            
            //Fills circle
            if (!hasOverlapMinimum()){
                drawCircle(g, data, minIndex, minValueColor);
            }
            if (!hasOverlapMaximum()){
                drawCircle(g, data, maxIndex, maxValueColor);
            }
            drawCircle(g, data, firstIndex, firstValueColor);
            drawCircle(g, data, lastIndex, lastValueColor);
        }
    }
    
    /**
     * Determines whether the minimum value circle overlaps with the first or last values.
     * This is useful in determining whether to not draw the minimum circle.
     * 
     * @return whether the "minimum" circle overlaps with other circles
     */
    private boolean hasOverlapMinimum(){
        return minIndex == lastIndex || minIndex == firstIndex;
    }
    
    /**
     * Determines whether the maximum value circle overlaps with the first or last values.
     * This is useful in determining whether to not draw the maximum circle.
     * 
     * @return whether the "maximum" circle overlaps with other circles
     */    
    private boolean hasOverlapMaximum(){
        return maxIndex == lastIndex || maxIndex == firstIndex;
    }

    /**
     * Draws a circle of a certain color at a certain position.
     * The pixel position is determined from the data value.
     * The data value is found by the 'index' element in 'data'.
     * 
     * @param g graphic context to draw (where circles are drawn)
     * @param data set of data that is graphed
     * @param index position in the data set where the circle is drawn (index position)
     * @param color color of circle that is drawn
     */
    protected void drawCircle(Graphics2D g, Point2DDataset data, int index, Color color){
            double x = Math.floor(scaledX(data.getXValues().getDouble(index)))+.5;
            double y = Math.floor(scaledY(data.getYValues().getDouble(index)))+.5;
            g.setColor(color);
            Shape circle = createShape(x, y, circleDiameter);
            g.fill(circle);        
    }
    
    /**
     * Creates a circle shape at the given position with given size.
     * @param x x position of shape
     * @param y y position of shape
     * @param size diameter of circle
     * @return ellipse (circle) shape
     */
    protected Shape createShape(double x, double y, double size) {
        double halfSize = size / 2;
        Ellipse2D.Double circle = new Ellipse2D.Double(x-halfSize, y-halfSize, size, size);
        return circle;
    } 
    
    /**
     * Operations for every value in data as it is processed.
     * This checks whether each new value is an important index.
     * An important index is if this y-value is a maximum, minimum, first, or last.
     * Note: this y-value is always the last value.
     * 
     * @param index element position in data set
     * @param valueX x value being processed
     * @param valueY y value being processed
     * @param scaledX x value scaled by data
     * @param scaledY y value scaled by data
     */
    @Override
    protected void processScaledValue(int index, double valueX, double valueY, double scaledX, double scaledY) {
        //Checks if new value is the new min or the new max
        
        //Base Case
        if (index == 0){
            firstIndex = 0;
            firstValueY = valueY;
            
            maxValueY = valueY;
            minValueY = valueY;
        }
        else{
            //Max
            if (maxValueY <= valueY){
                maxValueY = valueY;
                maxIndex = index;
            }
            //Min
            if (minValueY >= valueY){
                minValueY = valueY;
                minIndex = index;
            }  
        }
        
        //New point is always last point
        lastValueY = valueY;
        lastIndex = index;
    }
    
    /**
     * Applies the update to the renderer.
     * 
     * @param update the update to apply
     */    
    @Override
    public void update(SparklineGraph2DRendererUpdate update) {
        super.update(update);

        //Applies updates to members of this class
        if (update.getMinValueColor() != null){
            minValueColor = update.getMinValueColor();
        }
        if (update.getMaxValueColor() != null){
            maxValueColor = update.getMaxValueColor();
        }
        if (update.getCircleDiameter() != null){
            circleDiameter = update.getCircleDiameter();
        }
        if (update.getDrawCircles() != null){
            drawCircles = update.getDrawCircles();
        }
        if (update.getInterpolation() != null) {
            interpolation = update.getInterpolation();
        } 
        if (update.getAspectRatio() != null){
            aspectRatio = update.getAspectRatio();
        }
    }
    
    /**
     * Creates an object that allows updating of sparkline parameters.
     * @return sparkline update
     */
    @Override
    public SparklineGraph2DRendererUpdate newUpdate() {
        return new SparklineGraph2DRendererUpdate();
    }
    
    /**
     * The current interpolation used for the line.
     * @return current interpolation scheme of line
     */
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }  
    
    /**
     * The index corresponding to the maximum y-value.
     * If there are multiple maximums, the greatest index is returned.
     * @return index of maximum
     */
    public int getMaxIndex(){
        return maxIndex;
    }
    
    /**
     * The index corresponding to the minimum y-value.
     * If there are multiple minimums, the greatest index is returned.
     * @return index of the minimum
     */
    public int getMinIndex(){
        return minIndex;
    }
    
    /**
     * The index corresponding to the first value.
     * This value should always be zero.
     * @return index of the first value (zero)
     */
    public int getFirstIndex(){
        return firstIndex;
    }
    
    /**
     * The index corresponding to the last value.
     * @return index of the last value (data.size() - 1)
     */
    public int getLastIndex(){
        return lastIndex;
    }
    
    /**
     * The maximum y-value in the list of data.
     * If there are multiple maximum values, the last maximum
     * (determined by the greatest index) is the value returned.
     * @return The data value of the maximum
     */
    public double getMaxValue(){
        return maxValueY;
    }
    
    /**
     * Gets the minimum y-value in the list of data.
     * If there are multiple minimum values, the last minimum
     * (determined by the greatest index) is the value returned.
     * @return The data value of the minimum
     */
    public double getMinValue(){
        return minValueY;
    }
    
    /**
     * Gets the first y-value in the list of data.
     * @return the data value for the first index
     */
    public double getFirstValue(){
        return firstValueY;
    }
    
    /**
     * Gets the last y-value in the list of data.
     * @return The data value for the last index
     */
    public double getLastValue(){
        return lastValueY;
    }
    
    /**
     * Gets the decision of whether the draw function also draws circles at important data points.
     * @return whether circles get drawn along the line
     */
    public boolean getDrawCircles(){
        return drawCircles;
    }
    
    /**
     * Gets the color for the circle drawn at the minimum y-value.
     * @return color for circle
     */
    public Color getMinValueColor(){
        return minValueColor;
    }
    
    /**
     * Gets the color of the circle drawn at the maximum y-value.
     * @return color for circle
     */
    public Color getMaxValueColor(){
        return maxValueColor;
    }
    
    /**
     * Gets the color of the circle drawn at the last y-value.
     * @return color for circle
     */
    public Color getLastValueColor(){
        return lastValueColor;
    }
    
    /**
     * Gets the diameter for all circles that are drawn along the line in pixels.
     * @return diameter of circles drawn on line in pixels 
     */
    public int getCircleDiameter(){
        return circleDiameter;
    }
    
    /**
     * Gets the preferred width to height ratio that must be maintained within the graph area.
     * Ratio of width (pixels) to height (pixels).
     * @return width to height ratio
     */
    public double getAspectRatio(){
        return aspectRatio;
    }
    
    /**
     * Adjusts the area margins to maintain the aspect ratio.
     * The aspect ratio is applied only to the graph area (margins are ignored).
     * 
     * The area margin for all borders is set to 1 by default.
     * 
     * The left/right area margins are increased if the width needs to shrink to maintain the ratio.
     * The top/bottom area margins are increased if the height needs to shrink to maintain the ratio.
     * Note that the width and height can never be increased, so the aspect ratio is maintained by shrinking axes.
     * 
     * <p>
     * Example:
     *  Width = 100 pixels
     *  Height = 20 pixels
     *  Ratio is 4 : 1  (W : H)
     * 
     * The width and height could then be 100 : 25 or 80 : 20
     * Since the height cannot be increased from 20 to 25, the first option would not work.
     * The option 80 : 20 is then set.
     * The width is decreased from 100 to 80 by setting the left and right area margin to 10 pixels.
     * </p>
     */
    private void adjustGraphToAspectRatio(){
        //Only looks at available graph area region
        int relevantHeight = super.getImageHeight() - bottomMargin - topMargin,
            relevantWidth  = super.getImageWidth() - rightMargin - leftMargin;
        
        //Defaults
        rightAreaMargin = 1;
        leftAreaMargin = 1;
        topAreaMargin = 1;
        bottomAreaMargin = 1;
            
        //Shrink width to maintain aspect ratio
        if (relevantHeight * aspectRatio <= relevantWidth){
            double preferredWidth = relevantHeight * aspectRatio;
            int marginSize = (int) (relevantWidth - preferredWidth) / 2;
            
            rightAreaMargin = 1 + marginSize;
            leftAreaMargin = 1 + marginSize;
        }
        //Shrink height to maintain aspect ratio
        else {
            double preferredHeight = relevantWidth / aspectRatio;
            int marginSize = (int) (relevantHeight - preferredHeight) / 2;
            
            topAreaMargin = 1 + marginSize;
            bottomAreaMargin = 1 + marginSize;
        }
    }
}