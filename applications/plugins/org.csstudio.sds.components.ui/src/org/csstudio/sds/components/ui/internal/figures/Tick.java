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

/**
 * A tick shown along an axis.
 *
 * @author Joerg Rathlev
 */
final class Tick {

    /**
     * The type of this tick.
     */
    private final TickType _type;

    /**
     * The value at which this tick appears.
     */
    private final double _value;

    /**
     * Creates a new tick.
     *
     * @param type the type of this tick.
     * @param value the value at which this tick appears.
     */
    public Tick(final TickType type, final double value) {
        if (type == null) {
            throw new IllegalArgumentException("type == null");
        }

        _type = type;
        _value = value;
    }

    /**
     * Returns the type of this tick.
     *
     * @return the type of this tick.
     */
    public TickType type() {
        return _type;
    }

    /**
     * Returns the value at which this tick appears.
     *
     * @return the value at which this tick appears.
     */
    public double value() {
        return _value;
    }

    /**
     * Returns a string representation of this tick.
     *
     * @return a string representation of this tick.
     */
    @Override
    public String toString() {
        return "[" + _type + "," + _value + "]";
    }
}
