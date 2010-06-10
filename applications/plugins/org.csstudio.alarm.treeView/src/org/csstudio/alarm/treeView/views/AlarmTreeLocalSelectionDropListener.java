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

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Drop Listener for the AlarmTreeView
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 09.06.2010
 */
public final class AlarmTreeLocalSelectionDropListener implements TransferDropTargetListener {

    private final AlarmTreeView _alarmTreeView;

    /**
     * Constructor.
     * @param alarmTreeView
     */
    public AlarmTreeLocalSelectionDropListener(@Nonnull final AlarmTreeView alarmTreeView) {
        _alarmTreeView = alarmTreeView;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public Transfer getTransfer() {
        return LocalSelectionTransfer.getTransfer();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled(@Nonnull final DropTargetEvent event) {
        final ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        return dropTargetIsSubtreeNode(event) && canDrop(selection, event);
    }

    /**
     * Checks if the target of the drop operation is a SubtreeNode.
     */
    private boolean dropTargetIsSubtreeNode(@Nonnull final DropTargetEvent event) {
        return (event.item instanceof TreeItem)
                && (event.item.getData() instanceof SubtreeNode);
    }

    /**
     * Checks if the given selection can be dropped into the alarm tree. The selection can be
     * dropped if all of the selected items are alarm tree nodes and the drop target is not one
     * of the nodes or a child of one of the nodes. (The dragged items must also all share a
     * common parent, but this is already checked in the drag listener.)
     */
    private boolean canDrop(@Nonnull final ISelection selection, @Nonnull final DropTargetEvent event) {
        final SubtreeNode dropTarget = (SubtreeNode) event.item.getData();
        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection s = (IStructuredSelection) selection;
            for (final Iterator<?> i = s.iterator(); i.hasNext();) {
                final Object o = i.next();
                if (o instanceof IAlarmTreeNode) {
                    if ( (o == dropTarget) || isChild(dropTarget, (IAlarmTreeNode) o)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Returns whether the first node is a direct or indirect child of the second node.
     */
    private boolean isChild(@Nonnull final IAlarmSubtreeNode directParent, @Nonnull final IAlarmTreeNode parent) {
        final IAlarmSubtreeNode parentParent = directParent.getParent();
        if (parentParent == null) {
            return false;
        }
        if (parentParent == parent) {
            return true;
        }
        return isChild(parentParent, parent);
    }

    /**
     * {@inheritDoc}
     */
    public void dragEnter(@Nonnull final DropTargetEvent event) {
        // EMPTY
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
        final SubtreeNode dropTarget = (SubtreeNode) event.item.getData();
        final List<IAlarmTreeNode> droppedNodes = AlarmTreeView.selectionToNodeList(LocalSelectionTransfer
                .getTransfer().getSelection());
        if (event.detail == DND.DROP_COPY) {
            try {
                copyNodes(droppedNodes, dropTarget);
            } catch (final DirectoryEditException e) {
                MessageDialog.openError(_alarmTreeView.getSite().getShell(),
                                        "Copying Nodes",
                                        "An error occured. The nodes could not be copied.");
            }
        } else if (event.detail == DND.DROP_MOVE) {
            try {
                moveNodes(droppedNodes, dropTarget);
            } catch (final DirectoryEditException e) {
                MessageDialog.openError(_alarmTreeView.getSite().getShell(),
                                        "Moving Nodes",
                                        "An error occured. The nodes could not be moved.");
            }
        }
        final TreeViewer viewer = _alarmTreeView.getViewer();
        if (viewer != null) {
            viewer.refresh();
        } else {
            throw new IllegalStateException("Viewer of " + AlarmTreeView.class.getName() +
            " mustn't be null at this point.");
        }
    }

    private void copyNodes(@Nonnull final List<IAlarmTreeNode> nodes,
                           @Nonnull final SubtreeNode target) throws DirectoryEditException {
        for (final IAlarmTreeNode node : nodes) {
            DirectoryEditor.copyNode(node, target);
        }
    }


    private void moveNodes(@Nonnull final List<IAlarmTreeNode> nodes,
                           @Nonnull final SubtreeNode target) throws DirectoryEditException {
        for (final IAlarmTreeNode node : nodes) {
            DirectoryEditor.moveNode(node, target);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dragOperationChanged(@Nonnull final DropTargetEvent event) {
        // TODO Auto-generated method stub

    }
}
