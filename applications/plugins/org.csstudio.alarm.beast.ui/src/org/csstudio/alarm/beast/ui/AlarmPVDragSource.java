/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Helper for drag-and-drop support that handles dragging
 *  of Alarm PV info as text out of an alarm GUI element
 *  into other tools that accept text.
 *  @author Kay Kasemir
 */
public class AlarmPVDragSource implements DragSourceListener
{
    /** Provider of currently selected AlarmTreePV items */
    final private ISelectionProvider selection_provider;

    /** DND library DragSource */
    final private DragSource source;

    /** Currently selected Alarm PVs, updated when drag starts */
    final private ArrayList<AlarmTreePV> pvs = new ArrayList<AlarmTreePV>();

    /** Initialize
     *  @param control Control from which to support 'drag'
     *  @param selection_provider Provider of currently selected AlarmPV items
     */
    public AlarmPVDragSource(final Control control,
                             final ISelectionProvider selection_provider)
    {
        this.selection_provider = selection_provider;
        source = new DragSource(control, DND.DROP_COPY);
        source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
        source.addDragListener(this);
    }

    // @see DragSourceListener
    @Override
    @SuppressWarnings("unchecked")
    public void dragStart(final DragSourceEvent event)
    {
        // Get currently selected alarm PVs
        pvs.clear();
        final List<AlarmTreeItem> items =
            ((IStructuredSelection)selection_provider.getSelection()).toList();
        for (AlarmTreeItem item : items)
            if (item instanceof AlarmTreePV)
                pvs.add((AlarmTreePV) item);
        // Anything worth dragging?
        if (pvs.size() < 1)
            event.doit = false;
    }

    // @see DragSourceListener
    @Override
    public void dragSetData(final DragSourceEvent event)
    {
        if (TextTransfer.getInstance().isSupportedType(event.dataType))
        {
            final StringBuilder buf = new StringBuilder();
            for (AlarmTreePV pv : pvs)
                buf.append(pv.getVerboseDescription());
            event.data = buf.toString();
        }
    }

    // @see DragSourceListener
    @Override
    public void dragFinished(final DragSourceEvent event)
    {
        pvs.clear();
    }
}
