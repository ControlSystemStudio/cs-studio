/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

/** Thread that periodically triggers the PVListModel's value notifications.
 *
 *  @author Kay Kasemir
 */
public class PVListModelValueUpdateThread implements Runnable
{
    private PVListModel pv_list;
    private Thread thread;
    private boolean go;
    private int delay;

    public PVListModelValueUpdateThread(PVListModel pv_list)
    {
        this.pv_list = pv_list;
        thread = new Thread(this);
        go = true;
        delay = 100;
        thread.start();
    }

    /** Must be called to stop the thread. */
    public void dispose()
    {
        go = false;
        // In here, I tried to wait for the thread to actually quit:
        // 1) Interrupt, in case the thread is in sleep()
        // thread.interrupt();
        // 2) then wait
        // thread.join();
        //
        // Bad mistake, hangup on shutdown of eclipse:
        // This dispose call, called in the UI thread, would wait
        // for the value update thread to stop.
        // That value update thread could meanwhile be in
        // fireNewEntryValues() -> syncExcec -> update table
        // .. and the syncExec waits for the UI thread to finish
        // the table update
        // ==> deadlock!
    }

    /** @return Returns the periodic check delay in millisecs. */
    public int getDelay()
    {
        return delay;
    }

    /** @param delay The new periodic check delay in millisecs. */
    public void setDelay(int delay)
    {
        if (delay > 1)
            this.delay = delay;
    }

    /**  @see java.lang.Runnable#run() */
    @Override
    public void run()
    {
        while (go)
        {
            pv_list.updateAnyChangedEntries();
            try
            {
                Thread.sleep(delay);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
