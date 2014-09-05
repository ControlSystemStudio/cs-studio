/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A numeric color map defined by interpolated colors (gradients).
 *
 * @author sjdallst
 */
class NumberColorMapGradient implements NumberColorMap {

    private final Color[] colors;
    private final String name;

    /**
     * Creates a new color map.
     * <p>
     * TODO: use List, separate NaN color, allow for relative percentage offsets
     * 
     * @param colors 
     */
    public NumberColorMapGradient(Color[] colors, String name) {
        this.colors = colors;
        this.name = name;
    }

    @Override
    public NumberColorMapInstance createInstance(Range range) {
        return new ValueColorSchemeInstanceGradient(colors, range);
    }

    class ValueColorSchemeInstanceGradient implements NumberColorMapInstance {

        protected Color[] colors;
        protected List<Double> percentages = new ArrayList<>();
        protected int nanColor;
        protected Range range;

        public ValueColorSchemeInstanceGradient(Color[] colors, Range range) {
            this.range = range;
            this.colors = colors;
            this.nanColor = colors[colors.length - 1].getRGB();
            percentages = percentageRange(colors.length - 2);
        }

        @Override
        public int colorFor(double value) {
            if (Double.isNaN(value)) {
                return nanColor;
            }
            if (range == null) {
                throw new NullPointerException("range can not be null.");
            }
            double fullRange = range.getMaximum().doubleValue() - range.getMinimum().doubleValue();
            int alpha = 0, red = 0, green = 0, blue = 0;
            if (fullRange > 0) {
                for (int i = 0; i < percentages.size() - 1; i++) {
                    if (range.getMinimum().doubleValue() + percentages.get(i) * fullRange <= value && value <= range.getMinimum().doubleValue() + percentages.get(i + 1) * fullRange) {
                        double normalValue = MathUtil.normalize(value, range.getMinimum().doubleValue() + percentages.get(i) * fullRange, range.getMinimum().doubleValue() + percentages.get(i + 1) * fullRange);
                        normalValue = Math.min(normalValue, 1.0);
                        normalValue = Math.max(normalValue, 0.0);
                        alpha = 255;
                        red = (int) (colors[i].getRed() + (colors[i + 1].getRed() - colors[i].getRed()) * normalValue);
                        green = (int) (colors[i].getGreen() + (colors[i + 1].getGreen() - colors[i].getGreen()) * normalValue);
                        blue = (int) (colors[i].getBlue() + (colors[i + 1].getBlue() - colors[i].getBlue()) * normalValue);
                    }
                }
            } else {
                for (int i = 0; i < percentages.size() - 1; i++) {
                    if (percentages.get(i) <= .5 && .5 <= percentages.get(i + 1)) {
                        double normalValue = 0;
                        normalValue = Math.min(normalValue, 1.0);
                        normalValue = Math.max(normalValue, 0.0);
                        alpha = 255;
                        red = (int) (colors[i].getRed() + (colors[i + 1].getRed() - colors[i].getRed()) * normalValue);
                        green = (int) (colors[i].getGreen() + (colors[i + 1].getGreen() - colors[i].getGreen()) * normalValue);
                        blue = (int) (colors[i].getBlue() + (colors[i + 1].getBlue() - colors[i].getBlue()) * normalValue);
                    }
                }
            }
            if (value > range.getMaximum().doubleValue()) {
                alpha = 255;
                red = (colors[colors.length - 2].getRed());
                green = (colors[colors.length - 2].getGreen());
                blue = (colors[colors.length - 2].getBlue());
            }
            if (value < range.getMinimum().doubleValue()) {
                alpha = 255;
                red = (colors[0].getRed());
                green = (colors[0].getGreen());
                blue = (colors[0].getBlue());
            }
            return (alpha << 24) | (red << 16) | (green << 8) | blue;
        }

        @Override
        public String toString() {
            return name + " " + range;
        }

    }
    
    private static ArrayList<Double> percentageRange(int size) {
        ArrayList<Double> percentages = new ArrayList<>();

        percentages.add(0.0);

        for (int i = 1; i <= size; i++) {
            percentages.add((double) i / size);
        }

        return percentages;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
