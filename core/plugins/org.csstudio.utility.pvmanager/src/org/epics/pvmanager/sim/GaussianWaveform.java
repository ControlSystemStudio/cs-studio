/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import java.util.Random;
import org.epics.pvmanager.data.VDoubleArray;
import static org.epics.pvmanager.data.ValueFactory.*;
import org.epics.util.time.Timestamp;

/**
 * Function to simulate a waveform containing a gaussian that moves to the
 * right.
 *
 * @author carcassi
 */
public class GaussianWaveform extends SimFunction<VDoubleArray> {

    private Random rand = new Random();
    private double[] buffer;
    private VDoubleArray lastValue;
    private int counter;

    /**
     * Creates a gaussian waveform with a normal distribution (average zero and
     * standard deviation one), updating every 100ms (10Hz).
     */
    public GaussianWaveform() {
        this(1.0, 100.0, 0.1);
    }

    /**
     * Creates a gaussian waveform signal with a gaussian distribution, updating at the rate
     * specified.
     *
     * @param stdDev standard deviation of the gaussian distribution
     * @param nSamples number of elements in the waveform
     * @param interval time between samples in seconds
     */
    public GaussianWaveform(Double stdDev, Double nSamples, Double interval) {
        super(interval, VDoubleArray.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
        int size = nSamples.intValue();
        buffer = new double[size];
        populateGaussian(buffer, stdDev);
    }

    static void populateGaussian(double[] array, double stdDev) {
        for (int i = 0; i < array.length; i++) {
            array[i] = gaussian(i, array.length / 2.0, stdDev);
        }
    }

    private double[] generateNewValue() {
        if (counter >= buffer.length) {
            counter -= buffer.length;
        }
        int localCounter = counter;
        double[] newArray = new double[buffer.length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = buffer[localCounter];
            localCounter++;
            if (localCounter >= buffer.length) {
                localCounter -= buffer.length;
            }
        }

        counter++;

        return newArray;
    }

    /**
     * 1D gaussian, centered on centerX and with the specified width.
     * @param x coordinate x
     * @param centerX center of the gaussian on x
     * @param width width of the gaussian in all directions
     * @return the value of the function at the given coordinates
     */
    public static double gaussian(double x, double centerX, double width) {
        return Math.exp((-Math.pow((x - centerX), 2.0)) / width);
    }

    @Override
    VDoubleArray nextValue() {
        if (lastTime == null)
            lastTime = Timestamp.now();
        return newVDoubleArray(generateNewValue(), alarmNone(),
                newTime(lastTime), newDisplay(-0.5, -0.35, -0.25, "x", Constants.DOUBLE_FORMAT,
                1.0, 1.10, 1.25, -0.5, 1.25));
    }
}
