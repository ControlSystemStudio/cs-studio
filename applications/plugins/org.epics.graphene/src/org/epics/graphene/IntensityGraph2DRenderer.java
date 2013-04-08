/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.util.List;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class IntensityGraph2DRenderer {

    private int width = 300;
    private int height = 200;
    private ValueColorScheme colorScheme;
    
    private boolean rangeFromDataset = true;
    private double startPlotX = java.lang.Double.MIN_VALUE;
    private double endPlotX = java.lang.Double.MAX_VALUE;
    private double startPlotY = java.lang.Double.MIN_VALUE;
    private double endPlotY = java.lang.Double.MAX_VALUE;
    
    private double integratedMinX = java.lang.Double.MAX_VALUE;
    private double integratedMinY = java.lang.Double.MAX_VALUE;
    private double integratedMaxX = java.lang.Double.MIN_VALUE;
    private double integratedMaxY = java.lang.Double.MIN_VALUE;

    public IntensityGraph2DRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public IntensityGraph2DRenderer() {
        this(300, 200);
    }

    public int getImageHeight() {
        return height;
    }

    public int getImageWidth() {
        return width;
    }

    public double getEndPlotX() {
        return endPlotX;
    }

    public double getEndPlotY() {
        return endPlotY;
    }

    public double getIntegratedMaxX() {
        return integratedMaxX;
    }

    public double getIntegratedMaxY() {
        return integratedMaxY;
    }

    public double getIntegratedMinX() {
        return integratedMinX;
    }

    public double getIntegratedMinY() {
        return integratedMinY;
    }

    public double getStartPlotX() {
        return startPlotX;
    }

    public double getStartPlotY() {
        return startPlotY;
    }
    
    public void update(IntensityGraph2DRendererUpdate update) {
        if (update.getImageHeight() != null) {
            height = update.getImageHeight();
        }
        if (update.getImageWidth() != null) {
            width = update.getImageWidth();
        }
        if (update.isRangeFromDataset() != null) {
            rangeFromDataset = update.isRangeFromDataset();
        }
        if (update.getStartX() != null) {
            startPlotX = update.getStartX();
        }
        if (update.getStartY() != null) {
            startPlotY = update.getStartY();
        }
        if (update.getEndX() != null) {
            endPlotX = update.getEndX();
        }
        if (update.getEndY() != null) {
            endPlotY = update.getEndY();
        }
        
    }

    public void draw(Graphics2D g, Cell2DDataset data) {
        
        
        // Retain the integrated min/max
        integratedMinX = java.lang.Double.isNaN(data.getXRange().getMinimum().doubleValue()) ? integratedMinX : Math.min(integratedMinX, data.getXRange().getMinimum().doubleValue());
        integratedMinY = java.lang.Double.isNaN(data.getYRange().getMinimum().doubleValue()) ? integratedMinY : Math.min(integratedMinY, data.getYRange().getMinimum().doubleValue());
        integratedMaxX = java.lang.Double.isNaN(data.getXRange().getMaximum().doubleValue()) ? integratedMaxX : Math.max(integratedMaxX, data.getXRange().getMaximum().doubleValue());
        integratedMaxY = java.lang.Double.isNaN(data.getYRange().getMaximum().doubleValue()) ? integratedMaxY : Math.max(integratedMaxY, data.getYRange().getMaximum().doubleValue());
        
        // Determine range of the plot.
        // If no range is set, use the one from the dataset
        double startXPlot;
        double startYPlot;
        double endXPlot;
        double endYPlot;
        if (rangeFromDataset) {
            startXPlot = integratedMinX;
            startYPlot = integratedMinY;
            endXPlot = integratedMaxX;
            endYPlot = integratedMaxY;
        } else {
            startXPlot = startPlotX;
            startYPlot = startPlotY;
            endXPlot = endPlotX;
            endYPlot = endPlotY;
        }
        int margin = 3;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);

        // Compute axis
        ValueAxis xAxis = ValueAxis.createAutoAxis(startXPlot, endXPlot, Math.max(2, width / 60));
        ValueAxis yAxis = ValueAxis.createAutoAxis(startYPlot, endYPlot, Math.max(2, height / 60));
        HorizontalAxisRenderer xAxisRenderer = new HorizontalAxisRenderer(xAxis, margin, g);
        VerticalAxisRenderer yAxisRenderer = new VerticalAxisRenderer(yAxis, margin, g);

        // Compute graph area
        int xStartGraph = yAxisRenderer.getAxisWidth();
        int xEndGraph = width - margin;
        int yStartGraph = margin;
        int yEndGraph = height - xAxisRenderer.getAxisHeight();
        int plotWidth = xEndGraph - xStartGraph;
        int plotHeight = yEndGraph - yStartGraph;

        // Draw axis
        xAxisRenderer.draw(g, 0, xStartGraph, xEndGraph, width, yEndGraph);
        yAxisRenderer.draw(g, 0, yStartGraph, yEndGraph, height, xStartGraph);


        double rangeX = endXPlot - startXPlot;
        double rangeY = endYPlot - startYPlot;

       
        
        // Draw reference lines
        g.setColor(new Color(240, 240, 240));
        int[] xTicks = xAxisRenderer.horizontalTickPositions();
        for (int xTick : xTicks) {
            g.drawLine(xTick, yStartGraph, xTick, yEndGraph);
        }
        int[] yTicks = yAxisRenderer.verticalTickPositions();
        for (int yTick : yTicks) {
            g.drawLine(xStartGraph, height - yTick, xEndGraph, height - yTick);
        }
        
        // Set color scheme
        colorScheme = ValueColorSchemes.grayScale(data.getStatistics());

        ///////////////////////////////////////////////////////////////////////
        
        int countY = 0;
        int countX = 0;

        int xWidthTotal = xEndGraph - xStartGraph;
        int yHeightTotal = yEndGraph - yStartGraph;
        int xRange = data.getXBoundaries().getInt(data.getXCount()) - data.getXBoundaries().getInt(0);
        int yRange = data.getYBoundaries().getInt(data.getYCount()) - data.getYBoundaries().getInt(0);
        double initCellHeights = ((data.getYBoundaries().getDouble(1) - data.getYBoundaries().getDouble(0))*yHeightTotal)/yRange;
        int initCellHeight = (int) (Math.floor(initCellHeights));
        if ((initCellHeights - initCellHeight) > 0.5)
        {
            initCellHeight++;
        }
        int yPosition = yEndGraph - initCellHeight;
        while (countY <= data.getYBoundaries().getDouble(data.getYCount()-1))
        {
            countX = 0;
            int xPosition = xStartGraph;
            double cellHeights = ((data.getYBoundaries().getDouble(countY+1) - data.getYBoundaries().getDouble(countY))*yHeightTotal)/yRange;
            int cellHeight = (int) (Math.floor(cellHeights));
            if (cellHeights - cellHeight > 0.5)
            {
                cellHeight++;
            }
            while (countX <= data.getXBoundaries().getDouble(data.getXCount()-1))
            {
                double cellWidths = ((data.getXBoundaries().getDouble(countX+1)-data.getXBoundaries().getDouble(countX))*xWidthTotal)/xRange;
                int cellWidth = (int) (Math.floor(cellWidths));
                System.out.println(cellWidth);
                if (cellWidths - cellWidth > 0)
                {
                    cellWidth++;
                }
                System.out.println(cellWidth);
                g.setColor(new Color(colorScheme.colorFor(data.getValue(countX, countY))));
                g.fillRect(xPosition, yPosition, cellWidth, cellHeight);
                xPosition = xPosition + cellWidth;
                countX++;
            }
            yPosition = yPosition - cellHeight;
            countY++;
        }

    }    
}
