/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/** SWT Tree with workaround for MULTI-selection issue
 * 
 *  See Bug 259141:
 *  Tree.getSelection() is extremely slow with SWT.VIRTUAL and SWT.MULTI
 *  https://bugs.eclipse.org/bugs/show_bug.cgi?id=259141
 *  
 *  <p>When allowing selection of multiple items in SWT.TREE,
 *  this can be very slow.
 *  Expanding a tree branch can take 10 seconds, while the same tree
 *  with SWT.SINGLE reacts without observable delay.
 *  
 *  <p>A workaround submitted to the bug report by
 *  Nicolas Bros <nbros@mia-software.com>
 *  allows usage of the Tree with SWT.VIRTUAL and SWT.MULTI
 *  
 *  @author Kay Kasemir
 */
public class MultiSelectionTree extends Tree
{
	final Set<TreeItem> cachedSelection = new HashSet<TreeItem>();

	/** Initialize
	 *  @param parent
	 *  @param style
	 */
	public MultiSelectionTree(final Composite parent, final int style)
    {
		super(parent, style);
		addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetSelected(final SelectionEvent e)
			{
				if (! (e.item instanceof TreeItem))
					return;
				final TreeItem treeItem = (TreeItem) e.item;
				// Ctrl+Click : add or remove from selection
				if ((e.stateMask & SWT.CONTROL) != 0
						|| (e.stateMask & SWT.COMMAND) != 0)
				{
					if (cachedSelection.contains(treeItem))
						cachedSelection.remove(treeItem);
					else
						cachedSelection.add(treeItem);
				}
				// Shift+Click : select a range of items
				else if ((e.stateMask & SWT.SHIFT) != 0)
				{
					final TreeItem[] selection = getSystemSelection();
					cachedSelection.clear();
					for (TreeItem selected : selection)
						cachedSelection.add(selected);
				}
				// simple click with no modifiers : selection of a
				// single item
				else
				{
					cachedSelection.clear();
					cachedSelection.add(treeItem);
				}
				// System.out.println("widgetSelected: " + e);
			}
		});
    }

	@Override
    protected void checkSubclass()
    {
    	// allow sub-classing, which in principle is not allowed for SWT.Tree
    }

	private TreeItem[] getSystemSelection()
	{
		return super.getSelection();
	}

	@Override
	public TreeItem[] getSelection()
	{
		return cachedSelection.toArray(new TreeItem[cachedSelection.size()]);
	}

	@Override
	public void setSelection(final TreeItem item)
	{
		cachedSelection.clear();
		cachedSelection.add(item);
		super.setSelection(item);
	}

	@Override
	public void setSelection(final TreeItem[] items)
	{
		cachedSelection.clear();
		for (TreeItem treeItem : items)
			cachedSelection.add(treeItem);
		super.setSelection(items);
	}
}
