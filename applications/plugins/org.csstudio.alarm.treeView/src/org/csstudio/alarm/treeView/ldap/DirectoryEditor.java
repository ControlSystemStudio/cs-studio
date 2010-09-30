/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.alarm.treeView.ldap;

import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.PVNodeItem;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.alarm.treeView.views.ITreeModificationItem;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;

/**
 * Editor for the alarm tree in the LDAP directory. The methods of this class
 * update the LDAP directory and also the tree model.
 *
 * @author Joerg Rathlev
 */
public final class DirectoryEditor {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(DirectoryEditor.class);

    /**
     * Private constructor.
     */
    private DirectoryEditor() {
        // Don't instantiate.
    }

    /**
     * Renames a node.
     *
     * @param node
     *            the node.
     * @param newName
     *            the new name. This should be just the simple name, not an LDAP
     *            name.
     * @return
     * @throws DirectoryEditException
     *             if an error occurs.
     */
    @CheckForNull
    public static ITreeModificationItem rename(@Nonnull final IAlarmTreeNode node,
                                               @Nonnull final String newName)
        throws DirectoryEditException {

        final LdapName oldLdapName = node.getLdapName();
        final LdapName newLdapName = new LdapName(oldLdapName.getRdns());

        final ITreeModificationItem item;

        final IAlarmSubtreeNode parent = node.getParent();
        if (parent != null && parent.getChild(newName) != null) {
            throw new DirectoryEditException("Either root node selected or name " + newName + " does already exist on this level.", null);
        }

        if (node.getSource().equals(TreeNodeSource.LDAP)) {
            item = new RenameModificationItem(node, newName, newLdapName, oldLdapName);
        } else {
            item = null;
        }


        if (parent != null) {
            parent.removeChild(node);
        }
        node.setName(newName); // rename on tree item

        if (parent != null) {
            parent.addChild(node);
        }

        return item;
    }

    /**
     * Moves a node into a new subtree node. If the node is a subtree node, the
     * whole subtree will be moved, including its children.
     *
     * @param node
     *            the node.
     * @param target
     *            the target node which will become the new parent of the node.
     * @throws DirectoryEditException
     *             if an error occurs.
     */
//    @Nonnull
//    public static ITreeModificationItem moveNode(@Nonnull final IAlarmTreeNode node,
//                                                 @Nonnull final SubtreeNode target)
//        throws DirectoryEditException {
//
//        final LdapName oldNodeName = node.getLdapName();
//        final LdapName targetName = target.getLdapName();
//
//        final LdapName newLdapName = new LdapName(targetName.getRdns());
//        final String nodeObjectClass = node.getTreeNodeConfiguration().getNodeTypeName();
//        final String nodeSimpleName = node.getName();
//
//        final ITreeModificationItem item = new MoveSubtreeModificationItem(newLdapName,
//                                                                           oldNodeName,
//                                                                           nodeSimpleName,
//                                                                           nodeObjectClass,
//                                                                           targetName);
//
//        // store parent for child removal at the end of this method
//        final IAlarmSubtreeNode parent = node.getParent();
//
//        if (node instanceof IAlarmSubtreeNode) { // modifies the parent reference of the node
//            target.addSubtreeChild((IAlarmSubtreeNode) node);
//        } else {
//            target.addPVChild((IAlarmProcessVariableNode) node);
//        }
//        if (parent == null) {
//            throw new DirectoryEditException("Node " + node.getName() + " does not have a parent (==ROOT)." +
//                                             "\nMove action not permitted on ROOT.", null);
//        }
//        // remove child from old location
//        parent.removeChild(node);
//
//        return item;
//    }

    /**
     * Recursively deletes a node and all of its children.
     *
     * @param node
     *            the node.
     * @throws DirectoryEditException
     *             if an error occurs.
     */
    @CheckForNull
    public static ITreeModificationItem deleteRecursively(@Nonnull final IAlarmTreeNode node)
        throws DirectoryEditException {

        final LdapName nodeName = new LdapName(node.getLdapName().getRdns());

        final ITreeModificationItem item;
        if (node.getSource().equals(TreeNodeSource.LDAP)) {
            item = new DeleteRecursivelyModificationItem(nodeName);
        } else {
            item = null;
        }

        if (node instanceof IAlarmSubtreeNode) {
            ((IAlarmSubtreeNode) node).removeChildren();
        }
        final IAlarmSubtreeNode parent = node.getParent();
        if (parent != null) {
            parent.removeChild(node);
        } else {
            throw new DirectoryEditException("Node is ROOT. Remove action mustn't be triggered on ROOT.",
                                             null);
        }
        return item;
    }


    /**
     * Creates a copy of a node under a new subtree node. If the node to be
     * copied is a subtree node, all of its children will be copied into the new
     * node, too.
     *
     * @param node
     *            the original node.
     * @param target
     *            the target into which the node will be copied.
     * @throws DirectoryEditException
     *             if an error occurs.
     */
    @CheckForNull
    public static Queue<ITreeModificationItem> copyNode(@Nonnull final IAlarmTreeNode node,
                                                        @Nonnull final IAlarmSubtreeNode target)
        throws DirectoryEditException {

        final Attributes attrs = new BasicAttributes();
        attrs.put(ATTR_FIELD_OBJECT_CLASS, node.getTreeNodeConfiguration().getObjectClass());

        IAlarmTreeNode copy;
        if (node instanceof IAlarmProcessVariableNode) {
            copy = copyProcessVariableNode((IAlarmProcessVariableNode) node, target);
            try {
                AlarmTreeNodeModifier.setAlarmState((IAlarmProcessVariableNode) copy, attrs);
            } catch (final NamingException e) {
                throw new DirectoryEditException("Alarm state attributes of " +
                                                 node.getName() + " could not be retrieved.", e);
            }
        } else if (node instanceof IAlarmSubtreeNode) {
            copy = copySubtreeNode(node, target);
        } else {
            throw new DirectoryEditException("Node " + node.getName() +
                                             " is neither a subtree node nor a process variable node.", null);
        }

        final Queue<ITreeModificationItem> items = new ConcurrentLinkedQueue<ITreeModificationItem>();
        if (target.getSource().equals(TreeNodeSource.LDAP)) {
            items.add(new CreateLdapEntryModificationItem(copy.getLdapName(), attrs));
        }

        if (node instanceof IAlarmSubtreeNode) {
            // Recursion
            for (final IAlarmTreeNode child : ((IAlarmSubtreeNode) node).getChildren()) {
                final Queue<ITreeModificationItem> innerItems = copyNode(child, (IAlarmSubtreeNode) copy);
                items.addAll(innerItems);
            }
        }

        return items;
    }


    @Nonnull
    private static IAlarmTreeNode copySubtreeNode(@Nonnull final IAlarmTreeNode node,
                                                  @Nonnull final IAlarmSubtreeNode target) {
        IAlarmTreeNode copy;
        copy = new SubtreeNode.Builder(node.getName(),
                                       node.getTreeNodeConfiguration(),
                                       target.getSource())
                              .setParent(target).build();
        copyProperties(node, copy);
        return copy;
    }


    /**
     * Creates a new process variable node which is a copy of the given process
     * variable node.
     *
     * @param node
     *            the original.
     * @param target
     *            the target.
     * @throws DirectoryEditException
     *             if an error occurs.
     */
    @Nonnull
    private static IAlarmTreeNode copyProcessVariableNode(@Nonnull final IAlarmProcessVariableNode node,
                                                          @Nonnull final IAlarmSubtreeNode target)
    throws DirectoryEditException {

        final IAlarmProcessVariableNode copy =
            new ProcessVariableNode.Builder(node.getName(), target.getSource()).setParent(target).build();
        copy.updateAlarm(node.getAlarm());
        copy.setHighestUnacknowledgedAlarm(node.getHighestUnacknowledgedAlarm());
        copyProperties(node, copy);
        return copy;
    }

    /**
     * Copies the properties from one node to another node.
     *
     * @param source
     *            the source.
     * @param destination
     *            the destination.
     */
    private static void copyProperties(@Nonnull final IAlarmTreeNode source,
                                       @Nonnull final IAlarmTreeNode destination) {
        for (final EpicsAlarmcfgTreeNodeAttribute id : EpicsAlarmcfgTreeNodeAttribute.values()) {
            final String value = source.getOwnProperty(id);
            destination.setProperty(id, value);
        }
    }


    /**
     * Creates an entry for a process variable record (eren) in the directory
     * below the given parent.
     *
     * @param parent
     *            the parent node.
     * @param recordName
     *            the name of the process variable record.
     */
    @CheckForNull
    public static ITreeModificationItem createProcessVariableRecord(@Nonnull final IAlarmSubtreeNode parent,
                                                                    @Nonnull final String recordName) {

        final IAlarmProcessVariableNode node = new ProcessVariableNode.Builder(recordName, parent
                .getSource()).setParent(parent).build();

        final Attributes attrs = new BasicAttributes();
        attrs.put(ATTR_FIELD_OBJECT_CLASS, LdapEpicsAlarmcfgConfiguration.RECORD.getObjectClass());

        retrieveInitialStateSynchronously(node);

        if (parent.getSource().equals(TreeNodeSource.LDAP)) {
            return new CreateLdapEntryModificationItem(node.getLdapName(), attrs);
        }
        return null;

    }


    private static void retrieveInitialStateSynchronously(@Nonnull final IAlarmProcessVariableNode node) {
        final List<PVNodeItem> initItems = Collections.singletonList(new PVNodeItem(node));

        final IAlarmService alarmService = AlarmTreePlugin.getDefault().getAlarmService();
        if (alarmService != null) {
            alarmService.retrieveInitialState(initItems);
        } else {
            LOG.warn("Initial state could not be retrieved because alarm service is not available.");
        }
    }


    /**
     * Creates an entry for a component (ecom) in the directory below the given
     * parent.
     *
     * @param parent
     *            the parent node.
     * @param componentName
     *            the name of the component.
     */
    @CheckForNull
    public static ITreeModificationItem createComponent(@Nonnull final IAlarmSubtreeNode parent,
                                                        @Nonnull final String componentName) {

        final SubtreeNode node =
            new SubtreeNode.Builder(componentName,
                                    LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                    parent.getSource()).setParent(parent).build();

        final Attributes attrs = new BasicAttributes();
        attrs.put(ATTR_FIELD_OBJECT_CLASS,
                  LdapEpicsAlarmcfgConfiguration.COMPONENT.getObjectClass());

        if (parent.getSource().equals(TreeNodeSource.LDAP)) {
            return new CreateLdapEntryModificationItem(node.getLdapName(), attrs);
        }
        return null;
    }
}
