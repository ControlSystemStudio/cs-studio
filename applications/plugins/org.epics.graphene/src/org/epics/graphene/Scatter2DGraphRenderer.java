/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.awt.image.BufferedImage;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class Scatter2DGraphRenderer {

    private int width = 300;
    private int height = 200;
    
    private boolean rangeFromDataset = true;
    private double startPlotX = java.lang.Double.MIN_VALUE;
    private double endPlotX = java.lang.Double.MAX_VALUE;
    private double startPlotY = java.lang.Double.MIN_VALUE;
    private double endPlotY = java.lang.Double.MAX_VALUE;
    
    private double integratedMinX = java.lang.Double.MAX_VALUE;
    private double integratedMinY = java.lang.Double.MAX_VALUE;
    private double integratedMaxX = java.lang.Double.MIN_VALUE;
    private double integratedMaxY = java.lang.Double.MIN_VALUE;

    public Scatter2DGraphRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Scatter2DGraphRenderer() {
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
    
    public void update(LineGraphRendererUpdate update) {
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

    public void draw(Graphics2D g, Point2DDataset data) {
        int dataCount = data.getCount();
        
        // Retain the integrated min/max
        integratedMinX = java.lang.Double.isNaN(data.getXMinValue()) ? integratedMinX : Math.min(integratedMinX, data.getXMinValue());
        integratedMinY = java.lang.Double.isNaN(data.getYMinValue()) ? integratedMinY : Math.min(integratedMinY, data.getYMinValue());
        integratedMaxX = java.lang.Double.isNaN(data.getXMaxValue()) ? integratedMaxX : Math.max(integratedMaxX, data.getXMaxValue());
        integratedMaxY = java.lang.Double.isNaN(data.getYMaxValue()) ? integratedMaxY : Math.max(integratedMaxY, data.getYMaxValue());
        
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

        // Scale data
        int[] scaledX = new int[dataCount];
        int[] scaledY = new int[dataCount];
        ListNumber xValues = data.getXValues();
        ListNumber yValues = data.getYValues();
        for (int i = 0; i < scaledY.length; i++) {
            scaledX[i] = (int) (xStartGraph + NumberUtil.scale(xValues.getDouble(i), startXPlot, endXPlot, plotWidth));
            scaledY[i] = (int) (height - xAxisRenderer.getAxisHeight() - NumberUtil.scale(yValues.getDouble(i), startYPlot, endYPlot, plotHeight));
        }
        
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

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(Color.BLACK);
        // Make sure that the line does not go ouside the chart
        g.setClip(xStartGraph - 1, yStartGraph - 1, plotWidth + 2, plotHeight + 2);
        for (int i = 0; i < scaledY.length; i++) {
            g.draw(createShape(scaledX[i], scaledY[i]));
        }

    }
    
    private Shape createShape(double x, double y) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x-2, y);
        path.lineTo(x+2, y);
        path.moveTo(x, y-2);
        path.lineTo(x, y+2);
        return path;
    }
}
