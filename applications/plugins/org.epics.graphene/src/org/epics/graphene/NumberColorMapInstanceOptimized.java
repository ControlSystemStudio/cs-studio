/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.Range;

/**
 * An optimized instance of a color map, where colors are pre-calculated.
 * <p>
 * TODO: allow choice of number of colors
 * 
 * @author sjdallst
 */
class NumberColorMapInstanceOptimized implements NumberColorMapInstance {

    private int arrayLength = 1000;
    private int[] colors = new int[arrayLength];
    private int nanColor;
    private Range range;
    private double max, min, total;
    private final String name;

    NumberColorMapInstanceOptimized(NumberColorMapInstance instance, Range range) {
        min = range.getMinimum().doubleValue();
        max = range.getMaximum().doubleValue();
        total = max - min;
        for (int i = 0; i < arrayLength; i++) {
            //account for possible rounding errors on last entry.
            if (i == arrayLength - 1) {
                colors[i] = instance.colorFor(max);
            } else {
                colors[i] = instance.colorFor(min + i * (total / ((double) (arrayLength - 1))));
            }
        }
        this.range = range;
        this.name = instance.toString() + " opt(" + arrayLength + ")";
    }

    // TODO: what is this doing?
    NumberColorMapInstanceOptimized(NumberColorMapInstance instance, Range oldRange, Range newRange) {
        double oldMin = oldRange.getMinimum().doubleValue();
        double oldMax = oldRange.getMaximum().doubleValue();
        double oldTotal = oldMax - oldMin;
        for (int i = 0; i < arrayLength; i++) {
            //account for possible rounding errors on last entry.
            if (i == arrayLength - 1) {
                colors[i] = instance.colorFor(oldMax);
            } else {
                colors[i] = instance.colorFor(oldMin + i * (oldTotal / ((double) (arrayLength - 1))));
            }
        }
        min = newRange.getMinimum().doubleValue();
        max = newRange.getMaximum().doubleValue();
        total = max - min;
        this.range = newRange;
        this.name = instance.toString() + " opt2(" + arrayLength + ")";
    }

    @Override
    public int colorFor(double value) {
        int index = (int) ((value - min) / total * (arrayLength - 1));
        if (index < 0) {
            index = 0;
        } else if (index >= colors.length) {
            index = colors.length - 1;
        }
        return colors[index];
    }

    @Override
    public String toString() {
        return name;
    }
    
}
