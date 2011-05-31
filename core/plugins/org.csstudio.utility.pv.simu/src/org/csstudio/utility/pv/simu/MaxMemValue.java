/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/** Dynamic value that holds maximum memory (MB)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MaxMemValue extends Value
{
    private static final double MB = 1024.0 * 1024.0;
    private final INumericMetaData meta;

    /** Initialize
     *  @param name
     */
    public MaxMemValue(final String name)
    {
        super(name);
        final double max = Runtime.getRuntime().maxMemory() / MB;
        meta = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 3, "MB");
        final ISeverity severity = ValueFactory.createOKSeverity();
        setValue(ValueFactory.createDoubleValue(TimestampFactory.now(), severity, severity.toString(), meta, Quality.Original,
                new double[] { max }));
    }
}
