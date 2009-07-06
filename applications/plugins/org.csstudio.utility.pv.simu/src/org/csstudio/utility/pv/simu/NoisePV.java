package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.TimestampFactory;

/** Simulated PV that generates noise.
 *  @author Kay Kasemir
 */
public class NoisePV extends SimulatedPV
{
    /** Initialize
     *  @param name PV name
     */
    public NoisePV(final String name)
    {
        super(name);
    }
    
    @Override
    protected void update()
    {
        setValue(TimestampFactory.now(), min + (Math.random() * (max - min)));
    }
}
