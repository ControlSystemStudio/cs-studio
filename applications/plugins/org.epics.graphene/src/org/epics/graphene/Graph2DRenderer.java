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
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;
import static org.epics.graphene.InterpolationScheme.CUBIC;
import static org.epics.graphene.InterpolationScheme.LINEAR;
import static org.epics.graphene.InterpolationScheme.NEAREST_NEIGHBOUR;
import static org.epics.graphene.ReductionScheme.FIRST_MAX_MIN_LAST;
import static org.epics.graphene.ReductionScheme.NONE;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListMath;
import org.epics.util.array.ListNumber;

/**
 * The base class for all graph renderers.
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

    /**
     * Creates a graph renderer.
     * 
     * @param graphWidth the graph width
     * @param graphHeight the graph height
     */
    public Graph2DRenderer(int graphWidth, int graphHeight) {
        this.imageWidth = graphWidth;
        this.imageHeight = graphHeight;
    }

    /**
     * The current height of the graph.
     * 
     * @return the graph height
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * The current width of the graph.
     * 
     * @return the graph width
     */
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
    // Strategy for generating labels and scaling value of the axis
    private ValueScale xValueScale = ValueScales.linearScale();
    private ValueScale yValueScale = ValueScales.linearScale();
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

    /**
     * The current strategy to calculate the x range for the graph.
     * 
     * @return the x axis range calculator
     */
    public AxisRange getXAxisRange() {
        return xAxisRange;
    }

    /**
     * The current strategy to calculate the y range for the graph.
     * 
     * @return the y axis range calculator
     */
    public AxisRange getYAxisRange() {
        return yAxisRange;
    }

    /**
     * The aggregated range of all the data that has been rendered.
     * 
     * @return the aggregated data x range
     */
    public Range getXAggregatedRange() {
        return xAggregatedRange;
    }

    /**
     * The aggregated range of all the data that has been rendered.
     * 
     * @return the aggregated data y range
     */
    public Range getYAggregatedRange() {
        return yAggregatedRange;
    }

    /**
     * The range of the x axis in the last graph rendering.
     * 
     * @return the x axis range
     */
    public Range getXPlotRange() {
        return xPlotRange;
    }

    /**
     * The range of the y axis in the last graph rendering.
     * 
     * @return the y axis range
     */
    public Range getYPlotRange() {
        return yPlotRange;
    }

    /**
     * Applies the update to the renderer.
     * <p>
     * When sub-classing, one should re-implement this method by first calling it
     * and then applying all the updates specific to the sub-class.
     * 
     * @param update the update to apply
     */
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
        if (update.getXValueScale()!= null) {
            xValueScale = update.getXValueScale();
        }
        if (update.getYValueScale() != null) {
            yValueScale = update.getYValueScale();
        }
    }
    
    static Range aggregateRange(Range dataRange, Range aggregatedRange) {
        if (aggregatedRange == null) {
            return dataRange;
        } else {
            return RangeUtil.sum(dataRange, aggregatedRange);
        }
    }
    
    /**
     * Creates a new update for the given graph.
     * 
     * @return a new update object
     */
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
        ValueAxis xAxis = xValueScale.references(xPlotRange, 2, Math.max(2, getImageWidth() / 60));
        ValueAxis yAxis = yValueScale.references(yPlotRange, 2, Math.max(2, getImageHeight() / 60));
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

    private ScaledData scaleNoReducation(ListNumber xValues, ListNumber yValues) {
        ScaledData scaledData = new ScaledData();
        int dataCount = xValues.size();
        scaledData.scaledX = new double[dataCount];
        scaledData.scaledY = new double[dataCount];
        for (int i = 0; i < scaledData.scaledY.length; i++) {
            scaledData.scaledX[i] = scaledX(xValues.getDouble(i));
            scaledData.scaledY[i] = scaledY(yValues.getDouble(i));;
        }
        scaledData.end = dataCount;
        return scaledData;
    }

    private ScaledData scaleFirstMaxMinLastReduction(ListNumber xValues, ListNumber yValues) {
        // The number of points generated by this is about 4 times the 
        // number of points on the x axis. If the number of points is less
        // than that, it's not worth it. Don't do the data reduction.
        if (xValues.size() < xPlotCoordWidth * 4) {
            return scaleNoReducation(xValues, yValues);
        }
        
        ScaledData scaledData = new ScaledData();
        scaledData.scaledX = new double[((int) xPlotCoordWidth + 1)*4 ];
        scaledData.scaledY = new double[((int) xPlotCoordWidth + 1)*4];
        int cursor = 0;
        int previousPixel = (int) scaledX(xValues.getDouble(0));
        double last = scaledY(yValues.getDouble(0));
        double min = last;
        double max = last;
        scaledData.scaledX[0] = previousPixel;
        scaledData.scaledY[0] = min;
        cursor++;
        for (int i = 1; i < xValues.size(); i++) {
            int currentPixel = (int) scaledX(xValues.getDouble(i));
            if (currentPixel == previousPixel) {
                last = scaledY(yValues.getDouble(i));
                min = MathIgnoreNaN.min(min, last);
                max = MathIgnoreNaN.max(max, last);
            } else {
                scaledData.scaledX[cursor] = previousPixel;
                scaledData.scaledY[cursor] = max;
                cursor++;
                scaledData.scaledX[cursor] = previousPixel;
                scaledData.scaledY[cursor] = min;
                cursor++;
                scaledData.scaledX[cursor] = previousPixel;
                scaledData.scaledY[cursor] = last;
                cursor++;
                previousPixel = currentPixel;
                last = scaledY(yValues.getDouble(i));
                min = last;
                max = last;
                scaledData.scaledX[cursor] = currentPixel;
                scaledData.scaledY[cursor] = last;
                cursor++;
            }
        }
        scaledData.scaledX[cursor] = previousPixel;
        scaledData.scaledY[cursor] = max;
        cursor++;
        scaledData.scaledX[cursor] = previousPixel;
        scaledData.scaledY[cursor] = min;
        cursor++;
        scaledData.end = cursor;
        return scaledData;
    }
    
    private static class ScaledData {
        private double[] scaledX;
        private double[] scaledY;
        private int start;
        private int end;
    }
    
    protected void drawValueLine(ListNumber xValues, ListNumber yValues, InterpolationScheme interpolation) {
        ReductionScheme reductionScheme = ReductionScheme.NONE;

        ScaledData scaledData;
        
        switch (reductionScheme) {
            default:
                throw new IllegalArgumentException("Reduction scheme " + reductionScheme + " not supported");
            case NONE:
                scaledData = scaleNoReducation(xValues, yValues);
                break;
        }
        
        // create path
        Path2D path;
        switch (interpolation) {
            default:
            case NEAREST_NEIGHBOUR:
                path = nearestNeighbour(scaledData);
                break;
            case LINEAR:
                path = linearInterpolation(scaledData);
                break;
            case CUBIC:
                path = cubicInterpolation(scaledData);
                break;
        }

        // Draw the line
        g.draw(path);
    }
    
    protected void drawValueExplicitLine(ListNumber xValues, ListNumber yValues, InterpolationScheme interpolation, ReductionScheme reduction) {
        ScaledData scaledData;
        
        // Narrow the data
        int start = org.epics.util.array.ListNumbers.binarySearchValueOrLower(xValues, xPlotValueStart);
        int end = org.epics.util.array.ListNumbers.binarySearchValueOrHigher(xValues, xPlotValueEnd);
        
        xValues = ListMath.limit(xValues, start, end + 1);
        yValues = ListMath.limit(yValues, start, end + 1);
        
        switch (reduction) {
            default:
                throw new IllegalArgumentException("Reduction scheme " + reduction + " not supported");
            case NONE:
                scaledData = scaleNoReducation(xValues, yValues);
                break;
            case FIRST_MAX_MIN_LAST:
                scaledData = scaleFirstMaxMinLastReduction(xValues, yValues);
                break;
        }
        
        // create path
        Path2D path;
        switch (interpolation) {
            default:
            case NEAREST_NEIGHBOUR:
                path = nearestNeighbour(scaledData);
                break;
            case LINEAR:
                path = linearInterpolation(scaledData);
                break;
            case CUBIC:
                path = cubicInterpolation(scaledData);
                break;
        }

        // Draw the line
        g.draw(path);
    }

    private static Path2D.Double nearestNeighbour(ScaledData scaledData) {
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[start], scaledY[start]);
        for (int i = 1; i < end; i++) {
            double halfX = scaledX[i - 1] + (scaledX[i] - scaledX[i - 1]) / 2;
            if (!java.lang.Double.isNaN(scaledY[i-1])) {
                line.lineTo(halfX, scaledY[i - 1]);
                if (!java.lang.Double.isNaN(scaledY[i]))
                    line.lineTo(halfX, scaledY[i]);
            } else {
                line.moveTo(halfX, scaledY[i]);
            }
        }
        line.lineTo(scaledX[end - 1], scaledY[end - 1]);
        return line;
    }

    private static Path2D.Double linearInterpolation(ScaledData scaledData) {
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[start], scaledY[start]);
        for (int i = 1; i < end; i++) {
            line.lineTo(scaledX[i], scaledY[i]);
        }
        return line;
    }

    private static Path2D.Double cubicInterpolation(ScaledData scaledData) {
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double path = new Path2D.Double();
        path.moveTo(scaledX[start], scaledY[start]);
        for (int i = 1; i < end; i++) {
            // Extract 4 points (take care of boundaries)
            double y1 = scaledY[i - 1];
            double y2 = scaledY[i];
            double x1 = scaledX[i - 1];
            double x2 = scaledX[i];
            double y0;
            double x0;
            if (i > 1) {
                y0 = scaledY[i - 2];
                x0 = scaledX[i - 2];
            } else {
                y0 = y1 - (y2 - y1) / 2;
                x0 = x1 - (x2 - x1);
            }
            double y3;
            double x3;
            if (i < end - 1) {
                y3 = scaledY[i + 1];
                x3 = scaledX[i + 1];
            } else {
                y3 = y2 + (y2 - y1) / 2;
                x3 = x2 + (x2 - x1) / 2;
            }

            // Convert to Bezier
            double bx0 = x1;
            double by0 = y1;
            double bx3 = x2;
            double by3 = y2;
            double bdy0 = (y2 - y0) / (x2 - x0);
            double bdy3 = (y3 - y1) / (x3 - x1);
            double bx1 = bx0 + (x2 - x0) / 6.0;
            double by1 = (bx1 - bx0) * bdy0 + by0;
            double bx2 = bx3 - (x3 - x1) / 6.0;
            double by2 = (bx2 - bx3) * bdy3 + by3;

            path.curveTo(bx1, by1, bx2, by2, bx3, by3);
        }
        return path;
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
        return xValueScale.scaleValue(value, xPlotValueStart, xPlotValueEnd, xPlotCoordStart, xPlotCoordEnd);
    }

    protected final double scaledY(double value) {
        return yValueScale.scaleValue(value, yPlotValueStart, yPlotValueEnd, yPlotCoordEnd, yPlotCoordStart);
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
