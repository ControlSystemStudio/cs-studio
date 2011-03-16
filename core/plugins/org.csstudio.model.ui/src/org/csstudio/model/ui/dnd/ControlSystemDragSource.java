/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.model.ui.dnd;

import org.csstudio.model.ControlSystemObjectAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Utility for allowing Drag-and-Drop "Drag" of Control System Items.
 *
 *  Provides data as control system item as well as string for non-CSS targets.
 *
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
abstract public class ControlSystemDragSource
{
    /** Initialize 'drag' source
     *  @param control Control from which control system items may be dragged
     */
    public ControlSystemDragSource(final Control control)
    {
        final DragSource source = new DragSource(control, DND.DROP_COPY);

        source.addDragListener(new DragSourceAdapter()
        {
        	@Override
        	public void dragStart(DragSourceEvent event)
        	{
        	    final Object selection = getSelection();

        		if (selection == null)
        		{   // No selection, don't start the drag
        			event.doit = false;
        			return;
        		}

        		// Calculate the transfer types:
        		source.setTransfer(supportedTransfers(selection));
        	}

            @Override
            public void dragSetData(final DragSourceEvent event)
            {   // Drag has been performed, provide data
            	final Object selection = getSelection();
            	for (Transfer transfer : supportedTransfers(selection))
            	{
            		if (transfer.isSupportedType(event.dataType))
            		{
            			if (transfer instanceof SerializableItemTransfer)
            			{
            			    final SerializableItemTransfer objectTransfer =
            			        (SerializableItemTransfer) transfer;
            				event.data = ControlSystemObjectAdapter.convert(selection, objectTransfer.getTragetClass());
            			}
            			else if (transfer instanceof TextTransfer)
            			{   // TextTransfer needs String
            				event.data = selection.toString();
            			}
            		}
            	}
            }
        });
    }

    /** @param selection Object to be transferred
     *  @return {@link Transfer}s for that Object
     */
    private static Transfer[] supportedTransfers(final Object selection)
    {
        // Obtain types that can be transferred via serialization
		final Class<?>[] types = ControlSystemObjectAdapter.getSerializableTypes(selection);
		// Get transfers for those types
		final Transfer[] serializedTransfers = SerializableItemTransfer.getTransfers(types);
		// Add TextTransfer to the list
		final Transfer[] supportedTransfers = new Transfer[serializedTransfers.length + 1];
		for (int i=0; i<serializedTransfers.length; ++i)
		    supportedTransfers[i] = serializedTransfers[i];
		supportedTransfers[serializedTransfers.length] = TextTransfer.getInstance();
		return supportedTransfers;
    }

    /** To be implemented by derived class:
     *  Provide the control system items that should be 'dragged'
     *  from this drag source
     *  @return Control system item(s)
     */
    abstract public Object getSelection();
}
