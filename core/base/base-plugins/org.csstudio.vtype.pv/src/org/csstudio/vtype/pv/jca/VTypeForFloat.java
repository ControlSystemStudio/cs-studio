/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import gov.aps.jca.dbr.DBR_TIME_Float;
import gov.aps.jca.dbr.GR;

import org.epics.vtype.VFloat;
import org.epics.vtype.VTypeToString;

/** Wrap DBR as VType
 *
 *  <p>Based on ideas from org.epics.pvmanager.jca, Gabriele Carcassi
 *  @author Kay Kasemir
 */
public class VTypeForFloat extends DBRAlarmTimeDisplayWrapper<DBR_TIME_Float> implements VFloat
{
    public VTypeForFloat(final GR metadata, final DBR_TIME_Float dbr)
    {
        super(metadata, dbr);
    }

    @Override
    public Float getValue()
    {
        return dbr.getFloatValue()[0];
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
