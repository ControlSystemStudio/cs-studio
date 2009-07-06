package org.csstudio.utility.pv.simu;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for simulated PVs
 *  @author Kay Kasemir
 */
public class SimulatedPVFactory implements IPVFactory
{
    /** {@inheritDoc} */
    public PV createPV(final String name)
    {
        if (name.startsWith(SinePV.NAME))
            return new SinePV(name);
        return new NoisePV(name);
    }
}
