/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.util.List;

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
@SuppressWarnings("nls")
public class GlobalAlarm extends AlarmTreePV
{
    /** Time of alarm */
    private ITimestamp timestamp;

    /** Create global alarm with path and alarm info,
     *  but without GUI detail (guidance,..) nor valid RDB ID.
     *  If possible, uses alarm tree elements from existing configurations.
     *  For example, uses an existing configuration root.
     *  If not found, new configuration elements are created to lead
     *  up to the new global alarm.
     *
     *  @param configurations Existing configurations
     *  @param full_path
     *  @param severity
     *  @param message
     *  @param timestamp
     *  @return GlobalAlarm
     *  @throws Error when path is not complete
     */
    public static GlobalAlarm fromPath(final List<AlarmTreeRoot> configurations, final String full_path,
            final SeverityLevel severity, final String message,
            final ITimestamp timestamp)
    {
        final String path[] = AlarmTreePath.splitPath(full_path);
        if (path.length <= 1)
            throw new Error("Incomplete path " + full_path);

        AlarmTreeItem parent = findOrCreateRoot(configurations, path[0]);
        for (int i=1; i<path.length-1; ++i)
            parent = findOrCreateItem(parent, path[i]);
        return findOrCreateAlarm(parent, path[path.length-1], severity, message, timestamp);
    }

    /** Locate alarm in known configuration.
     *  If not found, create a new alarm.
     *  @param parent Parent item within known configuration
     *  @param name Name of desired element
     *  @param severity
     *  @param message
     *  @param timestamp
     *  @return Global alarm
     */
    private static GlobalAlarm findOrCreateAlarm(final AlarmTreeItem parent,
            final String name, final SeverityLevel severity, final String message,
            final ITimestamp timestamp)
    {
        // Check for existing item
        for (int i=0; i<parent.getChildCount(); ++i)
        {
            final AlarmTreeItem item = parent.getClientChild(i);
            if ((item instanceof GlobalAlarm) && item.getName().equals(name))
            {
                // Update existing alarm
                final GlobalAlarm alarm = (GlobalAlarm) item;
                alarm.setAlarmState(severity, severity, message);
                alarm.setAlarmTime(timestamp);
                return alarm;
            }
        }
        // Create new item
        return new GlobalAlarm(parent, name, -1, severity, message, timestamp);
    }

    /** Locate item in known configuration.
     *  If not found, create a new item and add to known configuration
     *  @param parent Parent item within known configuration
     *  @param name Name of desired element
     *  @return Item
     */
    private static AlarmTreeItem findOrCreateItem(final AlarmTreeItem parent,
            final String name)
    {
        // Check for existing item
        for (int i=0; i<parent.getChildCount(); ++i)
        {
            final AlarmTreeItem item = parent.getClientChild(i);
            if (item.getName().equals(name))
                return item;
        }
        // Create new item
        return new AlarmTreeItem(parent, name, -1);
    }

    /** Locate root in known configurations.
     *  If not found, create a new root and add to known configurations
     *  @param configurations Currently known configurations
     *  @param name Name of root
     *  @return Root
     */
    private static AlarmTreeRoot findOrCreateRoot(
            final List<AlarmTreeRoot> configurations, final String name)
    {
        // Check for existing root
        for (AlarmTreeRoot root : configurations)
            if (root.getName().equals(name))
                return root;
        // Create new root
        final AlarmTreeRoot root = new AlarmTreeRoot(name, -1);
        configurations.add(root);
        return root;
    }

    // Similar to the AlarmTreePV, but doesn't track 'current' state,
    // only 'alarm' state
    private GlobalAlarm(final AlarmTreeItem parent, final String name, final int id,
            final SeverityLevel severity, final String message, final ITimestamp timestamp)
    {
        super(parent, name, id);

        setAlarmState(severity, severity, message);
        this.timestamp = timestamp;
        if (parent != null)
            parent.maximizeSeverity(null);
    }

    private void setAlarmTime(final ITimestamp timestamp)
    {
        this.timestamp = timestamp;
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
