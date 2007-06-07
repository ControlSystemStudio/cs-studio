package org.csstudio.utility.pv.epics;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for EPICS V3 PVs.
 *  @author Kay Kasemir
 */
public class EPICSPVFactory implements IPVFactory
{
    /** {@inheritDoc} */
    public PV createPV(String name)
    {
        // IOC doesn't seem to provide meta info for the .RTYP channels
        if (name.endsWith(".RTYP")) //$NON-NLS-1$
            return new EPICS_V3_PV(name, true);
        return new EPICS_V3_PV(name);
    }
}
