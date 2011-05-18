/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.alarm.treeview.views;

import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * Provides drag support for the alarm tree for drag and drop of structural nodes. Drag and drop
 * of structural nodes uses the LocalSelectionTransfer.
 */
public final class AlarmTreeLocalSelectionDragListener implements TransferDragSourceListener {

    private final AlarmTreeView _alarmTreeView;

    /**
     * Constructor.
     * @param alarmTreeView view
     */
    public AlarmTreeLocalSelectionDragListener(@Nonnull final AlarmTreeView alarmTreeView) {
        _alarmTreeView = alarmTreeView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Transfer getTransfer() {
        return LocalSelectionTransfer.getTransfer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragStart(@Nonnull final DragSourceEvent event) {
        final TreeViewer viewer = _alarmTreeView.getViewer();
        if (viewer != null) {
            final List<IAlarmTreeNode> selectedNodes = AlarmTreeView.selectionToNodeList(viewer.getSelection());
            event.doit = canDrag(selectedNodes);
            if (event.doit) {
                LocalSelectionTransfer.getTransfer().setSelection(viewer.getSelection());
            }
        } else {
            throw new IllegalStateException("Viewer of " + AlarmTreeView.class.getName() +
            " mustn't be null at this point.");
        }
    }

    /**
     * Returns whether the given list of nodes can be dragged. The nodes can be dragged if they
     * are all children of the same parent node.
     */
    private boolean canDrag(@Nonnull final List<IAlarmTreeNode> nodes) {
        if (nodes.isEmpty()) {
            return false;
        }
        final IAlarmSubtreeNode firstParent = nodes.get(0).getParent();
        for (final IAlarmTreeNode node : nodes) {
            if (node.getParent() != firstParent) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragSetData(@Nonnull final DragSourceEvent event) {
        final TreeViewer viewer = _alarmTreeView.getViewer();
        if (viewer != null) {
            LocalSelectionTransfer.getTransfer().setSelection(viewer.getSelection());
        } else {
            throw new IllegalStateException("Viewer of " + AlarmTreeView.class.getName() +
            " mustn't be null at this point.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragFinished(@Nonnull final DragSourceEvent event) {
        LocalSelectionTransfer.getTransfer().setSelection(null);
    }
}
