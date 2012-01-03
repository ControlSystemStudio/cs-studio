/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: Ranges.java,v 1.1 2009/08/26 07:08:44 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import javax.annotation.Nonnull;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 29.06.2007
 */
public final class Ranges {

    /**
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.1 $
     * @since 10.12.2007
     */
    public static class Value {
        /**
         * The minimum Value.
         */
        private final long _min;
        /**
         * The Maximum Value.
         */
        private final long _max;
        /**
         * The default Value.
         */
        private final long _default;

        /**
         * The default Constructor.
         * @param min the minimum Value.
         * @param max the maximum Value.
         * @param def the default Value.
         */
        public Value(final long min, final long max, final long def) {
            this._min = min;
            this._max = max;
            _default = def;
        }

        /**
         *
         * @return the default Value.
         */
        public final long getDefault() {
            return _default;
        }

        /**
         *
         * @return the maximum Value.
         */
        public final long getMax() {
            return _max;
        }

        /**
         *
         * @return the minimum Value.
         */
        public final long getMin() {
            return _min;
        }
    }

    /**
     *  The Value for Tslot_Init. (OK)
     */
    public static final Value TSLOT_INIT = getRangeValue(37 + 15, 16383, 550);
    /**
     * The Value for maxTsdr. (OK)
     */
    /**TODO: Der max Wert von_maxTsdr stimmt nicht. fehlen hinweise auf dier größe von t_Bit.*/
    public static final Value MAX_TSDR = getRangeValue(37, Long.MAX_VALUE, 150);

    /**
     *  The Value for tset. (OK)
     */
    public static final Value TSET = getRangeValue(1, 494, 1);

    /** The Value for GAP. (OK)*/
    public static final Value GAP_RANGE = getRangeValue(1, 100, 1);

    /** The retray limit for the Profibus. (Max Different 8 or 15)*/
    public static final Value RETRAY_LIMIT = getRangeValue(1, 15, 3);

    /** The Target Rotation Time for the Profibus. (OK)*/
    public static final Value TTR = getRangeValue(0, 16777960, 750000);

    /** The minimum Watchdog time for the Profibus. (OK)*/
    public static final Value WATCHDOG = getRangeValue(0, 65535, 1000);

    /** The Slave Flag*/
    public static final Value SLAVE_FLAG = getRangeValue(0, 65535, 128);

    /** The min value for a U8 / U16 or U32 type.*/
    static final short MIN = 0;
    /** The max value for a U8 type.             */
    static final short MAX_U8 = 255;
    /** The max value for a U16 type.            */
    static final int MAX_U16 = 65535;
    /** The max value for a U32 type.            */
    static final long MAX_U32 = (long) Math.pow(2, 32);

    //CHECKSTLYE OFF: DeclarationOrder
    /**
     *  The  Value for minTsdr. (OK)
     */
    /**TODO: Der max Wert von_minTsdr stimmt nicht. fehlen hinweise auf die größe von maxTsdr.*/
    public static final Value MIN_TSDR = getRangeValue(11, MAX_U16, 11);

    /** The Value for tqui. (OK)*/
    public static final Value TQUI = getRangeValue(0, MAX_U8, 0);

    //CHECKSTLYE ON: DeclarationOrder

    /**
     * Default Constructor.
     */
    private Ranges() {
        // Default Constructor.
    }

    /**
     *
     * @param min limit
     * @param max limit
     * @param def default Value.
     * @return return a Range Value with min / max limits
     */
    @Nonnull
    public static Value getRangeValue(final long min, final long max, final long def) {
        return new Value(min, max, def);
    }

}
