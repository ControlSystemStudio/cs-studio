/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.display.pvtable.Preferences;
import org.epics.vtype.VType;

/** A PV table model, i.e. list of {@link PVTableItem}s
 *
 *  @author Kay Kasemir
 */
public class PVTableModel implements PVTableItemListener
{
    /** Period for update checks
     *  @see #performUpdates()
     */
    private static final long UPDATE_PERIOD_MS = 200;

    final private int updateItemThreshold = Preferences.getUpdateItemThreshold();
    
    /** The list of items in this table. */
    private List<PVTableItem> items = new ArrayList<PVTableItem>();
    
    final private List<PVTableModelListener> listeners = new ArrayList<PVTableModelListener>();

    final private Timer update_timer = new Timer("PVTableUpdate", true); //$NON-NLS-1$
    
    /** @see #performUpdates() */
    private Set<PVTableItem> changed_items = new HashSet<PVTableItem>();
    
    /** Initialize */
    public PVTableModel()
    {
        update_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                performUpdates();
            }
        }, UPDATE_PERIOD_MS, UPDATE_PERIOD_MS);
    }
    
    /** @param listener Listener to add */
    public void addListener(final PVTableModelListener listener)
    {
    	listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final PVTableModelListener listener)
    {
    	listeners.remove(listener);
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

	/** Add table item
	 *  @param pv_name PV Name
	 *  @return Added item
	 */
	public PVTableItem addItem(final String pv_name)
	{
		return addItem(pv_name, Preferences.getTolerance(), null);
	}

	/** Add table item
	 *  @param pv_name PV Name
	 *  @param tolerance Tolerance
	 *  @return Added item
	 */
	public PVTableItem addItem(final String pv_name, final double tolerance)
	{
		return addItem(pv_name, tolerance, null);
	}

	/** Add table item
	 *  @param pv_name PV Name
	 *  @param tolerance Tolerance
	 *  @param saved Saved value, may be <code>null</code>
	 *  @return Added item
	 */
	public PVTableItem addItem(final String pv_name, final double tolerance, final VType saved)
	{
		final PVTableItem item = new PVTableItem(pv_name, tolerance, saved, this);
		items.add(item);
        for (PVTableModelListener listener : listeners)
            listener.modelChanged();
		return item;
	}
	
	/** Remove table item (also disposes it)
	 *  @param item Item to remove from model
	 */
	public void removeItem(final PVTableItem item)
	{
	    item.dispose();
	    items.remove(item);
        for (PVTableModelListener listener : listeners)
            listener.modelChanged();
	}

	/** Invoked by timer to perform accumulated updates.
	 * 
	 *  <p>If only one item changed, update that item.
	 *  If multiple items changed, refresh the whole table.
	 */
    private void performUpdates()
    {
        synchronized (changed_items)
        {
            if (changed_items.size() < updateItemThreshold)
            {   // Update exactly the changed items
                for (PVTableItem item : changed_items)
                    for (PVTableModelListener listener : listeners)
                        listener.tableItemChanged(item);
                changed_items.clear();
                return;
            }
            changed_items.clear();
        }
        // Too many items changed, update the whole table
        for (PVTableModelListener listener : listeners)
            listener.tableItemsChanged();
    }
	
	/** {@inheritDoc} */
	@Override
	public void tableItemChanged(final PVTableItem item)
	{
	    synchronized (changed_items)
	    {
	        changed_items.add(item);
	    }
	}
	
	/** Save snapshot value of each item */
	public void save()
	{
		for (PVTableItem item : items)
			item.save();
		for (PVTableModelListener listener : listeners)
		{
			listener.tableItemsChanged();
            listener.modelChanged();
		}
	}

	/** Restore saved values */
	public void restore()
	{
		for (PVTableItem item : items)
			item.restore();
	}
	
	/** Must be invoked when 'done' by the creator of the model. */
	public void dispose()
	{
		for (PVTableItem item : items)
			item.dispose();
		items.clear();
	}

	/** Inform listeners that model changed */
	public void fireModelChange()
	{
	    for (PVTableModelListener listener : listeners)
	        listener.modelChanged();
	}
}
