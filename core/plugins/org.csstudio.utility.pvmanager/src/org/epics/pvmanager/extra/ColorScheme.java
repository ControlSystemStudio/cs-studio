/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.extra;

import java.awt.Color;
import java.util.Random;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Util;

/**
 *
 * @author carcassi
 */
public abstract class ColorScheme {
    
    /**
     * Calculate the color for the value according to the ranges and puts it
     * into the colors buffer.
     *
     * @param value the value to color
     * @param ranges the display ranges
     * @return the RGB color
     */
    public abstract int color(double value, Display ranges);

    /**
     * A simple color scheme that uses only one gradient for the whole
     * display range.
     *
     * @param minValueColor color for the minimum value in the display range
     * @param maxValueColor color for the maximum value in the display range
     * @return
     */
    public static ColorScheme singleRangeGradient(final Color minValueColor, final Color maxValueColor) {
        return new ColorScheme() {
            Random rand = new Random();

            @Override
            public int color(double value, Display ranges) {
                double normalValue = Util.normalize(value, ranges);
                int alpha = 0;
                int red = (int) (minValueColor.getRed() + (maxValueColor.getRed() - minValueColor.getRed()) * normalValue);
                int green = (int) (minValueColor.getGreen() + (maxValueColor.getGreen() - minValueColor.getGreen()) * normalValue);
                int blue = (int) (minValueColor.getBlue() + (maxValueColor.getBlue() - minValueColor.getBlue()) * normalValue);
                return (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        };
    }

    public static ColorScheme multipleRangeGradient(final Color lowerDisplayColor,
            final Color lowerAlarmColor, final Color lowerWarningColor,
            final Color upperWarningColor, final Color upperAlarmColor,
            final Color upperDisplayColor) {
        return new ColorScheme() {
            Random rand = new Random();

            @Override
            public int color(double value, Display ranges) {
                double normalValue = 0.0;
                Color minValueColor = null;
                Color maxValueColor = null;
                // Determine in which range the value is.
                // The equals are put so that if the value is at the limit,
                // the range closer to the center wins
                if (value < ranges.getLowerDisplayLimit()) {
                    return lowerDisplayColor.getRGB();
                } else if (value < ranges.getLowerAlarmLimit()) {
                    normalValue = Util.normalize(value, ranges.getLowerDisplayLimit(), ranges.getLowerAlarmLimit());
                    minValueColor = lowerDisplayColor;
                    maxValueColor = lowerAlarmColor;
                } else if (value < ranges.getLowerWarningLimit()) {
                    normalValue = Util.normalize(value, ranges.getLowerAlarmLimit(), ranges.getLowerWarningLimit());
                    minValueColor = lowerAlarmColor;
                    maxValueColor = lowerWarningColor;
                } else if (value <= ranges.getUpperWarningLimit()) {
                    normalValue = Util.normalize(value, ranges.getLowerWarningLimit(), ranges.getUpperWarningLimit());
                    minValueColor = lowerWarningColor;
                    maxValueColor = upperWarningColor;
                } else if (value <= ranges.getUpperAlarmLimit()) {
                    normalValue = Util.normalize(value, ranges.getUpperWarningLimit(), ranges.getUpperAlarmLimit());
                    minValueColor = upperWarningColor;
                    maxValueColor = upperAlarmColor;
                } else {
                    return upperDisplayColor.getRGB();
                }

                int alpha = 0;
                int red = (int) (minValueColor.getRed() + (maxValueColor.getRed() - minValueColor.getRed()) * normalValue);
                int green = (int) (minValueColor.getGreen() + (maxValueColor.getGreen() - minValueColor.getGreen()) * normalValue);
                int blue = (int) (minValueColor.getBlue() + (maxValueColor.getBlue() - minValueColor.getBlue()) * normalValue);
                return (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        };
    }
}
