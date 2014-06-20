/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;

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
    private PV pv;
    
    final private PVListener value_listener = new PVListenerAdapter()
    {
        @Override
        public void valueChanged(final PV pv, final VType pv_value)
        {
            value = VTypeHelper.format(pv_value);
            severity = VTypeHelper.getSeverity(pv_value);
            model.itemUpdated(PVTreeItem.this);
        }

        @Override
        public void disconnected(final PV pv)
        {
            value = "Disconnected";
            severity = AlarmSeverity.UNDEFINED;
            model.itemUpdated(PVTreeItem.this);
        }
    };

    /** Most recent value. */
    private volatile String value = null;

    /** Most recent severity. */
    private volatile AlarmSeverity severity = AlarmSeverity.UNDEFINED;

    /** The PV used for getting the record type. */
    private PV type_pv;
    private String type;

    /** Array of fields to read for this record type.
     *  Fields are removed as they are read, so in the end this
     *  array will be empty
     */
    final private ArrayList<String> links_to_read = new ArrayList<String>();

    /** Used to read the links of this pv. */
    private PV link_pv = null;
    private String link_value;

    /** Tree item children, populated with info from the input links. */
    private ArrayList<PVTreeItem> links = new ArrayList<PVTreeItem>();

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

        Plugin.getLogger().log(Level.FINE,
                "New Tree item {0}, record name {1}",
                new Object[] { pv_name, record_name});


        // Now add this one, otherwise the previous call would have found 'this'.
        if (parent != null)
            parent.links.add(this);

        // Is this a link to follow, or just a constant to display?
        // Hardware links "@vme..." or constant numbers "-12.3"
        // cause us to stop here:
        if (! PVNameFilter.isPvName(pv_name))
        {
            pv = null;
            return;
        }

        // Try to read the pv
        try
        {
        	pv = createPV(pv_name, value_listener);
        }
        catch (Exception e)
        {
            Plugin.getLogger().log(Level.SEVERE, "PV creation error" , e);
        }
        
        // Avoid loops.
        // If the model already contains an entry with this name,
        // we simply display this new item, but we won't
        // follow its input links.
        // Behavior is not fully predictable:
        // When PV is in tree multiple times, instances receive their RTYP
        // into at various times. Once they have it, there are no more loops.
        // Until they have it, they'll look for it in parallel.
        final PVTreeItem other = model.findPV(pv_name);
        if (other != null  &&  other.type != null)
        {
            type = other.type;
            Plugin.getLogger().fine("Known item, not traversing inputs (again)");
            return;
        }
    	try
    	{
            final PVListener listener = new StringListener()
    	    {
				@Override
				public void handleText(final String text)
				{
    	            // type should be a text.
    	            // If it starts with a number, it's probably not an
    	            // EPICS record type but a simulated PV
    	            final char first_char = text.charAt(0);
    	            if (first_char >= 'a' && first_char <= 'z')
    	                type = text;
    	            else
    	                type = Messages.UnkownPVType;
    	            // Only need one update
    	            type_pv.removeListener(this);
    	            updateType();
				}
    	    };
            type_pv = createPV(record_name + ".RTYP", listener);
        }
        catch (Exception e)
        {
            Plugin.getLogger().log(Level.SEVERE, "PV.RTYP creation error" , e);
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

    /** @return PV for the given name */
    private PV createLinkPV(final String name) throws Exception
    {
    	final PVListener listener = new StringListener()
        {
			@Override
			public void handleText(final String text)
			{
				link_value = text;
                // The value could be
                // a) a record name followed by "... NPP NMS". Remove that.
                // b) a hardware input/output "@... " or "#...". Keep that.
                if (link_value.length() > 1 &&
                    link_value.charAt(0) != '@' &&
                    link_value.charAt(0) != '#')
                {
                    int i = link_value.indexOf(' ');
                    if (i > 0)
                        link_value = link_value.substring(0, i);
                }
                // Only one update
                link_pv.removeListener(this);
                updateLink();
			}
	    };
	    return createPV(name, listener);
    }

    /** Delete the type_pv */
    private void disposeTypePV()
    {
        if (type_pv != null)
        {
            PVPool.releasePV(type_pv);
            type_pv = null;
        }
    }

    /** Delete the link_pv */
    private void disposeLinkPV()
    {
        if (link_pv != null)
        {
            PVPool.releasePV(link_pv);
            link_pv = null;
        }
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

    /** @return Returns a String. No really, it does! */
    @Override
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(info);
        b.append(" '");
        b.append(pv_name);
        b.append("'");
        if (type != null)
        {
            b.append("  (");
            b.append(type);
            b.append(")");
        }
        if (value != null)
        {
            b.append("  =  ");
            b.append(value);
        }
        return b.toString();
    }

    /** Thread-safe handling of the 'type' update. */
    private void updateType()
    {
        Plugin.getLogger().log(Level.FINE,
                "{0} received type {1}", new Object[] { pv_name, type });
        // Already disposed?
        if (type_pv == null)
            return;
        // We got the type, so close the connection.
        disposeTypePV();
        // Display the received type of this record.
        model.itemChanged(PVTreeItem.this);

        links_to_read.clear();
        final List<String> fields = model.getFieldInfo().get(type);
        if (fields != null)
            links_to_read.addAll(fields);
        if (links_to_read.size() <= 0)
        {
            Plugin.getLogger().log(Level.FINE,
                    "{0} has unknown record type {1}", new Object[] { pv_name, type });
            return;
        }
        getNextLink();
    }

    /** Helper for reading next link from links_to_read. */
    private void getNextLink()
    {
        // Probably superflous, but can't hurt
        disposeLinkPV();
        // Any more links to read?
        if (links_to_read.size() <= 0)
            return;
        final String field = links_to_read.get(0);
        final String link_name = record_name + "." + field;
        try
        {
            link_pv = createLinkPV(link_name);
        }
        catch (Exception e)
        {
            Plugin.getLogger().log(Level.SEVERE, "PV." + field + " creation error" , e);
        }
    }

    /** Thread-safe handling of the 'link_pv' update. */
    private void updateLink()
    {
        if (link_pv == null)
        {
            Plugin.getLogger().log(Level.FINE,
                    "{0} already disposed", pv_name);
            return;
        }
        else
        {
        	Plugin.getLogger().log(Level.FINE,
                    "{0} received ''{1}''", new Object[] { link_pv.getName(), link_value });
        }
        disposeLinkPV();

        // Remove field for which we received update from
        // list of links to read
        if (links_to_read.size() <= 0)
        {
            Plugin.getLogger().log(Level.FINE,
                    "{0} update without active link?",link_pv.getName());
            return;
        }
        final String field = links_to_read.remove(0);
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
        getNextLink();
    }
}
