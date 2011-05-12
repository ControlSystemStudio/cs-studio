/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

/** Dynamic value that produces ramp (sawtooth)
 *  @author Kay Kasemir, Xihui Chen
 */
public class RampValue extends DynamicValue
{
    final private boolean down;
    private double value;

    /** Initialize
     *  @param name
     */
    public RampValue(final String name)
    {
        super(name);
        down = step < 0;
        step = Math.abs(step);
        if (step <= 0)
            step = 1;
        if(step > Math.abs(max-min))
        	step = Math.abs((max-min)/10);
        value = down ? max : min;
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {    	
        if (down)
        {
            value = value - step;
            if (value < min)
                value = max;
        }
        else
        {
            value = value + step;
            if (value > max)
                value = min;
        }
           setValue(value);
    }
}
