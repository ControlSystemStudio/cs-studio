/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.io.PrintWriter;

/** Root of the alarm configuration tree.
 *  @author Kay Kasemir, Xihui Chen
 */
public class AlarmTreeRoot extends AlarmTree
{
    /** Initialize alarm tree root
     *  @param id RDB ID of root element
     *  @param name Name of root element
     */
    public AlarmTreeRoot(final int id, final String name)
    {
        super(id, name, null);
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
    private int getPVCount(final AlarmTree item)
    {
        if (item instanceof AlarmTreePV)
            return 1;
        int count = 0;
        for (int i=0; i<item.getChildCount(); ++i)
            count += getPVCount(item.getChild(i));
        return count;
    }

    /** Write XML representation of alarm tree
     *  @param out Stream to which to send XML output
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    final public void writeXML(final PrintWriter out)  throws Exception
    {
        out.append("<config name=\"" + getName() +"\">\n");
        for (int i=0; i<getChildCount(); ++i)
        {
            final AlarmTree child = getChild(i);
            child.writeItemXML(out, 1);
        }
        out.append("</config>\n");
    }

    /** Locate alarm tree item by path
     *  @param path Path to item
     *  @return Item or <code>null</code> if not found
     */
    public AlarmTree getItemByPath(final String path)
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
        AlarmTree item = this;
        for (int i=1;  i < steps.length  &&  item != null;    ++i)
            item = item.getChild(steps[i]);
        return item;
    }
}
