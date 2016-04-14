/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.time.Instant;
import java.util.List;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmConfigurationReader;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.eclipse.osgi.util.NLS;

/** A 'global' alarm
 *  Similar to the AlarmTreePV, but doesn't track 'current' state, value,
 *  configuration detail.
 *  Instead, it handles async. retrieval of GUI info to allow display
 *  of a few 'global' alarms without having to read the complete
 *  alarm configuration.
 *
 *  @author Kay Kasemir
 */
public class GlobalAlarm extends AlarmTreeLeaf
{
    private static final long serialVersionUID = 3755987841035146999L;

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
    static GlobalAlarm fromPath(final List<AlarmTreeRoot> configurations, final String full_path,
            final SeverityLevel severity, final String message,
            final Instant timestamp)
    {
        final String path[] = AlarmTreePath.splitPath(full_path);
        if (path.length <= 1)
            throw new Error("Incomplete path " + full_path); //$NON-NLS-1$

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
            final Instant timestamp)
    {
        // Check for existing item
        for (int i=0; i<parent.getChildCount(); ++i)
        {
            final AlarmTreeItem item = parent.getChild(i);
            if ((item instanceof GlobalAlarm) && item.getName().equals(name))
            {
                // Update existing alarm
                final GlobalAlarm alarm = (GlobalAlarm) item;
                alarm.setAlarmState(severity, severity, message, timestamp);
                return alarm;
            }
        }
        // Create new item
        return new GlobalAlarm(parent, name, severity, message, timestamp);
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
            final AlarmTreeItem item = parent.getChild(i);
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

    /** Initialize
     *  @param parent Parent item within known configuration
     *  @param name Name
     *  @param severity Alarm severity
     *  @param message Alarm message
     *  @param timestamp Alarm time stamp
     */
    private GlobalAlarm(final AlarmTreeItem parent, final String name,
            final SeverityLevel severity, final String message, final Instant timestamp)
    {
        super(parent, name, -1);
        setAlarmState(severity, severity, message, timestamp);

        // Description is fetched in background thread, initialize to 'unknown'
        setDescription(Messages.AlarmDescriptionUnknown);
    }

    /** @return Text (multi-line) that can be used as a tool-tip to
     *          describe this item and its current state
     */
    @Override
    public synchronized String getToolTipText()
    {
        return NLS.bind(Messages.GlobalAlarm_ToolTipFmt,
            new Object[]
            {
                getPathName(),
                getDescription(),
                getDuration(),
                getSeverity().getDisplayName(),
                getMessage()
            });
    }

    // Complete the guidance etc. from RDB
    public void completeGuiInfo(final RDBUtil rdb, final SQL sql) throws Exception
    {
        final AlarmConfigurationReader reader = new AlarmConfigurationReader(rdb, sql);
        // Item names are not necessarily unique,
        // so name & ID-of-parent are required for lookup.
        // To get all parent IDs, start at the root
        final AlarmTreeRoot root = getRoot();
        // Lock the root
        // When several global alarms for the same root trigger at about the same time,
        // multiple ReadInfoJob instances will try to complete the GUI info.
        // If they concurrently try to update the 'root' and other higher-level
        // elements, multiple jobs will try to set the ID of these elements,
        // but it is not permitted to update the ID once set.
        // By locking on the root, we assert that the ReadInfoJob instances for one alarm
        // tree run in sequence, not parallel, which also prevents double-lookup of the
        // same info.
        synchronized (root)
        {
            completeGuiInfo(reader, root);
        }
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
                children[i] = item.getChild(i);
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
