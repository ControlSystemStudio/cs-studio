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

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;

import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Rdn;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmTreeNodePropertyId;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.platform.logging.CentralLogger;

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
     * @throws DirectoryEditException
     *             if an error occurs.
     */
    public static void rename(@Nonnull final IAlarmTreeNode node, @Nonnull final String newName)
        throws DirectoryEditException {
            node.setName(newName);
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
    public static void moveNode(@Nonnull final IAlarmTreeNode node,
                                @Nonnull final SubtreeNode target)
        throws DirectoryEditException {

        // store parent for later child removal
        final IAlarmSubtreeNode parent = node.getParent();

        if (node instanceof IAlarmSubtreeNode) {
            target.addSubtreeChild((IAlarmSubtreeNode) node);
        } else {
            target.addPVChild((IAlarmProcessVariableNode) node);
        }
        if (parent == null) {
            throw new DirectoryEditException("Node " + node.getName() + " does not have a parent (==ROOT)." +
                                             "\nMove action not permitted on ROOT.", null);
        }
        // remove child from old location
        parent.removeChild(node);
    }

    /**
     * Recursively deletes a node and all of its children.
     *
     * @param node
     *            the node.
     * @throws DirectoryEditException
     *             if an error occurs.
     */
    public static void deleteRecursively(@Nonnull final IAlarmTreeNode node)
        throws DirectoryEditException {

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
    @Nonnull
    public static IAlarmTreeNode copyNode(@Nonnull final IAlarmTreeNode node,
                                          @Nonnull final IAlarmSubtreeNode target)
    throws DirectoryEditException {

        if (node instanceof IAlarmProcessVariableNode) {
            return copyProcessVariableNode((IAlarmProcessVariableNode) node, target);
        }
        final IAlarmSubtreeNode copy =
            new SubtreeNode.Builder(node.getName(), node.getObjectClass()).setParent(target).build();
        copyProperties(node, copy);

        for (final IAlarmTreeNode child : ((IAlarmSubtreeNode) node).getChildren()) {
            copyNode(child, copy);
        }
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
            new ProcessVariableNode.Builder(node.getName()).setParent(target).build();
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
        for (final AlarmTreeNodePropertyId id : AlarmTreeNodePropertyId.values()) {
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
     * @throws DirectoryEditException if the entry could not be created.
     */
    public static void createProcessVariableRecord(@Nonnull final IAlarmSubtreeNode parent,
                                                   @Nonnull final String recordName)
    throws DirectoryEditException {
        try {
            final Rdn rdn = new Rdn(LdapEpicsAlarmCfgObjectClass.RECORD.getNodeTypeName(), recordName);
            final Attributes attrs = createBaseAttributesForEntry(LdapEpicsAlarmCfgObjectClass.RECORD, rdn);
            // TODO (jpenning) : retrieve initial alarm states not from LDAP (Epics-Control) but from DAL
            final IAlarmProcessVariableNode node = new ProcessVariableNode.Builder(recordName).setParent(parent).build();
            AlarmTreeNodeModifier.setAlarmState(node, attrs);
        } catch (final NamingException e) {
            LOG.error("Error creating directory entry", e);
            throw new DirectoryEditException(e.getMessage(), e);
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
     * @throws DirectoryEditException
     *             if the entry could not be created.
     */
    public static void createComponent(@Nonnull final SubtreeNode parent,
                                       @Nonnull final String componentName) throws DirectoryEditException {

        new SubtreeNode.Builder(componentName, LdapEpicsAlarmCfgObjectClass.COMPONENT).setParent(parent).build();
    }

    /**
     * Returns the base attributes (name, objectclass and EPICS type) for a new
     * entry with the given object class and name.
     *
     * @param objectClass
     *            the object class of the new entry.
     * @param rdn
     *            the relative name of the entry.
     * @return the attributes for the new entry.
     */
    @Nonnull
    private static Attributes createBaseAttributesForEntry(@Nonnull final LdapEpicsAlarmCfgObjectClass objectClass,
                                                           @Nonnull final Rdn rdn) {
        final Attributes result = rdn.toAttributes();
        result.put(ATTR_FIELD_OBJECT_CLASS, objectClass.getDescription());
        return result;
    }
}
