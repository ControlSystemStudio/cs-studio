package org.csstudio.platform.ui.internal.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.dnd.ICssDragSourceListener;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * This proxy class provides compatibility implementations for the methods
 * described by the <code>DragSourceListener</code> interface that rely on
 * different transfer types for every dragged control system item type.
 * 
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * 
 * @author Alexander Will
 */
public final class InternalDragSourceProxy extends DragSourceAdapter {
	/**
	 * Contains all class types of resources, that will be provided during a DnD
	 * operation.
	 */
	private Class[] _acceptedTypes;

	/**
	 * The drag source listener, the calls are delegated to.
	 */
	private ICssDragSourceListener _dragSourceListener;
	
	/**
	 * Cache the offered selection.
	 */
	private List _selection;

	/**
	 * Constructs a drag source adapter, which only provides items during DnD,
	 * that are of one of the specified types. The only prerequisite for the
	 * class types is, that they have to be derived from
	 * {@link IControlSystemItem}.
	 * 
	 * @param acceptedTypes
	 *            The item types, that should be provided during DnD (need to be
	 *            derived from {@link IControlSystemItem}.
	 * @param dragSourceListener
	 *            The drag source listener, the calls are delegated to.
	 */
	public InternalDragSourceProxy(final Class[] acceptedTypes,
			final ICssDragSourceListener dragSourceListener) {
		_acceptedTypes = acceptedTypes;
		_dragSourceListener = dragSourceListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragStart(final DragSourceEvent event) {
		super.dragStart(event);
		_selection = _dragSourceListener.getCurrentSelection();

		if (_selection.size() < 1) {
			event.doit = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragSetData(final DragSourceEvent event) {
		List<IControlSystemItem> items = getFilteredSelection(_selection);

		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < items.size(); ++i) {
				if (i > 0) {
					buf.append(", "); //$NON-NLS-1$
				}
				buf.append(items.get(i).toString());
			}
			event.data = buf.toString();
		} else {
			event.data = items;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragFinished(final DragSourceEvent event) {
		_dragSourceListener.dragFinished(event);
	}

	/**
	 * @param fullSelection
	 *            A list with the complete selection.
	 * @return returns the currently selected items, that pass the class types
	 *         filter
	 */
	@SuppressWarnings("unchecked")
	private List<IControlSystemItem> getFilteredSelection(
			final List fullSelection) {
		List<IControlSystemItem> filteredSelection = new ArrayList<IControlSystemItem>();

		for (Object item : fullSelection) {
			boolean added = false;
			for (Class clazz : _acceptedTypes) {
				if (clazz.isAssignableFrom(item.getClass())) {
					if (!added) {
						filteredSelection.add((IControlSystemItem) item);
						added = true;
					}
				}
			}
		}

		return filteredSelection;
	}
}
