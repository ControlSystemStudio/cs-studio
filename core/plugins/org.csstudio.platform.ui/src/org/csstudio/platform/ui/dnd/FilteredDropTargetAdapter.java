package org.csstudio.platform.ui.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dnd.ControlSystemItemTransfer;
import org.csstudio.platform.util.ControlSystemItemPath;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * This adapter class provides enhanced implementations for the methods
 * described by the <code>DropTargetListener</code> interface.
 * 
 * CSS clients that use this adapter will benefit especially from the
 * possibility to filter the resource types, that should be provided during a
 * DnD operation. An array of class types for resources, that are derived from
 * {@link IControlSystemItem} can be applied as filter.
 * 
 * <p>
 * Classes that wish to deal with <code>DropTargetEvent</code>s can extend
 * this class and override only the methods which they are interested in.
 * </p>
 * 
 * @see DragSourceListener
 * @see DragSourceEvent
 * @author Sven Wende, Stefan Hofer
 */
public abstract class FilteredDropTargetAdapter extends DropTargetAdapter {
	/**
	 * Contains all class types of resources, that will be provided during a DnD
	 * operation.
	 */
	private Class[] _acceptedTypes;

	/**
	 * Constructs a drop target adapter, which only accepts items during DnD,
	 * that are of one of the specified types. The only prerequisite for the
	 * class types is, that they have to be derived from
	 * {@link IControlSystemItem}.
	 * 
	 * @param acceptedTypes
	 *            the item types, that should be provided during DnD (need to be
	 *            derived from {@link IControlSystemItem}
	 */
	public FilteredDropTargetAdapter(final Class[] acceptedTypes) {
		// check filters first
		for (Class clazz : acceptedTypes) {
			if (!IControlSystemItem.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("Drag&Drop Filter >>"
						+ clazz.getName()
						+ "<< is not derived from IControlSystemItem.");
			}
		}
		_acceptedTypes = acceptedTypes;
	}

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
	protected abstract void doDrop(List<IControlSystemItem> items);

	/**
	 * Gets all items from the drop target event, that pass the type class
	 * filter.
	 * 
	 * @param event
	 *            the event
	 * @return all items, that pass the filter
	 */
	private List<IControlSystemItem> getFilteredItems(
			final DropTargetEvent event) {
		List<IControlSystemItem> filteredItems = new ArrayList<IControlSystemItem>();

		// handle transfers of control system items
		if (ControlSystemItemTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			IControlSystemItem[] items = (IControlSystemItem[]) ControlSystemItemTransfer
					.getInstance().nativeToJava(event.currentDataType);

			for (IControlSystemItem item : items) {
				for (Class clazz : _acceptedTypes) {
					if (clazz.isAssignableFrom(item.getClass())) {
						filteredItems.add(item);
					}
				}
			}
		}

		// handle text transfers
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String tmp = (String) TextTransfer.getInstance().nativeToJava(
					event.currentDataType);

			String[] rows = tmp.split("\n");

			for (String row : rows) {
				ControlSystemItemPath path = ControlSystemItemPath
						.createFromPortableString(row);

				IControlSystemItem item = CentralItemFactory
						.createControlSystemItem(path);

				if (item != null) {
					for (Class clazz : _acceptedTypes) {
						if (clazz.isAssignableFrom(item.getClass())) {
							filteredItems.add(item);
						}
					}
				}
			}

		}

		return filteredItems;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drop(final DropTargetEvent event) {
		List<IControlSystemItem> filteredItems = getFilteredItems(event);
		doDrop(filteredItems);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragEnter(final DropTargetEvent event) {
		List<IControlSystemItem> filteredItems = getFilteredItems(event);

		if (filteredItems.size() == 0) {
			event.detail = DND.DROP_NONE;
		}
	}
}