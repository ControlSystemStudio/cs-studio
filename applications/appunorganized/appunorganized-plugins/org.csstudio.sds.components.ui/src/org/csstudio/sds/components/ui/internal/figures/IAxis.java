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
interface IAxis {

    /**
     * <p>Converts a data value to a display coordinate.</p>
     *
     * <p>This method returns a coordinate for all data values for which a
     * coordinate can be calculated, even if the data value is not within the
     * data range of this axis. In other words, this method assumes an
     * infinitely large (more precisely,
     * {@code Integer.MIN_VALUE .. Integer.MAX_VALUE}) display. This behavior
     * is required for line graphs to make sure that the line is drawn with the
     * correct slope, even if some of the values it connects lie outside the
     * data range of the plot.</p>
     *
     * <p>If the specified value cannot be converted to a coordinate (for
     * example, if this axis is a logarithmic axis and value is lower than or
     * equal to zero), the return value is unspecified. Clients should call
     * {@link #isLegalValue} to check whether a value can be converted to a
     * coordinate.</p>
     *
     * @param value
     *            the data value.
     * @return the display coordinate.
     */
    int valueToCoordinate(double value);

    /**
     * Checks whether the specified value is a legal value for this axis. Legal
     * values are all values that can be converted to display coordinates,
     * whether or not the value lies within the data range of this axis. An
     * example of an illegal value is a value lower than or equal to zero for an
     * axis with logarithmic scaling.
     *
     * @param value
     *            the data value to check.
     * @return <code>true</code> if the value is legal, <code>false</code>
     *         otherwise.
     */
    boolean isLegalValue(double value);

    /**
     * Calculates the ticks to display on this axis.
     *
     * @param minMajorDistance
     *            the minimum distance of major ticks, in display units. Set
     *            this to a negative value or zero if you don't want any major
     *            ticks to be generated.
     * @param minMinorDistance
     *            the minimum distance of minor ticks, in display units. Set
     *            this to a negative value or zero if you don't want any minor
     *            ticks to be generated.
     * @return the list of ticks to display on this axis.
     */
    List<Tick> calculateTicks(int minMajorDistance, int minMinorDistance);

    /**
     * Calculates the ticks to display on this axis. This method places major
     * tickmarks only at integral values.
     *
     * @param minMajorDistance
     *            the minimum distance of major ticks, in display units. Set
     *            this to a negative value or zero if you don't want any major
     *            ticks to be generated.
     * @param minMinorDistance
     *            the minimum distance of minor ticks, in display units. Set
     *            this to a negative value or zero if you don't want any minor
     *            ticks to be generated.
     * @return the list of ticks to display on this axis.
     */
    List<Tick> calculateIntegerTicks(int minMajorDistance, int minMinorDistance);

    /**
     * Sets the data range to a new range. The specified upper bound must be
     * greater than the specified lower bound. Implementations of this interface
     * may impose additional restrictions.
     *
     * @param lower
     *            the new lower bound of the data range.
     * @param upper
     *            the new upper bound of the data range.
     */
    void setDataRange(double lower, double upper);

    /**
     * Sets the display size to a new value.
     *
     * @param size
     *            the new display size.
     */
    void setDisplaySize(int size);

}
