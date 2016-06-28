/*******************************************************************************
 * Copyright (c) 2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.sim;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;

/** Base for simulated PVs
 *
 *  @author Kay Kasemir, based on similar code in org.csstudio.utility.pv and diirt
 */
@SuppressWarnings("nls")
abstract public class SimulatedPV extends PV
{
    /** Timer for periodic updates */
    private final static ScheduledExecutorService executor =
        Executors.newScheduledThreadPool(1, (Runnable target) -> new Thread(target, "SimPV"));

    /** Task that was submitted for periodic updates */
    private ScheduledFuture<?> task;

    /** @param name Full PV name */
    public SimulatedPV(final String name)
    {
        super(name);

        // Simulated PVs are read-only
        notifyListenersOfPermissions(true);
    }

    /** Start periodic updates
     *  @param update_seconds Update period in seconds
     */
    protected void start(final double update_seconds)
    {
        final long milli = Math.round(Math.max(update_seconds, 0.1) * 1000);
        task = executor.scheduleAtFixedRate(this::update, milli, milli, TimeUnit.MILLISECONDS);
    }

    /** Prohibit write access */
    @Override
    public void write(final Object new_value) throws Exception
    {
        throw new Exception("Cannot write data of type" + new_value.getClass().getName());
    }

    /** Called by periodic timer */
    abstract protected void update();

    @Override
    protected void close()
    {
        if (! task.cancel(false))
            logger.log(Level.WARNING, "Cannot cancel updates for " + getName());
        super.close();
    }
}
