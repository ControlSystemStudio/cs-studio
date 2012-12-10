/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VType;

/** Formatter for {@link VType} values that uses the default {@link Display} info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DefaultVTypeFormat extends VTypeFormat
{
    /** {@inheritDoc} */
    @Override
    public void format(final Number number,
            final Display display, final StringBuilder buf)
    {
        if (display != null  &&  display.getFormat() != null)
            buf.append(display.getFormat().format(number));
        else
            buf.append(number);
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Default";
    }
}
