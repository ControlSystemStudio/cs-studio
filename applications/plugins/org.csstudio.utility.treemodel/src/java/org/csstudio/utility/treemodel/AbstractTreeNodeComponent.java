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
package org.csstudio.utility.treemodel;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

/**
 * Structural component to model LDAP perspectives.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2010
 * @param <T> der Enum type
 */
public abstract class AbstractTreeNodeComponent<T extends Enum<T> & ITreeNodeConfiguration<T>>
    implements INodeComponent<T> {

    private final String _name;

    private final T _type;

    private final ISubtreeNodeComponent<T> _parent;

    private final Attributes _attributes;

    private final LdapName _ldapName;


    /**
     * Constructor.
     * @param attributes
     * @param fullName
     * @throws InvalidNameException
     */
    public AbstractTreeNodeComponent(final String name,
                                     final T type,
                                     final ISubtreeNodeComponent<T> parent,
                                     final Attributes attributes,
                                     final LdapName fullName) throws InvalidNameException {
        _name = name;
        _type = type;
        _parent = parent;
        _attributes = attributes;
        _ldapName = new LdapName(fullName == null ? "" : fullName.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getType() {
        return _type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISubtreeNodeComponent<T> getParent() {
        return _parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute getAttribute(final String attrID) {
        if (_attributes == null) {
            return null;
        }
        return _attributes.get(attrID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapName getLdapName() {
        return (LdapName) _ldapName.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attributes getAttributes() {
        return (Attributes) _attributes.clone();
    }

}
