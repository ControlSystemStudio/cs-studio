/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.ui.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dnd.ControlSystemItemTransfer;
import org.csstudio.platform.util.ControlSystemItemPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.dnd.DND;
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
 * 
 * @deprecated
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
				throw new IllegalArgumentException("Drag&Drop Filter >>" //$NON-NLS-1$
						+ clazz.getName()
						+ "<< is not derived from IControlSystemItem."); //$NON-NLS-1$
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
			for (Class clazz : _acceptedTypes) {
				if (clazz.isAssignableFrom(item.getClass())) {
					filteredItems.add(item);
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
		List<IControlSystemItem> providedItems = new ArrayList<IControlSystemItem>();

		// handle transfers of control system items
		if (ControlSystemItemTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			IControlSystemItem[] items = (IControlSystemItem[]) event.data; 

			for (IControlSystemItem item : items) {
				if (item != null) {
					providedItems.add(item);
				}
			}
		}

		// handle text transfers
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String tmp = (String)event.data;

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

		}

		List<IControlSystemItem> filteredItems = getFilteredItems(providedItems);
		doDrop(filteredItems);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragEnter(final DropTargetEvent event) {
		List<IControlSystemItem> providedItems = new ArrayList<IControlSystemItem>();

		/*
		 * To be able to provide the right drop feedback, handling needs to be
		 * platform specific here.
		 * 
		 * For Windows operating systems, the .data can be accessed early.
		 * 
		 * For other operating systems (Mac, Linux) a workarround is applied,
		 * which works on the latest local selection. This does work within the
		 * same Eclipse instance.
		 */
		if (!Platform.getOS().equals(Platform.OS_WIN32)) {
			if (ControlSystemItemTransfer.getInstance().isSupportedType(
					event.currentDataType)) {
				// get the current items that are about to be dropped as local
				// objects
				List<IControlSystemItem> items = ControlSystemItemTransfer
						.getInstance().getSelectedItems();

				if (items == null) {
					// this should happen only in a few cases -> in this case,
					// we do nothing about the dropped data and can only show a
					// "OK" icon for the current transfer
					providedItems = null;
				} else {
					for (IControlSystemItem item : items) {
						if (item != null) {
							providedItems.add(item);
						}
					}
				}
			} else {
				// Since we don't know anything about the dragged data, we will
				// not change the DND feedback icon. Per default, an "OK" icon
				// will be shown.
				providedItems = null;
			}
		} else {
			// on Windows systems the data can be accessed early
			if (ControlSystemItemTransfer.getInstance().isSupportedType(
					event.currentDataType)) {
				IControlSystemItem[] items = (IControlSystemItem[]) ControlSystemItemTransfer
						.getInstance().nativeToJava(event.currentDataType);

				if (items != null) {
					for (IControlSystemItem item : items) {
						if (item != null) {
							providedItems.add(item);
						}
					}
				}
			} else if (TextTransfer.getInstance().isSupportedType(
					event.currentDataType)) {
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
			}
		}

		if (providedItems != null) {
			List<IControlSystemItem> filteredItems = getFilteredItems(providedItems);

			if (filteredItems.size() == 0) {
				event.detail = DND.DROP_NONE;
			}
		}
	}

}
