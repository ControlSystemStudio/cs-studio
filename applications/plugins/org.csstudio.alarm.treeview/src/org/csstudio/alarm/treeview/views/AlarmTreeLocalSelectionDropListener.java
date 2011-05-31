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

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.treeview.ldap.DirectoryEditException;
import org.csstudio.alarm.treeview.ldap.DirectoryEditor;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
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
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AlarmTreeLocalSelectionDropListener.class);
    
    private final AlarmTreeView _alarmTreeView;
    private final Queue<ITreeModificationItem> _ldapModificationItems;

    /**
     * Constructor.
     * @param alarmTreeView the view
     * @param modificationItems list to collect the alarm tree modifications
     */
    public AlarmTreeLocalSelectionDropListener(@Nonnull final AlarmTreeView alarmTreeView,
                                               @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        _alarmTreeView = alarmTreeView;
        _ldapModificationItems = modificationItems;
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
    public boolean isEnabled(@Nonnull final DropTargetEvent event) {
        final ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        return dropTargetIsSubtreeNode(event) && canDrop(selection, event);
    }

    /**
     * Checks if the target of the drop operation is a SubtreeNode.
     */
    private boolean dropTargetIsSubtreeNode(@Nonnull final DropTargetEvent event) {
        return event.item instanceof TreeItem
                && event.item.getData() instanceof SubtreeNode;
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
                    if ( o == dropTarget || isChild(dropTarget, (IAlarmTreeNode) o)) {
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
    @Override
    public void dragEnter(@Nonnull final DropTargetEvent event) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragLeave(@Nonnull final DropTargetEvent event) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragOver(@Nonnull final DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropAccept(@Nonnull final DropTargetEvent event) {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drop(@Nonnull final DropTargetEvent event) {
        final SubtreeNode dropTarget = (SubtreeNode) event.item.getData();
        final List<IAlarmTreeNode> droppedNodes =
            AlarmTreeView.selectionToNodeList(LocalSelectionTransfer.getTransfer().getSelection());
        if (event.detail == DND.DROP_COPY) {
            try {
                copyNodes(droppedNodes, dropTarget);
            } catch (final DirectoryEditException e) {
                MessageDialog.openError(_alarmTreeView.getSite().getShell(),
                                        "Copying Nodes",
                                        "An error occured. The nodes could not be copied.\n" + e.getMessage());
            }
        } else if (event.detail == DND.DROP_MOVE) {
            try {
                moveNodes(droppedNodes, dropTarget);
            } catch (final DirectoryEditException e) {
                MessageDialog.openError(_alarmTreeView.getSite().getShell(),
                                        "Moving Nodes",
                                        "An error occured. The nodes could not be moved.\n" + e.getMessage());
            }
        }
        final TreeViewer viewer = _alarmTreeView.getViewer();
        if (viewer != null) {
            viewer.refresh();
        } else {
            throw new IllegalStateException("Viewer of " +
                                            AlarmTreeView.class.getName() +
                                            " mustn't be null at this point.");
        }
    }

    private void copyNodes(@Nonnull final List<IAlarmTreeNode> nodes,
                           @Nonnull final IAlarmSubtreeNode target) throws DirectoryEditException {

        for (final IAlarmTreeNode node : nodes) {
            if (node.getTreeNodeConfiguration().equals(LdapEpicsAlarmcfgConfiguration.FACILITY)) {
                copyFacilityComponentAsInsertion(target, node);
            } else {
                if (target.canAddChild(node.getName())) {
                    final Queue<ITreeModificationItem> items = DirectoryEditor.copyNode(node, target);
                    _ldapModificationItems.addAll(items);
                } else {
                    String message = "Node '" + node.getName() + "' cannot be added to component '" + target.getName() + "'\n" +
                    		"Does it already exist?";
                    LOG.warn(message);
                    throw new DirectoryEditException(message, null);
                }
            }

        }
    }

    /**
     * Moving or copying facility nodes is special.
     * The normal case is the nodes  (COMPONENTS, RECORDS) are just copied to the new location
     * (and for the 'move action' deleted afterwards from the old location).
     *
     * The FACILITY node changes its type and becomes a COMPONENT! This action is (up to now)
     * not reversible.
     *
     * @param target
     * @param node
     * @throws DirectoryEditException
     */
    private void copyFacilityComponentAsInsertion(@Nonnull final IAlarmSubtreeNode target,
                                                  @Nonnull final IAlarmTreeNode node) throws DirectoryEditException {
        final String name = node.getName();
        // generate a new component for the facility
        final ITreeModificationItem item = DirectoryEditor.createComponent(target, name);
        if (item != null) {
            final IAlarmTreeNode newComp = target.getChild(name);
            if (newComp != null) {
                _ldapModificationItems.add(item);
                // copy the children of the facility below new component
                copyNodes(((IAlarmSubtreeNode) node).getChildren(), (IAlarmSubtreeNode) newComp);
            }
        }
    }


    private void moveNodes(@Nonnull final List<IAlarmTreeNode> nodes,
                           @Nonnull final IAlarmSubtreeNode target) throws DirectoryEditException {

        copyNodes(nodes, target);

        for (final IAlarmTreeNode node : nodes) {
            final ITreeModificationItem removeItem = DirectoryEditor.deleteRecursively(node);
            if (removeItem != null) {
                _ldapModificationItems.add(removeItem);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragOperationChanged(@Nonnull final DropTargetEvent event) {
        // Empty
    }
}
