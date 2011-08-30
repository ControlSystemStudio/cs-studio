/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.mpsbypasses.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.sns.mpsbypasses.Plugin;

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
 *  @author Kay Kasemir
 */
abstract public class GUIUpdateThrottle extends Thread
{
    /** Delay in millisecs for the initial update after trigger */
    final private long initial_millis;

    /** Delay in millisecs for the suppression of a burst of events */
    final private long suppression_millis;

    /** Counter for trigger events that arrived */
    private int triggers = 0;

    /** Flag that tells thread to run or exit */
    private volatile boolean run = true;
    
    /** Initialize
     *  @param initial_millis Delay [ms] for the initial update after trigger
     *  @param suppression_millis Delay [ms] for the suppression of a burst of events
     */
    public GUIUpdateThrottle(final long initial_millis,
                             final long suppression_millis)
    {
        super("GUIUpdateThrottle"); //$NON-NLS-1$
        this.initial_millis = initial_millis;
        this.suppression_millis = suppression_millis;
        setDaemon(true);
    }

    /** Register an event trigger. Will result in throttled call to
     *  <code>fire</code>
     */
    public void trigger()
    {
        synchronized (this)
        {   // Count suppressed events
            ++triggers;
            notifyAll();
        }
    }

    /** Thread Runnable that handles received triggers */
    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        try
        {
            while (run)
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
                synchronized (this)
                {
//                    System.out.println("Firing on " + triggers + " events");
                    triggers = 0;
                }
                if (run)
                    fire();
                // Suppress further updates a little to prevent flicker
                Thread.sleep(suppression_millis);
            }
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Plugin.ID).log(Level.SEVERE, "GUI Update failed", ex);
        }
    }

    /** To be implemented by derived class:
     *  Throttled event notification
     */
    abstract protected void fire();

    /** Tell thread to quit, but don't wait for that to happen */
    public void dispose()
    {
        run = false;
        trigger();
    }
}
