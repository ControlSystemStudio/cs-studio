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
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class LineGraphRenderer {

    private int width = 300;
    private int height = 200;
    private InterpolationScheme scheme = InterpolationScheme.NEAREST_NEIGHBOUR;
    
    private boolean rangeFromDataset = true;
    private double startPlotX = java.lang.Double.MIN_VALUE;
    private double endPlotX = java.lang.Double.MAX_VALUE;
    private double startPlotY = java.lang.Double.MIN_VALUE;
    private double endPlotY = java.lang.Double.MAX_VALUE;
    
    private double integratedMinX = java.lang.Double.MAX_VALUE;
    private double integratedMinY = java.lang.Double.MAX_VALUE;
    private double integratedMaxX = java.lang.Double.MIN_VALUE;
    private double integratedMaxY = java.lang.Double.MIN_VALUE;

    public LineGraphRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public LineGraphRenderer() {
        this(300, 200);
    }

    public int getImageHeight() {
        return height;
    }

    public int getImageWidth() {
        return width;
    }
    
    public InterpolationScheme getInterpolation() {
        return scheme;
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
        if (update.getInterpolation() != null) {
            scheme = update.getInterpolation();
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
        double[] scaledX = new double[dataCount];
        double[] scaledY = new double[dataCount];
        ListNumber xValues = data.getXValues();
        ListNumber yValues = data.getYValues();
        for (int i = 0; i < scaledY.length; i++) {
            scaledX[i] = xStartGraph + NumberUtil.scale(xValues.getDouble(i), startXPlot, endXPlot, plotWidth);
            scaledY[i] = height - xAxisRenderer.getAxisHeight() - NumberUtil.scale(yValues.getDouble(i), startYPlot, endYPlot, plotHeight);
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

        Path2D path;
        switch (scheme) {
            default:
            case NEAREST_NEIGHBOUR:
                path = nearestNeighbour(scaledX, scaledY);
                break;
            case LINEAR:
                path = linearInterpolation(scaledX, scaledY);
                break;
            case CUBIC:
                path = cubicInterpolation(scaledX, scaledY);
        }

        // Make sure that the line does not go ouside the chart
        g.setClip(xStartGraph - 1, yStartGraph - 1, plotWidth + 2, plotHeight + 2);
        
        // Draw the line
        g.setColor(Color.BLACK);
        g.draw(path);
    }

    private static Double nearestNeighbour(double[] scaledX, double[] scaledY) {
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

    private static Double linearInterpolation(double[] scaledX, double[] scaledY) {
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[0], scaledY[0]);
        for (int i = 1; i < scaledY.length; i++) {
            line.lineTo(scaledX[i], scaledY[i]);
        }
        return line;
    }

    private static Double cubicInterpolation(double[] scaledX, double[] scaledY) {
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
}
