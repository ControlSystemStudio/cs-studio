/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;


/** Simulated PV.
 *  <p>
 *  Provides updates based on changes of the underlying DynamicValue.
 *
 *  @author Kay Kasemir
 */
public class SimulatedPV extends BasicPV<DynamicValue>
{
    /** Initialize
     *  @param prefix PV type prefix
     *  @param name PV name
     */
    public SimulatedPV(final String prefix, final DynamicValue value)
    {
        super(prefix, value);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void start() throws Exception
    {
        running = true;
        value.addListener(this);
        value.start();
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        value.removeListener(this);
        value.stop();
        running = false;
    }
}
