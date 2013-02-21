/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.io.PrintWriter;
import java.util.List;

import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelRoot;
import org.csstudio.apputil.xml.XMLWriter;

/** Root of the alarm configuration tree.
 *  @author Kay Kasemir, Xihui Chen
 */
public class AlarmTreeRoot extends AlarmTreeItem
{
    /** Initialize alarm tree root
     * @param name Name of root element
     * @param id RDB ID of root element
     */
    public AlarmTreeRoot(final String name, final int id)
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
     *  The default alarm tree root does nothing, but the {@link AlarmClientModelRoot}
     *  will forward the request to the alarm server
     *  @param pv PV that needs to be ack'ed
     *  @param acknowledge Ack or un-ack?
     */
    protected void acknowledge(final AlarmTreePV pv, final boolean acknowledge)
    {
        // NOP
    }

    /** Notify listeners to the model that a leaf has changed its alarm state
     *
     *  <p>Default implementation has no listeners, so nothing is done.
     *  {@link AlarmClientModelRoot}, however, has a model that it notifies.
     *  @param leaf Lead that changed
     *  @param parent_changed <code>true</code> if this also triggered updates of parents
     */
    protected void notifyListeners(final AlarmTreeLeaf leaf, final boolean parent_changed)
    {
        // NOP
    }

    /** @return Number of PVs in tree */
    public synchronized int getLeafCount()
    {
        return getLeafCount(this);
    }

    /** @return Number of PVs below item (counts recursively) */
    private int getLeafCount(final AlarmTreeItem item)
    {
        if (item instanceof AlarmTreeLeaf)
            return 1;
        int count = 0;
        for (int i=0; i<item.getChildCount(); ++i)
            count += getLeafCount(item.getChild(i));
        return count;
    }

    /** @param leaves List to which all leaves of this tree are added */
    public void addLeavesToList(final List<AlarmTreeLeaf> leaves)
    {
        addLeavesToList(leaves, this);
    }

    /** @param leaves List to which leaves are added
     *  @param item Item where addition starts, then recurses down
     */
    private void addLeavesToList(final List<AlarmTreeLeaf> leaves, final AlarmTreeItem item)
    {
        if (item instanceof AlarmTreeLeaf)
        {
            leaves.add((AlarmTreeLeaf) item);
            return;
        }
        synchronized (item)
        {
            for (int i=0; i<item.getChildCount(); ++i)
                addLeavesToList(leaves, item.getChild(i));
        }
    }

    /** @return Number of elements (children, sub-children) in tree */
    public synchronized int getElementCount()
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
            total += getElementCount(item.getChild(i));
        return total;
    }

    /** Write XML representation of alarm tree
     *  @param out Stream to which to send XML output
     *  @throws Exception on error
     */
    final public void writeXML(final PrintWriter out)  throws Exception
    {
        writeXML(out, null);
    }

    /** Write XML representation of alarm tree
     *  @param out Stream to which to send XML output
     *  @param comments Comments to add to the header of the file, one line per array element
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    final public void writeXML(final PrintWriter out, final String comments[])  throws Exception
    {
        XMLWriter.header(out);
        if (comments != null)
        {
            out.append("<!--\n");
            for (String comment : comments)
            {
                out.append("     ");
                out.append(comment);
                out.append("\n");
            }
            out.append("  -->\n");
        }
        out.append("<config name=\"" + getName() +"\">\n");
        final int n = getChildCount();
        for (int i=0; i<n; ++i)
        {
            final AlarmTreeItem child = getChild(i);
            child.writeItemXML(out, 1);
        }
        out.append("</config>\n");
    }
}
