/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.GR;

import org.epics.vtype.VDouble;
import org.epics.vtype.VTypeToString;

/** Wrap DBR as VType
 *
 *  <p>Based on ideas from org.epics.pvmanager.jca, Gabriele Carcassi
 *  @author Kay Kasemir
 */
public class VTypeForDouble extends DBRAlarmTimeDisplayWrapper<DBR_TIME_Double> implements VDouble 
{
    public VTypeForDouble(final GR metadata, final DBR_TIME_Double dbr)
    {
        super(metadata, dbr);
    }

    @Override
    public Double getValue()
    {
        return dbr.getDoubleValue()[0];
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
