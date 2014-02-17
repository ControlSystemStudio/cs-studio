/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.*;

/**
 *
 * @author carcassi
 */
public class AreaGraph2DRenderer extends Graph2DRenderer<AreaGraph2DRendererUpdate> {

    public AreaGraph2DRenderer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
    }

    @Override
    public AreaGraph2DRendererUpdate newUpdate() {
        return new AreaGraph2DRendererUpdate();
    }

    /**
     *Draws the area to be put behind a graph on the given Graphics2D context, given the Cell1DDataset.
     * @param graphics Graphics2D: can not be null.
     * @param dataset Cell1DDataset 
     */
    public void draw(Graphics2D graphics, Cell1DDataset dataset) {
        
        Color dividerColor = new Color(196, 196, 196);
        Color lineColor = new Color(140, 140, 140);
        Color histogramColor = new Color(175, 175, 175);

        this.g = graphics;
        this.referenceLineColor = this.backgroundColor;
        calculateRanges(dataset.getXRange(), dataset.getStatistics());
        calculateLabels();
        calculateGraphArea();
        drawBackground();
        drawGraphArea();

        // Compute bin limits
        int[] binLimitsPx = new int[dataset.getXCount() + 1];
        int[] binHeightsPx = new int[dataset.getXCount()];
        
        for (int i = 0; i < dataset.getXCount(); i++) {
            binLimitsPx[i] = (int) scaledX(dataset.getXBoundaries().getDouble(i));
            binHeightsPx[i] = (int) scaledY(dataset.getValue(i));
        }
        binLimitsPx[dataset.getXCount()] = (int) scaledX(dataset.getXBoundaries().getDouble(dataset.getXCount()));
        
        // Draw histogram area
        int plotStart = (int) scaledY(getYPlotRange().getMinimum().doubleValue());
        for (int i = 0; i < binHeightsPx.length; i++) {
            graphics.setColor(histogramColor);
            graphics.fillRect(binLimitsPx[i], binHeightsPx[i], binLimitsPx[i+1] - binLimitsPx[i], plotStart - binHeightsPx[i]);
            graphics.setColor(dividerColor);
            // Draw the divider only if the vertical size is more than 0
            if ((plotStart - binHeightsPx[i]) > 0) {
                graphics.drawLine(binLimitsPx[i], binHeightsPx[i], binLimitsPx[i], plotStart);
            }
        }
        
        // Draw horizontal reference lines
        graphics.setColor(backgroundColor);
        drawHorizontalReferenceLines();
        
        // Draw histogram contour
        int previousHeight = plotStart;
        for (int i = 0; i < binHeightsPx.length; i++) {
            graphics.setColor(lineColor);
            graphics.drawLine(binLimitsPx[i], previousHeight, binLimitsPx[i], binHeightsPx[i]);
            graphics.drawLine(binLimitsPx[i], binHeightsPx[i], binLimitsPx[i+1], binHeightsPx[i]);
            previousHeight = binHeightsPx[i];
        }
        if (previousHeight > 0)
            graphics.drawLine(binLimitsPx[binLimitsPx.length - 1], previousHeight, binLimitsPx[binLimitsPx.length - 1], plotStart);
        
    }

}
