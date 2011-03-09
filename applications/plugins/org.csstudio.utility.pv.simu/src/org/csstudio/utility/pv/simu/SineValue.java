/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
