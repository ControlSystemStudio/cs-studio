/**
 *
 */
package org.csstudio.sds.ui.internal.viewer;

import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * Patched graphical viewer implementation.
 *
 * @author swende
 *
 */
public class PatchedGraphicalViewer extends ScrollingGraphicalViewer {
    private MenuManager contextMenu;

    /**
     * The original implementation in
     * {@link GraphicalViewerImpl#setContextMenu(MenuManager)} registers a menu
     * listener on the context menu. This causes a memory leak, because that
     * listener is never removed.
     */
    @Override
    public void setContextMenu(MenuManager manager) {
        // code from AbstractEditPartViewer base class (=super.super)
        if (contextMenu != null) {
            contextMenu.dispose();
        }

        contextMenu = manager;

        if (getControl() != null && !getControl().isDisposed()) {
            getControl().setMenu(contextMenu.createContextMenu(getControl()));
        }

        // code from GraphicalViewerImpl (=super)

        // ... is left out

        // ... and rewritten here
        if (contextMenu != null) {
            final IMenuListener menuListener = new IMenuListener() {
                public void menuAboutToShow(IMenuManager manager) {
                    flush();
                }
            };

            contextMenu.addMenuListener(menuListener);

            final Control control = getControl();

            if (control != null) {
                control.addDisposeListener(new DisposeListener() {
                    public void widgetDisposed(DisposeEvent e) {
                        contextMenu.removeMenuListener(menuListener);
                        control.removeDisposeListener(this);
                    }
                });
            }
        }
    }

    /**
     * Patch selections. Return amended selections that keep only weak
     * references to their underlying objects. This is part of a workaround for
     * a memory leak which is caused by popup menu actions that are contributed
     * via extension point 'org.eclipse.ui.popupMenus' and as
     * 'objectContribution'. Those actions will references the latest workbench
     * selection for as long as a new selection occurs (see
     * {@link ObjectPluginAction#selectionChanged(ISelection)}. In certain
     * situations this behaviour prevents garbage collection of SDS displays.
     */
    @Override
    public ISelection getSelection() {
        IStructuredSelection selection = (IStructuredSelection) super
                .getSelection();

        if (selection != null) {
            return new WeakStructuredSelection(selection);
        } else {
            return null;
        }
    }

}