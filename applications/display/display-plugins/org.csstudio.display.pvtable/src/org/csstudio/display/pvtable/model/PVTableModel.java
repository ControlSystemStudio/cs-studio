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

/** A PV table model, i.e. list of {@link PVTableItem}s
 *  <p>
 *  Updates are throttled:
 *  Changed items are accumulated, and depending on how many changed,
 *  just those items are notified, or the whole table is marked for update.
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
    public PVTableItem addItem(final String pv_name, final double tolerance, final SavedValue saved)
    {
        return addItem(new PVTableItem(pv_name, tolerance, saved, this));
    }

    /** Add table item
     *  @param item Item to add
     *  @return Added item
     */
    public PVTableItem addItem(final PVTableItem item)
    {
        items.add(item);
        for (PVTableModelListener listener : listeners)
            listener.modelChanged();
        return item;
    }

    /** Add a new item above an already existing item
     *  @param item Existing item. <code>null</code> to add at bottom.
     *  @param pv_name PV name (or comment) of new item
     *  @return Added item
     */
    public PVTableItem addItemAbove(final PVTableItem item, final String pv_name)
    {
        return addItemAbove(item, new PVTableItem(pv_name, Preferences.getTolerance(), null, this));
    }

    /** Add a new item above an already existing item
     *  @param item Existing item. <code>null</code> to add at bottom.
     *  @param new_item New item
     *  @return Added item
     */
    public PVTableItem addItemAbove(final PVTableItem item, final PVTableItem new_item)
    {
        if (item == null)
            return addItem(new_item);
        final int index = Math.max(0, items.indexOf(item));
        items.add(index, new_item);
        for (PVTableModelListener listener : listeners)
            listener.modelChanged();
        return new_item;
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
        final List<PVTableItem> to_update = new ArrayList<>();
        synchronized (changed_items)
        {   // Lock changed_items as briefly as possible to check what changed
            final int changed = changed_items.size();
            // Anything?
            if (changed <= 0)
                return;
            // Limited number, update those items?
            if (changed < updateItemThreshold)
                to_update.addAll(changed_items);
            // else: Many items, update whole table
            changed_items.clear();
        }

        if (to_update.isEmpty())
        {   // Too many items changed, update the whole table
            for (PVTableModelListener listener : listeners)
                listener.tableItemsChanged();
        }
        else
        {   // Update exactly the changed items
            for (PVTableItem item : to_update)
                for (PVTableModelListener listener : listeners)
                    listener.tableItemChanged(item);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void tableItemSelectionChanged(final PVTableItem item)
    {   // Model receives this from item. Forward to listeners of model
        for (PVTableModelListener listener : listeners)
            listener.tableItemSelectionChanged(item);
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

    /** Save snapshot value of all checked items */
    public void save()
    {
        for (PVTableItem item : items)
        {
            if (item.isSelected())
                item.save();
        }
        for (PVTableModelListener listener : listeners)
        {
            listener.tableItemsChanged();
            listener.modelChanged();
        }
    }

   /** Save snapshot value of each item
    *  @param items Items to save
    */
    public void save(final List<PVTableItem> items)
    {
        for (PVTableItem item : items)
            item.save();
        for (PVTableModelListener listener : listeners)
        {
            listener.tableItemsChanged();
            listener.modelChanged();
        }
    }

    /** Restore saved values for all checked items */
    public void restore()
    {
        for (PVTableItem item : items)
            if (item.isSelected())
                item.restore();
    }

    /** Restore saved values
    *  @param items Items to restore
     */
    public void restore(final List<PVTableItem> items)
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
