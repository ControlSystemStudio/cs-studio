/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import java.util.List;

import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VEnum;
import org.epics.vtype.VType;
import org.epics.vtype.VTypeToString;

/** Hold/decode data of {@link PVStructure} in {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class VTypeForEnum extends VTypeTimeAlarmBase implements VEnum
{
    final private int value;
    final private List<String> labels;

    public VTypeForEnum(final PVStructure struct) throws Exception
    {
        super(struct);
        final PVStructure section = struct.getSubField(PVStructure.class, "value");
        value = section.getSubField(PVInt.class, "index").get();
        labels = PVStructureHelper.getStrings(section, "choices");
    }

    @Override
    public List<String> getLabels()
    {
        return labels;
    }

    @Override
    public String getValue()
    {
        if (value >=0  &&  value < labels.size())
            return labels.get(value);
        return "Enum " + value;
    }

    @Override
    public int getIndex()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
