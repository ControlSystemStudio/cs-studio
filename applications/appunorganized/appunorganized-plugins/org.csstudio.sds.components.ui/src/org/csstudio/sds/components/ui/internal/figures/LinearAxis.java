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

import java.util.List;

/**
 * Maps data values to display coordinates.
 *
 * @author Joerg Rathlev
 */
final class LinearAxis implements IAxis {

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
     * Creates a new axis.
     *
     * @param dataLower
     *            the lower bound of the data range.
     * @param dataUpper
     *            the upper bound of the data range.
     * @param displaySize
     *            the size of the display.
     */
    LinearAxis(final double dataLower, final double dataUpper,
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
    public int valueToCoordinate(final double value) {
        double dataRange = _dataUpper - _dataLower;
        double scaling = (_displaySize - 1) / dataRange;

        long intermediate = Math.round((value - _dataLower) * scaling);
        // constrain the value to an integer value
        return intermediate > Integer.MAX_VALUE ? Integer.MAX_VALUE
                : (intermediate < Integer.MIN_VALUE ? Integer.MIN_VALUE
                        : (int) intermediate);
    }

    /**
     * Checks whether the specified value is a legal value for this axis. For
     * a linear axis, all values except NaN are legal.
     *
     * @param value
     *            the data value to check.
     * @return <code>true</code> if the value is legal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean isLegalValue(final double value) {
        // Note: cannot use == or != operator here because NaN != NaN.
        return !Double.isNaN(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> calculateTicks(final int minMajorDistance,
            final int minMinorDistance) {

        // TODO: calculate minor tickmarks. TickCalculator calculates only
        // major tickmarks.

        TickCalculator calc = new TickCalculator();
        if (_dataLower < _dataUpper) {
            calc.setMinimumValue(_dataLower);
            calc.setMaximumValue(_dataUpper);
        } else {
            calc.setMinimumValue(_dataUpper);
            calc.setMaximumValue(_dataLower);
        }
        calc.setMaximumTickCount(_displaySize / minMajorDistance);
        return calc.calculateTicks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> calculateIntegerTicks(final int minMajorDistance,
            final int minMinorDistance) {

        // TODO: calculate minor tickmarks. TickCalculator calculates only
        // major tickmarks.

        TickCalculator calc = new TickCalculator();
        if (_dataLower < _dataUpper) {
            calc.setMinimumValue(_dataLower);
            calc.setMaximumValue(_dataUpper);
        } else {
            calc.setMinimumValue(_dataUpper);
            calc.setMaximumValue(_dataLower);
        }
        calc.setMaximumTickCount(_displaySize / minMajorDistance);
        calc.setIntegerOnly(true);
        return calc.calculateTicks();
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

}
