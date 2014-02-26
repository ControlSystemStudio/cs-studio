/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_String;

import org.epics.vtype.VString;
import org.epics.vtype.VTypeToString;

/** Wrap DBR as VType
*
*  <p>Based on ideas from org.epics.pvmanager.jca, Gabriele Carcassi
*  @author Kay Kasemir
*/
public class VTypeForString extends DBRAlarmTimeWrapper<DBR_TIME_String> implements VString
{
    final private DBR_String string;
    
    public VTypeForString(final DBR_String dbr)
    {
        super((dbr instanceof DBR_TIME_String) ? (DBR_TIME_String) dbr : null);
        string = dbr;
    }

    @Override
    public String getValue()
    {
        return string.getStringValue()[0];
    }

    @Override
    public String toString()
    {
        return VTypeToString.toString(this);
    }
}
