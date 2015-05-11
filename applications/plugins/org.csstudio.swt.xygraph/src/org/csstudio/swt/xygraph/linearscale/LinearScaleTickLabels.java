/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.linearscale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Linear Scale tick labels.
 *
 * @author Xihui Chen
 */
public class LinearScaleTickLabels extends Figure {

    private static final String MINUS = "-";

    private static final int TICK_LABEL_GAP = 2;

    /**
     * Get base^exponent
     */
    private static BigDecimal pow(double base, int exponent) {
        BigDecimal result;
        if (exponent >= 0) {
            result = BigDecimal.valueOf(base).pow(exponent);
        } else {
            result = BigDecimal.ONE.divide(BigDecimal.valueOf(base).pow(-exponent));
        }
        return result;
    }

    /** the array of tick label vales */
    private ArrayList<Double> tickLabelValues;

    /** the array of tick label */
    private ArrayList<String> tickLabels;

    /** the array of tick label position in pixels */
    private ArrayList<Integer> tickLabelPositions;

    /** the array of visibility state of tick label */
    private ArrayList<Boolean> tickLabelVisibilities;

    /** the maximum length of tick labels */
    private int tickLabelMaxLength;

    /** the maximum height of tick labels */
    private int tickLabelMaxHeight;

    private int gridStepInPixel;

    private LinearScale scale;

    /**
     * Constructor.
     *
     * @param linearScale
     *            the scale
     */
    protected LinearScaleTickLabels(LinearScale linearScale) {

        this.scale = linearScale;
        tickLabels = new ArrayList<String>();
        tickLabelValues = new ArrayList<Double>();
        tickLabelPositions = new ArrayList<Integer>();
        tickLabelVisibilities = new ArrayList<Boolean>();

        setFont(this.scale.getFont());
        setForegroundColor(this.scale.getForegroundColor());
    }

    /**
     * @return the gridStepInPixel
     */
    public int getGridStepInPixel() {
        return gridStepInPixel;
    }

    /**
     * @return the tickLabelMaxHeight
     */
    public int getTickLabelMaxHeight() {
        return tickLabelMaxHeight;
    }

    /**
     * @return the tickLabelMaxLength
     */
    public int getTickLabelMaxLength() {
        return tickLabelMaxLength;
    }

    /**
     * Gets the tick label positions.
     *
     * @return the tick label positions
     */
    public ArrayList<Integer> getTickLabelPositions() {
        return tickLabelPositions;
    }

    /**
     * @return the tickVisibilities
     */
    public ArrayList<Boolean> getTickVisibilities() {
        return tickLabelVisibilities;
    }

    /**
     * Draw the X tick.
     *
     * @param grahics
     *            the graphics context
     */
    private void drawXTick(Graphics grahics) {
        // draw tick labels
        grahics.setFont(scale.getFont());
        for (int i = 0; i < tickLabelPositions.size(); i++) {
            if (tickLabelVisibilities.get(i) == true) {
                String text = tickLabels.get(i);
                int fontWidth = FigureUtilities.getTextExtents(text, getFont()).width;
                int x = (int) Math.ceil(tickLabelPositions.get(i) - fontWidth / 2.0);// +
                                                                                        // offset);
                grahics.drawText(text, x, 0);
            }
        }
    }

    /**
     * Draw the Y tick.
     *
     * @param grahpics
     *            the graphics context
     */
    private void drawYTick(Graphics grahpics) {
        // draw tick labels
        grahpics.setFont(scale.getFont());
        int fontHeight = tickLabelMaxHeight;
        for (int i = 0; i < tickLabelPositions.size(); i++) {
            if (tickLabelVisibilities.isEmpty() || tickLabels.isEmpty()) {
                break;
            }

            if (tickLabelVisibilities.get(i)) {
                String label = tickLabels.get(i);
                int x = 0;
                if (tickLabels.get(0).startsWith(MINUS) && !label.startsWith(MINUS)) {
                    x += FigureUtilities.getTextExtents(MINUS, getFont()).width;
                }
                int y = (int) Math.ceil(scale.getLength() - tickLabelPositions.get(i) - fontHeight
                        / 2.0);
                grahpics.drawText(label, x, y);
            }
        }
    }

    /**
     * Gets the grid step.
     *
     * @param lengthInPixels
     *            scale length in pixels
     * @param min
     *            minimum value
     * @param max
     *            maximum value
     * @return rounded value.
     */
    private double getGridStep(int lengthInPixels, double min, double max) {
        if ((int) scale.getMajorGridStep() != 0) {
            return scale.getMajorGridStep();
        }

        if (lengthInPixels <= 0) {
            lengthInPixels = 1;
        }
        boolean minBigger = false;
        if (min >= max) {
            if (max == min)
                max++;
            else {
                minBigger = true;
                double swap = min;
                min = max;
                max = swap;
            }
            // throw new IllegalArgumentException("min must be less than max.");
        }

        double length = Math.abs(max - min);
        double majorTickMarkStepHint = scale.getMajorTickMarkStepHint();
        if (majorTickMarkStepHint > lengthInPixels)
            majorTickMarkStepHint = lengthInPixels;
        // if(min > max)
        // majorTickMarkStepHint = -majorTickMarkStepHint;
        double gridStepHint = length / lengthInPixels * majorTickMarkStepHint;

        if (scale.isDateEnabled()) {
            // by default, make the least step to be minutes

            long timeStep;
            if (max - min < 1000) // <1 sec, step = 10 ms
                timeStep = 10l;
            else if (max - min < 60000) // < 1 min, step = 1 sec
                timeStep = 1000l;
            else if (max - min < 600000) // < 10 min, step = 10 sec
                timeStep = 10000l;
            else if (max - min < 6400000) // < 2 hour, step = 1 min
                timeStep = 60000l;
            else if (max - min < 43200000) // < 12 hour, step = 10 min
                timeStep = 600000l;
            else if (max - min < 86400000) // < 24 hour, step = 30 min
                timeStep = 1800000l;
            else if (max - min < 604800000) // < 7 days, step = 1 hour
                timeStep = 3600000l;
            else
                timeStep = 86400000l;

            if (scale.getTimeUnit() == Calendar.SECOND) {
                timeStep = 1000l;
            } else if (scale.getTimeUnit() == Calendar.MINUTE) {
                timeStep = 60000l;
            } else if (scale.getTimeUnit() == Calendar.HOUR_OF_DAY) {
                timeStep = 3600000l;
            } else if (scale.getTimeUnit() == Calendar.DATE) {
                timeStep = 86400000l;
            } else if (scale.getTimeUnit() == Calendar.MONTH) {
                timeStep = 30l * 86400000l;
            } else if (scale.getTimeUnit() == Calendar.YEAR) {
                timeStep = 365l * 86400000l;
            }
            double temp = gridStepHint + (timeStep - gridStepHint % timeStep);
            if (minBigger)
                temp = -temp;
            return temp;
        }

        double mantissa = gridStepHint;
        int exp = 0;
        if (mantissa < 1) {
            if (mantissa != 0)
                while (mantissa < 1) {
                    mantissa *= 10.0;
                    exp--;
                }
        } else {
            while (mantissa >= 10) {
                mantissa /= 10.0;
                exp++;
            }
        }

        double gridStep;
        if (mantissa > 7.5) {
            // 10*10^exp
            gridStep = 10 * Math.pow(10, exp);
        } else if (mantissa > 3.5) {
            // 5*10^exp
            gridStep = 5 * Math.pow(10, exp);
        } else if (mantissa > 1.5) {
            // 2.0*10^exp
            gridStep = 2 * Math.pow(10, exp);
        } else {
            gridStep = Math.pow(10, exp); // 1*10^exponent
        }
        if (minBigger)
            gridStep = -gridStep;
        return gridStep;
    }

    /**
     * If it has enough space to draw the tick label
     */
    private boolean hasSpaceToDraw(int previousPosition, int tickLabelPosition,
            String previousTickLabel, String tickLabel) {
        Dimension tickLabelSize = FigureUtilities.getTextExtents(tickLabel, scale.getFont());
        Dimension previousTickLabelSize = FigureUtilities.getTextExtents(previousTickLabel,
                scale.getFont());
        int interval = tickLabelPosition - previousPosition;
        int textLength = (int) (scale.isHorizontal() ? (tickLabelSize.width / 2.0 + previousTickLabelSize.width / 2.0)
                : tickLabelSize.height);
        boolean noLapOnPrevoius = true;

        boolean noLapOnEnd = true;
        // if it is not the end tick label
        if (tickLabelPosition != tickLabelPositions.get(tickLabelPositions.size() - 1)) {
            noLapOnPrevoius = interval > (textLength + TICK_LABEL_GAP);
            Dimension endTickLabelSize = FigureUtilities.getTextExtents(
                    tickLabels.get(tickLabels.size() - 1), scale.getFont());
            interval = tickLabelPositions.get(tickLabelPositions.size() - 1) - tickLabelPosition;
            textLength = (int) (scale.isHorizontal() ? (tickLabelSize.width / 2.0 + endTickLabelSize.width / 2.0)
                    : tickLabelSize.height);
            noLapOnEnd = interval > textLength + TICK_LABEL_GAP;
        }
        return noLapOnPrevoius && noLapOnEnd;
    }

    /**
     * Checks if the tick label is major tick. For example: 0.001, 0.01, 0.1, 1,
     * 10, 100...
     */
    private boolean isMajorTick(double tickValue) {
        if (!scale.isLogScaleEnabled()) {
            return true;
        }

        double log10 = Math.log10(tickValue);
        if (log10 == Math.rint(log10)) {
            return true;
        }

        return false;
    }

    /**
     * Updates tick label for normal scale.
     *
     * @param length
     *            scale tick length (without margin)
     */
    private void updateTickLabelForLinearScale(int length) {
        double min = scale.getRange().getLower();
        double max = scale.getRange().getUpper();
        double gridStep = getGridStep(length, min, max);
        gridStepInPixel = (int) (length * gridStep / (max - min));
        updateTickLabelForLinearScale(length, gridStep);
    };

    /**
     * Updates tick label for normal scale.
     *
     * @param length
     *            scale tick length (without margin)
     * @param tickStep
     *            the tick step
     */
    private void updateTickLabelForLinearScale(int length, double tickStep) {
        double min = scale.getRange().getLower();
        double max = scale.getRange().getUpper();

        boolean minBigger = max < min;

        double firstPosition;

        // make firstPosition as the right most of min based on tickStep
        if (min % tickStep <= 0) {
            firstPosition = min - min % tickStep;
        } else {
            firstPosition = min - min % tickStep + tickStep;
        }

        // the unit time starts from 1:00
        if (scale.isDateEnabled()) {
            double zeroOclock = firstPosition - 3600000;
            if (min < zeroOclock) {
                firstPosition = zeroOclock;
            }
        }

        // add min
        boolean minDateAdded = false;
        if (min > firstPosition == minBigger) {
            tickLabelValues.add(min);
            if (scale.isDateEnabled()) {
                Date date = new Date((long) min);
                tickLabels.add(scale.format(date, true));
                minDateAdded = true;
            } else {
                tickLabels.add(scale.format(min));
            }
            tickLabelPositions.add(scale.getMargin());
        }

        double b = firstPosition;
        double previousB = Double.NaN;
        int i = 0;
        while (max >= min ? b < max : b > max) {
            if(Double.isNaN(previousB) || b != previousB){
                if (scale.isDateEnabled()) {
                Date date = new Date((long) b);
                tickLabels.add(scale.format(date, b == firstPosition && !minDateAdded));
            } else {
                tickLabels.add(scale.format(b));
            }
                tickLabelValues.add(b);
                int tickLabelPosition = (int) ((b - min) / (max - min) * length) + scale.getMargin();
                // - LINE_WIDTH;
                tickLabelPositions.add(tickLabelPosition);
            }
            previousB = b;
            i++;
            b = firstPosition + (i * tickStep);
        }

        // always add max
        tickLabelValues.add(max);
        if (scale.isDateEnabled()) {
            Date date = new Date((long) max);
            tickLabels.add(scale.format(date, true));
        } else {
            tickLabels.add(scale.format(max));
        }
        tickLabelPositions.add(scale.getMargin() + length);
        // }

    }

    /**
     * Updates tick label for log scale.
     *
     * @param length
     *            the length of scale
     */
    private void updateTickLabelForLogScale(int length) {
        double min = scale.getRange().getLower();
        double max = scale.getRange().getUpper();
        if (min <= 0 || max <= 0)
            throw new IllegalArgumentException("the range for log scale must be in positive range");
        boolean minBigger = max < min;

        double logMin = Math.log10(min);
        int minLogDigit = (int) Math.ceil(logMin);
        int maxLogDigit = (int) Math.ceil(Math.log10(max));

        final BigDecimal minDec = BigDecimal.valueOf(min);
        BigDecimal tickStep = pow(10, minLogDigit - 1);
        BigDecimal firstPosition;

        if (minDec.remainder(tickStep).doubleValue() <= 0) {
            firstPosition = minDec.subtract(minDec.remainder(tickStep));
        } else {
            if (minBigger)
                firstPosition = minDec.subtract(minDec.remainder(tickStep));
            else
                firstPosition = minDec.subtract(minDec.remainder(tickStep)).add(tickStep);
        }

        // add min
        boolean minDateAdded = false;
        if (minDec.compareTo(firstPosition) == (minBigger ? 1 : -1)) {
            tickLabelValues.add(min);
            if (scale.isDateEnabled()) {
                Date date = new Date((long) minDec.doubleValue());
                tickLabels.add(scale.format(date, true));
                minDateAdded = true;
            } else {
                tickLabels.add(scale.format(minDec.doubleValue()));
            }
            tickLabelPositions.add(scale.getMargin());
        }

        for (int i = minLogDigit; minBigger ? i >= maxLogDigit : i <= maxLogDigit; i += minBigger ? -1 : 1) {
            if (Math.abs(maxLogDigit - minLogDigit) > 20) {// if the range is too big,
                                                    // skip minor ticks.
                BigDecimal v = pow(10, i);
                if (v.doubleValue() > max)
                    break;
                if (scale.isDateEnabled()) {
                    Date date = new Date((long) v.doubleValue());
                    tickLabels.add(scale.format(date, i == minLogDigit && !minDateAdded));
                } else {
                    tickLabels.add(scale.format(v.doubleValue()));
                }
                tickLabelValues.add(v.doubleValue());

                int tickLabelPosition = (int) ((Math.log10(v.doubleValue()) - logMin)
                        / (Math.log10(max) - logMin) * length)
                        + scale.getMargin();
                tickLabelPositions.add(tickLabelPosition);
            } else {
                // must use BigDecimal because it involves equal comparison
                for (BigDecimal j = firstPosition; minBigger ? j.doubleValue() >= pow(10, i - 1)
                        .doubleValue() : j.doubleValue() <= pow(10, i).doubleValue(); j = minBigger ? j
                        .subtract(tickStep) : j.add(tickStep)) {
                    if (minBigger ? j.doubleValue() < max : j.doubleValue() > max) {
                        break;
                    }

                    if (scale.isDateEnabled()) {
                        Date date = new Date(j.longValue());
                        tickLabels.add(scale.format(date, j == firstPosition && !minDateAdded));
                    } else {
                        tickLabels.add(scale.format(j.doubleValue()));
                    }
                    int position = (int) ((Math.log10(j.doubleValue()) - logMin)
                            / (Math.log10(max) - logMin) * length)
                            + scale.getMargin();
                    tickLabelPositions.add(position);
                    tickLabelValues.add(j.doubleValue());
                }
                tickStep = minBigger ? tickStep.divide(pow(10, 1)) : tickStep.multiply(pow(10, 1));
                firstPosition = minBigger ? pow(10, i - 1) : tickStep.add(pow(10, i));
            }
        }

        // add max
        if (minBigger ? max < tickLabelValues.get(tickLabelValues.size() - 1)
                : max > tickLabelValues.get(tickLabelValues.size() - 1)) {
            tickLabelValues.add(max);
            if (scale.isDateEnabled()) {
                Date date = new Date((long) max);
                tickLabels.add(scale.format(date, true));
            } else {
                tickLabels.add(scale.format(max));
            }
            tickLabelPositions.add(scale.getMargin() + length);
        }
    }

    /**
     * Gets max length of tick label.
     */
    private void updateTickLabelMaxLengthAndHeight() {
        int maxLength = 0;
        int maxHeight = 0;
        for (int i = 0; i < tickLabels.size(); i++) {
            if (tickLabelVisibilities.size() > i && tickLabelVisibilities.get(i)) {
                Dimension p = FigureUtilities.getTextExtents(tickLabels.get(i), scale.getFont());
                if (tickLabels.get(0).startsWith(MINUS) && !tickLabels.get(i).startsWith(MINUS)) {
                    p.width += FigureUtilities.getTextExtents(MINUS, getFont()).width;
                }
                if (p.width > maxLength) {
                    maxLength = p.width;
                }
                if (p.height > maxHeight) {
                    maxHeight = p.height;
                }
            }
        }
        tickLabelMaxLength = maxLength;
        tickLabelMaxHeight = maxHeight;
    }

    private void updateTickVisibility() {

        tickLabelVisibilities.clear();

        if (tickLabelPositions.isEmpty())
            return;

        for (int i = 0; i < tickLabelPositions.size(); i++) {
            tickLabelVisibilities.add(Boolean.TRUE);
        }


        // set the tick label visibility
        int previousPosition = 0;
        String previousLabel = null;
        Double previousValue = null;
        for (int i = 0; i < tickLabelPositions.size(); i++) {
            // check if it has space to draw
            boolean hasSpaceToDraw = true;
            String currentLabel = tickLabels.get(i);
            int currentPosition = tickLabelPositions.get(i);
            if (i != 0) {
                hasSpaceToDraw = hasSpaceToDraw(previousPosition, currentPosition,
                        previousLabel, currentLabel);
            }
            Double currentValue = tickLabelValues.get(i);
            // check if repeated
            boolean isRepeatSameTickAndNotEnd = currentValue.equals(previousValue)
                    && (i != 0 && i != tickLabelPositions.size() - 1);

            // check if it is major tick label
            boolean isMajorTickOrEnd = true;
            if (scale.isLogScaleEnabled()) {
                isMajorTickOrEnd = isMajorTick(tickLabelValues.get(i)) || i == 0
                        || i == tickLabelPositions.size() - 1;
            }

            if (!hasSpaceToDraw || isRepeatSameTickAndNotEnd || !isMajorTickOrEnd) {
                tickLabelVisibilities.set(i, Boolean.FALSE);
            } else {
                previousPosition = currentPosition;
                previousLabel = currentLabel;
                previousValue = currentValue;
            }
        }
    }

    @Override
    protected void paintClientArea(Graphics graphics) {
        graphics.translate(bounds.x, bounds.y);
        if (scale.isHorizontal()) {
            drawXTick(graphics);
        } else {
            drawYTick(graphics);
        }

        super.paintClientArea(graphics);
    }

    /**
     * Updates the tick labels.
     *
     * @param length
     *            scale length without margin
     */
    protected void update(int length) {
        tickLabels.clear();
        tickLabelValues.clear();
        tickLabelPositions.clear();

        if (scale.isLogScaleEnabled()) {
            updateTickLabelForLogScale(length);
        } else {
            updateTickLabelForLinearScale(length);
        }

        updateTickVisibility();
        updateTickLabelMaxLengthAndHeight();
    }

}
