/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.math.BigDecimal;
import java.text.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author carcassi
 */
public class ValueAxis {
    
    private double minValue;
    private double maxValue;
    private double[] tickValues;
    private String[] tickStrings;

    public ValueAxis(double minValue, double maxValue, double[] tickValues, String[] tickStrings) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.tickValues = tickValues;
        this.tickStrings = tickStrings;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double[] getTickValues() {
        return tickValues;
    }

    public String[] getTickLabels() {
        return tickStrings;
    }

    public static ValueAxis createAutoAxis(double minValue, double maxValue, int maxTicks) {
        return createAutoAxis(minValue, maxValue, maxTicks, Double.MIN_VALUE);
    }
    
    private static final DecimalFormat defaultFormat = new DecimalFormat("0.###");
    
    private static final DecimalFormat decimal0 = new DecimalFormat("0");
    private static final DecimalFormat decimal1 = new DecimalFormat("0.0");
    private static final DecimalFormat decimal2 = new DecimalFormat("0.00");
    
    private static final Map<Integer, DecimalFormat> formats = new ConcurrentHashMap<Integer, DecimalFormat>();
    
    static DecimalFormat formatWithFixedSignificantDigits(int significantDigits) {
        DecimalFormat result = formats.get(significantDigits);
        if (result == null) {
            StringBuilder pattern = new StringBuilder("0");
            for (int i = 0; i < significantDigits; i++) {
                if (i == 0) {
                    pattern.append(".");
                }
                pattern.append("0");
            }
            result = new DecimalFormat(pattern.toString());
            formats.put(significantDigits, result);
        }
        return result;
    }
    
    public static ValueAxis createAutoAxis(double minValue, double maxValue, int maxTicks, double minIncrement) {
        double increment = incrementForRange(minValue, maxValue, maxTicks, minIncrement);
        double[] ticks = createTicks(minValue, maxValue, increment);
        if (ticks.length < 2) {
            ticks = createSmallerTicks(minValue, maxValue, increment);
        }
        int rangeOrder = (int) orderOfMagnitude(minValue, maxValue);
        int incrementOrder = (int) orderOfMagnitude(increment);
        int nDigits = rangeOrder - incrementOrder;
        
        // The format will decide how many significant digit to show
        DecimalFormat format = defaultFormat;
        // The normalization and the exponent will need to agree and
        // decide what order of magnitude to format the number as
        double normalization = 1.0;
        String exponent = null;
        if (rangeOrder >= -3 && rangeOrder <= 3) {
            if (incrementOrder < 0) {
                format = formatWithFixedSignificantDigits(-incrementOrder);
            } else {
                format = formatWithFixedSignificantDigits(0);
            }
        } else if (rangeOrder > 3) {
            format = formatWithFixedSignificantDigits(nDigits);
            normalization = Math.pow(10.0, rangeOrder);
            exponent = Integer.toString(rangeOrder);
        } else if (rangeOrder < -3) {
            format = formatWithFixedSignificantDigits(nDigits);
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
    
    static String format(double number, DecimalFormat format, String exponent, double normalization) {
        if (exponent != null) {
            return format.format(number/normalization) + "e" + exponent;
        } else {
            return format.format(number/normalization);
        }
    }
    
    static double orderOfMagnitude(double value) {
        return Math.floor(Math.log10(value));
    }
    
    static double orderOfMagnitude(double min, double max) {
        return orderOfMagnitude(Math.max(Math.abs(max), Math.abs(min)));
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
        double increment = Math.max(range/maxTick, minIncrement);
        int order = (int) orderOfMagnitude(increment);
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
    
    /**
     * Determines how many ticks would there be in that range using that increment.
     * 
     * @param min value range start
     * @param max value range end
     * @param increment space between ticks
     * @return number of ticks in the range
     */
    static int countTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        return end - start + 1;
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
    
}
