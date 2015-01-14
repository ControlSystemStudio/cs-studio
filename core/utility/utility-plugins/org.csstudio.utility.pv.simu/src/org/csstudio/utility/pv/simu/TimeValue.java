/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.ITimestamp.Format;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/** Dynamic value that holds current time as string
 *  @author Kay Kasemir
 */
public class TimeValue extends DynamicValue
{
    /** Initialize
     *  @param name
     */
    public TimeValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        final ITimestamp now = TimestampFactory.now();
        final String text = now.format(Format.DateTimeSeconds);
        final ISeverity severity = ValueFactory.createOKSeverity();
        setValue(ValueFactory.createStringValue(now , severity, severity.toString(), Quality.Original, new String[] { text }));
    }
}
