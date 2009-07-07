package org.csstudio.utility.pv.simu;

/** Dynamic value that produces noise
 *  @author Kay Kasemir
 */
public class NoiseValue extends DynamicValue
{
    /** Initialize
     *  @param name
     */
    public NoiseValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        setValue(min + (Math.random() * (max - min)));
    }
}
