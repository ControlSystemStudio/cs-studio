/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

/** Dynamic value that holds the number of sim/sys/loc PVs
 *  @author Kay Kasemir
 */
public class PVCountValue extends DynamicValue
{
    /** Initialize
     *  @param name
     */
    public PVCountValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        final double count = LocalPVFactory.getValueCount() +
                             SimulatedPVFactory.getValueCount() +
                             SystemPVFactory.getValueCount();
        setValue((double )count);
    }
}
