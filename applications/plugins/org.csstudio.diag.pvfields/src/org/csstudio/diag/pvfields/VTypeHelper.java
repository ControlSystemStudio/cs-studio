/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;

/** Helper for handling {@link VType}
 *  @author Kay Kasemir
 */
public class VTypeHelper
{
     /** Format value as string
     *  @param value Value
     *  @return String representation
     */
    final public static String toString(final VType value)
    {
        if (value instanceof VNumber)
            return ((VNumber)value).getValue().toString();
        if (value instanceof VEnum)
        {
        	try
        	{
        		return ((VEnum)value).getValue();
        	}
        	catch (ArrayIndexOutOfBoundsException ex)
        	{	// PVManager doesn't handle enums that have no label
        		return "<enum " + ((VEnum)value).getIndex() + ">";
        	}
        }
        if (value instanceof VString)
            return ((VString)value).getValue();
        if (value == null)
            return "null";
        return value.toString();
    }
 }
