/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.model;

import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;

/** Helper for handling {@link VType} data
 *  @author Kay Kasemir
 */
public class VTypeHelper
{
    /** @param value {@link VType}
     *  @return Value as String
     */
    public static String getString(final VType value)
    {
        if (value == null)
            return null;
        if (value instanceof VNumber)
            return ((VNumber)value).getValue().toString();
        if (value instanceof VEnum)
            return ((VEnum)value).getValue();
        else if (value instanceof VString)
            return ((VString)value).getValue();
        // Else: Hope that value somehow represents itself
        return value.toString();
    }
}
