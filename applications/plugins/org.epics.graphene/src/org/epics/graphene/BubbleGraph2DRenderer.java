/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import org.epics.util.array.ListInt;
import org.epics.util.stats.Ranges;

/**
 *
 * @author carcassi
 */
public class BubbleGraph2DRenderer extends Graph2DRenderer<BubbleGraph2DRendererUpdate> {
    
    private Range zAggregatedRange;
    private AxisRangeInstance zAxisRange = AxisRanges.display().createInstance();
    private Range zPlotRange;
    private Integer focusPixelX;
    private Integer focusPixelY;
    private boolean highlightFocusValue = false;
    
    private Integer focusValueIndex;

    public BubbleGraph2DRenderer(int width, int height) {
        super(width, height);
    }

    @Override
    public void update(BubbleGraph2DRendererUpdate update) {
        super.update(update);
        if (update.getFocusPixelX()!= null) {
            focusPixelX = update.getFocusPixelX();
        }
        if (update.getFocusPixelY()!= null) {
            focusPixelY = update.getFocusPixelY();
        }
        if (update.getHighlightFocusValue()!= null) {
            highlightFocusValue = update.getHighlightFocusValue();
        }
    }

    protected void calculateRanges(Range xDataRange, Range xDisplayRange, Range yDataRange, Range yDisplayRange, Range zDataRange, Range zDisplayRange) {
        super.calculateRanges(xDataRange, xDisplayRange, yDataRange, yDisplayRange);
        zPlotRange = zAxisRange.axisRange(zDataRange, zDisplayRange);
    }

    /**
     *Draws a bubble graph on the given Graphics2D context using the given data.
     * @param g Graphics2D context, can not be null.
     * @param data consists of x, y, and z coordinates, as well as labels. x and y correspond to position on the graph. 
     * z corresponds to the diameter of the circle, and the label corresponds to the color.
     */
    public void draw(Graphics2D g, Point3DWithLabelDataset data) {
        if(g == null)
            throw new NullPointerException("g is null");
        this.g = g;
        
        calculateRanges(data.getXStatistics(), data.getXDisplayRange(),
                data.getYStatistics(), data.getYDisplayRange(),
                data.getZStatistics(), data.getZDisplayRange());
        
        drawBackground();
        calculateLabels();
        calculateGraphArea();        
        drawGraphArea();
        

        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        LabelColorScheme labelColor = LabelColorSchemes.orderedHueColor(data.getLabels());
        
        // Order values by 
        ListInt indexes = org.epics.util.array.ListNumbers.sortedView(data.getZValues()).getIndexes();
        
        // Reset current focused value
        focusValueIndex = null;
        final boolean isFocusValuePresent = focusPixelX != null && focusPixelY != null;
        Shape focusShape = null;
        
        // Make sure that the line does not go ouside the chart
        setClip(g);
        
        Range absZPlotRange = Ranges.absRange(zPlotRange);
        for (int j = indexes.size() - 1; j >= 0; j--) {
            int i = indexes.getInt(j);
            double zValue = data.getZValues().getDouble(i);
            double diameter = radiusScale(absZPlotRange.getMinimum().doubleValue(), Math.abs(zValue), absZPlotRange.getMaximum().doubleValue(),
                    3, 15);
            double x = scaledX(data.getXValues().getDouble(i));
            double y = scaledY(data.getYValues().getDouble(i));
            Shape bubble = createShape(x, y, diameter, zValue >= 0);
            newValue(x, y, diameter, i);
            g.setColor(new Color(labelColor.getColor(data.getLabels().get(i))));
            g.fill(bubble);
            g.setColor(Color.BLACK);
            g.draw(bubble);
            if (isFocusValuePresent) {
                double deltaX = focusPixelX - x;
                double deltaY = focusPixelY - y;
                if (Math.sqrt(deltaX*deltaX + deltaY*deltaY) < diameter / 2) {
                    focusValueIndex = i;
                    focusShape = bubble;
                }
            }
        }
        
        if (highlightFocusValue && focusShape != null) {
            g.setColor(Color.WHITE);
            g.fill(focusShape);
            g.setColor(Color.BLACK);
            g.draw(focusShape);
//            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//            g.setColor(Color.CYAN);
//            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//            g.drawLine(focusPixelX, focusPixelY, focusPixelX, focusPixelY);
        }

    }
    
    private double radiusScale(double minValue, double value, double maxValue, double minRadius, double maxRadius) {
        if (minValue < 0) {
            throw new UnsupportedOperationException("No negative value should be reaching here");
        }
        
        if (minValue == maxValue) {
            return minRadius + (maxRadius - minRadius) / 2;
        }
        
        if (value <= minValue) {
            return minRadius;
        }
        if (value >= maxValue) {
            return maxRadius;
        }
        return minRadius + MathUtil.scale(Math.sqrt(value), Math.sqrt(minValue), Math.sqrt(maxValue), (maxRadius - minRadius));
    }
    
    /**
     *Does nothing.
     * @param x
     * @param y
     * @param size
     * @param index
     */
    protected void newValue(double x, double y, double size, int index) {
        // Do nothing
    }
    
    private Shape createShape(double x, double y, double size, boolean positive) {
        double halfSize = size / 2;
        if (positive) {
            Ellipse2D.Double circle = new Ellipse2D.Double(x-halfSize, y-halfSize, size, size);
            return circle;
        } else {
            Rectangle2D.Double square = new Rectangle2D.Double(x-halfSize, y-halfSize, size, size);
            return square;
        }
    }

    @Override
    public BubbleGraph2DRendererUpdate newUpdate() {
        return new BubbleGraph2DRendererUpdate();
    }

    /**
     * Return the index of the focused value.
     * 
     * @return the index or null
     */
    public Integer getFocusValueIndex() {
        return focusValueIndex;
    }
    
}
