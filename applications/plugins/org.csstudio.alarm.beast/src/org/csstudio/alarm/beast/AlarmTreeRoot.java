/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.XMLWriter;

/** Root of the alarm configuration tree.
 *  @author Kay Kasemir, Xihui Chen
 */
public class AlarmTreeRoot extends AlarmTreeItem
{
    /** Initialize alarm tree root
     *  @param id RDB ID of root element
     *  @param name Name of root element
     */
    public AlarmTreeRoot(final int id, final String name)
    {
        super(null, name, id);
    }

    /** {@inheritDoc} */
    @Override
    public AlarmTreePosition getPosition()
    {
        return AlarmTreePosition.Root;
    }

    /** Called for each PV in the tree that's acknowledged.
     *  <p>
     *  When calling the public <code>acknowledge()</code> method,
     *  that will descend to all PVs, which in turn invoke this method
     *  on the root.
     *  The default alarm tree root does nothing, but the AlarmClientModelRoot
     *  will forward the request to the alarm server
     *  @param pv PV that needs to be ack'ed
     *  @param acknowledge Ack or un-ack?
     */
    protected void acknowledge(AlarmTreePV pv, boolean acknowledge)
    {
        // NOP
    }

    /** @return Number of PVs in tree */
    public int getPVCount()
    {
        return getPVCount(this);
    }

    /** @return Number of PVs below item (counts recursively) */
    private int getPVCount(final AlarmTreeItem item)
    {
        if (item instanceof AlarmTreePV)
            return 1;
        int count = 0;
        for (int i=0; i<item.getChildCount(); ++i)
            count += getPVCount(item.getClientChild(i));
        return count;
    }

    /** @return Number of elements (children, sub-children) in tree */
    public int getElementCount()
    {   // Consider root itself, then count children from here on down
        return 1 + getElementCount(this);
    }

    /** @return Number of elements (children, sub-children) below item (counts recursively) */
    private int getElementCount(final AlarmTreeItem item)
    {
        // Count direct children
        final int child_count = item.getChildCount();
        // Then add counts recursively
        int total = child_count;
        for (int i=0; i<child_count; ++i)
            total += getElementCount(item.getClientChild(i));
        return total;
    }

    /** Write XML representation of alarm tree
     *  @param out Stream to which to send XML output
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    final public void writeXML(final PrintWriter out)  throws Exception
    {
        XMLWriter.header(out);
        out.append("<config name=\"" + getName() +"\">\n");
        final int n = getChildCount();
        for (int i=0; i<n; ++i)
        {
            final AlarmTreeItem child = getClientChild(i);
            child.writeItemXML(out, 1);
        }
        out.append("</config>\n");
    }

    /** Locate alarm tree item by path
     *  @param path Path to item
     *  @return Item or <code>null</code> if not found
     */
    public AlarmTreeItem getItemByPath(final String path)
    {
        if (path == null)
            return null;
        final String[] steps = AlarmTreePath.splitPath(path);
        if (steps.length <= 0)
            return null;
        // Does root of path match?
        if (!steps[0].equals(getName()))
            return null;
        // Descend down the path
        AlarmTreeItem item = this;
        for (int i=1;  i < steps.length  &&  item != null;    ++i)
            item = item.getClientChild(steps[i]);
        return item;
    }
}
