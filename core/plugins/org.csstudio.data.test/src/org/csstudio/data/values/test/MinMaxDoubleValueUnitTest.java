/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.test;

import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.internal.SeverityInstances;
import org.junit.Test;
import static org.junit.Assert.*;

/** Simple test of value formatting.
 *  @author Kay Kasemir
 */
public class MinMaxDoubleValueUnitTest
{
    @SuppressWarnings("nls")
    @Test
    public void testMinMaxDouble()
    {
        INumericMetaData meta = ValueFactory.createNumericMetaData(
                        0.0, 10.0, 2.0, 8.0, 1.0, 9.0, 2, "socks");
        IMinMaxDoubleValue value = ValueFactory.createMinMaxDoubleValue(
                        TimestampFactory.now(),
                        SeverityInstances.minor,
                        "OK",
                        meta,
                        IValue.Quality.Interpolated,
                        new double[] { 3.14 },  3.1, 3.2);
        final String txt = value.toString();
        System.out.println(txt);
        // Compare all but the time stamp
        assertEquals("\t3.14 [ 3.1 ... 3.2 ]\tMINOR, OK", txt.substring(29));
    }
}
