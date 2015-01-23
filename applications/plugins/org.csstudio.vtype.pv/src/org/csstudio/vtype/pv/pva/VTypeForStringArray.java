/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import java.util.List;

import org.epics.pvdata.pv.PVStructure;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VType;
import org.epics.vtype.VTypeToString;

/** Hold/decode data of {@link PVStructure} in {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class VTypeForStringArray extends VTypeTimeAlarmDisplayBase implements VStringArray
{
    final private List<String> value;

    public VTypeForStringArray(final PVStructure struct) throws Exception
    {
        super(struct);
        value = PVStructureHelper.getStrings(struct, "value");
    }

    @Override
    public ListInt getSizes()
    {
        return new ArrayInt(value.size());
    }

    @Override
    public List<String> getData()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
