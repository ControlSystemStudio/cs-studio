/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public abstract class Graph2DRenderer<T extends Graph2DRendererUpdate> {
    
    protected double xPlotValueStart;
    protected double yPlotValueStart;
    protected double xPlotValueEnd;
    protected double yPlotValueEnd;
    
    protected double yPlotCoordHeight;
    protected double xPlotCoordWidth;
    
    protected double xPlotCoordStart;
    protected double yPlotCoordStart;
    protected double yPlotCoordEnd;
    protected double xPlotCoordEnd;
    
    protected int xAreaStart;
    protected int yAreaStart;
    protected int yAreaEnd;
    protected int xAreaEnd;

    public Graph2DRenderer(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }
    
    protected Graphics2D g;
    
    // Renderer external parameter //
    
    // Size of the image
    private int imageWidth;
    private int imageHeight;
    // Strategy for calculating the axis range
    private AxisRange xAxisRange = AxisRanges.integrated();
    private AxisRange yAxisRange = AxisRanges.integrated();
    // Colors and fonts
    protected Color backgroundColor = Color.WHITE;
    protected Color labelColor = Color.BLACK;
    protected Color referenceLineColor = new Color(240, 240, 240);
    protected Font labelFont = FontUtil.getLiberationSansRegular();
    // Image margins
    protected int bottomMargin = 2;
    protected int topMargin = 2;
    protected int leftMargin = 2;
    protected int rightMargin = 2;
    // area margins
    protected int bottomAreaMargin = 0;
    protected int topAreaMargin = 0;
    protected int leftAreaMargin = 0;
    protected int rightAreaMargin = 0;
    // Axis label margins
    protected int xLabelMargin = 3;
    protected int yLabelMargin = 3;
    
    // Computed parameters, visible to outside //
    
    private Range xAggregatedRange;
    private Range yAggregatedRange;
    private Range xPlotRange;
    private Range yPlotRange;
    protected FontMetrics labelFontMetrics;
    protected ListDouble xReferenceCoords;
    protected ListDouble xReferenceValues;
    protected List<String> xReferenceLabels;
    protected ListDouble yReferenceCoords;
    protected ListDouble yReferenceValues;
    protected List<String> yReferenceLabels;
    private int xLabelMaxHeight;
    private int yLabelMaxWidth;

    public AxisRange getXAxisRange() {
        return xAxisRange;
    }

    public AxisRange getYAxisRange() {
        return yAxisRange;
    }

    public Range getXAggregatedRange() {
        return xAggregatedRange;
    }

    public Range getYAggregatedRange() {
        return yAggregatedRange;
    }

    public Range getXPlotRange() {
        return xPlotRange;
    }

    public Range getYPlotRange() {
        return yPlotRange;
    }

    public void update(T update) {
        if (update.getImageHeight() != null) {
            imageHeight = update.getImageHeight();
        }
        if (update.getImageWidth() != null) {
            imageWidth = update.getImageWidth();
        }
        if (update.getXAxisRange() != null) {
            xAxisRange = update.getXAxisRange();
        }
        if (update.getYAxisRange() != null) {
            yAxisRange = update.getYAxisRange();
        }
    }
    
    static Range aggregateRange(Range dataRange, Range aggregatedRange) {
        if (aggregatedRange == null) {
            return dataRange;
        } else {
            return RangeUtil.sum(dataRange, aggregatedRange);
        }
    }
    
    public abstract T newUpdate();
    
    protected void calculateRanges(Range xDataRange, Range yDataRange) {
        xAggregatedRange = aggregateRange(xDataRange, xAggregatedRange);
        yAggregatedRange = aggregateRange(yDataRange, yAggregatedRange);
        xPlotRange = xAxisRange.axisRange(xDataRange, xAggregatedRange);
        yPlotRange = yAxisRange.axisRange(yDataRange, yAggregatedRange);
    }
    
    protected void drawHorizontalReferenceLines() {
        g.setColor(referenceLineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        ListNumber yTicks = yReferenceCoords;
        for (int i = 0; i < yTicks.size(); i++) {
            Shape line = new Line2D.Double(xAreaStart, yTicks.getDouble(i), xAreaEnd, yTicks.getDouble(i));
            g.draw(line);
        }
    }

    protected void drawVerticalReferenceLines() {
        g.setColor(referenceLineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        ListNumber xTicks = xReferenceCoords;
        for (int i = 0; i < xTicks.size(); i++) {
            Shape line = new Line2D.Double(xTicks.getDouble(i), yAreaStart, xTicks.getDouble(i), yAreaEnd);
            g.draw(line);
        }
    }
    
    protected void calculateGraphArea() {
        ValueAxis xAxis = ValueAxis.createAutoAxis(xPlotRange.getMinimum().doubleValue(), xPlotRange.getMaximum().doubleValue(), Math.max(2, getImageWidth() / 60));
        ValueAxis yAxis = ValueAxis.createAutoAxis(yPlotRange.getMinimum().doubleValue(), yPlotRange.getMaximum().doubleValue(), Math.max(2, getImageHeight() / 60));
        xReferenceLabels = Arrays.asList(xAxis.getTickLabels());
        yReferenceLabels = Arrays.asList(yAxis.getTickLabels());
        xReferenceValues = new ArrayDouble(xAxis.getTickValues());
        yReferenceValues = new ArrayDouble(yAxis.getTickValues());
        
        labelFontMetrics = g.getFontMetrics(labelFont);
        
        // Compute x axis spacing
        xLabelMaxHeight = labelFontMetrics.getHeight() - labelFontMetrics.getLeading();
        int areaFromBottom = bottomMargin + xLabelMaxHeight + xLabelMargin;
        
        // Compute y axis spacing
        int[] yLabelWidths = new int[yReferenceLabels.size()];
        yLabelMaxWidth = 0;
        for (int i = 0; i < yLabelWidths.length; i++) {
            yLabelWidths[i] = labelFontMetrics.stringWidth(yReferenceLabels.get(i));
            yLabelMaxWidth = Math.max(yLabelMaxWidth, yLabelWidths[i]);
        }
        int areaFromLeft = leftMargin + yLabelMaxWidth + yLabelMargin;

        xPlotValueStart = getXPlotRange().getMinimum().doubleValue();
        yPlotValueStart = getYPlotRange().getMinimum().doubleValue();
        xPlotValueEnd = getXPlotRange().getMaximum().doubleValue();
        yPlotValueEnd = getYPlotRange().getMaximum().doubleValue();
        xAreaStart = areaFromLeft;
        yAreaStart = topMargin;
        xAreaEnd = getImageWidth() - rightMargin - 1;
        yAreaEnd = getImageHeight() - areaFromBottom - 1;
        xPlotCoordStart = xAreaStart + topAreaMargin + 0.5;
        yPlotCoordStart = yAreaStart + leftAreaMargin + 0.5;
        xPlotCoordEnd = xAreaEnd - bottomAreaMargin + 0.5;
        yPlotCoordEnd = yAreaEnd - rightAreaMargin + 0.5;
        xPlotCoordWidth = xPlotCoordEnd - xPlotCoordStart;
        yPlotCoordHeight = yPlotCoordEnd - yPlotCoordStart;
        
        double[] xRefCoords = new double[xReferenceValues.size()];
        for (int i = 0; i < xRefCoords.length; i++) {
            xRefCoords[i] = scaledX(xReferenceValues.getDouble(i));
        }
        xReferenceCoords = new ArrayDouble(xRefCoords);
        
        double[] yRefCoords = new double[yReferenceValues.size()];
        for (int i = 0; i < yRefCoords.length; i++) {
            yRefCoords[i] = scaledY(yReferenceValues.getDouble(i));
        }
        yReferenceCoords = new ArrayDouble(yRefCoords);
    }

    protected void drawBackground() {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getImageWidth(), getImageHeight());
    }
    
    protected void drawGraphArea() {
        drawBackground();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // When drawing the reference line, align them to the pixel
        drawVerticalReferenceLines();
        drawHorizontalReferenceLines();;
        
        drawYLabels();
        drawXLabels();
    }
    
    private static final int MIN = 0;
    private static final int MAX = 1;
    
    private static void drawHorizontalReferencesLabel(Graphics2D graphics, FontMetrics metrics, String text, int yCenter, int[] drawRange, int xRight, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < yCenter || drawRange[MIN] > yCenter)
            return;
        
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < metrics.getHeight())
            return;
        
        Java2DStringUtilities.Alignment alignment = Java2DStringUtilities.Alignment.RIGHT;
        int targetY = yCenter;
        int halfHeight = metrics.getAscent() / 2;
        if (yCenter < drawRange[MIN] + halfHeight) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_RIGHT;
            targetY = drawRange[MIN];
        } else if (yCenter > drawRange[MAX] - halfHeight) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.BOTTOM_RIGHT;
            targetY = drawRange[MAX];
        }

        Java2DStringUtilities.drawString(graphics, alignment, xRight, targetY, text);
        
        if (updateMin) {
            drawRange[MAX] = targetY - metrics.getHeight();
        } else {
            drawRange[MIN] = targetY + metrics.getHeight();
        }
    }
    
    private static void drawVerticalReferenceLabel(Graphics2D graphics, FontMetrics metrics, String text, int xCenter, int[] drawRange, int yTop, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < xCenter || drawRange[MIN] > xCenter)
            return;
        
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < metrics.getHeight())
            return;
        
        Java2DStringUtilities.Alignment alignment = Java2DStringUtilities.Alignment.TOP;
        int targetX = xCenter;
        int halfWidth = metrics.stringWidth(text) / 2;
        if (xCenter < drawRange[MIN] + halfWidth) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_LEFT;
            targetX = drawRange[MIN];
        } else if (xCenter > drawRange[MAX] - halfWidth) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_RIGHT;
            targetX = drawRange[MAX];
        }

        Java2DStringUtilities.drawString(graphics, alignment, targetX, yTop, text);
        
        if (updateMin) {
            drawRange[MIN] = targetX + metrics.getHeight();
        } else {
            drawRange[MAX] = targetX - metrics.getHeight();
        }
    }
    

    protected final double scaledX(double value) {
        return xPlotCoordStart + NumberUtil.scale(value, xPlotValueStart, xPlotValueEnd, xPlotCoordWidth);
    }

    protected final double scaledY(double value) {
        return yPlotCoordEnd - NumberUtil.scale(value, yPlotValueStart, yPlotValueEnd, yPlotCoordHeight);
    }
    
    protected void setClip(Graphics2D g) {
        g.setClip(xAreaStart, yAreaStart, xAreaEnd - xAreaStart + 1, yAreaEnd - yAreaStart + 1);
    }

    protected void drawYLabels() {
        // Draw Y labels
        ListNumber yTicks = yReferenceCoords;
        if (yReferenceLabels != null && !yReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {yAreaStart, yAreaEnd};
            int xRightLabel = (int) (xAreaStart - yLabelMargin - 1);
            drawHorizontalReferencesLabel(g, metrics, yReferenceLabels.get(0), (int) Math.floor(yTicks.getDouble(0)),
                drawRange, xRightLabel, true, false);
            drawHorizontalReferencesLabel(g, metrics, yReferenceLabels.get(yReferenceLabels.size() - 1), (int) Math.floor(yTicks.getDouble(yReferenceLabels.size() - 1)),
                drawRange, xRightLabel, false, false);
            
            for (int i = 1; i < yReferenceLabels.size() - 1; i++) {
                drawHorizontalReferencesLabel(g, metrics, yReferenceLabels.get(i), (int) Math.floor(yTicks.getDouble(i)),
                    drawRange, xRightLabel, true, false);
            }
        }
    }

    protected void drawXLabels() {
        // Draw X labels
        ListNumber xTicks = xReferenceCoords;
        if (xReferenceLabels != null && !xReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {xAreaStart, xAreaEnd};
            int yTop = (int) (yAreaEnd + xLabelMargin + 1);
            drawVerticalReferenceLabel(g, metrics, xReferenceLabels.get(0), (int) Math.floor(xTicks.getDouble(0)),
                drawRange, yTop, true, false);
            drawVerticalReferenceLabel(g, metrics, xReferenceLabels.get(xReferenceLabels.size() - 1), (int) Math.floor(xTicks.getDouble(xReferenceLabels.size() - 1)),
                drawRange, yTop, false, false);
            
            for (int i = 1; i < xReferenceLabels.size() - 1; i++) {
                drawVerticalReferenceLabel(g, metrics, xReferenceLabels.get(i), (int) Math.floor(xTicks.getDouble(i)),
                    drawRange, yTop, true, false);
            }
        }
    }

}
