/**
 * 
 */
package org.csstudio.diag.epics.pvtree;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/** The PV Tree Model
 *  <p>
 *  This model currently has the TreeViewer as its single listener,
 *  so there is no full setup with listener interface, listener list etc.
 *  <p>
 *  Note that most of the logic is actually inside the PVTreeItem.
 *  @see PVTreeItem
 *  @author Kay Kasemir
 */
class PVTreeModel implements IStructuredContentProvider, ITreeContentProvider
{
    /** The view to which we are connected. */
    private final TreeViewer viewer;

    private PVTreeItem root;
    
    /** @param view */
    PVTreeModel(final TreeViewer viewer)
    {
        this.viewer = viewer;
        root = null;
    }
    
    /** Re-initialize the model with a new root PV. */
    public void setRootPV(final String name)
    {
        if (root != null)
        {
            root.dispose();
            root = null;
        }        
        root = new PVTreeItem(this, null, Messages.PV, name);
        itemChanged(root);
    }
    
    /** @return Returns a model item with given PV name or <code>null</code>. */
    public PVTreeItem findPV(final String pv_name)
    {
        return findPV(pv_name, root);
    }

    /** Searches for item from given item on down. */
    private PVTreeItem findPV(final String pv_name, final PVTreeItem item)
    {
        // Dead end?
        if (item == null)
            return null;
        // Is it this one?
        if (item.getName().equals(pv_name))
            return item;
        // Check each child recursively
        for (PVTreeItem child : item.getLinks())
        {
            final PVTreeItem found = findPV(pv_name, child);
            if (found != null)
                return found;
        }
        return null;
    }
    
    // IStructuredContentProvider
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
        // NOP
    }

    public void dispose()
    {
        if (root != null)
        {
            Plugin.getLogger().debug("PVTreeModel disposed"); //$NON-NLS-1$
            root.dispose();
            root = null;
        }
    }

    // IStructuredContentProvider
    public Object[] getElements(final Object parent)
    {
        if (parent instanceof PVTreeItem)
            return getChildren(parent);
        if (root != null)
            return new Object[] { root };
        return new Object[0];
    }

    // ITreeContentProvider
    public Object getParent(final Object child)
    {
        if (child instanceof PVTreeItem)
            return ((PVTreeItem) child).getParent();
        return null;
    }
    
    // ITreeContentProvider
    public Object[] getChildren(final Object parent)
    {
        if (parent instanceof PVTreeItem)
            return ((PVTreeItem) parent).getLinks();
        return new Object[0];
    }

    // ITreeContentProvider
    public boolean hasChildren(final Object parent)
    {
        if (parent instanceof PVTreeItem)
            return ((PVTreeItem) parent).hasLinks();
        return false;
    }

    /** Used by item to fresh the tree from the item on down. */
    public void itemUpdated(final PVTreeItem item)
    {
        if (viewer.getTree().isDisposed())
            return;
        viewer.update(item, null);
    }

    /** Used by item to fresh the tree from the item on down. */
    public void itemChanged(final PVTreeItem item)
    {
        if (viewer.getTree().isDisposed())
            return;
        if (item == root)
            viewer.refresh();
        else
            viewer.refresh(item);
        viewer.expandAll();
    }
}