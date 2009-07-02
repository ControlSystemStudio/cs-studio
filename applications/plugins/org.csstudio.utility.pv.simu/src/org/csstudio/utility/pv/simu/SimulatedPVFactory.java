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
        /* TODO
 Allow PV names like
  sine(0, 5, 0.1)
- Sine wave valued 0...5, updating every 0.1 seconds

Allow loading of both simu, epics, ...
and use PV name prefix
  simu://  resp.  epics:// to pick one.
I think a preference setting will select
the default system when no prefix is given.

Then add a 'local' PV.
         */
        return new SimulatedPV(name);
    }
}
