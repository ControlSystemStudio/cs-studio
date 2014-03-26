/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
import static org.epics.graphene.InterpolationScheme.NEAREST_NEIGHBOUR;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 * The base class for all graph renderers.
 *
 * @author carcassi
 */
public abstract class TemporalGraph2DRenderer<T extends TemporalGraph2DRendererUpdate> {
    
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
    public TemporalGraph2DRenderer(int graphWidth, int graphHeight) {
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
    private TimeAxisRange timeAxisRange = TimeAxisRanges.relative();
    private AxisRange axisRange = AxisRanges.integrated();
    // Strategy for generating labels and scaling value of the axis
    private TimeScale timeScale = TimeScales.linearAbsoluteScale();
    private ValueScale valueScale = ValueScales.linearScale();
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
    
    private Range aggregatedValueRange;
    private TimeInterval aggregatedTimeInterval;
    private Range plotValueRange;
    private TimeInterval plotTimeInterval;
    protected FontMetrics labelFontMetrics;
    protected ListDouble xReferenceCoords;
    protected ListDouble valueReferences;
    protected List<String> valueReferenceLabels;
    protected ListDouble yReferenceCoords;
    protected List<Timestamp> timeReferences;
    protected ListDouble normalizedTimeReferences;
    protected List<String> timeReferenceLabels;
    private int xLabelMaxHeight;
    private int yLabelMaxWidth;

    /**
     * The current strategy to calculate the x range for the graph.
     * 
     * @return the x axis range calculator
     */
    public AxisRange getAxisRange() {
        return axisRange;
    }

    /**
     * The current strategy to calculate the y range for the graph.
     * 
     * @return the y axis range calculator
     */
    public TimeAxisRange getTimeAxisRange() {
        return timeAxisRange;
    }

    /**
     * The aggregated range of all the data that has been rendered.
     * 
     * @return the aggregated data x range
     */
    public Range getAggregatedRange() {
        return aggregatedValueRange;
    }

    /**
     * The aggregated range of all the data that has been rendered.
     * 
     * @return the aggregated data y range
     */
    public TimeInterval getAggregatedTimeInterval() {
        return aggregatedTimeInterval;
    }

    /**
     * The range of the x axis in the last graph rendering.
     * 
     * @return the x axis range
     */
    public Range getPlotRange() {
        return plotValueRange;
    }

    /**
     * The range of the y axis in the last graph rendering.
     * 
     * @return the y axis range
     */
    public TimeInterval getPlotTimeInterval() {
        return plotTimeInterval;
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
        if (update.getAxisRange() != null) {
            axisRange = update.getAxisRange();
        }
        if (update.getTimeAxisRange() != null) {
            timeAxisRange = update.getTimeAxisRange();
        }
        if (update.getValueScale()!= null) {
            valueScale = update.getValueScale();
        }
        if (update.getTimeScale() != null) {
            timeScale = update.getTimeScale();
        }
    }
    
    static Range aggregateRange(Range dataRange, Range aggregatedRange) {
        if (aggregatedRange == null) {
            return dataRange;
        } else {
            return RangeUtil.sum(dataRange, aggregatedRange);
        }
    }
    
    static TimeInterval aggregateTimeInterval(TimeInterval dataTimeInterval, TimeInterval aggregatedTimeInterval) {
        if (aggregatedTimeInterval == null) {
            return dataTimeInterval;
        } else {
            if (aggregatedTimeInterval.contains(dataTimeInterval.getEnd()) &&
                    aggregatedTimeInterval.contains(dataTimeInterval.getStart())) {
                return aggregatedTimeInterval;
            }
            if (dataTimeInterval.contains(aggregatedTimeInterval.getEnd()) &&
                    dataTimeInterval.contains(aggregatedTimeInterval.getStart())) {
                return dataTimeInterval;
            }
            Timestamp start;
            Timestamp end;
            if (dataTimeInterval.getStart().compareTo(aggregatedTimeInterval.getStart()) > 0) {
                start = aggregatedTimeInterval.getStart();
            } else {
                start = dataTimeInterval.getStart();
            }
            if (dataTimeInterval.getEnd().compareTo(aggregatedTimeInterval.getEnd()) < 0) {
                end = aggregatedTimeInterval.getEnd();
            } else {
                end = dataTimeInterval.getEnd();
            }
            return TimeInterval.between(start, end);
        }
    }
    
    /**
     * Creates a new update for the given graph.
     * 
     * @return a new update object
     */
    public abstract T newUpdate();
    
    protected void calculateRanges(Range valueRange, TimeInterval timeInterval) {
        aggregatedValueRange = aggregateRange(valueRange, aggregatedValueRange);
        aggregatedTimeInterval = aggregateTimeInterval(timeInterval, aggregatedTimeInterval);
        plotValueRange = axisRange.axisRange(valueRange, aggregatedValueRange);
        plotTimeInterval = timeAxisRange.axisRange(timeInterval, aggregatedTimeInterval);
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
        TimeAxis timeAxis = timeScale.references(plotTimeInterval, 2, Math.max(2, getImageWidth() / 100));
        ValueAxis valueAxis = valueScale.references(plotValueRange, 2, Math.max(2, getImageHeight()/ 60));
        timeReferenceLabels = timeAxis.getTickLabels();
        valueReferenceLabels = Arrays.asList(valueAxis.getTickLabels());
        timeReferences = timeAxis.getTimestamps();
        normalizedTimeReferences = timeAxis.getNormalizedValues();
        valueReferences = new ArrayDouble(valueAxis.getTickValues());
        
        labelFontMetrics = g.getFontMetrics(labelFont);
        
        // Compute x axis spacing
        xLabelMaxHeight = labelFontMetrics.getHeight() - labelFontMetrics.getLeading();
        int areaFromBottom = bottomMargin + xLabelMaxHeight + xLabelMargin;
        
        // Compute y axis spacing
        int[] yLabelWidths = new int[valueReferenceLabels.size()];
        yLabelMaxWidth = 0;
        for (int i = 0; i < yLabelWidths.length; i++) {
            yLabelWidths[i] = labelFontMetrics.stringWidth(valueReferenceLabels.get(i));
            yLabelMaxWidth = Math.max(yLabelMaxWidth, yLabelWidths[i]);
        }
        int areaFromLeft = leftMargin + yLabelMaxWidth + yLabelMargin;

        xPlotValueStart = 0.0;
        yPlotValueStart = getPlotRange().getMinimum().doubleValue();
        xPlotValueEnd = 1.0;
        yPlotValueEnd = getPlotRange().getMaximum().doubleValue();
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
        
        double[] xRefCoords = new double[normalizedTimeReferences.size()];
        for (int i = 0; i < xRefCoords.length; i++) {
            xRefCoords[i] = scaledX(normalizedTimeReferences.getDouble(i));
        }
        xReferenceCoords = new ArrayDouble(xRefCoords);
        
        double[] yRefCoords = new double[valueReferences.size()];
        for (int i = 0; i < yRefCoords.length; i++) {
            yRefCoords[i] = scaledY(valueReferences.getDouble(i));
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
    
    protected void drawValueLine(ListNumber xValues, ListNumber yValues, InterpolationScheme interpolation) {
        // Scale data and sort data
        int dataCount = xValues.size();
        double[] scaledX = new double[dataCount];
        double[] scaledY = new double[dataCount];
        for (int i = 0; i < scaledY.length; i++) {
            scaledX[i] = scaledX(xValues.getDouble(i));
            scaledY[i] = scaledY(yValues.getDouble(i));;
        }
        
        Path2D path;
        switch (interpolation) {
            default:
                throw new IllegalArgumentException("Interpolation " + interpolation + " not supported");
            case NEAREST_NEIGHBOUR:
                path = nearestNeighbour(scaledX, scaledY);
                break;
            case PREVIOUS_VALUE:
                path = previousValue(scaledX, scaledY);
                break;
            case LINEAR:
                path = linearInterpolation(scaledX, scaledY);
                break;
            case CUBIC:
                path = cubicInterpolation(scaledX, scaledY);
        }

        // Draw the line
        g.draw(path);
    }

    private static Path2D.Double nearestNeighbour(double[] scaledX, double[] scaledY) {
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[0], scaledY[0]);
        for (int i = 1; i < scaledY.length; i++) {
            double halfX = scaledX[i - 1] + (scaledX[i] - scaledX[i - 1]) / 2;
            if (!java.lang.Double.isNaN(scaledY[i-1])) {
                line.lineTo(halfX, scaledY[i - 1]);
                if (!java.lang.Double.isNaN(scaledY[i]))
                    line.lineTo(halfX, scaledY[i]);
            } else {
                line.moveTo(halfX, scaledY[i]);
            }
        }
        line.lineTo(scaledX[scaledX.length - 1], scaledY[scaledY.length - 1]);
        return line;
    }

    private static Path2D.Double previousValue(double[] scaledX, double[] scaledY) {
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[0], scaledY[0]);
        // TODO: review for NaN support
        for (int i = 1; i < scaledY.length; i++) {
            line.lineTo(scaledX[i], scaledY[i-1]);
            line.lineTo(scaledX[i], scaledY[i]);
        }
//        line.lineTo(scaledX[scaledX.length - 1], scaledY[scaledY.length - 1]);
        //TODO: last value till end of the graph 
        return line;
    }

    private static Path2D.Double linearInterpolation(double[] scaledX, double[] scaledY) {
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[0], scaledY[0]);
        for (int i = 1; i < scaledY.length; i++) {
            line.lineTo(scaledX[i], scaledY[i]);
        }
        return line;
    }

    private static Path2D.Double cubicInterpolation(double[] scaledX, double[] scaledY) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(scaledX[0], scaledY[0]);
        for (int i = 1; i < scaledY.length; i++) {
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
            if (i < scaledY.length - 1) {
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
        return timeScale.scaleNormalizedTime(value, xPlotCoordStart, xPlotCoordEnd);
    }

    protected final double scaledY(double value) {
        return valueScale.scaleValue(value, yPlotValueStart, yPlotValueEnd, yPlotCoordEnd, yPlotCoordStart);
    }
    
    protected void setClip(Graphics2D g) {
        g.setClip(xAreaStart, yAreaStart, xAreaEnd - xAreaStart + 1, yAreaEnd - yAreaStart + 1);
    }

    protected void drawYLabels() {
        // Draw Y labels
        ListNumber yTicks = yReferenceCoords;
        if (valueReferenceLabels != null && !valueReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {yAreaStart, yAreaEnd};
            int xRightLabel = (int) (xAreaStart - yLabelMargin - 1);
            drawHorizontalReferencesLabel(g, metrics, valueReferenceLabels.get(0), (int) Math.floor(yTicks.getDouble(0)),
                drawRange, xRightLabel, true, false);
            drawHorizontalReferencesLabel(g, metrics, valueReferenceLabels.get(valueReferenceLabels.size() - 1), (int) Math.floor(yTicks.getDouble(valueReferenceLabels.size() - 1)),
                drawRange, xRightLabel, false, false);
            
            for (int i = 1; i < valueReferenceLabels.size() - 1; i++) {
                drawHorizontalReferencesLabel(g, metrics, valueReferenceLabels.get(i), (int) Math.floor(yTicks.getDouble(i)),
                    drawRange, xRightLabel, true, false);
            }
        }
    }

    protected void drawXLabels() {
        // Draw X labels
        ListNumber xTicks = xReferenceCoords;
        if (timeReferenceLabels != null && !timeReferenceLabels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {xAreaStart, xAreaEnd};
            int yTop = (int) (yAreaEnd + xLabelMargin + 1);
            drawVerticalReferenceLabel(g, metrics, timeReferenceLabels.get(0), (int) Math.floor(xTicks.getDouble(0)),
                drawRange, yTop, true, false);
            drawVerticalReferenceLabel(g, metrics, timeReferenceLabels.get(timeReferenceLabels.size() - 1), (int) Math.floor(xTicks.getDouble(timeReferenceLabels.size() - 1)),
                drawRange, yTop, false, false);
            
            for (int i = 1; i < timeReferenceLabels.size() - 1; i++) {
                drawVerticalReferenceLabel(g, metrics, timeReferenceLabels.get(i), (int) Math.floor(xTicks.getDouble(i)),
                    drawRange, yTop, true, false);
            }
        }
    }

}
