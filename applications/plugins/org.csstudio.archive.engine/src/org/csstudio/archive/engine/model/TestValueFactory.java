/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;


/** Helper for creating test data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestValueFactory
{
    final private static ISeverity severity = ValueFactory.createOKSeverity();
    final private static String status = "Test";
    final private static INumericMetaData meta_data =
        ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 9, 2, "Eggs");

    public static IValue getDouble(double value)
    {
        final ITimestamp time = TimestampFactory.now();
    	return ValueFactory.createDoubleValue(time,
                severity, status, meta_data,
                IValue.Quality.Original,
                new double[] { value } );
    }
}
