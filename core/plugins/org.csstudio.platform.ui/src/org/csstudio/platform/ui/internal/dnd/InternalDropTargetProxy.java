package org.csstudio.platform.ui.internal.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.platform.ui.dnd.ICssDropTargetListener;
import org.csstudio.platform.util.ControlSystemItemPath;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * This proxy class provides compatibility implementations for the methods
 * described by the <code>DropTargetAdapter</code> interface that rely on
 * different transfer types for every dragged control system item type.
 * 
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * 
 * @author Alexander Will
 */
public final class InternalDropTargetProxy extends DropTargetAdapter {
	/**
	 * Contains all class types of resources, that will be provided during a DnD
	 * operation.
	 */
	private Class[] _acceptedTypes;

	/**
	 * The drop target listener, the calls are delegated to.
	 */
	private ICssDropTargetListener _dropTargetListener;

	/**
	 * Constructs a drag source adapter, which only provides items during DnD,
	 * that are of one of the specified types. The only prerequisite for the
	 * class types is, that they have to be derived from
	 * {@link IControlSystemItem}.
	 * 
	 * @param acceptedTypes
	 *            The item types, that should be provided during DnD (need to be
	 *            derived from {@link IControlSystemItem}.
	 * @param dropTargetListener
	 *            The drop target listener, the calls are delegated to.
	 */
	public InternalDropTargetProxy(final Class[] acceptedTypes,
			final ICssDropTargetListener dropTargetListener) {
		_acceptedTypes = acceptedTypes;
		_dropTargetListener = dropTargetListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drop(final DropTargetEvent event) {
		List<IControlSystemItem> providedItems = new ArrayList<IControlSystemItem>();
		
		// handle text transfers
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String tmp = (String) TextTransfer.getInstance().nativeToJava(
					event.currentDataType);

			String[] rows = tmp.split("\n"); //$NON-NLS-1$

			for (String row : rows) {
				ControlSystemItemPath path = ControlSystemItemPath
						.createFromPortableString(row);

				IControlSystemItem item = CentralItemFactory
						.createControlSystemItem(path);

				if (item != null) {
					providedItems.add(item);
				}
			}
		} else if (ProcessVariableWithArchiveTransfer.getInstance()
				.isSupportedType(event.currentDataType)) {
			IProcessVariableWithArchive[] items = (IProcessVariableWithArchive[]) ProcessVariableWithArchiveTransfer
					.getInstance().nativeToJava(event.currentDataType);

			for (IProcessVariableWithArchive item : items) {
				if (item != null) {
					providedItems.add(item);
				}
			}
		} else if (ProcessVariableTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			IProcessVariable[] items = (IProcessVariable[]) ProcessVariableTransfer
					.getInstance().nativeToJava(event.currentDataType);

			for (IProcessVariable item : items) {
				if (item != null) {
					providedItems.add(item);
				}
			}
		} else if (ArchiveDataSourceTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			IArchiveDataSource[] items = (IArchiveDataSource[]) ArchiveDataSourceTransfer
					.getInstance().nativeToJava(event.currentDataType);

			for (IArchiveDataSource item : items) {
				if (item != null) {
					providedItems.add(item);
				}
			}
		} else if (ControlSystemItemTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			IControlSystemItem[] items = (IControlSystemItem[]) ControlSystemItemTransfer
					.getInstance().nativeToJava(event.currentDataType);

			for (IControlSystemItem item : items) {
				if (item != null) {
					providedItems.add(item);
				}
			}
		}

		List<IControlSystemItem> filteredItems = getFilteredItems(providedItems);
		_dropTargetListener.doDrop(filteredItems);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragEnter(final DropTargetEvent event) {
		if ((event.operations & DND.DROP_COPY) != 0) {
			event.detail = DND.DROP_COPY;
		} else {
			event.detail = DND.DROP_NONE;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragLeave(final DropTargetEvent event) {
		_dropTargetListener.dragLeave(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragOperationChanged(final DropTargetEvent event) {
		_dropTargetListener.dragOperationChanged(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragOver(final DropTargetEvent event) {
		_dropTargetListener.dragOver(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dropAccept(final DropTargetEvent event) {
		_dropTargetListener.dropAccept(event);
	}

	/**
	 * Gets all items from the drop target event, that pass the type class
	 * filter.
	 * 
	 * @param providedItems
	 *            the items, that are about to be dropped
	 * @return all items, that pass the filter
	 */
	@SuppressWarnings("unchecked")
	private List<IControlSystemItem> getFilteredItems(
			final List<IControlSystemItem> providedItems) {

		List<IControlSystemItem> filteredItems = new ArrayList<IControlSystemItem>();

		// handle transfers of control system items

		for (IControlSystemItem item : providedItems) {
			boolean added = false;
			for (Class clazz : _acceptedTypes) {
				if (clazz.isAssignableFrom(item.getClass())) {
					if (!added) {
						filteredItems.add(item);
						added = true;
					}
				}
			}
		}

		return filteredItems;
	}
}
