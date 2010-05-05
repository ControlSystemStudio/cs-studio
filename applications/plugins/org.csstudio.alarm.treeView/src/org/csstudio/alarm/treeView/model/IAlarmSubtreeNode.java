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
package org.csstudio.alarm.treeView.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.Name;

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
     * Removes the given child from this node. If the given node is not a direct
     * child of this subtree, does nothing. The child node must not have any
     * children itself. If the child node has children, this method does
     * nothing.
     *
     * @param child
     *            the child node to remove.
     */
    void removeChild(@Nonnull final IAlarmTreeNode child);

    /**
     * Adds the specified child node to the list of this node's children. Note:
     * it is not checked whether the parent node of the child is correctly set
     * to this node. This method is intended to be called only by constructors
     * of nodes.
     *
     * @param child the child node to add.
     */
    void addSubtreeChild(@Nonnull final IAlarmSubtreeNode child);

    /**
     * Adds the specified child node to the list of this node's children. Note:
     * it is not checked whether the parent node of the child is correctly set
     * to this node. This method is intended to be called only by constructors
     * of nodes.
     *
     * @param child the child node to add.
     */
    void addPVChild(@Nonnull final IAlarmTreeNode child);

    /**
     * Returns the name of this node in the LDAP directory.
     *
     * @return the name of this node in the directory.
     */
    @CheckForNull
    Name getLdapName();

    /**
     * Signals to this node that the alarm severity of one of its children
     * changed. This method must be called by children of this node when their
     * severity changes. If the change causes a severity change for this node,
     * this node will in turn notify its parent, so that the highest severity
     * is propagated to the root of the tree.
     *
     * @param child the child node whose severity status has changed.
     */
    void childSeverityChanged(@Nonnull IAlarmTreeNode subtreeNode);

}
