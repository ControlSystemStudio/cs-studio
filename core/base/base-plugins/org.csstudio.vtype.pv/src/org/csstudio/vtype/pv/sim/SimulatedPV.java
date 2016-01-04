/*******************************************************************************
 * Copyright (c) 2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.sim;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** Base for simulated scalar PVs
 *
 *  <p>Value is of type VDouble.
 *  Display settings use min/max,
 *  warnings generated at 20%/80% of range,
 *  alarms at 10%/90% of range.
 *
 *  @author Kay Kasemir, based on similar code in org.csstudio.utility.pv and diirt
 */
abstract public class SimulatedPV extends PV
{
    /** Timer for periodic updates */
    private final static ScheduledExecutorService executor =
        Executors.newScheduledThreadPool(1, (Runnable target) -> new Thread(target, "SimPV"));

    /** Format for Display */
    private final static NumberFormat format = new DecimalFormat();

    /** Task that was submitted for periodic updates */
    private ScheduledFuture<?> task;

    /** Display for value updates, also defines warning/alarm range */
    private Display display;

    /** @param name Full PV name */
    public SimulatedPV(final String name)
    {
        super(name);

        // Simulated PVs are read-only
        notifyListenersOfPermissions(true);
    }

    /** Init. 'display' and start periodic updates
     *  @param min Display ..
     *  @param max .. range
     *  @param period Update period in seconds
     */
    protected void start(final double min, final double max, final double period)
    {
        final double range = max - min;
        display = ValueFactory.newDisplay(min, min + range * 0.1, min + range * 0.2, "a.u.", format,
                                          min + range * 0.8, min + range * 0.9, max, min, max);
        final long milli = Math.round(Math.max(period, 0.1) * 1000);
        task = executor.scheduleAtFixedRate(this::update, milli, milli, TimeUnit.MILLISECONDS);
    }

    /** Prohibit write access */
    @Override
    public void write(final Object new_value) throws Exception
    {
        throw new Exception("Cannot write data of type" + new_value.getClass().getName());
    }

    /** Called by periodic timer */
    private void update()
    {
        final double value = compute();
        // Creates vtype with alarm according to display warning/alarm ranges
        final VType vtype = ValueFactory.newVDouble(value, display);
        notifyListenersOfValue(vtype);
    }

    /** Invoked for periodic update.
     *  @return Current value of the simulated PV
     */
    abstract public double compute();

    @Override
    protected void close()
    {
        task.cancel(false);
        try
        {
            task.get(1, TimeUnit.SECONDS);
        }
        catch (Exception ex)
        {
            // Shutting down anyway ..
            Logger.getLogger(getClass().getName())
                  .log(Level.WARNING, "Error stopping updates for " + getName(), ex);
        }
        super.close();
    }
}
