/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.configtool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;

/** Convert ALH config files to Alarm Handler config file
 *  @author Kay Kasemir
 *  @author Laurier Baribeau (pattern updates)
 */
@SuppressWarnings("nls")
public class ALHConverter
{
    private enum State
    {
        /** Waiting for 'root' element */
        ROOT,

        /** Waiting for group elements */
        GROUP
    }

    private State state = State.ROOT;

    private AlarmTreeRoot root;

    private AlarmTreeItem current_group;

    private AlarmTreePV current_pv;

    /** Initialize converter
     *  @param filename Name of ALH file to convert
     *  @throws Exception on error
     */
    public ALHConverter(final String filename) throws Exception
    {
        final FileReader alh = new FileReader(filename);
        try
        {
            parse(new BufferedReader(alh));
        }
        finally
        {
            alh.close();
        }
        check(root);
    }

    /** @return Root of Alarm Tree generated from ALH config */
    public AlarmTreeRoot getAlarmTree()
    {
        return root;
    }

    /** Parse
     *  @param alh ALH input file reader
     *  @throws Exception on error
     */
    private void parse(final BufferedReader alh) throws Exception
    {
        // Pattern for comment "... # ..."
        final Pattern comment_pattern = Pattern.compile("^\\s*#.*");

        // Pattern for "GROUP <parent> <group name>"
        // Any non-space is allowed in the group names
        final Pattern group_pattern = Pattern.compile("\\s*GROUP\\s+(\\S+)\\s+(\\S+)\\s*");

        // Pattern for "CHANNEL <group name> <pv name> <flags>"
        // Any non-space is allowed in the names.
        // ALH input with flags is allowed, but flags are actually ignored.
        final Pattern channel_pattern = Pattern.compile("\\s*CHANNEL\\s+(\\S+)\\s+(\\S+)(\\s+[-C][-D][-A][-T][-L])?\\s*");

        // Pattern for "$ALARMCOUNTFILTER 5 2"
        final Pattern filter_count_pattern = Pattern.compile("\\s*\\$ALARMCOUNTFILTER\\s+([0-9]+)\\s+([0-9]+)\\s*");

        // Pattern for web link "http://..."
        final Pattern http_pattern = Pattern.compile(".*\\s(https?://[a-zA-Z0-9-./]+)\\s*");

        for (String line = alh.readLine();  line != null;  line = alh.readLine())
        {
            // System.out.println(line);
            // Skip comments
            if (comment_pattern.matcher(line).matches())
                continue;

            final Matcher group_match = group_pattern.matcher(line);
            if (group_match.matches())
            {
                final String parent = group_match.group(1).trim();
                final String name = group_match.group(2).trim();
                handleGroup(parent, name);
                continue;
            }

            final Matcher channel_match = channel_pattern.matcher(line);
            if (channel_match.matches())
            {
                final String group_name = channel_match.group(1).trim();
                final String name = channel_match.group(2).trim();
                handleChannel(group_name, name);
                continue;
            }

            // Look for "$SEVRCOMMAND UP_ANY echo SOMETEXT | festival --tts"
            // or       "$SEVRCOMMAND UP_ANY say SOMETEXT"
            if (line.indexOf("$SEVRCOMMAND") >= 0)
            {
                int annunc = line.indexOf("| festival");
                if (annunc > 0)
                {
                    final int echo = line.indexOf("echo");
                    if (echo < annunc)
                    {
                        final String text = line.substring(echo + 5, annunc).trim();
                        handleAnnunciate(text);
                    }
                }
                else
                {
                    annunc = line.indexOf("UP_ANY say ");
                    if (annunc > 0)
                    {
                        final String text = line.substring(annunc + 11).trim();
                        handleAnnunciate(text);
                    }
                }
                continue;
            }

            final Matcher filter_match = filter_count_pattern.matcher(line);
            if (filter_match.matches())
            {
                final int count = Integer.parseInt(filter_match.group(1));
                final int delay = Integer.parseInt(filter_match.group(2));
                handleFilter(delay, count);
            }

            final Matcher http_match = http_pattern.matcher(line);
            if (http_match.matches())
            {
                //  $COMMAND guidance_rationale|
                String title = "Web";
                final String link = http_match.group(1);
                final int cmd = line.indexOf("$COMMAND");
                if (cmd >= 0)
                {
                    final int end = line.indexOf("|", cmd+9);
                    if (end > 0)
                        title = line.substring(cmd+9, end);
                }
                handleDisplay(title, link);
                continue;
            }
        }
    }

    private void handleGroup(final String parent_group, final String name) throws Exception
    {
        System.out.println("Group '" + parent_group + "' / '" + name + "'");
        if (state == State.ROOT)
        {
            root = new AlarmTreeRoot(name, 0);
            state = State.GROUP;
            return;
        }
        final AlarmTreeItem parent = findGroup(root, parent_group);
        if (parent == null)
            throw new Exception("Cannot find parent group '" + parent_group +
                    "' for group '" + name + "'");
        current_group = new AlarmTreeItem(parent, name, 0);
        current_pv = null;
    }

    private void handleChannel(final String group_name, final String name) throws Exception
    {
        System.out.println("Channel '" + group_name + "' / '" + name + "'");
        current_group = findGroup(root, group_name);
        if (current_group == null)
            throw new Exception("Cannot find parent group '" + group_name +
                    "' for channel '" + name + "'");
        current_pv = new AlarmTreePV(current_group, name, 0);
        current_pv.setLatching(true);
    }

    private void handleAnnunciate(final String text) throws Exception
    {
        if (current_pv == null)
            throw new Exception("Got annunciation text outside of channel: '" + text + "'");
        current_pv.setAnnunciating(true);
        current_pv.setDescription(text);
    }

    private void handleDisplay(final String title, final String detail) throws Exception
    {
        final AlarmTreeItem element =
            (current_pv != null) ? current_pv : current_group;
        if (element == null)
            throw new Exception("Got display outside of channel or group: '" +  title + "', " + detail);
        element.addDisplay(title, detail);
    }

    private void handleFilter(final int delay, final int count) throws Exception
    {
        if (current_pv == null)
            throw new Exception("Got filter outside of channel");
        current_pv.setDelay(delay);
        if (count > 1)
            current_pv.setCount(count);
        else
            current_pv.setCount(0);
    }

    /** Find a group in the tree
     *  @param item Root element from where to check
     *  @param name Name of group to locate
     *  @return That group or <code>null</code>
     */
    private AlarmTreeItem findGroup(final AlarmTreeItem item, final String name)
    {   // Nothing?  Or PV, which isn't a 'group' and has no sub-tree?
        if (item == null  ||  (item instanceof AlarmTreePV))
            return null;
        // Is it this item?
        if (item.getName().equals(name))
            return item;
        for (int i=0; i<item.getChildCount(); ++i)
        {
            final AlarmTreeItem found = findGroup(item.getChild(i), name);
            if (found != null)
                return found;
        }
        return null;
    }

    /** Basic consistency check, prints warnings for
     *  empty description or groups without PVs.
     *
     *  @param item Root element from where to check
     */
    private void check(final AlarmTreeItem item)
    {
        if (item instanceof AlarmTreePV)
        {
            final AlarmTreePV pv = (AlarmTreePV) item;
            if (pv.getDescription().length() <= 0)
                System.err.println("Warning: Empty description for PV '" +
                        pv.getName() + "'");
        }
        else
            if (item.getChildCount() <= 0)
                System.err.println("Warning: No sub-entries for '" +
                        item.getName() + "'");
        for (int i=0; i<item.getChildCount(); ++i)
            check(item.getChild(i));
    }
}
