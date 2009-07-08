package org.csstudio.utility.pv.simu;

/** Dynamic value that produces sine wave
 *  @author Kay Kasemir
 */
public class SineValue extends DynamicValue
{
    private double x = 0;
    private int DEFAULT_COUNT =10;

    /** Initialize
     *  @param name
     */
    public SineValue(final String name)
    {
        super(name);
        if(step <= 1)
        	step = DEFAULT_COUNT;
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        setValue(min + ((Math.sin(x)+1.0)/2.0 * (max - min)));
        x += 2.0*Math.PI / step;
    }
}
