package org.csstudio.utility.pv.simu;

/** Dynamic value that produces ramp (sawtooth)
 *  @author Kay Kasemir, Xihui Chen
 */
public class RampValue extends DynamicValue
{
    private double value;
    private int sign;

    /** Initialize
     *  @param name
     */
    public RampValue(final String name)
    {
        super(name);
        sign = max == min ? 0 : (max>min ? 1 : -1);
        step = Math.abs(step);
        if(step > Math.abs(max-min))
        	step = Math.abs((max-min)/10);
        value = min - step*sign;
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {    	
    	value = value + step*sign;        
        if (max > min ? (value > max) : (value < max))
            value = min;     
        setValue(value);
    }
}
