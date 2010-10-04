/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.io.PrintStream;

import org.csstudio.alarm.beast.AlarmTreePath;

/** Element of the alarm hierarchy
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmHierarchy
{
	/** Parent element, <code>null</code> for root */
	final private AlarmHierarchy parent;
	
	/** Name of the alarm */
	final private String name;

	/** RDB ID */
	final private int id;
	
    /** Full path name of this item. */
    final private String path_name;
	
    /** Child entries in the alarm tree */
	private AlarmHierarchy children[] = null;

	/** Initialize
	 *  @param parent Parent node
	 *  @param name Name
	 *  @param id RDB ID
	 */
	public AlarmHierarchy(final AlarmHierarchy parent, final String name, final int id)
    {
		this.parent = parent;
		this.name = name;
		this.id = id;
		if (parent == null)
			path_name = name;
		else
			path_name = AlarmTreePath.makePath(parent.getPathName(), name);
    }

	/** @param children Child entries
	 *  @throws Error when trying to set them more than once
	 */
	void setChildren(final AlarmHierarchy children[])
	{
		if (this.children != null)
			throw new Error("Alarm tree error, sub-elements already set for " + path_name);
		this.children = children;
	}
	
    /** @return Full path name to this item, including the item name itself */
    public String getPathName()
    {
        return path_name;
    }

	/** @return RDB ID for this alarm tree element */
	public int getID()
    {
	    return id;
    }

    /** Get the alarm tree element's name.
     *  For PVs, this may differ a little from the actual underlying control system PV:
     *  For example, the alarm name might be "Fred",
     *  but since the control system defaults to "system://Fred", the actual
     *  CS PV has a slightly different name.
     *  @return Name that was used when constructing this AlarmPV
     */
    public String getName()
    {
        return name;
    }

	/** @return Number of child nodes */
    public int getChildCount()
    {
        return children.length;
    }

	/** @param index 0 ... <code>getChildCount()-1</code>
     *  @return AlarmHierarchy child node
     */
    public AlarmHierarchy getChild(final int index)
    {
        return children[index];
    }

	/** Dump alarm hierarchy recursively for debugging 
	 *  @param out PrintStream
	 */
    public void dump(final PrintStream out)
    {
		out.println(toString());
		for (AlarmHierarchy child : children)
			child.dump(out);
    }

	/** Perform hierarchy consistency check
	 *  @throws Exception on error
	 */
    public void check() throws Exception
    {
    	// All subtree entries must point back to this as their parent
		for (AlarmHierarchy child : children)
		{
			if (child.parent != this)
				throw new Exception("Hierarchy error from " + child + " to " + this);
			child.check();
		}
    }
    
    /** @return Debug representation */
	@Override
	public String toString()
	{
		return path_name;
	}
}
