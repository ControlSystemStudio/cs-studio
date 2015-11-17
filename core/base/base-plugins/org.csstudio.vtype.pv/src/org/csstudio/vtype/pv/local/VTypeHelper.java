/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.local;

import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** Local Process Variable
 *
 *  @author Kay Kasemir
 */
public class VTypeHelper
{
    /** @param value_text Text that contains a value
     *  @return VType for the value text
     */
    public static VType toVType(final String value_text)
    {
        try
        {
            final double d = Double.parseDouble(value_text);
            return ValueFactory.newVDouble(d);
        }
        catch (Exception ex)
        {
            return ValueFactory.newVString(value_text, ValueFactory.alarmNone(), ValueFactory.timeNow());
        }
    }

    // All the array[] conversions clone the received value
    // to prevent side effects from changes to original array.
    // Inefficient, but safe, since ValueFactory implementation wraps data w/o copy.

    /** @param value Double array
     *  @return VType for value
     */
    public static VType toVType(final double[] value)
    {
        final double[] clone = new double[value.length];
        System.arraycopy(value, 0, clone, 0, value.length);
        return ValueFactory.toVType(clone);
    }

    /** @param value Float array
     *  @return VType for value
     */
    public static VType toVType(final float[] value)
    {
        final float[] clone = new float[value.length];
        System.arraycopy(value, 0, clone, 0, value.length);
        return ValueFactory.toVType(clone);
    }

    /** @param value Long array
     *  @return VType for value
     */
    public static VType toVType(final long[] value)
    {
        final long[] clone = new long[value.length];
        System.arraycopy(value, 0, clone, 0, value.length);
        return ValueFactory.toVType(clone);
    }

    /** @param value Int array
     *  @return VType for value
     */
    public static VType toVType(final int[] value)
    {
        final int[] clone = new int[value.length];
        System.arraycopy(value, 0, clone, 0, value.length);
        return ValueFactory.toVType(clone);
    }

    /** @param value Short array
     *  @return VType for value
     */
    public static VType toVType(final short[] value)
    {
        final short[] clone = new short[value.length];
        System.arraycopy(value, 0, clone, 0, value.length);
        return ValueFactory.toVType(clone);
    }

    /** @param value Byte array
     *  @return VType for value
     */
    public static VType toVType(final byte[] value)
    {
        final byte[] clone = new byte[value.length];
        System.arraycopy(value, 0, clone, 0, value.length);
        return ValueFactory.toVType(clone);
    }
}
