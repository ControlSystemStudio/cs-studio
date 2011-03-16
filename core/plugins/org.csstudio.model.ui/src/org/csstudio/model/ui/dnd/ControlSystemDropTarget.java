/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.model.ui.dnd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Utility for allowing Drag-and-Drop "Drop" of Control System Items.
 *
 *  Filters the received items to match the desired type for CSS.
 *  Can also accept plain text.
 *
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
abstract public class ControlSystemDropTarget
{
    final private DropTarget target;

    /** Initialize 'drop' target
     *  @param control Control onto which items may be dropped
     *  @param accepted (Base) class of accepted items
     */
    @SuppressWarnings("rawtypes")
    public ControlSystemDropTarget(final Control control,
            final Class... accepted)
    {
        target = new DropTarget(control, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);

        List<Transfer> supportedTransfers = new ArrayList<Transfer>();
        supportedTransfers.addAll(Arrays.asList(SerializableItemTransfer.getTransfers(accepted)));
        if (Arrays.asList(accepted).contains(String.class))
        {
        	supportedTransfers.add(TextTransfer.getInstance());
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
