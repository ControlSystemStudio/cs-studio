/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.util.logging.Level;

import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Job that reads a global alarm's GUI info as a background task.
 *  @author Kay Kasemir
 */
public class ReadInfoJob extends Job
{
    final private String rdb_url, rdb_user, rdb_password, rdb_schema;
    final private GlobalAlarm alarm;
    final private ReadInfoJobListener listener;

    /** Initialize job. Caller still has to <code>schedule()</code>!
     *  @param rdb_url Alarm RDB URL
     *  @param rdb_user .. user
     *  @param rdb_password .. password
     *  @param rdb_schema .. schema
     *  @param alarm Global alarm to update with GUI info from RDB
     *  @param listener Listener to be notified, or <code>null</code>
     */
    public ReadInfoJob(final String rdb_url, final String rdb_user, final String rdb_password,
    		final String rdb_schema,
            final GlobalAlarm alarm, final ReadInfoJobListener listener)
    {
        super(Messages.ReadConfigJobName);
        // Can take more than a second, so lower priority
        setPriority(LONG);
        this.rdb_url = rdb_url;
        this.rdb_user = rdb_user;
        this.rdb_password = rdb_password;
        this.rdb_schema = rdb_schema;
        this.alarm = alarm;
        this.listener = listener;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.AlarmClientModel_ReadingConfiguration, IProgressMonitor.UNKNOWN);
        RDBUtil rdb = null;
        try
        {
            rdb = RDBUtil.connect(rdb_url, rdb_user, rdb_password, false);
            monitor.subTask(Messages.AlarmClientModel_ReadingRDB);
            final SQL sql = new SQL(rdb, rdb_schema);
            alarm.completeGuiInfo(rdb, sql);
            rdb.getConnection().commit();
        }
        catch (Exception ex)
        {   // Log
            Activator.getLogger().log(Level.WARNING,
                    "Cannot read global alarm detail for " + alarm.getPathName(), ex);
            // End w/o informing listener
            monitor.done();
            return Status.OK_STATUS;
        }
        finally
        {
            if (rdb != null)
                rdb.close();
        }
        monitor.done();
        if (listener != null)
            listener.receivedAlarmInfo(alarm);
        return Status.OK_STATUS;
    }
}
