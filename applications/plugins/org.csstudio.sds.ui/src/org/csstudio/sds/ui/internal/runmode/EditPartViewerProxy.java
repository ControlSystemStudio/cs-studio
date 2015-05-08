package org.csstudio.sds.ui.internal.runmode;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.SelectionManager;
import org.eclipse.gef.dnd.TransferDragSourceListener;
import org.eclipse.gef.dnd.TransferDropTargetListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A proxy for an {@link EditPartViewer}. The class is only used in a
 * workarround to ensure that the wrapped EditPartViewer gets garbage
 * collected when the shell is disposed.
 *
 * @author Sven Wende
 */
public class EditPartViewerProxy implements EditPartViewer {
    private EditPartViewer _delegate;

    public EditPartViewerProxy(EditPartViewer delegate) {
        super();
        _delegate = delegate;
    }

    /**
     * Forge
     */
    public void dispose() {
        _delegate = null;
    }

    public void addDragSourceListener(TransferDragSourceListener listener) {
        if (_delegate != null) {
            _delegate.addDragSourceListener(listener);
        }
    }

    public void addDragSourceListener(org.eclipse.jface.util.TransferDragSourceListener listener) {
        if (_delegate != null) {
            _delegate.addDragSourceListener(listener);
        }

    }

    public void addDropTargetListener(TransferDropTargetListener listener) {
        if (_delegate != null) {
            _delegate.addDropTargetListener(listener);
        }

    }

    public void addDropTargetListener(org.eclipse.jface.util.TransferDropTargetListener listener) {
        if (_delegate != null) {
            _delegate.addDropTargetListener(listener);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (_delegate != null) {
            _delegate.addPropertyChangeListener(listener);
        }
    }

    public void appendSelection(EditPart editpart) {
        if (_delegate != null) {
            _delegate.appendSelection(editpart);
        }
    }

    public Control createControl(Composite composite) {
        return _delegate != null ? _delegate.createControl(composite) : null;
    }

    public void deselect(EditPart editpart) {
        if (_delegate != null) {
            _delegate.deselect(editpart);
        }

    }

    public void deselectAll() {
        if (_delegate != null) {
            _delegate.deselectAll();
        }
    }

    public EditPart findObjectAt(Point location) {
        return _delegate != null ? _delegate.findObjectAt(location) : null;
    }

    public EditPart findObjectAtExcluding(Point location, Collection exclusionSet) {
        return _delegate != null ? _delegate.findObjectAtExcluding(location, exclusionSet) : null;
    }

    public EditPart findObjectAtExcluding(Point location, Collection exclusionSet, Conditional conditional) {
        return _delegate != null ? _delegate.findObjectAtExcluding(location, exclusionSet, conditional) : null;
    }

    public void flush() {
        if (_delegate != null) {
            _delegate.flush();
        }
    }

    public EditPart getContents() {
        return _delegate != null ? _delegate.getContents() : null;
    }

    public MenuManager getContextMenu() {
        return _delegate != null ? _delegate.getContextMenu() : null;
    }

    public Control getControl() {
        return _delegate != null ? _delegate.getControl() : null;
    }

    public EditDomain getEditDomain() {
        return _delegate != null ? _delegate.getEditDomain() : null;
    }

    public EditPartFactory getEditPartFactory() {
        return _delegate != null ? _delegate.getEditPartFactory() : null;
    }

    public Map getEditPartRegistry() {
        return _delegate != null ? _delegate.getEditPartRegistry() : null;
    }

    public EditPart getFocusEditPart() {
        return _delegate != null ? _delegate.getFocusEditPart() : null;
    }

    public KeyHandler getKeyHandler() {
        return _delegate != null ? _delegate.getKeyHandler() : null;
    }

    public Object getProperty(String key) {
        return _delegate != null ? _delegate.getProperty(key) : null;
    }

    public ResourceManager getResourceManager() {
        return _delegate != null ? _delegate.getResourceManager() : null;
    }

    public RootEditPart getRootEditPart() {
        return _delegate != null ? _delegate.getRootEditPart() : null;
    }

    public List getSelectedEditParts() {
        return _delegate != null ? _delegate.getSelectedEditParts() : null;
    }

    public ISelection getSelection() {
        return _delegate != null ? _delegate.getSelection() : null;
    }

    public SelectionManager getSelectionManager() {
        return _delegate != null ? _delegate.getSelectionManager() : null;
    }

    public Map getVisualPartMap() {
        return _delegate != null ? _delegate.getVisualPartMap() : null;
    }

    public void registerAccessibleEditPart(AccessibleEditPart acc) {
        if (_delegate != null) {
            _delegate.registerAccessibleEditPart(acc);
        }
    }

    public void removeDragSourceListener(TransferDragSourceListener listener) {
        if (_delegate != null) {
            _delegate.removeDragSourceListener(listener);
        }
    }

    public void removeDragSourceListener(org.eclipse.jface.util.TransferDragSourceListener listener) {
        if (_delegate != null) {
            _delegate.removeDragSourceListener(listener);
        }
    }

    public void removeDropTargetListener(TransferDropTargetListener listener) {
        if (_delegate != null) {
            _delegate.removeDropTargetListener(listener);
        }
    }

    public void removeDropTargetListener(org.eclipse.jface.util.TransferDropTargetListener listener) {
        if (_delegate != null) {
            _delegate.removeDropTargetListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (_delegate != null) {
            _delegate.removePropertyChangeListener(listener);
        }
    }

    public void reveal(EditPart editpart) {
        if (_delegate != null) {
            _delegate.reveal(editpart);
        }
    }

    public void select(EditPart editpart) {
        if (_delegate != null) {
            _delegate.select(editpart);
        }
    }

    public void setContents(EditPart editpart) {
        if (_delegate != null) {
            _delegate.setContents(editpart);
        }
    }

    public void setContents(Object contents) {
        if (_delegate != null) {
            _delegate.setContents(contents);
        }
    }

    public void setContextMenu(MenuManager contextMenu) {
        if (_delegate != null) {
            _delegate.setContextMenu(contextMenu);
        }
    }

    public void setControl(Control control) {
        if (_delegate != null) {
            _delegate.setControl(control);
        }
    }

    public void setCursor(Cursor cursor) {
        if (_delegate != null) {
            _delegate.setCursor(cursor);
        }
    }

    public void setEditDomain(EditDomain domain) {
        if (_delegate != null) {
            _delegate.setEditDomain(domain);
        }
    }

    public void setEditPartFactory(EditPartFactory factory) {
        if (_delegate != null) {
            _delegate.setEditPartFactory(factory);
        }
    }

    public void setFocus(EditPart focus) {
        if (_delegate != null) {
            _delegate.setFocus(focus);
        }
    }

    public void setKeyHandler(KeyHandler keyHandler) {
        if (_delegate != null) {
            _delegate.setKeyHandler(keyHandler);
        }
    }

    public void setProperty(String propertyName, Object value) {
        if (_delegate != null) {
            _delegate.setProperty(propertyName, value);
        }
    }

    public void setRootEditPart(RootEditPart root) {
        if (_delegate != null) {
            _delegate.setRootEditPart(root);
        }
    }

    public void setRouteEventsToEditDomain(boolean value) {
        if (_delegate != null) {
            _delegate.setRouteEventsToEditDomain(value);
        }
    }

    public void setSelectionManager(SelectionManager manager) {
        if (_delegate != null) {
            _delegate.setSelectionManager(manager);
        }
    }

    public void unregisterAccessibleEditPart(AccessibleEditPart acc) {
        if (_delegate != null) {
            _delegate.unregisterAccessibleEditPart(acc);
        }
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        if (_delegate != null) {
            _delegate.addSelectionChangedListener(listener);
        }
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        if (_delegate != null) {
            _delegate.removeSelectionChangedListener(listener);
        }
    }

    public void setSelection(ISelection selection) {
        if (_delegate != null) {
            _delegate.setSelection(selection);
        }
    }

}