/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Handler for receiving drag-and-drop text
 *  @author Kay Kasemir
 */
abstract public class TextAsPVDropTarget extends DropTargetAdapter
{
    final private DropTarget target;

    /** Initialize
     *  @param target Control that should receive 'dropped' items
     */
    public TextAsPVDropTarget(final Control target)
    {
        this.target = new DropTarget(target, DND.DROP_MOVE | DND.DROP_COPY);
        this.target.setTransfer(new Transfer[]
        {
            TextTransfer.getInstance()
        });
        this.target.addDropListener(this);
    }

    // DropTargetAdapter: Only allow copy
    @Override
    public void dragEnter(final DropTargetEvent event)
    {
        if ((event.operations & DND.DROP_COPY) != 0)
            event.detail = DND.DROP_COPY;
        else
            event.detail = DND.DROP_NONE;
    }

    // DropTargetAdapter: received dropped text
    @Override
    public void drop(final DropTargetEvent event)
    {
        if (event.data == null)
        {
            event.detail = DND.DROP_NONE;
            return;
        }
        final String name = ((String) event.data).trim();
        handleDrop(name);
    }
    
    /** Must be implemented to handle the received PV name
     *  @param name The string that was dropped, presumably a PV name
     */
    public abstract void handleDrop(String name);
}
