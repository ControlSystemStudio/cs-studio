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
public class ScatterStatistics2DGraphRenderer {

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

    public ScatterStatistics2DGraphRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ScatterStatistics2DGraphRenderer() {
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

    public void draw(Graphics2D g, Statistics2DDataset data) {
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
//        int[] scaledX = new int[dataCount];
//        int[] scaledY = new int[dataCount];
//        ListNumber xValues = data.getXValues();
//        ListNumber yValues = data.getYValues();
//        for (int i = 0; i < scaledY.length; i++) {
//            scaledX[i] = (int) (xStartGraph + NumberUtil.scale(xValues.getDouble(i), startXPlot, endXPlot, plotWidth));
//            scaledY[i] = (int) (height - xAxisRenderer.getAxisHeight() - NumberUtil.scale(yValues.getDouble(i), startYPlot, endYPlot, plotHeight));
//        }
        
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
        for (int i = 0; i < data.getCount(); i++) {
            Statistics x = data.getXValues().get(i);
            Statistics y = data.getYValues().get(i);
            double xAvg = xOf(x.getAverage(), xStartGraph, startXPlot, endXPlot, plotWidth);
            double yAvg = yOf(y.getAverage(), yEndGraph, startYPlot, endYPlot, plotHeight);
            double xMin = xOf(x.getMinimum().doubleValue(), xStartGraph, startXPlot, endXPlot, plotWidth);
            double xMax = xOf(x.getMaximum().doubleValue(), xStartGraph, startXPlot, endXPlot, plotWidth);
            double xPlusStd = xOf(x.getAverage() + x.getStdDev(), xStartGraph, startXPlot, endXPlot, plotWidth);
            double xMinusStd = xOf(x.getAverage() - x.getStdDev(), xStartGraph, startXPlot, endXPlot, plotWidth);
            double yMin = yOf(y.getMinimum().doubleValue(), yEndGraph, startYPlot, endYPlot, plotHeight);
            double yMax = yOf(y.getMaximum().doubleValue(), yEndGraph, startYPlot, endYPlot, plotHeight);
            double yPlusStd = yOf(y.getAverage() + y.getStdDev(), yEndGraph, startYPlot, endYPlot, plotHeight);
            double yMinusStd = yOf(y.getAverage() - y.getStdDev(), yEndGraph, startYPlot, endYPlot, plotHeight);
            g.draw(createShape(xAvg, yAvg));
            g.draw(createHRangeShape(xMin, xPlusStd, xMinusStd, xMax, yAvg));
            g.draw(createVRangeShape(yMin, yPlusStd, yMinusStd, yMax, xAvg));
        }

    }
    
    private int xOf(double value, double xStartGraph, double startXPlot, double endXPlot, int plotWidth) {
        return (int) (xStartGraph + NumberUtil.scale(value, startXPlot, endXPlot, plotWidth));
    }
    
    private int yOf(double value, double yEndGraph, double startYPlot, double endYPlot, int plotHeight) {
        return (int) (yEndGraph - NumberUtil.scale(value, startYPlot, endYPlot, plotHeight));
    }
    
    //                       maxY
    //                    + stdDevY
    // minX | - stdDevX | avgx/avgy | + stdDevX | maxX
    //                    - stdDevY
    //                       minY
    private Shape createShape(double x, double y) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x-2, y-2);
        path.lineTo(x+2, y+2);
        path.moveTo(x+2, y-2);
        path.lineTo(x-2, y+2);
        return path;
    }
    
    private Shape createVRangeShape(double minY, double plusStdY, double minusStdY, double maxY, double x) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x, minY);
        path.lineTo(x, maxY);
        path.moveTo(x-3, minusStdY);
        path.lineTo(x+3, minusStdY);
        path.moveTo(x-3, plusStdY);
        path.lineTo(x+3, plusStdY);
        return path;
    }
    
    private Shape createHRangeShape(double minX, double plusStdX, double minusStdX, double maxX, double y) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(minX, y);
        path.lineTo(maxX, y);
        path.moveTo(minusStdX, y-2);
        path.lineTo(minusStdX, y+2);
        path.moveTo(plusStdX, y-2);
        path.lineTo(plusStdX, y+2);
        return path;
    }
}
