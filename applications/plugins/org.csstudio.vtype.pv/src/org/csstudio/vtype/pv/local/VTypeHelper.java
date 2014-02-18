/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.local;

import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

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
}
