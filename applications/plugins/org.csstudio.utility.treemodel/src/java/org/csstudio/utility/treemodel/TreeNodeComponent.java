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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import com.google.common.collect.Maps;

/**
 * Structural component for the content model tree.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @param <T>
 * @since 30.04.2010
 */
public class TreeNodeComponent<T extends Enum<T> & ITreeNodeConfiguration<T>> extends AbstractTreeNodeComponent<T>
    implements ISubtreeNodeComponent<T> {

    private final Set<T> _subComponentTypes;

    private final Map<String, INodeComponent<T>> _children = Maps.newLinkedHashMap();

    /**
     * Constructor.
     *
     * @param name component name
     * @param type component type
     * @param subComponentTypes types of its children components
     * @param parent reference to its parent, might be <code>null</code> for ROOT
     * @param attributes
     * @param fullName
     * @throws InvalidNameException
     */
    public TreeNodeComponent(final String name,
                             final T type,
                             final ISubtreeNodeComponent<T> parent,
                             final Attributes attributes,
                             final LdapName fullName) throws InvalidNameException {
        super(name, type, parent, attributes, fullName);
        _subComponentTypes = type.getNestedContainerTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<T> getSubComponentTypes() {
        return _subComponentTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(final INodeComponent<T> child) {

        if(!_subComponentTypes.contains(child.getType())) {
            throw new IllegalArgumentException("The child type " + child.getType() + " of node " +
                                               child.getName() + " is not permitted for this component " +
                                               getName() + "!");
        }

        final String nameKey = child.getName();

        if (!_children.containsKey(nameKey)) {
            _children.put(nameKey, child);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<INodeComponent<T>> getDirectChildren() {
        return _children.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, INodeComponent<T>> getChildrenByType(final T type) {
        if (_children.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<String, INodeComponent<T>> resultMap = new HashMap<String, INodeComponent<T>>();

        for (final Entry<String, INodeComponent<T>> childEntry : _children.entrySet()) {
            final INodeComponent<T> child = childEntry.getValue();
            if (child.getType().equals(type)) {
                resultMap.put(child.getLdapName().toString(), child);
            }
            if (child.hasChildren()) {
                resultMap.putAll( ((ISubtreeNodeComponent<T>) child).getChildrenByType(type));
            }
        }
        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        return !_children.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INodeComponent<T> getChild(final String name) {
        return _children.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChild(final String name) {
        if (_children.containsKey(name)) {
            _children.remove(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllChildren() {
        _children.clear();
    }

}
