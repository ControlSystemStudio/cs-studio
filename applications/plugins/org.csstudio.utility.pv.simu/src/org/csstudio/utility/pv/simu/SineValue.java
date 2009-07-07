package org.csstudio.utility.pv.simu;

/** Dynamic value that produces sine wave
 *  @author Kay Kasemir
 */
public class SineValue extends DynamicValue
{
    /** A full sinewave is created in these many updates */
    final private static int steps = 20;

    private double x = 0;

    /** Initialize
     *  @param name
     */
    public SineValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        setValue(min + ((Math.sin(x)+1.0)/2.0 * (max - min)));
        x += 2.0*Math.PI / steps;
    }
}
