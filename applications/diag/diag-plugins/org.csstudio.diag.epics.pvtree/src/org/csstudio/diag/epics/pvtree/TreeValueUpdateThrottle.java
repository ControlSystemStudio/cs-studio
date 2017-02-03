/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.csstudio.diag.epics.pvtree.Plugin.logger;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

/** Throttle for tree viewer 'update' calls
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TreeValueUpdateThrottle
{
    private final static long update_period_ms = Math.round(Preferences.getUpdatePeriod() * 1000);
    private final TreeViewer viewer;
    private final Set<PVTreeItem> updateable = new LinkedHashSet<>();
    private final Thread throttle_thread;
    private volatile boolean run = true;

    public TreeValueUpdateThrottle(final TreeViewer viewer)
    {
        this.viewer = viewer;
        throttle_thread = new Thread(this::doRun);
        throttle_thread.setName("PVTreeUpdates");
        throttle_thread.setDaemon(true);
        throttle_thread.start();
    }

    /** Request a tree viewer 'update'
     *  @param item Item to update
     */
    public void scheduleUpdate(final PVTreeItem item)
    {
        synchronized (updateable)
        {
            updateable.add(item);
            updateable.notifyAll();
        }
    }

    public void clearPendingUpdates()
    {
        synchronized (updateable)
        {
            updateable.clear();
        }
    }

    private void doRun()
    {
        final Tree tree = viewer.getTree();
        try
        {
            while (run)
            {
                final PVTreeItem[] items;
                synchronized (updateable)
                {
                    while (run  &&  updateable.isEmpty())
                        updateable.wait();
                    items = updateable.toArray(new PVTreeItem[updateable.size()]);
                    updateable.clear();
                }
                if (! run  ||  tree.isDisposed())
                    return;

                tree.getDisplay().asyncExec(() ->
                {
                    if (tree.isDisposed())
                        return;
                    for (PVTreeItem item : items)
                        viewer.update(item, null);
                });

                // Suppress updates
                Thread.sleep(update_period_ms);
            }
        }
        catch (Throwable ex)
        {
            logger.log(Level.WARNING, "PVTree update error", ex);
        }
    }

    public void shutdown()
    {
        run = false;
        synchronized (updateable)
        {
            updateable.notifyAll();
        }
        try
        {
            throttle_thread.join(2000);
        }
        catch (final InterruptedException ex)
        {
            // Ignore, closing down anyway
        }
        if (throttle_thread.isAlive())
            logger.log(Level.WARNING, "PVTree update throttle fails to terminate within 2 seconds");
    }
}
