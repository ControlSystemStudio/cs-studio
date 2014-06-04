/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.*;
import org.epics.util.time.Timestamp;
import org.epics.vtype.ValueUtil;

/**
 * Function to simulate a 2D waveform containing a sine wave.
 *
 * @author carcassi
 */
public class Sine2DWaveform extends SimFunction<VDoubleArray> {

    private final double periodInSeconds;
    private final double wavelengthInSamples;
    private final int xSamples;
    private final int ySamples;
    private final double angle;
    private Timestamp initialReference;

    /**
     * Creates sine wave of 100 samples, with period of 1 second, wavelength of
     * 100 samples along the x axis, updating at 10 Hz.
     */
    public Sine2DWaveform() {
        this(1.0, 100.0, 0.1);
    }
    
    /**
     * Creates sine wave of 100 samples, with given period and given wavelength of
     * 100 samples along the x axis, updating at given rate.
     *
     * @param periodInSeconds the period measured in seconds
     * @param wavelengthInSamples the wavelength measured in samples
     * @param updateRateInSeconds the update rate in seconds
     */
    public Sine2DWaveform(Double periodInSeconds, Double wavelengthInSamples, Double updateRateInSeconds) {
        this(periodInSeconds, wavelengthInSamples, 100.0, updateRateInSeconds);
    }

    /**
     * Creates sine wave of 100 samples, with given period and given wavelength of
     * given number of samples along the x axis, updating at given rate.
     *
     * @param periodInSeconds the period measured in seconds
     * @param wavelengthInSamples the wavelength measured in samples
     * @param nSamples the number of samples
     * @param updateRateInSeconds the update rate in seconds
     */
    public Sine2DWaveform(Double periodInSeconds, Double wavelengthInSamples, Double nSamples, Double updateRateInSeconds) {
        this(periodInSeconds, wavelengthInSamples, 0.0, nSamples, nSamples, updateRateInSeconds);
    }

    /**
     * Creates sine wave with given parameters.
     *
     * @param periodInSeconds the period measured in seconds
     * @param wavelengthInSamples the wavelength measured in samples
     * @param angle the direction of propagation for the wave
     * @param xSamples number of samples on the x direction
     * @param ySamples number of samples on the y direction
     * @param updateRateInSeconds the update rate in seconds
     */
    public Sine2DWaveform(Double periodInSeconds, Double wavelengthInSamples, Double angle, Double xSamples, Double ySamples, Double updateRateInSeconds) {
        super(updateRateInSeconds, VDoubleArray.class);
        this.periodInSeconds = periodInSeconds;
        this.wavelengthInSamples = wavelengthInSamples;
        this.xSamples = xSamples.intValue();
        this.ySamples = ySamples.intValue();
        this.angle = angle;
        if (this.xSamples <= 0 || this.ySamples <= 0) {
            throw new IllegalArgumentException("Number of sample must be a positive integer.");
        }
    }

    private ListDouble generateNewValue(final double omega, final double t, double k) {
        final double kx = Math.cos(angle * Math.PI / 180.0) * k;
        final double ky = Math.sin(angle * Math.PI / 180.0) * k;
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                int x = index % xSamples;
                int y = index / xSamples;
                return Math.sin(omega * t + kx* x + ky * y);
            }

            @Override
            public int size() {
                return xSamples*ySamples;
            }
        };
    }

    @Override
    VDoubleArray nextValue() {
        if (initialReference == null) {
            initialReference = lastTime;
        }
        double t = lastTime.durationFrom(initialReference).toSeconds();
        double omega = 2 * Math.PI / periodInSeconds;
        double k = 2 * Math.PI / wavelengthInSamples;
        double min = -1.0;
        double max = 1.0;
        double range = 0.0;
        return (VDoubleArray) ValueFactory.newVNumberArray(generateNewValue(omega, t, k), new ArrayInt(ySamples, xSamples),
                ValueUtil.defaultArrayDisplay(new ArrayInt(ySamples, xSamples)), alarmNone(),
                newTime(lastTime), newDisplay(min, min + range * 0.1, min + range * 0.2, "", Constants.DOUBLE_FORMAT,
                min + range * 0.8, min + range * 0.9, max, min, max));
    }
}
