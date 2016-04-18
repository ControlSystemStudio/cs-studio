/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimestampHelper;
import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.alarm.beast.XMLTags;
import org.csstudio.apputil.xml.XMLWriter;
import org.eclipse.osgi.util.NLS;

/** Base class for items in the <u>client's</u> alarm configuration tree.
 *  <p>
 *  Basic hierarchy:
 *  <ul>
 *  <li>One {@link AlarmTreeRoot}
 *  <li>Many {@link AlarmTreeItem} entries to build the hierarchy
 *  <li>Finally {@link AlarmTreePV} entries as leaves
 *  </ul>
 *  @see AlarmTreeRoot
 *  @see AlarmTreeComponent
 *  @see AlarmTreePV
 *
 *  @author Kay Kasemir, Xihui Chen
 */
public class AlarmTreeItem extends TreeItem
{
    private static final long serialVersionUID = -8597126519675742036L;

    private volatile int disabled_children = 0;

    /** Sub-tree elements of this item which are currently in alarm */
    final private transient List<AlarmTreeItem> alarm_children = new CopyOnWriteArrayList<>();

    // Using arrays for guidance, ..., commands to be thread-safe

    /** Guidance messages */
    private volatile GDCDataStructure guidance[] = new GDCDataStructure[0];

    /** Related displays */
    private volatile GDCDataStructure displays[] = new GDCDataStructure[0];

    /** Commands */
    private volatile GDCDataStructure commands[] = new GDCDataStructure[0];

    /** Automated Actions */
    private volatile AADataStructure automated_actions[] = new AADataStructure[0];

    /** Current severity of this item/subtree */
    private volatile SeverityLevel current_severity = SeverityLevel.OK;

    /** Highest/latched alarm severity of this item/subtree */
    private volatile SeverityLevel severity = SeverityLevel.OK;

    /**  Highest/latched alarm message of this item/subtree */
    private volatile String message = SeverityLevel.OK.getDisplayName();

    /** Time of last configuration change */
    private volatile Instant config_time;

    /** Initialize alarm tree item
     *  @param parent Parent item or <code>null</code>
     *  @param name Name of the item
     *  @param id RDB ID
     *  @throws Error on tree structure error
     */
    public AlarmTreeItem(final AlarmTreeItem parent, final String name, final int id)
    {
        super(parent, name, id);
    }

    /** @return Text (multi-line) that can be used as a tool-tip to
     *          describe this item and its current state
     */
    public String getToolTipText()
    {
        return NLS.bind(Messages.Alarm_TT,
            new Object[]
            {
                getPathName(),
                getCurrentSeverity().getDisplayName(),
                getSeverity().getDisplayName(),
                getMessage()
            });
    }

    /** @return Description of this item's position in the alarm tree */
    public AlarmTreePosition getPosition()
    {
        if (getParent() instanceof AlarmTreeRoot)
            return AlarmTreePosition.Area;
        return AlarmTreePosition.System;
    }

    /** @return Alarm tree root element */
    @Override
    public AlarmTreeRoot getRoot()
    {
        final TreeItem root = super.getRoot();
        if (root instanceof AlarmTreeRoot)
            return (AlarmTreeRoot) root;
        throw new Error("Alarm tree has no root"); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    public AlarmTreeItem getParent()
    {
        return (AlarmTreeItem) super.getParent();
    }

    /** @return Number of child nodes */
    final public int getDisabledChildCount()
    {
        return disabled_children;
    }

    /** {@inheritDoc} */
    @Override
    public AlarmTreeItem getChild(final int i)
    {
        return (AlarmTreeItem) super.getChild(i);
    }

    /** {@inheritDoc} */
    @Override
    public AlarmTreeItem getChild(final String name)
    {
        return (AlarmTreeItem) super.getChild(name);
    }

    /** Locate alarm tree item by path, starting at this element
     *  @param path Path to item
     *  @return Item or <code>null</code> if not found
     */
    @Override
    public AlarmTreeItem getItemByPath(final String path)
    {
        return (AlarmTreeItem) super.getItemByPath(path);
    }

    /** @return Guidance messages */
    public GDCDataStructure[] getGuidance()
    {
        final GDCDataStructure[] safe_copy = guidance;
        return Arrays.copyOf(safe_copy, safe_copy.length);
    }

    /** @param guidance Guidance messages */
    void setGuidance(final GDCDataStructure guidance[])
    {
        this.guidance = Objects.requireNonNull(guidance);
    }

    /** @return Related displays */
    public GDCDataStructure[] getDisplays()
    {
        final GDCDataStructure[] safe_copy = displays;
        return Arrays.copyOf(safe_copy, safe_copy.length);
    }

    /** Add related display
     *  @param title
     *  @param display
     */
    public void addDisplay(final String title, final String display)
    {
        final GDCDataStructure[] safe_copy = displays;
        final GDCDataStructure[] new_displays = Arrays.copyOf(safe_copy, safe_copy.length + 1);
        new_displays[displays.length] = new GDCDataStructure(title, display);
        displays = new_displays;
    }

    /** @param displays Related displays */
    void setDisplays(final GDCDataStructure displays[])
    {
        this.displays = Objects.requireNonNull(displays);
    }

    /** @return Commands */
    public GDCDataStructure[] getCommands()
    {
        final GDCDataStructure[] safe_copy = commands;
        return Arrays.copyOf(safe_copy, safe_copy.length);
    }

    /** @param commands Commands */
    void setCommands(final GDCDataStructure[] commands)
    {
        this.commands = Objects.requireNonNull(commands);
    }

    /** @return Automated Actions */
    public AADataStructure[] getAutomatedActions()
    {
        final AADataStructure[] safe_copy = automated_actions;
        return Arrays.copyOf(safe_copy, safe_copy.length);
    }

    /** @param automated_actions Automated Actions */
    void setAutomatedActions(final AADataStructure[] automated_actions)
    {
        this.automated_actions = Objects.requireNonNull(automated_actions);
    }

    /** @return Time of last configuration change */
    public String getConfigTime()
    {
        final Instant save_copy = config_time;
        if (save_copy == null)
            return Messages.Unknown;
        return TimestampHelper.format(save_copy);
    }

    /** @param config_time Time of last configuration change */
    void setConfigTime(final Instant config_time)
    {
        this.config_time = config_time;
    }

    /** @return Number of sub-elements in configuration hierarchy
     *          which are currently in alarm
     */
    public int getAlarmChildCount()
    {
        return alarm_children.size();
    }

    /** Get one of the child elements which are currently in alarm.
     *  @param index Child element index 0 .. (getAlarmChildCount()-1)
     *  @return Sub-item in alarm hierarchy
     */
    public AlarmTreeItem getAlarmChild(final int index)
    {
        return alarm_children.get(index);
    }

    /** @return Current severity */
    public SeverityLevel getCurrentSeverity()
    {
        return current_severity;
    }

    /** @return Highest or latched severity */
    public SeverityLevel getSeverity()
    {
        return severity;
    }

    /** @return Highest or latched alarm message */
    public String getMessage()
    {
        return message;
    }

    /** Update alarm state of this item, maximize alarm tree severities.
     *
     *  Ends up maximizing severity of parent chain,
     *  so caller must lock root.
     *
     *  @param current_severity Current severity of PV
     *  @param severity Alarm severity
     *  @param message Alarm message
     *  @param leaf PV that triggered this update
     *  @return NONE if this item already has the same severities and message, PV if only this item has changed,
     *              or PV_AND_PARENT if both this item and its parent have changed
     */
    protected synchronized ChangeLevel setAlarmState(final SeverityLevel current_severity,
            final SeverityLevel severity, final String message,
            final AlarmTreeLeaf pv)
    {
        if (getCurrentSeverity() == current_severity &&
            getSeverity() == severity  &&
            getMessage().equals(message))
            return ChangeLevel.NONE;
        this.current_severity = current_severity;
        this.severity = severity;
        this.message = message;
        final AlarmTreeItem parent = getParent();
        if (parent != null)
            return parent.maximizeSeverity() ? ChangeLevel.PV_AND_PARENT : ChangeLevel.PV;
        return ChangeLevel.PV;
    }

    /** Acknowledge or un-acknowledge current alarms.
     *  <p>
     *  For PV entries, it actually acknowledges the alarm.
     *  For other hierarchy entries, it acknowledges all child alarms.
     *
     *  @param acknowledge Acknowledge, or un-acknowledge?
     */
    public void acknowledge(final boolean acknowledge)
    {
        // Acknowledging alarms will recurse to the PVs,
        // then call up to the root to send a notification
        // to JMS (for the AlarmClientModelRoot)
        // To prevent deadlocks, first lock the root,
        // then this and other affected tree items
        final AlarmTreeRoot root = getRoot();
        synchronized (root)
        {
            synchronized (this)
            {
                final int n = getChildCount();
                for (int i=0; i<n; ++i)
                    getChild(i).acknowledge(acknowledge);
            }
        }
    }

    /** Set severity/status of this item by maximizing over its child
     *  severities.
     *  Recursively updates parent items, so caller must have locked the root.
     *
     *  @return <code>true</code> if the severity of this item or any of its parents changed after
     *          this method is executed, or <code>false</code> if the severity remained the same
     */
    public synchronized boolean maximizeSeverity()
    {
        boolean changed = false;
        // Get maximum child severity and its status
        SeverityLevel new_current_severity = SeverityLevel.OK;
        SeverityLevel new_severity = SeverityLevel.OK;
        String new_message = SeverityLevel.OK.getDisplayName();
        alarm_children.clear();
        disabled_children = 0;
        final int n = getChildCount();
        for (int i=0; i<n; ++i)
        {
            final AlarmTreeItem child = getChild(i);
            // Maximize 'current' severity
            if (child.getCurrentSeverity().ordinal() > new_current_severity.ordinal())
                new_current_severity = child.getCurrentSeverity();
            // Maximize latched severity/status
            final SeverityLevel child_sevr = child.getSeverity();
            final int level = child_sevr.ordinal();
            if (level > 0)
                alarm_children.add(child);
            if (level > new_severity.ordinal())
            {
                new_severity = child_sevr;
                new_message = child.getMessage();
            }
            if (child instanceof AlarmTreePV)
            {
                if ( ((AlarmTreePV) child).isEnabled() == false)
                    ++disabled_children;
            }
            else
                disabled_children += child.getDisabledChildCount();
        }

        if (new_current_severity != current_severity  ||
            new_severity != severity  ||
            !new_message.equals(message))
        {
            current_severity = new_current_severity;
            severity = new_severity;
            message = new_message;
            changed = true;
        }

        // Percolate changes towards root
        final AlarmTreeItem parent = getParent();
        if (parent != null)
            return parent.maximizeSeverity() || changed;
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    protected synchronized void dump_item(final PrintStream out, final String indent)
    {
        final String indent1 = indent + "  ";
        out.println(indent + "* " + toString());
        if (guidance.length > 0)
        {
            for (GDCDataStructure guide : guidance)
            {
                out.println(indent1 + "Guidance:");
                out.println(indent1 + "- Title: " + guide.getTitle());
                out.println(indent1 + "- Details: " + guide.getDetails());
            }
        }
        if (displays.length > 0)
        {
            for (GDCDataStructure display : displays)
            {
                out.println(indent1 + "Display:");
                out.println(indent1 + "- Title: " + display.getTitle());
                out.println(indent1 + "- Details: " + display.getDetails());
            }
        }
        if (commands.length > 0)
        {
            for (GDCDataStructure command : commands)
            {
                out.println(indent1 + "Command:");
                out.println(indent1 + "- Title: " + command.getTitle());
                out.println(indent1 + "- Details: " + command.getDetails());
            }
        }
        if (automated_actions.length > 0)
        {
            for (AADataStructure aa : automated_actions)
            {
                out.println(indent1 + "Command:");
                out.println(indent1 + "- Title: " + aa.getTitle());
                out.println(indent1 + "- Details: " + aa.getDetails());
                out.println(indent1 + "- Delay: " + aa.getDelay());
            }
        }
    }

    /** @return XML tag for this tree item */
    protected String getXMLTag()
    {
        return XMLTags.COMPONENT;
    }

    /** Write XML representation of this element, including children
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    final protected void writeItemXML(final PrintWriter out, final int level) throws Exception
    {
        final String tag = getXMLTag();
        XMLWriter.start(out, level, tag + " " + XMLTags.NAME + "=\"" + getName() + "\"");
        out.println();
        writeConfigXML(out, level+1);
        synchronized (this)
        {
            final int n = getChildCount();
            for (int i=0; i<n; ++i)
                getChild(i).writeItemXML(out, level+1);
        }
        XMLWriter.end(out, level, tag);
        out.println();
    }

    /** Write configuration detail in XML format to output.
     *  Default writes guidance, displays, commands.
     *  Derived classes can add more detail
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     */
    protected void writeConfigXML(final PrintWriter out, final int level)
    {
        writeGCD_XML(out, level, XMLTags.GUIDANCE, guidance);
        writeGCD_XML(out, level, XMLTags.DISPLAY, displays);
        writeGCD_XML(out, level, XMLTags.COMMAND, commands);
        writeAA_XML(out, level, XMLTags.AUTOMATED_ACTION, automated_actions);
    }

    /** Write GDCDataStructure as XML
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     *  @param tag XML Tag
     *  @param gcd The data
     */
    private void writeGCD_XML(final PrintWriter out, final int level,
            final String tag, final GDCDataStructure gcd[])
    {
        if (gcd == null  ||  gcd.length <= 0)
            return;
        for (GDCDataStructure guid : gcd)
        {
            XMLWriter.start(out, level, tag);
            out.println();
            XMLWriter.XML(out, level+1, XMLTags.TITLE, guid.getTitle());
            XMLWriter.XML(out, level+1, XMLTags.DETAILS, guid.getDetails());
            XMLWriter.end(out, level, tag);
            out.println();
        }
    }

    /** Write AADataStructure as XML
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     *  @param tag XML Tag
     *  @param aa The data
     */
    private void writeAA_XML(final PrintWriter out, final int level,
            final String tag, final AADataStructure aads[]) {
        if (aads == null || aads.length <= 0)
            return;
        for (AADataStructure data : aads) {
            XMLWriter.start(out, level, tag);
            out.println();
            XMLWriter.XML(out, level + 1, XMLTags.TITLE, data.getTitle());
            XMLWriter.XML(out, level + 1, XMLTags.DETAILS, data.getDetails());
            XMLWriter.XML(out, level + 1, XMLTags.DELAY, data.getDelay());
            XMLWriter.end(out, level, tag);
            out.println();
        }
    }

    /** @return Short string representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append(" (").append(current_severity.getDisplayName()).append("/");
        buf.append(severity.getDisplayName()).append("/");
        buf.append(message).append(")");
        final int n = getChildCount();
        if (n > 0)
        {
            buf.append(" - ");
            for (int i=0; i<n; ++i)
            {
                if (i > 0)
                    buf.append(", ");
                buf.append(getChild(i).getName());
            }
        }
        return buf.toString();
    }
}
