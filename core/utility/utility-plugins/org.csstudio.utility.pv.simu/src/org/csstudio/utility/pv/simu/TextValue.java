/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/** Static value that holds a text
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TextValue extends Value
{
    /** Initialize
     *  @param name
     *  @param text
     *  @param valid
     */
    public TextValue(final String name, final String text, final boolean valid)
    {
        super(name);
        final ISeverity severity;
        final String status;
        if (valid)
        {
            severity = ValueFactory.createOKSeverity();
            status = severity.toString();
        }
        else
        {
            severity = ValueFactory.createInvalidSeverity();
            status = "undefined";
        }
        setValue(ValueFactory.createStringValue(TimestampFactory.now(), severity, status, Quality.Original,
                new String[] { text }));
    }

    /** Initialize
     *  @param name
     *  @param text
     */
    public TextValue(final String name, final String text)
    {
        this(name, text, true);
    }
}
