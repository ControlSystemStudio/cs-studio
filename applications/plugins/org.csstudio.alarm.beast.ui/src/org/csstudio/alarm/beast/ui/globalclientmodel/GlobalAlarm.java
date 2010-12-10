/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.AlarmConfigurationReader;
import org.csstudio.alarm.beast.AlarmTreeItem;
import org.csstudio.alarm.beast.AlarmTreePV;
import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.AlarmTreeRoot;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** A 'global' alarm
 *
 *  @author Kay Kasemir
 */
public class GlobalAlarm extends AlarmTreePV
{
    /** Create global alarm with path and alarm info,
     *  but without GUI detail (guidance,..) nor valid RDB ID
     *  @param full_path
     *  @param severity
     *  @param message
     *  @param timestamp
     *  @return GlobalAlarm
     */
    public static GlobalAlarm fromPath(final String full_path,
            final SeverityLevel severity, final String message,
            final ITimestamp timestamp)
    {
        final String path[] = AlarmTreePath.splitPath(full_path);

        AlarmTreeItem parent = null;
        for (int i=0; i<path.length-1; ++i)
        {
            if (i == 0)
                parent = new AlarmTreeRoot(path[i], -1);
            else
                parent = new AlarmTreeItem(parent, path[i], -1);
        }
        return new GlobalAlarm(parent, path[path.length-1], -1,
                severity, message, timestamp);
    }


    // Similar to the AlarmTreePV, but doesn't track 'current' state,
    // only 'alarm' state
    private GlobalAlarm(final AlarmTreeItem parent, final String name, final int id,
            final SeverityLevel severity, final String message, final ITimestamp timestamp)
    {
        super(parent, name, id);

        setAlarmState(severity, severity, message);
        if (parent != null)
            parent.maximizeSeverity(null);
    }

    // Complete the guidance etc. from RDB
    public void completeGuiInfo(final RDBUtil rdb, final SQL sql) throws Exception
    {
        final AlarmConfigurationReader reader = new AlarmConfigurationReader(rdb, sql);
        // Item names are not necessarily unique,
        // so name & ID-of-parent are required for lookup.
        // To get all parent IDs, start at the root
        completeGuiInfo(reader, getClientRoot());
        reader.closeStatements();
    }

    // Complete GUI info for given item, recursing to child entries
    private void completeGuiInfo(final AlarmConfigurationReader reader, final AlarmTreeItem item) throws Exception
    {
        readGuiInfo(reader, item);
        // Create thread-safe copy of child list while performing the slow RDB lookup
        final AlarmTreeItem children[];
        synchronized (item)
        {
            children = new AlarmTreeItem[item.getChildCount()];
            for (int i=0; i<children.length; ++i)
                children[i] = item.getClientChild(i);
        }
        for (AlarmTreeItem child : children)
            completeGuiInfo(reader, child);
    }

    // Complete guidance etc. of item
    private void readGuiInfo(final AlarmConfigurationReader reader, final AlarmTreeItem item) throws Exception
    {
        // Assume info is known
        if (item.getID() >= 0)
            return;
        reader.completeItemInfo(item);
    }
}
