/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.actions.AcknowledgeAction;
import org.csstudio.alarm.beast.ui.actions.AutomatedAction;
import org.csstudio.alarm.beast.ui.actions.CommandAction;
import org.csstudio.alarm.beast.ui.actions.CopyToClipboardAction;
import org.csstudio.alarm.beast.ui.actions.DurationAction;
import org.csstudio.alarm.beast.ui.actions.GuidanceAction;
import org.csstudio.alarm.beast.ui.actions.RelatedDisplayAction;
import org.csstudio.alarm.beast.ui.actions.SendEMailAction;
import org.csstudio.email.EMailSender;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

/** Helper for handling the context menu of alarm tree items
 *  @author Kay Kasemir, Xihui Chen
 */
public class ContextMenuHelper
{
    /** The number of displayed guidance, display and command actions
     *  in the context menu is each limited to this max. count.
     */
    private static final int max_context_entries = Preferences.getMaxContextMenuEntries();

    /** To record what has been added to the context menu. */
    private final ArrayList<GDCDataStructure> addedGuidance = new ArrayList<GDCDataStructure>();
    private final ArrayList<GDCDataStructure> addedDisplays = new ArrayList<GDCDataStructure>();
    private final ArrayList<GDCDataStructure> addedCommands = new ArrayList<GDCDataStructure>();
    private final ArrayList<AADataStructure> addedAutoActions = new ArrayList<AADataStructure>();

    /** Add menu entries for guidance messages, related displays,
     *  and acknowledgment
     *  @param viewer Viewer that provided the selected items. May be <code>null</code>.
     *                If provided, some menu actions may change the selection.
     *  @param manager Manager of context menu
     *  @param shell Shell that menu actions may use
     *  @param items Currently selected alarm tree items
     *  @param allow_write Allow configuration changes, acknowledgement?
     */
    public ContextMenuHelper(Viewer viewer, final IMenuManager manager,
            final Shell shell,
            final List<AlarmTreeItem> items,
            final boolean allow_write)
    {
        // Determine how many PVs, with and w/o alarm we have.
        // Alarm PVs
        final List<AlarmTreeLeaf> alarm_pvs = new ArrayList<AlarmTreeLeaf>();
        // Items with active alarm
        final List<AlarmTreeItem> alarms = new ArrayList<AlarmTreeItem>();
        // Items with ack'ed alarm
        final List<AlarmTreeItem> ack_alarms = new ArrayList<AlarmTreeItem>();

        for (AlarmTreeItem item : items)
        {
            if (item instanceof AlarmTreeLeaf)
                alarm_pvs.add((AlarmTreeLeaf) item);
            final SeverityLevel severity = item.getSeverity();
            if (severity.ordinal() > 0)
            {
                if (severity.isActive())
                {
                    alarms.add(item);
                }
                else
                    ack_alarms.add(item);
            }
        }

        // Duration of alarm if it's only one
        if (alarm_pvs.size() == 1)
            manager.add(new DurationAction(shell, alarm_pvs.get(0)));

        // Add one menu entry per guidance
        for (AlarmTreeItem item : items)
            addGuidanceMessages(manager, shell, item);
        // Add one menu entry for each related display
        for (AlarmTreeItem item : items)
            addRelatedDisplays(manager, shell, item);
        // Add one menu entry for each related automated action
        for (AlarmTreeItem item : items)
            addAutomatedActions(manager, shell, item);
        if (allow_write)
        {
            // Add one menu entry for each command
            for (AlarmTreeItem item : items)
                addCommands(manager, shell, item);
        }
        // In case there are any PVs in alarm,
        // add action to acknowledge/un-acknowledge them
        if (alarm_pvs.size() > 0)
        {
            manager.add(new CopyToClipboardAction(alarm_pvs));
            // TODO Copy to clipboard
            if (EMailSender.isEmailSupported())
                manager.add(new SendEMailAction(shell, alarm_pvs));
        }

        if (allow_write)
        {
            if (alarms.size() > 0)
            {
                final AcknowledgeAction action = new AcknowledgeAction(true, alarms);
                action.clearSelectionOnAcknowledgement(viewer);
                manager.add(action);
            }
            if (ack_alarms.size() > 0)
            {
                final AcknowledgeAction action = new AcknowledgeAction(false, ack_alarms);
                action.clearSelectionOnAcknowledgement(viewer);
                manager.add(action);
            }
        }
    }

    /** Recursively add guidance messages
     *  @param manager Menu to which to add guidance entries
     *  @param shell Shell to use
     *  @param item Item who's displays to add, recursing to parent
     */
    private void addGuidanceMessages(final IMenuManager manager,
            final Shell shell, final AlarmTreeItem item)
    {
        if (item == null  ||  addedGuidance.size() > max_context_entries)
            return;
        addGuidanceMessages(manager, shell, item.getParent());
        for (GDCDataStructure guidance_entry : item.getGuidance())
        {    // avoid duplicates
            if (addedGuidance.contains(guidance_entry))
                continue;
            manager.add(new GuidanceAction(shell, item.getPosition(),
                                       guidance_entry));
            addedGuidance.add(guidance_entry);
            if (addedGuidance.size() > max_context_entries)
            {
                manager.add(new GuidanceAction(shell, null,
                        new GDCDataStructure(Messages.MoreTag, Messages.MoreGuidanceInfo)));
                break;
            }
        }
    }

    /** Recursively add related displays
     *  @param manager Menu to which to add related display action
     *  @param shell Shell to use
     *  @param item Item who's displays to add, recursing to parent
     */
    private void addRelatedDisplays(final IMenuManager manager,
            final Shell shell, AlarmTreeItem item)
    {
        if (item == null  ||  addedDisplays.size() > max_context_entries)
            return;
        addRelatedDisplays(manager, shell, item.getParent());
        for (GDCDataStructure display : item.getDisplays())
        {   // avoid duplicates
            if (addedDisplays.contains(display))
                continue;
            manager.add(new RelatedDisplayAction(shell, item.getPosition(),
                    display));
            addedDisplays.add(display);
            if (addedDisplays.size() > max_context_entries)
            {   // Using RelatedDisplayAction here would give a better icon,
                // but then it would try to execute the info as a display
                // instead of displaying it.
                // So use GuidanceAction to display detail abot too-many-displays
                manager.add(new GuidanceAction(shell, null,
                        new GDCDataStructure(Messages.MoreTag, Messages.MoreDisplaysInfo)));
                break;
            }
        }
    }

    /** Recursively add commands
     *  @param manager Menu to which to add related display action
     *  @param shell Shell to use
     *  @param item Item who's displays to add, recursing to parent
     */
    private void addCommands(final IMenuManager manager,
            final Shell shell, AlarmTreeItem item)
    {
        if (item == null  ||  addedCommands.size() > max_context_entries)
            return;
        addCommands(manager, shell, item.getParent());
        for (GDCDataStructure command : item.getCommands())
        {   // avoid duplicates
            if (addedCommands.contains(command))
                continue;
             manager.add(new CommandAction(shell, item.getPosition(), command));
             addedCommands.add(command);
             if (addedCommands.size() > max_context_entries)
             {  // See comment in addRelatedDisplays
                 manager.add(new GuidanceAction(shell, null,
                         new GDCDataStructure(Messages.MoreTag, Messages.MoreCommandsInfo)));
                 break;
             }
        }
    }

    /** Recursively add automated actions
     *  @param manager Menu to which to add related display action
     *  @param shell Shell to use
     *  @param item Item who's displays to add, recursing to parent
     */
    private void addAutomatedActions(final IMenuManager manager,
            final Shell shell, AlarmTreeItem item)
    {
        if (item == null  ||  addedAutoActions.size() > max_context_entries)
            return;
        addAutomatedActions(manager, shell, item.getParent());
        for (AADataStructure action : item.getAutomatedActions())
        {   // avoid duplicates
            if (addedAutoActions.contains(action))
                continue;
            // Skip sevrpv: actions
            if (action.getDetails().startsWith("sevrpv:"))
                continue;
             manager.add(new AutomatedAction(shell, item, action));
             addedAutoActions.add(action);
             if (addedAutoActions.size() > max_context_entries)
             {  // See comment in addRelatedDisplays
                 manager.add(new GuidanceAction(shell, null,
                         new GDCDataStructure(Messages.MoreTag, Messages.MoreAutoActionsInfo)));
                 break;
             }
        }
    }
}
