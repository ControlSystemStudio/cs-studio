/*
 * Copyright 2008-2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.extra;

import java.awt.Color;

/**
 *
 * @author carcassi
 */
public class WaterfallPlotParameters {

    final int maxHeight;
    final ColorScheme colorScheme;
    final boolean adaptiveRange;

    public int getMaxHeight() {
        return maxHeight;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public boolean isAdaptiveRange() {
        return adaptiveRange;
    }

    public WaterfallPlotParameters() {
        maxHeight = 50;
        colorScheme = ColorScheme.singleRangeGradient(Color.BLACK, Color.WHITE);
        adaptiveRange = false;
    }

    public WaterfallPlotParameters(int maxHeight, ColorScheme colorScheme, boolean adaptiveRange) {
        this.maxHeight = maxHeight;
        this.colorScheme = colorScheme;
        this.adaptiveRange = adaptiveRange;
    }

    public WaterfallPlotParameters withMaxHeight(int maxHeight) {
        return new WaterfallPlotParameters(maxHeight, colorScheme, adaptiveRange);
    }

    public WaterfallPlotParameters withColorScheme(ColorScheme colorScheme) {
        return new WaterfallPlotParameters(maxHeight, colorScheme, adaptiveRange);
    }

    public WaterfallPlotParameters withAdaptiveRange(boolean adaptiveRange) {
        return new WaterfallPlotParameters(maxHeight, colorScheme, adaptiveRange);
    }

    
}
