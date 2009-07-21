package org.csstudio.opibuilder.editor;

import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

/**
 * Patched Scrolling graphical viewer implementation.
 * 
 * @author Xihui Chen
 * 
 */
public class PatchedScrollingGraphicalViewer extends ScrollingGraphicalViewer {
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

}