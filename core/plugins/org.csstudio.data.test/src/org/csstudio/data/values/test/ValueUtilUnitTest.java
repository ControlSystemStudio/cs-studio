/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.test;

import static org.junit.Assert.*;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.ValueUtil;
import org.junit.Test;

/** JUnit test of the {@link ValueUtil}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueUtilUnitTest
{
    @Test
    public void testArrays()
    {
        // Int. (log) data
        final long data[] = new long[10];
        for (int i=0; i<data.length; ++i)
            data[i] = i;

        // Wrap as IValue
        final IValue value = ValueFactory.createLongValue(TimestampFactory.now(), ValueFactory.createOKSeverity(),  "OK", null,  IValue.Quality.Original, data);

        // Check ValueUtil
        assertEquals(data.length, ValueUtil.getSize(value));

        assertEquals((double) data[0], ValueUtil.getDouble(value), 0.001);


        final double dbl[] = ValueUtil.getDoubleArray(value);

        assertEquals(data.length, dbl.length);
        for (int i=0; i<data.length; ++i)
        {
            assertEquals((double) data[i], dbl[i], 0.001);
            assertEquals((double) data[i], ValueUtil.getDouble(value, i), 0.001);
        }
    }
}
