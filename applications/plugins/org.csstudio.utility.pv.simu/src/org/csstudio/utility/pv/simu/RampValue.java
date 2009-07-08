package org.csstudio.utility.pv.simu;

/** Dynamic value that produces ramp (sawtooth)
 *  @author Kay Kasemir
 */
public class RampValue extends DynamicValue
{
    /** A full sawtooth is created in these many updates */
    final private static int steps = 20;

    private int step = 0;

    /** Initialize
     *  @param name
     */
    public RampValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        setValue(min + (step * (max - min) / steps));
        ++step;
        if (step > steps)
            step = 0;
    }
}
