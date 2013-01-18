/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.dnd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;

/**
 * General purpose utility to allowing Drag-and-Drop "Drop" of any
 * adaptable or serializable object.
 * <p>
 * Filters the received items to match the desired type, based on the
 * order or preference specified. Can also accept plain text.
 *
 * @author Gabriele Carcassi
 * @author Kay Kasemir
 */
abstract public class ControlSystemDropTarget
{
    final private DropTarget target;

    /** Initialize 'drop' target
     *  @param control Control onto which items may be dropped
     *  @param accepted (Base) class of accepted items
     */
    public ControlSystemDropTarget(final Control control,
            final Class<?>... accepted)
    {
        target = new DropTarget(control, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);

        final List<Transfer> supportedTransfers = new ArrayList<Transfer>();
        for (Class<?> clazz : accepted)
        {
            if (clazz == String.class)
                supportedTransfers.add(TextTransfer.getInstance());
            if (clazz == File.class)
                supportedTransfers.add(FileTransfer.getInstance());
            else
            {
                final SerializableItemTransfer xfer = SerializableItemTransfer.getTransfer(clazz.getName());
                if (xfer != null)
                    supportedTransfers.add(xfer);
            }
        }
        target.setTransfer(supportedTransfers.toArray(new Transfer[supportedTransfers.size()]));

        target.addDropListener(new DropTargetAdapter()
        {
            /** Used internally by the system when a DnD operation enters the control.
             *  {@inheritDoc}
             */
            @Override
            public void dragEnter(final DropTargetEvent event)
            {
            	// Seems DropTarget it is not honoring the order of the transferData:
            	// Making sure is right
				boolean done = false;
            	for (Transfer transfer : target.getTransfer()) {
            		for (TransferData data : event.dataTypes) {
            			if (transfer.isSupportedType(data)) {
            				event.currentDataType = data;
            				done = true;
            				break;
            			}
            		}
            		if (done)
            			break;
            	}

                if ((event.operations & DND.DROP_COPY) != 0)
                    event.detail = DND.DROP_COPY;
                else
                    event.detail = DND.DROP_NONE;
            }

            /** Data was dropped into the target.
             *  Check the actual type, handle received data.
             */
            @Override
            public void drop(final DropTargetEvent event)
            {
            	handleDrop(event.data);
            }
        });
    }

    /** To be implemented by derived class.
     *
     *  Will be called for each 'dropped' item that
     *  has the accepted data type
     *
     *  @param item Control system item
     */
    abstract public void handleDrop(Object item);

}
