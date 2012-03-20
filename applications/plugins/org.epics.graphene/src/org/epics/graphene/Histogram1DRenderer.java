/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import static org.epics.graphene.NumberUtil.normalize;

/**
 *
 * @author carcassi
 */
public class Histogram1DRenderer {

    public Histogram1DRenderer(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }
    
    private int imageWidth;
    private int imageHeight;

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void update(Histogram1DRendererUpdate update) {
        if (update.getImageHeight() != null)
            imageHeight = update.getImageHeight();
        if (update.getImageWidth() != null)
            imageWidth = update.getImageWidth();
    }

    public void draw(Graphics2D graphics, Histogram1D hist) {
        int imageWidth = this.getImageWidth();
        int imageHeight = this.getImageHeight();
        
        Color backgroundColor = Color.WHITE;
        Color axisTextColor = Color.BLACK;
        Color axisColor = Color.BLACK;
        Color dividerColor = new Color(196, 196, 196);
        Color lineColor = new Color(140, 140, 140);
        Color histogramColor = new Color(175, 175, 175);
        
        double xValueMin = hist.getMinValueRange();
        double xValueMax = hist.getMaxValueRange();
        ValueAxis xAxis = ValueAxis.createAutoAxis(xValueMin, xValueMax, imageWidth / 60);
        double[] xValueTicks = xAxis.getTickValues();
        String[] xLabels = xAxis.getTickLabels();
        
        int yValueMin = hist.getMinCountRange();
        int yValueMax = hist.getMaxCountRange();
        // In bigger plots, too many horizonal lines make it too confusing,
        // so distance between each vertical ticks is higher at smaller heights
        // and smaller at higher heights.
        int nYTicks = 0;
        if (imageHeight < 80) {
            nYTicks = 2;
        } else if (imageHeight < 360) {
            nYTicks = 6;
        } else {
            nYTicks = imageHeight / 60;
        }
        ValueAxis yAxis = ValueAxis.createAutoAxis(yValueMin, yValueMax, Math.max(4, nYTicks), 1.0);
        double[] yValueTicks = yAxis.getTickValues();
        String[] yLabels = yAxis.getTickLabels();

        // Labels
        Font axisFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        graphics.setFont(axisFont);
        
        // Compute axis size
        
        int margin = 3; // 3 px of margin all around
        int axisMargin = 3; // 3 px of margin all around
        int xAxisTickSize = 3;
        FontMetrics metrics = graphics.getFontMetrics();
        
        // Compute x axis spacing
        int[] xLabelWidths = new int[xLabels.length];
        for (int i = 0; i < xLabelWidths.length; i++) {
            xLabelWidths[i] = metrics.stringWidth(xLabels[i]);
        }
        int xAxisFromBottom = margin + metrics.getHeight() - metrics.getLeading() + axisMargin;
        
        // Compute y axis spacing
        int[] yLabelWidths = new int[yLabels.length];
        int yLargestLabel = 0;
        for (int i = 0; i < yLabelWidths.length; i++) {
            yLabelWidths[i] = metrics.stringWidth(yLabels[i]);
            yLargestLabel = Math.max(yLargestLabel, yLabelWidths[i]);
        }
        int yAxisFromLeft = margin + yLargestLabel + axisMargin;
        
        // Compute plot size
        
        int plotWidth = imageWidth - yAxisFromLeft - margin;
        int plotHeight = imageHeight - xAxisFromBottom - margin;
        
        // Compute ticks
        int[] xTicks = new int[xLabels.length];
        for (int i = 0; i < xTicks.length; i++) {
            xTicks[i] = yAxisFromLeft + (int) (normalize(xValueTicks[i], xValueMin, xValueMax) * plotWidth);
        }
        int[] yTicks = new int[yLabels.length];
        for (int i = 0; i < yTicks.length; i++) {
            yTicks[i] = xAxisFromBottom + (int) (normalize(yValueTicks[i], yValueMin, yValueMax) * plotHeight);
        }
        
        // Compute bin limits
        int[] binLimitsPx = new int[hist.getNBins() + 1];
        int[] binHeightsPx = new int[hist.getNBins()];
        
        for (int i = 0; i < hist.getNBins(); i++) {
            binLimitsPx[i] = yAxisFromLeft + (int) (normalize(hist.getBinValueBoundary(i), xValueMin, xValueMax) * plotWidth);
            binHeightsPx[i] = (int) (normalize(hist.getBinCount(i), yValueMin, yValueMax) * plotHeight);
        }
        binLimitsPx[hist.getNBins()] = yAxisFromLeft + (int) (normalize(hist.getBinValueBoundary(hist.getNBins()), xValueMin, xValueMax) * plotWidth);

        // Draw background
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        // Draw x-axis
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(axisColor);
        graphics.drawLine(yAxisFromLeft, imageHeight - xAxisFromBottom, yAxisFromLeft + plotWidth, imageHeight - xAxisFromBottom);
        for (int i = 0; i < xLabels.length; i++) {
            Rectangle2D bounds = metrics.getStringBounds(xLabels[i], graphics);
            graphics.setColor(axisTextColor);
            graphics.drawString(xLabels[i], xTicks[i] - ((int) ((bounds.getWidth() / 2))), imageHeight - margin);
            graphics.setColor(axisColor);
            graphics.drawLine(xTicks[i], imageHeight - xAxisFromBottom, xTicks[i], imageHeight - xAxisFromBottom + xAxisTickSize);
        }

        // Draw y-axis
        graphics.setColor(axisTextColor);
        for (int i = 0; i < yLabels.length; i++) {
            int halfHeight = (metrics.getAscent()) / 2 - 1;
            graphics.drawString(yLabels[i], yAxisFromLeft - yLabelWidths[i] - axisMargin, imageHeight - yTicks[i] + halfHeight);
        }
        
        // Draw histogram area
        for (int i = 0; i < binHeightsPx.length; i++) {
            graphics.setColor(histogramColor);
            graphics.fillRect(binLimitsPx[i], imageHeight - xAxisFromBottom - binHeightsPx[i], binLimitsPx[i+1] - binLimitsPx[i], binHeightsPx[i]);
            graphics.setColor(dividerColor);
            // Draw the divider only if the vertical size is more than 0
            if (binHeightsPx[i] > 0)
                graphics.drawLine(binLimitsPx[i], imageHeight - xAxisFromBottom - binHeightsPx[i], binLimitsPx[i], imageHeight - xAxisFromBottom - 1);
        }
        
        // Draw horizontal reference lines
        for (int i = 0; i < yTicks.length; i++) {
            if (yTicks[i] != xAxisFromBottom) {
                graphics.setColor(backgroundColor);
                graphics.drawLine(yAxisFromLeft, imageHeight - yTicks[i], yAxisFromLeft + plotWidth, imageHeight - yTicks[i]);
            }
        }
        
        // Draw histogram contour
        int previousHeight = 0;
        for (int i = 0; i < binHeightsPx.length; i++) {
            graphics.setColor(lineColor);
            // Draw the countour only when the height is not 0
            if (binHeightsPx[i] > 0 || previousHeight > 0) {
                graphics.drawLine(binLimitsPx[i], imageHeight - xAxisFromBottom - Math.max(previousHeight, 1), binLimitsPx[i], imageHeight - xAxisFromBottom - Math.max(binHeightsPx[i], 1));
            }
            if (binHeightsPx[i] > 0) {
                graphics.drawLine(binLimitsPx[i], imageHeight - xAxisFromBottom - binHeightsPx[i], binLimitsPx[i+1], imageHeight - xAxisFromBottom - binHeightsPx[i]);
            }
            previousHeight = binHeightsPx[i];
        }
        if (previousHeight > 0)
            graphics.drawLine(binLimitsPx[binLimitsPx.length - 1], imageHeight - xAxisFromBottom - previousHeight, binLimitsPx[binLimitsPx.length - 1], imageHeight - xAxisFromBottom - 1);
        
        
        
    }
}
