/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import java.util.logging.Level;

import org.csstudio.display.pace.Activator;

/** GUI Update throttle
 *  <p>
 *  Assume that the GUI sometimes receives an update that should be
 *  processed right away.
 *  At other times it receives a burst of updates, where it would
 *  be best to wait a little and then redraw to show "everything"
 *  instead of reacting to each single update right away
 *  which only results in flicker and may even be much slower overall.
 *  <p>
 *  This class delays the first update a little bit,
 *  so in case it's a burst, those updates accumulate.
 *  Then it updates, and suppresses further updates for a while
 *  to limit flicker.
 *  Finally, it starts over.
 *  <p>
 *  To use this throttle, implement the <code>update</code> routine
 *  to handle the throttled triggers.
 *
 *  @param <T> Type of event object passed into <code>trigger</code>
 *  @author Kay Kasemir
 */
abstract public class GUIUpdateThrottle<T> implements Runnable
{
    /** Delay in millisecs for the initial update after trigger */
    final private long initial_millis;

    /** Delay in millisecs for the suppression of a burst of events */
    final private long suppression_millis;

    /** Timer for scheduling the update test */
    final private Thread throttle;

    /** Item used in the last trigger */
    private T item = null;

    /** Counter for trigger events that arrived */
    private int triggers = 0;

    /** Initialize
     *  @param initial_millis Delay [ms] for the initial update after trigger
     *  @param suppression_millis Delay [ms] for the suppression of a burst of events
     */
    public GUIUpdateThrottle(final long initial_millis,
                             final long suppression_millis)
    {
        this.initial_millis = initial_millis;
        this.suppression_millis = suppression_millis;
        throttle = new Thread(this, "GUIUpdateThrottle"); //$NON-NLS-1$
        throttle.setDaemon(true);
        throttle.start();
    }

    /** Register an event trigger. Will result in throttled call to
     *  <code>update</code>
     *  @param item Item that caused this trigger. For individual triggers,
     *              this will appear in the <code>update</code> call.
     *              For bursts, the individual cause of each burst trigger
     *              will be lost.
     */
    public void trigger(final T item)
    {
        synchronized (this)
        {   // Count suppressed events
            ++triggers;
            this.item = item;
            notifyAll();
        }
    }

    /** Thread Runnable that handles received triggers */
    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                // Wait for a trigger
                synchronized (this)
                {
                    while (triggers <= 0)
                        wait();
                }
                // Wait a little longer, so in case of a burst, we update
                // after already receiving more than just the start of the
                // burst
                Thread.sleep(initial_millis);
                final T update_item;
                synchronized (this)
                {
//                    System.out.println("Firing on " + triggers + " events");
                    if (triggers == 1)
                        update_item = item;
                    else
                        update_item = null;
                    triggers = 0;
                }
                update(update_item);
                // Suppress further updates a little to prevent flicker
                Thread.sleep(suppression_millis);
            }
        }
        catch (InterruptedException ex)
        {
            Activator.getLogger().log(Level.WARNING, "GUIUpdateThrottle error", ex); //$NON-NLS-1$
        }
    }

    /** To be implemented by derived class:
     *  Throttled event notification
     *  @param item Item to update for single-item update,
     *              or <code>null</code> if this is the result
     *              of a burst update
     */
    abstract protected void update(T item);
}
