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
import java.util.Arrays;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class BubbleGraph2DRenderer extends Graph2DRenderer<Graph2DRendererUpdate> {
    
    private Range zAggregatedRange;
    private AxisRange zAxisRange = AxisRanges.integrated();
    private Range zPlotRange;

    public BubbleGraph2DRenderer(int width, int height) {
        super(width, height);
    }

    protected void calculateRanges(Range xDataRange, Range yDataRange, Range zDataRange) {
        super.calculateRanges(xDataRange, yDataRange);
        zAggregatedRange = aggregateRange(zDataRange, zAggregatedRange);
        zPlotRange = zAxisRange.axisRange(zDataRange, zAggregatedRange);
    }

    public void draw(Graphics2D g, Point3DWithLabelDataset data) {
        this.g = g;
        
        calculateRanges(data.getXStatistics(), data.getYStatistics(), data.getZStatistics());
        
        drawBackground();
        calculateGraphArea();
        drawGraphArea();
        

        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        LabelColorScheme labelColor = LabelColorSchemes.orderedHueColor(data.getLabels());
        
        // Order values by 
        ListInt indexes = org.epics.util.array.ListNumbers.sortedView(data.getZValues()).getIndexes();
        
        // Make sure that the line does not go ouside the chart
        setClip(g);
        for (int j = indexes.size() - 1; j >= 0; j--) {
            int i = indexes.getInt(j);
            double size = radiusScale(zPlotRange.getMinimum().doubleValue(), data.getZValues().getDouble(i), zPlotRange.getMaximum().doubleValue(),
                    3, 15);
            double x = scaledX(data.getXValues().getDouble(i));
            double y = scaledY(data.getYValues().getDouble(i));
            Shape bubble = createShape(x, y, size);
            newValue(x, y, size, i);
            g.setColor(new Color(labelColor.getColor(data.getLabels().get(i))));
            g.fill(bubble);
            g.setColor(Color.BLACK);
            g.draw(bubble);
        }

    }
    
    private double radiusScale(double minValue, double value, double maxValue, double minRadius, double maxRadius) {
        if (minValue < 0) {
            throw new UnsupportedOperationException("For now, the value for the size has to be always positive");
        }
        if (value <= minValue) {
            return minRadius;
        }
        if (value >= maxValue) {
            return maxRadius;
        }
        return minRadius + NumberUtil.scale(Math.sqrt(value), Math.sqrt(minValue), Math.sqrt(maxValue), (maxRadius - minRadius));
    }
    
    protected void newValue(double x, double y, double size, int index) {
        // Do nothing
    }
    
    private Shape createShape(double x, double y, double size) {
        double halfSize = size / 2;
        Ellipse2D.Double circle = new Ellipse2D.Double(x-halfSize, y-halfSize, size, size);
        return circle;
    }

    @Override
    public Graph2DRendererUpdate newUpdate() {
        return new Graph2DRendererUpdate();
    }
}
