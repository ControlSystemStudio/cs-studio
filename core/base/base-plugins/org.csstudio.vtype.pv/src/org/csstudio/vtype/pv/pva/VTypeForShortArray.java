/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import java.util.List;

import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStructure;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListShort;
import org.diirt.vtype.ArrayDimensionDisplay;
import org.diirt.vtype.VShortArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.VTypeToString;
import org.diirt.vtype.ValueUtil;

/** Hold/decode data of {@link PVStructure} in {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class VTypeForShortArray extends VTypeTimeAlarmDisplayBase implements VShortArray
{
    final private ListShort value;

    public VTypeForShortArray(final PVStructure struct)
    {
        super(struct);
        final PVScalarArray pv_array = struct.getSubField(PVScalarArray.class, "value");
        final int length = pv_array.getLength();
        final short[] data = new short[length];
        PVStructureHelper.convert.toShortArray(pv_array, 0, length, data, 0);
        value = new ArrayShort(data);
    }

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay()
    {
        return ValueUtil.defaultArrayDisplay(this);
    }

    @Override
    public ListInt getSizes()
    {
        return new ArrayInt(value.size());
    }

    @Override
    public ListShort getData()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
