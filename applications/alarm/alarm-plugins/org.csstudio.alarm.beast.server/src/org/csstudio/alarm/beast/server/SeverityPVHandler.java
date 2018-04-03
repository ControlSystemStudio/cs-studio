/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.csstudio.alarm.beast.server.Activator.logger;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVPool;

/** Handle the update of "severity PVs"
 *
 *  <p>Maintains the PV connections,
 *  updates the PVs in a background thread.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityPVHandler
{
    // TODO Add an optional 'heartbeat' PV

    private static final int CONNECTION_SECS = (int) Preferences.getConnectionGracePeriod();


    /** Pending updates */
    private static final ConcurrentHashMap<String, SeverityLevel> updates = new ConcurrentHashMap<>();


    /** Map of PVs by name */
    private static final ConcurrentHashMap<String, PV> pvs = new ConcurrentHashMap<>();


    /** Initialize, start SeverityPVUpdater thread */
    public static void initialize()
    {
        final Thread thread = new Thread(SeverityPVHandler::run, "SeverityPVUpdater");
        thread.setDaemon(true);
        thread.start();
    }

    /** Executed by SeverityPVUpdater:
     *  Waits for requested updates and performs them.
     */
    private static void run()
    {
        while (true)
        {
            try
            {
                if (updates.isEmpty())
                    Thread.sleep(500);
                else
                    performUpdates();
            }
            catch (Exception ex)
            {
                logger.log(Level.WARNING, "SeverityPVUpdater error", ex);
            }
        }
    }

    /** Perform all requested updates, clearing the 'updates' map */
    private static void performUpdates()
    {
        final Iterator<Entry<String, SeverityLevel>> entries = updates.entrySet().iterator();
        while (entries.hasNext())
        {
            final Entry<String, SeverityLevel> entry = entries.next();
            final String pv_name = entry.getKey();
            final SeverityLevel severity = entry.getValue();
            entries.remove();

            logger.log(Level.FINE, "Should update PV '" + pv_name + "' to " + severity.name());

            // Get or create PV
            final PV pv = pvs.computeIfAbsent(pv_name, name ->
            {
                try
                {
                    return PVPool.getPV(name);
                }
                catch (Exception ex)
                {
                    logger.log(Level.WARNING, "Cannot create severity PV '" + name + "'", ex);
                    return null;
                }
            });
            if (pv == null)
                continue;

            try
            {
                // Assert connection
                int timeout = CONNECTION_SECS;
                while (pv.read() == null)
                {
                    TimeUnit.SECONDS.sleep(1);
                    if (--timeout < 0)
                        throw new Exception("No connection");
                }
                // Write severity
                pv.write(severity.ordinal());
            }
            catch (Exception ex)
            {
                logger.log(Level.WARNING, "Cannot set severity PV '" + pv.getName() + "' to " + severity.ordinal(), ex);
            }
        }
    }

    /** Request an update
     *  @param severity_pv_name Name of severity PV
     *  @param severity Severity to write
     */
    public static void update(final String severity_pv_name, final SeverityLevel severity)
    {
        // Write to map, handle in SeverityPVUpdater thread
        // If SeverityPVUpdater is slow, and several updates arrive for the same PV,
        // this will place the most recent severity for that PV in the map.
        updates.put(severity_pv_name, severity);
    }

    /** Release all PVs */
    public static void stop()
    {
        final Iterator<PV> pv_iter = pvs.values().iterator();
        while (pv_iter.hasNext())
        {
            final PV pv = pv_iter.next();
            PVPool.releasePV(pv);
            pv_iter.remove();
        }
    }
}
