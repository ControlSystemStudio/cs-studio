/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.util.ArrayList;
import java.util.List;

/** A PV table model, i.e. list of {@link PVTableItem}s
 *
 *  @author Kay Kasemir
 */
public class PVTableModel implements PVTableItemListener
{
    /** The list of items in this table. */
    private List<PVTableItem> items = new ArrayList<PVTableItem>();
    
    final private List<PVTableModelListener> listeners = new ArrayList<PVTableModelListener>();

    public void addListener(final PVTableModelListener listener)
    {
    	listeners.add(listener);
    }
    
    /** @return Returns number of items (rows) in model. */
    public int getItemCount()
    { 
    	return items.size();
    }

    /** @return Returns item (row). */
	public PVTableItem getItem(final int row)
	{
        if (row >= 0  &&  row < items.size())
            return items.get(row);
        return null;
	}

	public void addItem(final String pv_name, final double tolerance)
	{
		items.add(new PVTableItem(pv_name, tolerance, this));
	}

	@Override
	public void tableItemChanged(final PVTableItem item)
	{
		// TODO Pass 1st update on
		// Then turn following updates into overall model refresh:
		// Start timer, if more updates arrive signal overall model refresh in timer
		for (PVTableModelListener listener : listeners)
			listener.tableItemChanged(item);
	}
	
	/** Save snapshot value of each item */
	public void save()
	{
		for (PVTableItem item : items)
			item.save();
		for (PVTableModelListener listener : listeners)
			listener.tableItemsChanged();
	}

	/** Must be invoked when 'done' by the creator of the model. */
	public void dispose()
	{
		for (PVTableItem item : items)
			item.dispose();
		items.clear();
	}
}
