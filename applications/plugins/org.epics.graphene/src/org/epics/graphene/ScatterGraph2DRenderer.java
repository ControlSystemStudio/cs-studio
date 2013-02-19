/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.*;
import java.awt.geom.Path2D;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class ScatterGraph2DRenderer extends Graph2DRenderer<ScatterGraph2DRendererUpdate> {

    public ScatterGraph2DRenderer(int width, int height) {
        super(width, height);
        topAreaMargin = 2;
        bottomAreaMargin = 2;
        leftAreaMargin = 2;
        rightAreaMargin = 2;
    }

    @Override
    public ScatterGraph2DRendererUpdate newUpdate() {
        return new ScatterGraph2DRendererUpdate();
    }

    public void draw(Graphics2D g, Point2DDataset data) {
        // Prepare the plot area
        calculateRanges(data.getXStatistics(), data.getYStatistics());
        this.g = g;
        calculateGraphArea();
        drawGraphArea();

        // Draw the plot
        ListNumber xValues = data.getXValues();
        ListNumber yValues = data.getYValues();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        setClip(g);
        g.setColor(Color.BLACK);
        for (int i = 0; i < xValues.size(); i++) {
            drawValue(g, xValues.getDouble(i), yValues.getDouble(i));
        }

    }
    
    private void drawValue(Graphics2D g, double x, double y) {
        g.draw(createShape((int) scaledX(x), (int) scaledY(y)));
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
