package org.csstudio.platform.ui.dnd;

import java.util.List;

import org.eclipse.swt.dnd.DragSourceListener;

/**
 * Enhancement of the <code>DragSourceListener</code> interface. CSS clients
 * will have to provide implementations of this interface in order to attach DnD
 * functionality to controls.
 * 
 * @author Alexander Will
 */
public interface ICssDragSourceListener extends DragSourceListener {
	/**
	 * Subclasses must implement this method and should return the currently
	 * selected objects, e.g. the current selection of a TreeViewer.
	 * 
	 * @return the currently selected objects
	 */
	List getCurrentSelection();
}
