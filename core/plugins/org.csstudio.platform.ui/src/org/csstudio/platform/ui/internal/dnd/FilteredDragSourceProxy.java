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
package org.csstudio.platform.ui.internal.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.dnd.ICssDragSourceListener;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * This proxy class provides enhanced implementations for the methods
 * described by the <code>DragSourceListener</code> interface.
 * 
 * CSS clients, that use this adapter will benefit especially from the
 * possibility to filter the resource types that should be provided during a DnD
 * operation. An array of class types for resources that are derived from
 * {@link IControlSystemItem} can be applied as filter.
 * 
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * 
 * @see DragSourceListener
 * @see DragSourceEvent
 * @author Sven Wende, Stefan Hofer
 */
public final class FilteredDragSourceProxy extends DragSourceAdapter {

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
	public FilteredDragSourceProxy(final Class[] acceptedTypes,
			final ICssDragSourceListener dragSourceListener) {
		_dragSourceListener = dragSourceListener;
		_acceptedTypes = acceptedTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragStart(final DragSourceEvent event) {
		ControlSystemItemTransfer.getInstance().setSelectedItems(
				getFilteredSelection(_dragSourceListener.getCurrentSelection()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragSetData(final DragSourceEvent event) {
		List<IControlSystemItem> items = null;
		List currentSelection = _dragSourceListener.getCurrentSelection();

		if ((currentSelection != null) && (currentSelection.size() > 0)) {
			items = getFilteredSelection(currentSelection);
		} else {
			items = ControlSystemItemTransfer.getInstance().getSelectedItems();
		}

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
				sb.append("\n"); //$NON-NLS-1$
			}

			event.data = sb.toString();

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
			for (Class clazz : _acceptedTypes) {
				if (clazz.isAssignableFrom(item.getClass())) {
					filteredSelection.add((IControlSystemItem) item);
				}
			}
		}

		return filteredSelection;
	}	
}
