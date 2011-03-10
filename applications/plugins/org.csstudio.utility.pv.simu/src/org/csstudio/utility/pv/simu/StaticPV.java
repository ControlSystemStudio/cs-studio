/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

/** Static PV.
 *  <p>
 *  PV that displays the underlying Value, but never changes.
 *
 *  @author Kay Kasemir
 */
public class StaticPV extends BasicPV<Value>
{
    /** Initialize
     *  @param prefix PV type prefix
     *  @param value PV name
     */
    public StaticPV(final String prefix, final Value value)
    {
        super(prefix, value);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void start() throws Exception
    {
        running = true;
        // Send initial update
        changed(value);
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        running = false;
    }
}
