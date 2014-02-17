/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Arrays;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class ScatterGraph2DRenderer extends Graph2DRenderer<ScatterGraph2DRendererUpdate> {
    
    public static java.util.List<InterpolationScheme> supportedInterpolationScheme = Arrays.asList(InterpolationScheme.NONE, InterpolationScheme.LINEAR, InterpolationScheme.CUBIC);

    public ScatterGraph2DRenderer(int width, int height) {
        super(width, height);
        topAreaMargin = 2;
        bottomAreaMargin = 2;
        leftAreaMargin = 2;
        rightAreaMargin = 2;
    }
    
    private InterpolationScheme interpolation = InterpolationScheme.NONE;

    @Override
    public ScatterGraph2DRendererUpdate newUpdate() {
        return new ScatterGraph2DRendererUpdate();
    }

    @Override
    public void update(ScatterGraph2DRendererUpdate update) {
        super.update(update);
        if (update.getInterpolation() != null) {
            interpolation = update.getInterpolation();
        }
    }

    public void draw(Graphics2D g, Point2DDataset data) {
        // Prepare the plot area
        calculateRanges(data.getXStatistics(), data.getYStatistics());
        this.g = g;
        calculateLabels();
        calculateGraphArea();
        drawBackground();
        drawGraphArea();

        // Draw the plot
        ListNumber xValues = data.getXValues();
        ListNumber yValues = data.getYValues();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        setClip(g);
        g.setColor(Color.BLACK);
        for (int i = 0; i < xValues.size(); i++) {
            drawValue(g, xValues.getDouble(i), yValues.getDouble(i));
        }
        
        if (interpolation != InterpolationScheme.NONE) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            drawValueLine(data.getXValues(), data.getYValues(), interpolation);
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
