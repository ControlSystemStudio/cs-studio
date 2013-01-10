/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.areapanel;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;

public class AreaAlarmModel implements AlarmClientModelListener
{
	final private AreaAlarmModelListener listener;
	private AlarmClientModel model;
	private volatile AlarmTreeItem[] items;
	
	public AreaAlarmModel(final AreaAlarmModelListener listener) throws Exception
	{
		this.listener = listener;
		model = AlarmClientModel.getInstance();
		model.addListener(this);
		determinePanelItems();
	}

	/** {@inheritDoc} */
	@Override
    public void newAlarmConfiguration(final AlarmClientModel model)
    {
		determinePanelItems();
		listener.areaModelChanged();
    }

	/** Recursively collect items for the panel from model */
	private void determinePanelItems()
    {
		final int panel_level = Preferences.getHierarchyLevel();
		final List<AlarmTreeItem> items = new ArrayList<AlarmTreeItem>();
		final AlarmTreeRoot root = model.getConfigTree();
		synchronized (root)
        {
			collectPanelItems(root, items, 0, panel_level);
        }
		this.items = items.toArray(new AlarmTreeItem[items.size()]);
    }

	/** Recursively collect items for the panel
	 *  @param item Item from alarm tree
	 *  @param items {@link List} where panel items are collected
	 *  @param item_level Level of item in alarm tree
	 *  @param panel_level Level to display in panel
	 */
	private void collectPanelItems(final AlarmTreeItem item,
			final List<AlarmTreeItem> items, final int item_level, final int panel_level)
    {
		if (item_level > panel_level)
			return;
		final int n = item.getChildCount();
		if (item_level == panel_level)
			items.add(item);
		for (int i=0;  i<n;  ++i)
			collectPanelItems(item.getClientChild(i), items, item_level+1, panel_level);
    }
	
	/** @return Items in alarm panel */
	public AlarmTreeItem[] getItems()
	{
		return items;
	}

	/** {@inheritDoc} */
	@Override
    public void serverModeUpdate(final AlarmClientModel model,
    		final boolean maintenance_mode)
    {
	    // Ignore
    }

	/** {@inheritDoc} */
	@Override
    public void serverTimeout(final AlarmClientModel model)
    {
		listener.serverTimeout();
    }

	/** {@inheritDoc} */
	@Override
    public void newAlarmState(final AlarmClientModel model, final AlarmTreePV pv,
    		final boolean parent_changed)
    {
		if (! parent_changed)
			return;
		// Something in the alarm tree hierarchy changed.
		// Does it affect one or more items that we display?
		// Could search from pv.getParent() upwards until we
		// find an item.
		// Overall, however, it seems easier to just check
		// in the AlarmPanelItem if it needs to redraw
		listener.alarmsChanged();
    }

	/** Must be called when model no longer used to release resources */
	public void close()
    {
		model.removeListener(this);
		model.release();
    }
}
