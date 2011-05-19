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
package org.csstudio.alarm.treeview.model;

import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Extends the general alarm tree node interface for all nodes, that may possess
 * child nodes.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 28.04.2010
 */
public interface IAlarmSubtreeNode extends IAlarmTreeNode {

    /**
     * Removes the given child reference from this node.
     *
     * @param child
     *            the child node to remove.
     */
    void removeChild(@Nonnull final IAlarmTreeNode child);

    /**
     * Removes all children of this node recursively.
     */
    void removeChildren();

    /**
     * Checks, whether the given child is already present. If this is the case, it cannot be added.
     * 
     * @param name name of the child to be added
     * @return true, if the child can be added
     */
    boolean canAddChild(@Nonnull final String name);
    
    /**
     * Adds the specified child node to the list of this node's children unless a
     * node with this name is already in the list, then nothing is added and false is
     * returned.
     * Note: it is not checked whether the parent node of the child is correctly set
     * to this node. This method is intended to be called only by constructors
     * of nodes.
     *
     * @param child the child node to add.
     * @returns false, if the adding of the child did not succeed
     */
    boolean addChild(@Nonnull final IAlarmTreeNode child);


    /**
     * Signals to this node that the alarm severity of one of its children
     * changed. This method must be called by children of this node when their
     * severity changes. If the change causes a severity change for this node,
     * this node will in turn notify its parent, so that the highest severity
     * is propagated to the root of the tree.
     *
     * @param child the child node whose severity status has changed.
     */
    void childSeverityChanged(@Nonnull IAlarmTreeNode child);

    /**
     * Finds all process variable nodes with the given name below this node. If
     * no nodes are found, returns an empty list.
     * @param name the name of the nodes.
     * @return a list of the nodes.
     */
    @Nonnull
    List<IAlarmProcessVariableNode> findProcessVariableNodes(@Nonnull String name);

    /**
     * Finds all process variable nodes below this node. If
     * no nodes are found, returns an empty list.
     * @return a list of the nodes.
     */
    @Nonnull
    List<IAlarmProcessVariableNode> findAllProcessVariableNodes();

    /**
     * Returns a collection of the PV nodes (leaf nodes) below this subtree
     * node that have unacknowledged alarms.
     *
     * @return a collection of the PV nodes with unacknowledged alarms.
     */
    @Nonnull
    Collection<IAlarmProcessVariableNode> collectUnacknowledgedAlarms();

    /**
     * Returns the direct children of this node.
     * @return a list of children if there are any, an empty list otherwise
     */
    @Nonnull
    List<IAlarmTreeNode> getChildren();

    /**
     * Returns the direct child by name.
     * @return the child component with the specified name, if there is any, otherwise null
     */
    @CheckForNull
    IAlarmTreeNode getChild(@Nonnull final String name);

    /**
     * Removes all children and their subtrees from the node. Clears all caches and maps.
     */
    void clearChildren();

}
