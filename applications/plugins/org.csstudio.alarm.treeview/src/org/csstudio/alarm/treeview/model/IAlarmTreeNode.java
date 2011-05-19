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
 package org.csstudio.alarm.treeview.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.ldap.LdapName;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;


/**
 * A node in the alarm tree.
 *
 * @author Joerg Rathlev
 */
public interface IAlarmTreeNode {

	/**
	 * Returns the name of this node.
	 *
	 * @return the name of this node.
	 */
    @Nonnull
	String getName();

	/**
	 * Sets the name of this node.
	 *
	 * @param name
	 *            the new name.
	 */
	void setName(@Nonnull String name);

    /**
     * Returns the name of this node in the LDAP directory.
     *
     * @return the name of this node in the directory.
     */
    @CheckForNull
    LdapName getLdapName();

	/**
	 * Returns the tree configuration type of this node in the directory. If this node does
	 * not correspond to an entry in the directory, returns <code>null</code>.
	 *
	 * @return the tree node configuration type of this node in the directory.
	 */
	@Nonnull
	LdapEpicsAlarmcfgConfiguration getTreeNodeConfiguration();

    /**
     * Returns the parent node of this node. If this node does not have a
     * parent, returns {@code null}.
     *
     * @return the parent node of this node.
     */
	@CheckForNull
	IAlarmSubtreeNode getParent();

	/**
	 * Sets the parent node of this node.
	 * @param parent the parent node
	 */
	void setParent(@CheckForNull IAlarmSubtreeNode parent);

    /**
     * Returns the value of a property. If the property is not set on this node,
     * the value will be inherited from its parent node.
     *
     * @param property the property
     * @return the property value, or <code>null</code> if the property is not
     *         set on this node or a parent node.
     */
    @CheckForNull
    String getInheritedProperty(@Nonnull final EpicsAlarmcfgTreeNodeAttribute property);

    
    /**
     * Returns the value of a property. If the property is not set on this node,
     * the value will be inherited from its parent node.
     * This should be called when the property string is used as an URL. When defined, the file: protocol may be
     * omitted in the URL string as a convenience to the user. It is silently added here if no valid URL protocol is present
     * in the string.
     * 
     * @param property the property
     * @return the property value, or <code>null</code> if the property is not
     *         set on this node or a parent node.
     */
    @CheckForNull
    String getInheritedPropertyWithUrlProtocol(@Nonnull final EpicsAlarmcfgTreeNodeAttribute property);

    /**
     * Returns the property value that is set on this node. The value is not
     * inherited from a parent node if no value is set on this node.
     *
     * @param property
     *            the property.
     * @return the property value, or <code>null</code> if the property is not
     *         set on this node.
     */
    @CheckForNull
    String getOwnProperty(@Nonnull EpicsAlarmcfgTreeNodeAttribute property);

    /**
     * Sets the property to the given value.
     * @param property .
     * @param value the value, if <code>null</code> is passed, the property is removed
     */
    void setProperty(@Nonnull final EpicsAlarmcfgTreeNodeAttribute property, @CheckForNull final String value);

    /**
     * Returns the highest severity of the alarms in the subtree below this
     * node.
     *
     * @return the alarm severity for this node.
     */
    @Nonnull
    EpicsAlarmSeverity getAlarmSeverity();

    /**
     * Returns the severity of the highest unacknowledged alarm for this node.
     * If there is no unacknowledged alarm for this node, returns UNKNOWN.
     *
     * @return the severity of the highest unacknowledged alarm for this node.
     */
    @Nonnull
    EpicsAlarmSeverity getUnacknowledgedAlarmSeverity();

    /**
     * Returns whether this node has any children.
     * @return {@code true} if this node has children, {@code false} otherwise.
     */
    boolean hasChildren();

    /**
     * Returns the source of this alarm tree node (e.g. LDAP or XML).
     */
    @Nonnull
    TreeNodeSource getSource();

}
