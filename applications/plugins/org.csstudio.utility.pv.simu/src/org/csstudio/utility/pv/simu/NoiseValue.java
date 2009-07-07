package org.csstudio.utility.pv.simu;


public class NoiseValue extends DynamicValue
{
    /**
     * @param name
     */
    public NoiseValue(final String name)
    {
        super(name);
    }

    @Override
    protected void update()
    {
        setValue(min + (Math.random() * (max - min)));
    }
}
