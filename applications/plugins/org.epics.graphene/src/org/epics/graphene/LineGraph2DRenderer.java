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
import org.epics.util.array.SortedListView;

/**
 *
 * @author carcassi
 */
public class LineGraph2DRenderer extends Graph2DRenderer<LineGraph2DRendererUpdate> {

    @Override
    public LineGraph2DRendererUpdate newUpdate() {
        return new LineGraph2DRendererUpdate();
    }

    private InterpolationScheme scheme = InterpolationScheme.NEAREST_NEIGHBOUR;
    

    public LineGraph2DRenderer(int width, int height) {
        super(width, height);
    }

    public LineGraph2DRenderer() {
        this(300, 200);
    }
    
    public InterpolationScheme getInterpolation() {
        return scheme;
    }
    
    @Override
    public void update(LineGraph2DRendererUpdate update) {
        super.update(update);
        if (update.getInterpolation() != null) {
            scheme = update.getInterpolation();
        }
    }

    public void draw(Graphics2D g, Point2DDataset data) {
        this.g = g;
        
        calculateRanges(data.getXStatistics(), data.getYStatistics());
        calculateGraphArea();
        drawBackground();
        drawGraphArea();
        
        // Scale data and sort data
        int dataCount = data.getCount();
        double[] scaledX = new double[dataCount];
        double[] scaledY = new double[dataCount];
        SortedListView xValues = org.epics.util.array.ListNumbers.sortedView(data.getXValues());
        ListNumber yValues = org.epics.util.array.ListNumbers.sortedView(data.getYValues(), xValues.getIndexes());
        for (int i = 0; i < scaledY.length; i++) {
            scaledX[i] = scaledX(xValues.getDouble(i));
            scaledY[i] = scaledY(yValues.getDouble(i));;
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

        setClip(g);
        
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
