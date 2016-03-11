/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.sds.components.ui.internal.figures;

import java.util.ArrayList;
import java.util.List;

/**
 * A logarithmic axis.
 *
 * @author Joerg Rathlev
 */
final class LogarithmicAxis implements IAxis {

    /**
     * The lower bound of the data range of this axis.
     */
    private double _dataLower;

    /**
     * The upper bound of the data range of this axis.
     */
    private double _dataUpper;

    /**
     * The size of this axis in display units.
     */
    private int _displaySize;

    /**
     * Creates a new logarithmic axis.
     *
     * @param dataLower
     *            the lower bound of the data range.
     * @param dataUpper
     *            the uppper bound of the data range.
     * @param displaySize
     *            the display size.
     */
    LogarithmicAxis(final double dataLower, final double dataUpper,
            final int displaySize) {
        if (displaySize < 0) {
            throw new IllegalArgumentException("Invalid display size");
        }

        _dataLower = dataLower;
        _dataUpper = dataUpper;
        _displaySize = displaySize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataRange(final double lower, final double upper) {
        _dataLower = lower;
        _dataUpper = upper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplaySize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid display size");
        }

        _displaySize = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int valueToCoordinate(final double value) {
        double dataRange = Math.log10(_dataUpper) - Math.log10(_dataLower);
        double scaling = (_displaySize - 1) / dataRange;

        long intermediate = Math.round((Math.log10(value) - Math.log10(_dataLower)) * scaling);
        // constrain the value to an integer value
        return intermediate > Integer.MAX_VALUE ? Integer.MAX_VALUE
                : (intermediate < Integer.MIN_VALUE ? Integer.MIN_VALUE
                        : (int) intermediate);
    }

    /**
     * Checks whether the specified value is a legal value for this axis. For
     * a logarithmic axis, all values &gt; 0 are legal.
     *
     * @param value
     *            the data value to check.
     * @return <code>true</code> if the value is legal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean isLegalValue(final double value) {
        return value > 0.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> calculateTicks(final int minMajorDistance, final int minMinorDistance) {
        double lower = Math.log10(_dataLower);
        double upper = Math.log10(_dataUpper);

        // convert the minimum distance into log data units
        double dataRange = upper - lower;
        double scaling = (_displaySize - 1) / dataRange;
        double exactDistance = minMajorDistance / scaling;

        // now use basically the same algorithm as in TickCalculator,
        // except we already are calculating in log scale
//        double magnitude = Math.floor(exactDistance);
//        _tickDistance = Math.ceil(exactDist / Math.pow(10, o - 1)) * Math.pow(10, o - 1);
        double distance = Math.ceil(exactDistance);
        double lowestTick = Math.ceil(lower / distance) * distance;

        List<Tick> result = new ArrayList<Tick>();
        if (distance > 0) {
            double value = lowestTick;
            while (value <= upper) {
                Tick tick = new Tick(TickType.MAJOR, Math.pow(10, value));
                result.add(tick);

                // TODO: calculate minor tickmarks. The minor tickmarks would
                // have to be added here, in between the major tickmarks.

                value += distance;
            }
        }
        return result;

//        TickCalculator calc = new TickCalculator();
//        calc.setMinimumValue(_dataLower);
//        calc.setMaximumValue(_dataUpper);
//        calc.setMaximumTickCount(_displaySize / minMajorDistance);
//        return calc.calculateTicks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> calculateIntegerTicks(final int minMajorDistance,
            final int minMinorDistance) {
        // TODO filter to only integer ticks.
        return calculateTicks(minMajorDistance, minMinorDistance);
    }

}
