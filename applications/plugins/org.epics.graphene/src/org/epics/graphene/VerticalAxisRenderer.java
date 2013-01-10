/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 *
 * @author carcassi
 */
class VerticalAxisRenderer {
    
    private ValueAxis axis;
    private static Font defaultAxisFont = FontUtil.getLiberationSansRegular();
    private Font axisFont = defaultAxisFont;
    private int axisWidth;
    private int leftMargin;
    private int tickSize = 3;
    private int textTickMargin = 0;
    private String[] yLabels;
    private double[] yValueTicks;
    private int[] yLabelWidths;
    private Color axisColor = Color.BLACK;
    private Color axisTextColor = Color.BLACK;
    private FontMetrics metrics;
    private int[] yTicks;

    VerticalAxisRenderer(ValueAxis valueAxis, int leftMargin, Graphics2D graphics) {
        this.axis = valueAxis;
        this.leftMargin = leftMargin;
        metrics = graphics.getFontMetrics(axisFont);
        
        // Compute y axis spacing
        yValueTicks = axis.getTickValues();
        yLabels = axis.getTickLabels();
        yLabelWidths = new int[yLabels.length];
        int yLargestLabel = 0;
        for (int i = 0; i < yLabelWidths.length; i++) {
            yLabelWidths[i] = metrics.stringWidth(yLabels[i]);
            yLargestLabel = Math.max(yLargestLabel, yLabelWidths[i]);
        }
        axisWidth = leftMargin + yLargestLabel + textTickMargin + tickSize;
        
    }
    
    public void draw(Graphics2D graphics, int startImage, int startAxis, int endAxis, int endImage, int axisPosition) {
        int plotHeight = endAxis - startAxis;
        int imageHeight = endImage - startImage;
        
        yTicks = new int[yLabels.length];
        for (int i = 0; i < yTicks.length; i++) {
            yTicks[i] = imageHeight - endAxis + (int) (NumberUtil.normalize(yValueTicks[i], axis.getMinValue(), axis.getMaxValue()) * plotHeight);
        }

        // Draw y-axis
        graphics.setColor(axisTextColor);
        graphics.setFont(axisFont);
        for (int i = 0; i < yLabels.length; i++) {
            int halfHeight = (metrics.getAscent()) / 2 - 1;
            graphics.drawString(yLabels[i], axisPosition - yLabelWidths[i] - textTickMargin - tickSize, imageHeight - yTicks[i] + halfHeight);
        }
    }
    
    public int getAxisWidth() {
        return axisWidth;
    }
    
    public int[] verticalTickPositions() {
        return yTicks;
    }
    
}
