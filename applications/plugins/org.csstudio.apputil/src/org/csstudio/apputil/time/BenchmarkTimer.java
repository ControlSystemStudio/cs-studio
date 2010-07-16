/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

/** Simple benchmark timer.
 *  <p>
 *  Typical use is
 *  <ol>
 *  <li>Create (which also starts)
 *  <li>stop(), use get... to get time
 *  </ol>
 *  It's also possible to stop multiple times to get "lap" readings,
 *  or to 'continue' a timer from the last 'stop'.
 *  @author Kay Kasemir
 */
public class BenchmarkTimer
{
    /** System nanosecs when started */
    private long start;
    
    /** System nanosecs of last snapshot */
    private long elapsed;
    
    /** Construct and start the timer */
    public BenchmarkTimer()
    {
        start();
    }
    
    /** Start/Reset the timer */
    public void start()
    {
        start = System.nanoTime();
    }

    /** Stop the timer, get a snapshot since start.
     *  @return Elapsed time in nanoseconds
     */
    public long stop()
    {
        elapsed = System.nanoTime() - start;
        return elapsed;
    }
    
    /** Continue timer that was stop()ed from when it was stopped */
    public void cont()
    {
        start = System.nanoTime() - elapsed;
    }

    /** @return Milliseconds between <code>start()</code> and <code>stop()</code> */
    public long getMilliseconds()
    {
        return elapsed / 1000000L;
    }

    /** @return Seconds between <code>start()</code> and <code>stop()</code> */
    public double getSeconds()
    {
        return elapsed / 1.0e9;
    }

    @Override
    public String toString()
    {
        final RelativeTime passed = new RelativeTime(getSeconds());
        return passed.toString();
    }
}
