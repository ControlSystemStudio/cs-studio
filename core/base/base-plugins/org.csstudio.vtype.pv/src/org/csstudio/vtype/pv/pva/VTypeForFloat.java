/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import org.epics.pvdata.pv.PVFloat;
import org.epics.pvdata.pv.PVStructure;
import org.diirt.vtype.VFloat;
import org.diirt.vtype.VType;
import org.diirt.vtype.VTypeToString;

/** Hold/decode data of {@link PVStructure} in {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class VTypeForFloat extends VTypeTimeAlarmDisplayBase implements VFloat
{
    final private float value;

    public VTypeForFloat(final PVStructure struct)
    {
        super(struct);
        value = struct.getSubField(PVFloat.class, "value").get();
    }

    @Override
    public Float getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
