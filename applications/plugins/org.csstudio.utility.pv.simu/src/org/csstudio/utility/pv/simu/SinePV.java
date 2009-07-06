package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.TimestampFactory;

/** Simulated PV that generates a sine wave.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SinePV extends SimulatedPV
{
    /** A full sinewave is created in these many updates */
    final private static int steps = 20;

    /** (Start of) name for this type of PV */
    public static final String NAME = "sine";
    
    private double x = 0;
    
    /** Initialize
     *  @param name PV name
     */
    public SinePV(final String name)
    {
        super(name);
    }
    
    @Override
    protected void update()
    {
        setValue(TimestampFactory.now(), min + ((Math.sin(x)+1.0)/2.0 * (max - min)));
        x += 2.0*Math.PI / steps;
    }
}
