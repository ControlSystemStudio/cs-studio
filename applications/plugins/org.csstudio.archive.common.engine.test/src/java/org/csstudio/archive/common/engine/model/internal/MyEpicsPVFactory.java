/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model.internal;


import org.csstudio.platform.libs.epics.EpicsPlugin.MonitorMask;
import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;

/** PV Factory for EPICS V3 PVs.
 * 
 *  Copied to set the the monitor mask per channel.
 *  
 *  @author Kay Kasemir
 */
public class MyEpicsPVFactory implements IPVFactory
{
    /** PV type prefix */
    public static final String PREFIX = "epics"; //$NON-NLS-1$

    /** {@inheritDoc} */
    @Override
    public PV createPV(final String name)
    {
        // IOC doesn't seem to provide meta info for the .RTYP channels
        if (name.endsWith(".RTYP")) //$NON-NLS-1$
            return new EPICS_V3_PV(name, true);
        return new EPICS_V3_PV(name);
    }
    
    public PV createPV(final String name,
                       final String mask) {
        return new MyEpicsV3PV(name, MonitorMask.valueOf(mask));
    }
}
