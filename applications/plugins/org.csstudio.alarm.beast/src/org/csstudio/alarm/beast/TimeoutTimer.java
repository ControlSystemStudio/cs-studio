/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** Thread that checks for timeouts.
 *  <p>
 *  See http://www.javacoffeebreak.com/articles/network_timeouts by David Reilly
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class TimeoutTimer extends Thread
{
    /** Granularity or fraction of timeout at which timeouts are checked.
     *  Timeout must be greater than granularity.
     */
    final public static int GRANULARITY = 10;

    /** Run flag for thread
     *  @see #cancel()
     */
    private boolean run = true;

    /** Timeout period in milliseconds */
    final private long timeout;

    /** Elapsed time so far. */
    private long elapsed;

    private boolean timed_out = false;

    /** Initialize with default name
     *  @param timeout Timeout period in milliseconds.
     *  @see #GRANULARITY
     */
    public TimeoutTimer(final long timeout)
    {
        this("Timeout Timer", timeout);
    }

    /** Initialize
     *  @param name Name of the timer
     *  @param timeout Timeout period in milliseconds.
     *  @see #GRANULARITY
     */
    public TimeoutTimer(final String name, final long timeout)
    {
        super(name);
        this.timeout = timeout < GRANULARITY ? GRANULARITY : timeout;
        setDaemon(true);
        reset();
    }

    /** @return <code>true</code> while timed out */
    public synchronized boolean isTimedOut()
    {
        return timed_out;
    }

    /** Reset the timer.
     *  While the timer is running, this resets the wait period.
     *  Once timed out, it re-starts the check.
     */
    public synchronized void reset()
    {
        elapsed = 0;
        if (timed_out)
        {
            timed_out = false;
            notifyAll();
        }
    }

    /** Cancel the timer thread.
     *  Does not wait for thread to exit.
     *  @see #join()
     */
    public void cancel()
    {
        run = false;
        interrupt();
    }

    /** Thread's main routine.
     *  Periodically check for timeouts.
     *  Once timed out, wait for a reset.
     */
    @Override
    public void run()
    {
        final long delay = timeout / GRANULARITY;
        while (run)
        {
            // while run  &&  elapsed < timeout
            try
            {
                sleep(delay);
            }
            catch (InterruptedException ex)
            {
                continue;
            }
            synchronized (this)
            {   // Accumulate time
                elapsed += delay;
                if (elapsed < timeout)
                    continue;
                timed_out = true;
            }
            // elapsed >= timeout. Signal timeout without holding lock
            timeout();
            synchronized (this)
            {
                while (timed_out && run)
                {
                    try
                    {
                        wait();
                    }
                    catch (InterruptedException ex)
                    {
                        // continue;
                    }
                }
            }
        }
    }

    /** Derived class must implement this routine
     *  which gets invoked on timeouts.
     */
    abstract protected void timeout();
}
