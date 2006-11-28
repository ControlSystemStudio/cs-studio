package org.csstudio.platform.ui.dnd;

import java.util.List;

import org.csstudio.platform.model.IControlSystemItem;
import org.eclipse.swt.dnd.DropTargetListener;

/**
 * Enhancement of the <code>DropTargetListener</code> interface. CSS clients
 * will have to provide implementations of this interface in order to attach DnD
 * functionality to controls.
 * 
 * @author Alexander Will
 */
public interface ICssDropTargetListener extends DropTargetListener {
	/**
	 * This method is called after a successfull drop operaton. It provides the
	 * ability for subclasses to process the dropped control system items, that
	 * passed the filter. Use the <i>instanceOf</i> operator, to distinguish
	 * between the different types that are possible if you specified more than
	 * one type in your filter.
	 * 
	 * @param items
	 *            control system items, that were dropped
	 */
	void doDrop(List<IControlSystemItem> items);
}
