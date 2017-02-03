/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.csstudio.diag.epics.pvtree.Plugin.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;

/** One item in the PV tree model.
 *  <p>
 *  Since an 'item' is a PV, possibly for a record
 *  which has inputs, and those inputs is what we
 *  want to drill down, this class currently includes
 *  almost all the logic behind the tree creation.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class PVTreeItem
{
   /** The model to which this whole tree belongs. */
    private final PVTreeModel model;

    /** The parent of this item, or <code>null</code>. */
    private final PVTreeItem parent;

    /** The info provided by the parent or creator ("PV", "INPA", ...) */
    private final String info;

    /** The name of this PV tree item as shown in the tree. */
    private final String pv_name;

    /** The name of the record.
     *  <p>
     *  For example, the 'name' could be 'fred.SEVR', then 'fred'
     *  would be the record name.
     */
    private final String record_name;

    /** The PV used for getting the current value. */
    private volatile PV pv;

    /** Value received while updates might have been were frozen */
    private volatile String current_value = null;

    /** Alarm received while updates might have been were frozen */
    private volatile AlarmSeverity current_severity = AlarmSeverity.UNDEFINED;

    /** Marker used for values that are not from PV but constant */
    private static final String CONSTANT_VALUE = Messages.Constant;

    /** Most recent value to show (may be frozen). */
    private volatile String value = null;

    /** Most recent severity (may be frozen). */
    private volatile AlarmSeverity severity = AlarmSeverity.UNDEFINED;

    /** Listener to value updates for this item */
    private final PVListener value_listener = new PVListenerAdapter()
    {
        @Override
        public void valueChanged(final PV pv, final VType pv_value)
        {
            current_value = VTypeHelper.format(pv_value);
            current_severity = VTypeHelper.getSeverity(pv_value);
            updateValue();
        }

        @Override
        public void disconnected(final PV pv)
        {
            current_value = "Disconnected";
            current_severity = AlarmSeverity.UNDEFINED;
            updateValue();
        }
    };

    /** Listener to type_pv */
    private volatile StringListener type_listener;

    /** The PV used for getting the record type. */
    private AtomicReference<PV> type_pv = new AtomicReference<>();

    /** Type received from type_pv */
    private volatile String type;

    /** Array of fields to read for this record type.
     *  Fields are removed as they are read, so in the end this
     *  array will be empty
     */
    private final ConcurrentLinkedQueue<String> links_to_read = new ConcurrentLinkedQueue<>();

    /** Listener to link_pv */
    private final StringListener link_listener = new StringListener()
    {
        @Override
        public void handleText(String text)
        {
            // The value could be
            // a) a record name followed by "... NPP NMS". Remove that.
            // b) a hardware input/output "@... " or "#...". Keep that.
            if (text.length() > 1 &&
                    text.charAt(0) != '@' &&
                            text.charAt(0) != '#')
            {
                int i = text.indexOf(' ');
                if (i > 0)
                    text = text.substring(0, i);
            }
            link_value = text;
            updateLink();
        }
    };

    /** Used to read the links of this pv. */
    private AtomicReference<PV> link_pv = new AtomicReference<>();

    /** Link's value */
    private volatile String link_value;

    /** Tree item children, populated with info from the input links. */
    private List<PVTreeItem> links = new ArrayList<>();

    /** Create a new PV tree item.
     *  @param model The model to which this whole tree belongs.
     *  @param parent The parent of this item, or <code>null</code>.
     *  @param info The info provided by the parent or creator ("PV", "INPA", ...)
     *  @param pv_name The name of this PV entry.
     */
    public PVTreeItem(final PVTreeModel model,
            final PVTreeItem parent,
            final String info,
            final String pv_name)
    {
        this.model = model;
        this.parent = parent;
        this.info = info;
        this.pv_name = pv_name;
        this.type = null;

        // In case this is "record.field", get the record name.
        final int sep = pv_name.lastIndexOf('.');
        if (sep > 0)
            record_name = pv_name.substring(0, sep);
        else
            record_name = pv_name;

        logger.log(Level.FINE,
                "New Tree item {0}, record name {1}",
                new Object[] { pv_name, record_name});


        // Now add this one, otherwise the previous call would have found 'this'.
        if (parent != null)
            parent.links.add(this);

        // Is this a link to follow, or just a constant to display?
        // Hardware links "@vme..." or constant numbers "-12.3"
        // cause us to stop here:
        if (! PVNameFilter.isPvName(pv_name))
        {   // No PV
            pv = null;
            // Clear alarm
            severity = null;
            value = CONSTANT_VALUE;
            return;
        }

        // Try to read the pv
        try
        {
            pv = createPV(pv_name, value_listener);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "PV creation error" , e);
        }

        // Avoid loops.
        // If the model already contains an entry with this name,
        // we simply display this new item, but we won't
        // follow its input links.
        // Behavior is not fully predictable:
        // When PV is in tree multiple times, instances receive their RTYP
        // info at various times. Once they have it, there are no more loops.
        // Until they have it, they'll look for it in parallel.
        final PVTreeItem other = model.findPV(pv_name);
        if (other != null  &&  other.type != null)
        {
            type = other.type;
            logger.fine("Known item, not traversing inputs (again)");
            return;
        }
        // Determine record type.
        // This is where this tool starts to only support IOCs with records,
        // since generic CA servers lack ".RTYP"
        try
        {
            type_listener = new StringListener()
            {
                @Override
                public void handleText(final String text)
                {
                    // type should be a text.
                    // If it starts with a number, it's probably not an
                    // EPICS record type but a simulated PV
                    final char first_char = text.length() > 0  ?  text.charAt(0)  : '\0';
                    if (first_char >= 'a' && first_char <= 'z')
                        type = text;
                    else
                        type = Messages.UnknownPVType;
                    updateType();
                }
            };
            type_pv.set(createPV(record_name + ".RTYP", type_listener));
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "PV.RTYP creation error" , ex);
        }
    }

    /** Create and start PV
     *  @param name PV Name
     *  @param listener Listener
     *  @return PV
     *  @throws Exception on error
     */
    private PV createPV(final String name, final PVListener listener) throws Exception
    {
        final PV pv = PVPool.getPV(name);
        pv.addListener(listener);
        return pv;
    }

    /** Dispose this and all child entries. */
    public void dispose()
    {
        for (PVTreeItem item : links)
            item.dispose();
        if (pv != null)
        {
            pv.removeListener(value_listener);
            PVPool.releasePV(pv);
            pv = null;
        }
        disposeLinkPV();
        disposeTypePV();
    }

    /** Delete the type_pv
     *  @return <code>true</code> if disposing, <code>false</code> if was already disposed
     */
    private boolean disposeTypePV()
    {
        final PV pv = type_pv.getAndSet(null);
        if (pv == null)
            return false;
        pv.removeListener(type_listener);
        type_listener = null;
        PVPool.releasePV(pv);
        return true;
    }

    /** Delete the link_pv
     *  @return Name of link PV that was disposed, <code>null</code> if no PV to dispose
     */
    private String disposeLinkPV()
    {
        final PV pv = link_pv.getAndSet(null);
        if (pv == null)
            return null;
        final String name = pv.getName();
        pv.removeListener(link_listener);
        PVPool.releasePV(pv);
        return name;
    }

    /** @return Returns the name of this PV. */
    public String getPVName()
    {   return pv_name; }

    /** @return Severity of current value. May be <code>null</code>. */
    public AlarmSeverity getSeverity()
    {   return severity; }

    /** @return Returns the record type of this item or <code>null</code>. */
    public String getType()
    {   return type;    }

    /** @return Returns the parent or <code>null</code>. */
    public PVTreeItem getParent()
    {   return parent;    }

    /** @return Returns the first link or <code>null</code>. */
    public PVTreeItem getFirstLink()
    {
        if (links.size() > 0)
            return links.get(0);
        return null;
    }

    /** @return Returns the all links. */
    public PVTreeItem[] getLinks()
    {
        return (PVTreeItem[]) links.toArray(new PVTreeItem[links.size()]);
    }

    /** @return Returns <code>true</code> if this item has any links. */
    public boolean hasLinks()
    {
        return links.size() > 0;
    }

    /** Thread-safe handling of the 'type' update. */
    private void updateType()
    {
        logger.log(Level.FINE,
                "{0} received type {1}", new Object[] { pv_name, type });

        // We got the type, so close the connection.
        if (! disposeTypePV())
            return; // Already disposed

        // Display the received type of this record.
        model.itemChanged(PVTreeItem.this);

        links_to_read.clear();
        final List<String> fields = model.getFieldInfo().get(type);
        if (fields != null)
            links_to_read.addAll(fields);
        if (links_to_read.size() <= 0)
        {
            logger.log(Level.FINE,
                    "{0} has unknown record type {1}", new Object[] { pv_name, type });
            return;
        }
        model.incrementLinks(links_to_read.size());
        getNextLink();
    }

    /** Helper for reading next link from links_to_read. */
    private void getNextLink()
    {
        // Probably superfluous, but can't hurt
        disposeLinkPV();
        // Any more links to read?
        final String field = links_to_read.peek();
        if (field == null)
            return;
        final String link_name = record_name + "." + field;
        try
        {
            link_pv.set(createPV(link_name, link_listener));
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "PV." + field + " creation error" , e);
        }
    }

    /** Thread-safe handling of the 'link_pv' update. */
    private void updateLink()
    {
        // Only one update
        final String link_name = disposeLinkPV();
        if (link_name == null)
        {
            logger.log(Level.FINE, "{0} already disposed link", pv_name);
            return;
        }
        else
            logger.log(Level.FINE, "{0} received ''{1}''", new Object[] { link_name, link_value });

        // Remove field for which we received update from
        // list of links to read
        final String field = links_to_read.poll();
        if (field == null)
        {
            logger.log(Level.FINE, "{0} update without active link?", link_name);
            return;
        }
        // If there is a value in the link, display this
        // (and sub-items)
        // TODO: This is not 100% correct. If a link happens to contain
        // an empty string right now, it will not be included in the tree.
        // But the value could change later; we won't catch that...
        if (link_value.length() > 0)
        {
            new PVTreeItem(model, PVTreeItem.this, field, link_value);
            model.itemChanged(PVTreeItem.this);
        }
        // This could decrement the links read so far to zero,
        // since the new PVTreeItem just created has not
        // started to request its links.
        // Tree will thus expand a few times,
        // whenever a bunch of links have resolved,
        // but at least not for every single change
        model.decrementLinks();
        getNextLink();
    }

    /** Update value (and severity) from 'current_value/severity'
     *  .. unless the model is 'frozen'
     */
    public void updateValue()
    {
        if (model.isFrozen() && value != null)
            return;
        value = current_value;
        severity = current_severity;
        model.itemUpdated(PVTreeItem.this);
    }

    /** @return Returns a String. No really, it does! */
    @Override
    public String toString()
    {
        final StringBuffer b = new StringBuffer();
        b.append(info).append(" '").append(pv_name).append("'");
        if (type != null)
            b.append("  (").append(type).append(")");
        if (value == CONSTANT_VALUE)
            b.append(" ").append(CONSTANT_VALUE);
        else if (value != null)
            b.append("  =  ").append(value);
        else
            b.append(" [DISCONNECTED]");
        return b.toString();
    }
}
