/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import static org.epics.graphene.ValueAxis.orderOfMagnitude;
import org.epics.util.text.NumberFormats;

/**
 *
 * @author carcassi
 */
final class LinearValueScale implements ValueScale {

    @Override
    public double scaleValue(double value, double minValue, double maxValue, double newMinValue, double newMaxValue) {
        double oldRange = maxValue - minValue;
        double newRange = newMaxValue - newMinValue;
        return newMinValue + (value - minValue) / oldRange * newRange;
    }
    private static final DecimalFormat defaultFormat = new DecimalFormat("0.###");

    @Override
    public ValueAxis references(Range range, int minRefs, int maxRefs) {
        double minValue = range.getMinimum().doubleValue();
        double maxValue = range.getMaximum().doubleValue();
        double minIncrement = Double.MIN_VALUE;
        double increment = incrementForRange(minValue, maxValue, maxRefs, minIncrement);
        double[] ticks = createTicks(minValue, maxValue, increment);
        if (ticks.length < 2) {
            ticks = createSmallerTicks(minValue, maxValue, increment);
        }
        int rangeOrder = (int) orderOfMagnitude(minValue, maxValue);
        int incrementOrder = (int) orderOfMagnitude(increment);
        int nDigits = rangeOrder - incrementOrder;
        
        // The format will decide how many significant digit to show
        NumberFormat format = defaultFormat;
        // The normalization and the exponent will need to agree and
        // decide what order of magnitude to format the number as
        double normalization = 1.0;
        String exponent = null;
        if (rangeOrder >= -3 && rangeOrder <= 3) {
            if (incrementOrder < 0) {
                format = NumberFormats.format(-incrementOrder);
            } else {
                format = NumberFormats.format(0);
            }
        } else if (rangeOrder > 3) {
            format = NumberFormats.format(nDigits);
            normalization = Math.pow(10.0, rangeOrder);
            exponent = Integer.toString(rangeOrder);
        } else if (rangeOrder < -3) {
            format = NumberFormats.format(nDigits);
            normalization = Math.pow(10.0, rangeOrder);
            exponent = Integer.toString(rangeOrder);
        }
        
        String[] labels = new String[ticks.length];
        for (int i = 0; i < ticks.length; i++) {
            double value = ticks[i];
            labels[i] = format(value, format, exponent, normalization);
        }
        return new ValueAxis(minValue, maxValue, ticks, labels);
    }
    
    static String format(double number, NumberFormat format, String exponent, double normalization) {
        if (exponent != null) {
            return format.format(number/normalization) + "e" + exponent;
        } else {
            return format.format(number/normalization);
        }
    }
    
    /**
     * Find the space between ticks given the constraints.
     * 
     * @param min range start
     * @param max range end
     * @param maxTick maximum ticks
     * @param minIncrement minimum increment
     * @return the increment between each tick
     */
    static double incrementForRange(double min, double max, int maxTick, double minIncrement) {
        double range = max - min;
        double increment;
        if(range!=0)
            increment = Math.max(range/maxTick, minIncrement);
        else
            increment = 0;
        int order;
        if(increment != 0)
            order = (int) orderOfMagnitude(increment);
        else
            order = 0;
        BigDecimal magnitude = BigDecimal.ONE.scaleByPowerOfTen(order);
        double normalizedIncrement = increment / magnitude.doubleValue();
        
        if (normalizedIncrement <= 1.0) {
            return magnitude.doubleValue();
        } else if (normalizedIncrement <= 2.0) {
            return magnitude.multiply(BigDecimal.valueOf(2)).doubleValue();
        } else if (normalizedIncrement <= 5.0) {
            return magnitude.multiply(BigDecimal.valueOf(5)).doubleValue();
        } else {
            return magnitude.multiply(BigDecimal.valueOf(10)).doubleValue();
        }
    }
    
    static double orderOfMagnitude(double value) {
        return Math.floor(Math.log10(value));
    }
    
    static double orderOfMagnitude(double min, double max) {
        return orderOfMagnitude(Math.max(Math.abs(max), Math.abs(min)));
    }
    
    /**
     * Create values for the axis tick given the range and the increment.
     * 
     * @param min value range start
     * @param max value range end
     * @param increment space between ticks
     * @return values for the ticks
     */
    static double[] createTicks(double min, double max, double increment) {
        long start = (long) Math.ceil(min / increment);
        long end = (long) Math.floor(max / increment);
        double[] ticks = new double[(int) (end-start+1)];
        for (int i = 0; i < ticks.length; i++) {
            ticks[i] = (i + start) * increment;
        }
        return ticks;
    }

    private static double[] createSmallerTicks(double minValue, double maxValue, double increment) {
        int order = (int) orderOfMagnitude(increment);
        BigDecimal magnitude = BigDecimal.ONE.scaleByPowerOfTen(order);
        double normalizedIncrement = increment / magnitude.doubleValue();
        double smallerIncrement;
        if (normalizedIncrement < 1.1) {
            smallerIncrement = BigDecimal.ONE.scaleByPowerOfTen(order - 1).multiply(BigDecimal.valueOf(5)).doubleValue();
        } else if (normalizedIncrement < 2.1) {
            smallerIncrement = magnitude.doubleValue();
        } else if (normalizedIncrement < 5.1) {
            smallerIncrement = magnitude.multiply(BigDecimal.valueOf(2)).doubleValue();
        } else {
            smallerIncrement = magnitude.multiply(BigDecimal.valueOf(5)).doubleValue();
        }
        
        return createTicks(minValue, maxValue, smallerIncrement);
    }
    
}
