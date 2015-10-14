/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.GR;

import org.diirt.vtype.VInt;
import org.diirt.vtype.VTypeToString;

/** Wrap DBR as VType
 *
 *  <p>Based on ideas from org.epics.pvmanager.jca, Gabriele Carcassi
 *  @author Kay Kasemir
 */
public class VTypeForInt extends DBRAlarmTimeDisplayWrapper<DBR_TIME_Int> implements VInt
{
    public VTypeForInt(final GR metadata, final DBR_TIME_Int dbr)
    {
        super(metadata, dbr);
    }

    @Override
    public Integer getValue()
    {
        return dbr.getIntValue()[0];
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
