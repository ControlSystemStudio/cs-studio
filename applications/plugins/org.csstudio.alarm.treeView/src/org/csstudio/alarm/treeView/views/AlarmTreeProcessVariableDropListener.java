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
package org.csstudio.alarm.treeView.views;

import java.util.Queue;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableNameTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Provides support for dropping process variables into the alarm tree.
 */
public final class AlarmTreeProcessVariableDropListener implements TransferDropTargetListener {

    private final AlarmTreeView _alarmTreeView;
    private final Queue<ITreeModificationItem> _ldapModificationItems;

    /**
     * Constructor.
     * @param alarmTreeView
     * @param ldapModificationItems
     */
    public AlarmTreeProcessVariableDropListener(@Nonnull final AlarmTreeView alarmTreeView,
                                                @Nonnull final Queue<ITreeModificationItem> ldapModificationItems) {
        _alarmTreeView = alarmTreeView;
        _ldapModificationItems = ldapModificationItems;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public Transfer getTransfer() {
        return ProcessVariableNameTransfer.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled(@Nonnull final DropTargetEvent event) {
        return dropTargetIsSubtreeNode(event);
    }

    /**
     * Checks if the target of the drop operation is a SubtreeNode.
     */
    private boolean dropTargetIsSubtreeNode(@Nonnull final DropTargetEvent event) {
        return (event.item instanceof TreeItem)
                && (event.item.getData() instanceof SubtreeNode);
    }

    /**
     * {@inheritDoc}
     */
    public void dragEnter(@Nonnull final DropTargetEvent event) {
        // only copy is supported
        event.detail = event.operations & DND.DROP_COPY;
    }

    /**
     * {@inheritDoc}
     */
    public void dragOperationChanged(@Nonnull final DropTargetEvent event) {
        // only copy is supported
        event.detail = event.operations & DND.DROP_COPY;
    }

    /**
     * {@inheritDoc}
     */
    public void dragLeave(@Nonnull final DropTargetEvent event) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    public void dragOver(@Nonnull final DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
    }

    /**
     * {@inheritDoc}
     */
    public void dropAccept(@Nonnull final DropTargetEvent event) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    public void drop(@Nonnull final DropTargetEvent event) {
        final SubtreeNode parent = (SubtreeNode) event.item.getData();
        final IProcessVariable[] droppedPVs = (IProcessVariable[]) event.data;
        for (final IProcessVariable pv : droppedPVs) {
            // TODO (jpenning) This is slow for many pvs. Fix: Better give all pvs at once
            final ITreeModificationItem item = DirectoryEditor.createProcessVariableRecord(parent, pv.getName());
            if (item != null) {
                _ldapModificationItems.add(item);
            }
        }
        final TreeViewer viewer = _alarmTreeView.getViewer();
        if (viewer != null) {
            viewer.refresh(parent);
        } else {
            throw new IllegalStateException("Viewer of " + AlarmTreeView.class.getName() +
            " mustn't be null at this point.");
        }
    }
}
