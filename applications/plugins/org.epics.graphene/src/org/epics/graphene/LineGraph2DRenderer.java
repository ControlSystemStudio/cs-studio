/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ListNumber;
import org.epics.util.array.SortedListView;
import org.epics.util.stats.Ranges;

/**
 * Renderer for a line graph.
 *
 * @author carcassi
 */
public class LineGraph2DRenderer extends Graph2DRenderer<LineGraph2DRendererUpdate> {

    public static java.util.List<InterpolationScheme> supportedInterpolationScheme = Arrays.asList(InterpolationScheme.NEAREST_NEIGHBOR, InterpolationScheme.LINEAR, InterpolationScheme.CUBIC);
    public static java.util.List<ReductionScheme> supportedReductionScheme = Arrays.asList(ReductionScheme.FIRST_MAX_MIN_LAST, ReductionScheme.NONE);
    
    @Override
    public LineGraph2DRendererUpdate newUpdate() {
        return new LineGraph2DRendererUpdate();
    }
    
    private NumberColorMap valueColorScheme = NumberColorMaps.GRAY;
    private NumberColorMapInstance valueColorSchemeInstance;
    private Range datasetRange;
    private InterpolationScheme interpolation = InterpolationScheme.NEAREST_NEIGHBOR;
    private ReductionScheme reduction = ReductionScheme.FIRST_MAX_MIN_LAST;
    // Pixel focus
    private Integer focusPixelX;
    
    private boolean highlightFocusValue = false;

    private int focusValueIndex = -1;
    
    /**
     * Creates a new line graph renderer.
     * 
     * @param imageWidth the graph width
     * @param imageHeight the graph height
     */
    public LineGraph2DRenderer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
    }

    /**
     * The current interpolation used for the line.
     * 
     * @return the current interpolation
     */
    public InterpolationScheme getInterpolation() {
        return interpolation;
    }
    
    /**
     *Current state of highlightFocusValue.
     * <ul>
     *  <li>True - highlight and show the value the mouse is on.</li>
     *  <li>False - Avoid calculation involved with finding the highlighted value/ do not highlight the value.</li>
     * </ul>
     * @return true or false
     */
    public boolean isHighlightFocusValue() {
        return highlightFocusValue;
    }
    
    /**
     *Current index of the value that the mouse is focused on.
     * @return focused index (in the dataset).
     */
    public int getFocusValueIndex() {
        return focusValueIndex;
    }
    
    /**
     *Current x-position(pixel) of the value that the mouse is focused on.
     * @return the x position that the mouse is focused on in the graph (pixel).
     */
    public Integer getFocusPixelX() {
        return focusPixelX;
    }
    
    @Override
    public void update(LineGraph2DRendererUpdate update) {
        super.update(update);
        if(update.getValueColorScheme() != null){
            valueColorScheme = update.getValueColorScheme();
            valueColorSchemeInstance = valueColorScheme.createInstance(datasetRange);
        }
        if (update.getInterpolation() != null) {
            interpolation = update.getInterpolation();
        }
        if (update.getDataReduction() != null) {
            reduction = update.getDataReduction();
        }
        if (update.getFocusPixelX()!= null) {
            focusPixelX = update.getFocusPixelX();
        }
        if (update.getHighlightFocusValue()!= null) {
            highlightFocusValue = update.getHighlightFocusValue();
        }
    }

    /**
     * Draws the graph on the given graphics context.
     * 
     * @param g the graphics on which to display the data
     * @param data the data to display
     */
    public void draw(Graphics2D g, Point2DDataset data) {
        this.g = g;
        
        calculateRanges(data.getXStatistics(), data.getXDisplayRange(), data.getYStatistics(), data.getYDisplayRange());
        calculateLabels();
        calculateGraphArea();        
        drawBackground();
        drawGraphArea();
        
        SortedListView xValues = org.epics.util.array.ListNumbers.sortedView(data.getXValues());
        ListNumber yValues = org.epics.util.array.ListNumbers.sortedView(data.getYValues(), xValues.getIndexes());

        setClip(g);
        g.setColor(Color.BLACK);

        currentIndex = 0;
        currentScaledDiff = getImageWidth();
        drawValueExplicitLine(xValues, yValues, interpolation, reduction);
        if (focusPixelX != null) {
            focusValueIndex = xValues.getIndexes().getInt(currentIndex);
            if (highlightFocusValue) {
                g.setColor(new Color(0, 0, 0, 128));
                int x = (int) scaledX(xValues.getDouble(currentIndex));
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
                g.drawLine(x, yAreaCoordStart, x, yAreaCoordEnd - 1);
            }
        } else {
            focusValueIndex = -1;
        }
    }
    
    /**
     *Draws a graph with multiple lines, each pertaining to a different set of data.
     * @param g Graphics2D object used to perform drawing functions within draw.
     * @param data can not be null
     */
    public void draw(Graphics2D g, List<Point2DDataset> data) {
        this.g = g;
        
        //Calculate range, range will end up being from the lowest point to highest in all of the given data.
        for(Point2DDataset dataPiece: data){
          super.calculateRanges(dataPiece.getXStatistics(), dataPiece.getXDisplayRange(), dataPiece.getYStatistics(), dataPiece.getYDisplayRange());
        }
        calculateLabels();
        calculateGraphArea();
        drawBackground();
        drawGraphArea();
        
        Range datasetRangeCheck = Ranges.range(0,data.size());
        
        //Set color scheme
        if(valueColorSchemeInstance == null || datasetRange == null || datasetRange != datasetRangeCheck){
            datasetRange = datasetRangeCheck;
            valueColorSchemeInstance = valueColorScheme.createInstance(datasetRange);
        }
        //Draw a line for each set of data in the data array.
        for(int datasetNumber = 0; datasetNumber < data.size(); datasetNumber++){
            SortedListView xValues = org.epics.util.array.ListNumbers.sortedView(data.get(datasetNumber).getXValues());
            ListNumber yValues = org.epics.util.array.ListNumbers.sortedView(data.get(datasetNumber).getYValues(), xValues.getIndexes());        
            setClip(g);
            g.setColor(new Color(valueColorSchemeInstance.colorFor((double)datasetNumber)));
            drawValueExplicitLine(xValues, yValues, interpolation, reduction);
        }
    }

    @Override
    protected void processScaledValue(int index, double valueX, double valueY, double scaledX, double scaledY) {
        if (focusPixelX != null) {
            double scaledDiff = Math.abs(scaledX - focusPixelX);
            if (scaledDiff < currentScaledDiff) {
                currentIndex = index;
                currentScaledDiff = scaledDiff;
            }
        }
    }
    
    private int currentIndex;
    private double currentScaledDiff;

    
}
