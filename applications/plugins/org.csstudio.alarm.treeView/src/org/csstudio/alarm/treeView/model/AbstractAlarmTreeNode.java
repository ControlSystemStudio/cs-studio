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
package org.csstudio.alarm.treeView.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.AlarmTreeNodePropertyId;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Abstract base class for alarm tree nodes.
 *
 * @author Joerg Rathlev
 */
public abstract class AbstractAlarmTreeNode extends PlatformObject implements
		IAlarmTreeNode {

	/**
	 * The properties of this node.
	 */
	private final Map<AlarmTreeNodePropertyId, String> _properties;

	/**
	 * The parent node of this node.
	 */
    private IAlarmSubtreeNode _parent;

    /**
     * The name of this node.
     */
    private String _name;

    /**
     * The configuration type of this node in the directory.
     */
    private final LdapEpicsAlarmcfgConfiguration _configurationType;

    /**
     * The source from where this node originated (e.g. LDAP or XML, etc)
     */
    private final TreeNodeSource _source;


    /**
	 * Creates a new abstract alarm tree node.
	 */
	protected AbstractAlarmTreeNode(@Nonnull final String name,
	                                @Nonnull final LdapEpicsAlarmcfgConfiguration oc,
	                                @Nonnull final TreeNodeSource source) {
	    _name = name;
	    _configurationType = oc;
	    _source = source;
		_properties = new EnumMap<AlarmTreeNodePropertyId, String>(AlarmTreeNodePropertyId.class);
	}

	/**
	 * Sets a property of this node.
	 *
	 * @param property
	 *            the property to set.
	 * @param value
	 *            the value.
	 */
	public final void setProperty(@Nonnull final AlarmTreeNodePropertyId property, @CheckForNull final String value) {
		if (value != null) {
			_properties.put(property, value);
		} else {
			_properties.remove(property);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@CheckForNull
	public IAlarmSubtreeNode getParent() {
	    return _parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParent(@Nonnull final IAlarmSubtreeNode parent) {
	    _parent = parent;
	}

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public LdapEpicsAlarmcfgConfiguration getTreeNodeConfiguration() {
        return _configurationType;
    }

	/**
     * {@inheritDoc}
     */
    @CheckForNull
    public final String getProperty(@Nonnull final AlarmTreeNodePropertyId property) {
        String result = _properties.get(property);
        if (result == null) {
            final IAlarmSubtreeNode parent = getParent();
            if (parent != null) {
                result = parent.getProperty(property);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public final String getOwnProperty(@Nonnull final AlarmTreeNodePropertyId property) {
        return _properties.get(property);
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    public LdapName getLdapName() {
        try {
            if (_configurationType == null) {
                return new LdapName("");
            }

            final LdapName result =
                new LdapName(Collections.singletonList(new Rdn(_configurationType.getNodeTypeName(), _name)));

            final IAlarmSubtreeNode parent = getParent();
            if (parent != null) {

                final LdapName ldapName = parent.getLdapName();
                if (ldapName == null) {
                    return null;
                }
                result.addAll(0, ldapName);
            }
            return result;
        } catch (final InvalidNameException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public final String getName() {
    	return _name;
    }

    /**
     * {@inheritDoc}
     */
    public final void setName(@Nonnull final String name) {
    	_name = name;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String toString() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final TreeNodeSource getSource() {
       return _source;
    }
}
