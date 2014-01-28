/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carcassi
 */
class HorizontalAxisRenderer {
    
    private ValueAxis axis;
    private static Font defaultAxisFont = FontUtil.getLiberationSansRegular();
    private Font axisFont = defaultAxisFont;
    private int axisHeight;
    private int bottomMargin = 3;
    private int tickSize = 3;
    private int textTickMargin = 0;
    private String[] xLabels;
    private double[] xValueTicks;
    private int[] xTicks;
    private Color axisColor = new Color(192, 192, 192);
    private Color axisTickColor = Color.BLACK;
    private Color axisTextColor = Color.BLACK;
    private FontMetrics metrics;

    HorizontalAxisRenderer(ValueAxis valueAxis, int bottomMargin, Graphics2D graphics) {
        this.axis = valueAxis;
        this.bottomMargin = bottomMargin;
        metrics = graphics.getFontMetrics(axisFont);
        
        // Compute x axis spacing
        xValueTicks = axis.getTickValues();
        xLabels = axis.getTickLabels();
        int[] xLabelWidths = new int[xLabels.length];
        for (int i = 0; i < xLabelWidths.length; i++) {
            xLabelWidths[i] = metrics.stringWidth(xLabels[i]);
        }
        axisHeight = bottomMargin + metrics.getHeight() - metrics.getLeading() + textTickMargin + tickSize;
        
    }
    
    public void draw(Graphics2D graphics, int startImage, int startAxis, int endAxis, int endImage, int axisPosition) {
        int plotWidth = endAxis - startAxis;
        int imageWidth = endImage - startImage;
        int imageHeight = axisPosition + getAxisHeight();
        xTicks = new int[xLabels.length];
        for (int i = 0; i < xTicks.length; i++) {
            xTicks[i] = startAxis + (int) (NumberUtil.normalize(xValueTicks[i], axis.getMinValue(), axis.getMaxValue()) * plotWidth);
        }
        
        // Draw x-axis
        graphics.setColor(axisColor);
        graphics.setFont(axisFont);
        graphics.drawLine(startAxis, imageHeight - getAxisHeight(), startAxis + plotWidth, imageHeight - getAxisHeight());
        int[] drawRange = new int[] {0, imageWidth};
        
        // Draw first and last value first, as they must be there
        graphics.setColor(axisTextColor);
        drawCenteredText(graphics, metrics, xLabels[0], xTicks[0], drawRange, imageHeight - bottomMargin, true, false);
        drawCenteredText(graphics, metrics, xLabels[xLabels.length - 1], xTicks[xLabels.length - 1], drawRange, imageHeight - bottomMargin, false, false);
        
        for (int i = 0; i < xLabels.length; i++) {
            graphics.setColor(axisTextColor);
            drawCenteredText(graphics, metrics, xLabels[i], xTicks[i], drawRange, imageHeight - bottomMargin, true, true);
            graphics.setColor(axisTickColor);
            graphics.drawLine(xTicks[i], imageHeight - getAxisHeight(), xTicks[i], imageHeight - getAxisHeight() + tickSize);
        }
        
    }
    
    
    public int getAxisHeight() {
        return axisHeight;
    }
    private static final int MIN = 0;
    private static final int MAX = 1;
    private static final int marginBetweenXLabels = 4;
    private static void drawCenteredText(Graphics2D graphics, FontMetrics metrics, String text, int center, int[] drawRange, int y, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < center || drawRange[MIN] > center)
            return;
        
        double width = metrics.getStringBounds(text, graphics).getWidth();
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < width)
            return;
        
        int targetX = center - (int) ((width / 2));
        if (targetX < drawRange[MIN]) {
            if (centeredOnly)
                return;
            targetX = drawRange[MIN];
        } else if (targetX + width > drawRange[MAX]) {
            if (centeredOnly)
                return;
            targetX = drawRange[MAX] - (int) width;
        }
        
        graphics.drawString(text, targetX, y);
        
        if (updateMin) {
            drawRange[MIN] = targetX + (int) width + marginBetweenXLabels;
        } else {
            drawRange[MAX] = targetX - marginBetweenXLabels;
        }
    }

    
    public int[] horizontalTickPositions() {
        return xTicks;
    }
    
}
