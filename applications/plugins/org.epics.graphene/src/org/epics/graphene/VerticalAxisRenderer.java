/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * A rendering tool that is capable of drawing a vertical (Y) axis
 * with appropriate labels and label spacing.
 * 
 * <p>The <code>VerticalAxisRenderer</code> is package-private and
 * is inaccessible out of the Graphene package.
 * 
 * @author carcassi
 */
class VerticalAxisRenderer {
    
    private ValueAxis   axis;
    private Color       axisTextColor = Color.BLACK;
    private FontMetrics metrics;
    
    private static Font defaultAxisFont = FontUtil.getLiberationSansRegular();
    private Font        axisFont = defaultAxisFont;
    
    private int      axisWidth;
    private int      tickSize = 3;
    private int      textTickMargin = 0;
    private String[] yLabels;
    private double[] yValueTicks;
    private int[]    yLabelWidths;

    private int[] yTicks;

    /**
     * Creates the render tool for the vertical axis.
     * Package-private and instantiation is not possible outside of the Graphene package.
     * @param valueAxis the values for the axis
     * @param leftMargin the margin size in pixels between the left side of the image and the vertical axis
     * @param graphics how to calculate the fonts
     */
    VerticalAxisRenderer(ValueAxis valueAxis, int leftMargin, Graphics2D graphics) {
        this.axis = valueAxis;
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
    
    /**
     * Draws the tick marks on the vertical axis with each value associated with the tick mark.
     * @param graphics where drawing is done
     * @param startImage the vertical (Y) pixel for the start location of the image
     * @param startAxis the vertical (Y) pixel for the start location of the vertical axis
     * @param endAxis the vertical (Y) pixel for the end location of the vertical axis
     * @param endImage the vertical (Y) pixel for the end location of the image
     * @param axisPosition the distance from the edge of the image to the vertical axis
     */
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
    
    /**
     * Returns the width of the axis.
     * @return width of axis in pixels
     */
    public int getAxisWidth() {
        return axisWidth;
    }
    
    /**
     * Returns the 'tick' positions on the vertical axis.
     * @return set of 'ticks' on the vertical y-axis
     */
    public int[] verticalTickPositions() {
        return yTicks;
    }
    
}
