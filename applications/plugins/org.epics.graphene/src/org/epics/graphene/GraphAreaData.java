/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.graphene;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class GraphAreaData {
    
    GraphBuffer graphBuffer;
    
    Range xValueRange;
    Range yValueRange;
    ValueScale xValueScale;
    ValueScale yValueScale;
    
    Font labelFont;
    Color labelColor;
    Color referenceLineColor;
    
    ListInt xReferencePixels;
    ListDouble xReferenceValues;
    List<String> xReferenceLabels;
    int xLabelMargin;
    int xLabelMaxHeight;
    
    ListInt yReferencePixels;
    ListDouble yReferenceValues;
    List<String> yReferenceLabels;
    int yLabelMargin;
    int yLabelMaxWidth;
    
    int xGraphLeft;
    int xGraphRight;
    int yGraphBottom;
    int yGraphTop;
    
    int xAreaLeft;
    int xAreaRight;
    int yAreaBottom;
    int yAreaTop;
    
    int bottomAreaMargin = 0;
    int topAreaMargin = 0;
    int leftAreaMargin = 0;
    int rightAreaMargin = 0;

    /**
     * Changes the buffer where the graph area is going to be rendered.
     * 
     * @param graphBuffer a graph buffer
     */
    public void setGraphBuffer(GraphBuffer graphBuffer) {
        this.graphBuffer = graphBuffer;
    }

    /**
     * Change the portion of the buffer allocated to displaying the graph.
     * It gives the range of pixels (inclusive of both sides) where the
     * graph will be displayed.
     * <p>
     * The coordinate system is that of a standard image, where (0,0) is the
     * top left corner.
     * 
     * @param xAreaLeft the first pixel on the left (inclusive)
     * @param yAreaBottom the first pixel on the bottom (inclusive)
     * @param xAreaRight the last pixel on the right (inclusive)
     * @param yAreaTop the last pixel on the top (inclusive)
     */
    public void setGraphArea(int xAreaLeft, int yAreaBottom, int xAreaRight, int yAreaTop) {
        this.xAreaLeft = xAreaLeft;
        this.yAreaBottom = yAreaBottom;
        this.xAreaRight = xAreaRight;
        this.yAreaTop = yAreaTop;
    }

    /**
     * Changes the margin of the graph area, effectively moving the displayed
     * value range further into the graph. This is useful if value points
     * are represented by a glyph, and one wants to leave space so that the
     * glyph is not cropped. The space is still on the graph area, so reference
     * lines and other points right outside the value range will be displayed.
     * 
     * @param leftAreaMargin the number of pixels to be left to the left of the values
     * @param bottomAreaMargin the number of pixels to be left to the bottom of the values
     * @param rightAreaMargin the number of pixels to be left to the right of the values
     * @param topAreaMargin the number of pixels to be left to the top of the values
     */
    public void setGraphAreaMargins(int leftAreaMargin, int bottomAreaMargin, int rightAreaMargin, int topAreaMargin) {
        this.leftAreaMargin = leftAreaMargin;
        this.bottomAreaMargin = bottomAreaMargin;
        this.rightAreaMargin = rightAreaMargin;
        this.topAreaMargin = topAreaMargin;
    }
    
    /**
     * Changes the margin between the labels and the graph area. This are
     * is left blank, and is not part of the graph area.
     * 
     * @param xLabelMargin margin in pixel between the bottom labels and the bottom part of the graph
     * @param yLabelMargin margin in pixel between the left labels and the left part of the graph
     */
    public void setLabelMargin(int xLabelMargin, int yLabelMargin) {
        this.xLabelMargin = xLabelMargin;
        this.yLabelMargin = yLabelMargin;
    }
    
    /**
     * Sets the ranges of the values that are going to be displayed.
     * <p>
     * The value ranges need for a series of calculation before it is actually
     * determined to what pixels they correspond in the graph area.
     * 
     * @param xValueRange the ranges of values on the horizontal axis
     * @param xValueScale the scale for the horizontal axis
     * @param yValueRange the ranges of values on the vertical axis
     * @param yValueScale the scale for the vertical axis
     */
    public void setRanges(Range xValueRange, ValueScale xValueScale, Range yValueRange, ValueScale yValueScale) {
        this.xValueRange = xValueRange;
        this.yValueRange = yValueRange;
        this.xValueScale = xValueScale;
        this.yValueScale = yValueScale;
    }
    
    /**
     * Prepares the label text and values for both the bottom and left axis.
     * 
     * @param labelFont the font for the label
     * @param labelColor the color for the label
     */
    public void prepareLabels(Font labelFont, Color labelColor) {
        this.labelFont = labelFont;
        this.labelColor = labelColor;
        
        // Calculate horizontal axis references. If range is zero, use special logic
        if (!xValueRange.getMinimum().equals(xValueRange.getMaximum())) {
            ValueAxis xAxis = xValueScale.references(xValueRange, 2, Math.max(2, (xAreaRight - xAreaLeft + 1) / 55));
            xReferenceLabels = Arrays.asList(xAxis.getTickLabels());
            xReferenceValues = new ArrayDouble(xAxis.getTickValues());            
        } else {
            // TODO: use something better to format the number
            xReferenceLabels = Collections.singletonList(xValueRange.getMinimum().toString());
            xReferenceValues = new ArrayDouble(xValueRange.getMinimum().doubleValue());            
        }      
        
        // Calculate vertical axis references. If range is zero, use special logic
        if (!yValueRange.getMinimum().equals(yValueRange.getMaximum())) {
            ValueAxis yAxis = yValueScale.references(yValueRange, 2, Math.max(2, (yAreaBottom - yAreaTop + 1) / 55));
            yReferenceLabels = Arrays.asList(yAxis.getTickLabels());
            yReferenceValues = new ArrayDouble(yAxis.getTickValues());
        } else {
            // TODO: use something better to format the number
            yReferenceLabels = Collections.singletonList(yValueRange.getMinimum().toString());
            yReferenceValues = new ArrayDouble(yValueRange.getMinimum().doubleValue());            
        }
        
        FontMetrics labelFontMetrics = graphBuffer.getGraphicsContext().getFontMetrics(labelFont);
        
        // Compute x axis spacing
        xLabelMaxHeight = labelFontMetrics.getHeight() - labelFontMetrics.getLeading();
        
        // Compute y axis spacing
        int[] yLabelWidths = new int[yReferenceLabels.size()];
        yLabelMaxWidth = 0;
        for (int i = 0; i < yLabelWidths.length; i++) {
            yLabelWidths[i] = labelFontMetrics.stringWidth(yReferenceLabels.get(i));
            yLabelMaxWidth = Math.max(yLabelMaxWidth, yLabelWidths[i]);
        }
    }
    
    /**
     * If the range is zero, fake a range.
     * 
     * @param range a range
     * @return the same range, or one that is safe to draw
     */
    private Range safeRange(Range range) {
        if (range.getMinimum().doubleValue() == range.getMaximum().doubleValue()) {
            return RangeUtil.range(range.getMinimum().doubleValue() - 1.0, range.getMaximum().doubleValue() + 1.0);
        } else {
            return range;
        }
    }
    
    /**
     * Final computation to prepare pixel position of graph area and references.
     * 
     * @param asCell whether the graph area should represent cells or points
     */
    public void prepareGraphArea(boolean asCell, Color referenceLineColor) {
        this.referenceLineColor = referenceLineColor;
        
        // Prepare x positions
        xGraphLeft = xAreaLeft + yLabelMaxWidth + yLabelMargin;
        xGraphRight = xAreaRight;
        if (asCell) {
            graphBuffer.setXScaleAsCell(safeRange(xValueRange), xGraphLeft + leftAreaMargin, xGraphRight - rightAreaMargin, xValueScale);
        } else {
            graphBuffer.setXScaleAsPoint(safeRange(xValueRange), xGraphLeft + leftAreaMargin, xGraphRight - rightAreaMargin, xValueScale);
        }
        
        // Prepare y positions
        yGraphTop = yAreaTop;
        yGraphBottom = yAreaBottom - xLabelMaxHeight - xLabelMargin;
        if (asCell) {
            graphBuffer.setYScaleAsCell(safeRange(yValueRange), yGraphBottom - bottomAreaMargin, yGraphTop + topAreaMargin, yValueScale);
        } else {
            graphBuffer.setYScaleAsPoint(safeRange(yValueRange), yGraphBottom - bottomAreaMargin, yGraphTop + topAreaMargin, yValueScale);
        }
        
        //Only calculates reference coordinates if calculateLabels() was called
        if (xReferenceValues != null) {
            int[] xRefCoords = new int[xReferenceValues.size()];
            for (int i = 0; i < xRefCoords.length; i++) {
                xRefCoords[i] = graphBuffer.xValueToPixel(xReferenceValues.getDouble(i));
            }
            if (asCell && xRefCoords[xReferenceValues.size() - 1] == xGraphRight + 1) {
                xRefCoords[xReferenceValues.size() - 1]--;
            }
            xReferencePixels = new ArrayInt(xRefCoords);
        }
        
        if (yReferenceValues != null) {
            int[] yRefCoords = new int[yReferenceValues.size()];
            for (int i = 0; i < yRefCoords.length; i++) {
                yRefCoords[i] = graphBuffer.yValueToPixel(yReferenceValues.getDouble(i));
            }
            if (asCell && yRefCoords[yReferenceValues.size() - 1] == yGraphTop - 1) {
                yRefCoords[yReferenceValues.size() - 1]++;
            }
            yReferencePixels = new ArrayInt(yRefCoords);
        }
    }
    
    protected void drawGraphArea() {
        graphBuffer.getGraphicsContext().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphBuffer.drawVerticalReferenceLines(xReferencePixels, referenceLineColor, yGraphBottom, yGraphTop);
        graphBuffer.drawHorizontalReferenceLines(yReferencePixels, referenceLineColor, xGraphLeft, xGraphRight);
        
        graphBuffer.drawBottomLabels(xReferenceLabels, xReferencePixels, labelColor, labelFont, xGraphLeft, xGraphRight, yGraphBottom + xLabelMargin + 1);
        graphBuffer.drawLeftLabels(yReferenceLabels, yReferencePixels, labelColor, labelFont, yGraphBottom, yGraphTop, xGraphLeft - yLabelMargin - 1);
    }
    
}
