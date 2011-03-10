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

/** Dynamic value that holds free memory (MB)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FreeMemValue extends DynamicValue
{
    private static final double MB = 1024.0 * 1024.0;
    private final INumericMetaData meta;

    /** Initialize
     *  @param name
     */
    public FreeMemValue(final String name)
    {
        super(name);
        final double max = Runtime.getRuntime().maxMemory() / MB;
        meta = ValueFactory.createNumericMetaData(0, max, 0, max, 0, max, 3, "MB");
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        final double free = Runtime.getRuntime().freeMemory() / MB;
        final ISeverity severity = ValueFactory.createOKSeverity();
        setValue(ValueFactory.createDoubleValue(TimestampFactory.now(), severity, severity.toString(), meta, Quality.Original,
                new double[] { free }));
    }
}
