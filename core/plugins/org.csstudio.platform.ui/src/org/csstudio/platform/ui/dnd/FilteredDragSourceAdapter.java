package org.csstudio.platform.ui.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dnd.ControlSystemItemTransfer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * This adapter class provides enhanced implementations for the methods
 * described by the <code>DragSourceListener</code> interface.
 * 
 * CSS clients, that use this adapter will benefit especially from the
 * possibility to filter the resource types that should be provided during a
 * DnD operation. An array of class types for resources that are derived from
 * {@link IControlSystemItem} can be applied as filter.
 * 
 * <p>
 * Classes that wish to deal with <code>DragSourceEvent</code>s can extend
 * this class and override only the methods which they are interested in.
 * </p>
 * 
 * @see DragSourceListener
 * @see DragSourceEvent
 * @author Sven Wende, Stefan Hofer
 */
public abstract class FilteredDragSourceAdapter extends DragSourceAdapter {
	/**
	 * Contains all class types of resources, that will be provided during a DnD
	 * operation.
	 */
	private Class[] _acceptedTypes;

	/**
	 * Constructs a drag source adapter, which only provides items during DnD,
	 * that are of one of the specified types. The only prerequisite for the
	 * class types is, that they have to be derived from
	 * {@link IControlSystemItem}.
	 * 
	 * @param acceptedTypes
	 *            The item types, that should be provided during DnD (need to be
	 *            derived from {@link IControlSystemItem}.
	 */
	public FilteredDragSourceAdapter(final Class[] acceptedTypes) {
		// check filters first
		for(Class clazz : acceptedTypes) {
			if(!IControlSystemItem.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("Drag&Drop Filter >>"+clazz.getName() +"<< is not derived from IControlSystemItem.");
			}
		}
		_acceptedTypes = acceptedTypes;
	}

	/**
	 * Subclasses must implement this method and should return the
	 * currently selected objects, e.g. the current selection of a TreeViewer.
	 * 
	 * @return the currently selected objects
	 */
	protected abstract List getCurrentSelection();

	/**
	 * @return returns the currently selected items, that pass the class types
	 *         filter
	 */
	private List<IControlSystemItem> getFilteredSelection() {
		List<IControlSystemItem> filteredSelection = new ArrayList<IControlSystemItem>();

		List fullSelection = getCurrentSelection();

		for (Object item : fullSelection) {
			for (Class clazz : _acceptedTypes) {
				if (clazz.isAssignableFrom(item.getClass())) {
					filteredSelection.add((IControlSystemItem) item);
				}
			}
		}

		return filteredSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragSetData(final DragSourceEvent event) {
		List<IControlSystemItem> items = getFilteredSelection();

		if (ControlSystemItemTransfer.getInstance().isSupportedType(
				event.dataType)) {
			event.data = items;
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			StringBuffer sb = new StringBuffer();
			// concatenate a String, which contains items line by line
			for (IControlSystemItem item : items) {
				String path = CentralItemFactory.createControlSystemItemPath(
						item).toPortableString();
				sb.append(path);
				sb.append("\n");
			}

			event.data = sb.toString();

		}
	}
}
