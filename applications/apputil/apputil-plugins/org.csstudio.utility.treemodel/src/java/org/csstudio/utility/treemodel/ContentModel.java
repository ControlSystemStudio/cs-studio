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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;



/**
 * Generic content model to capture arbitrary LDAP tree structures.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 03.05.2010
 * @param <T> the object class type for which a tree shall be created
 */
public final class ContentModel<T extends Enum<T> & ITreeNodeConfiguration<T>> {


    private static final Logger LOG = Logger.getLogger(ContentModel.class.getName());
    /**
     * A type object to give access to the type specific functionality of the tree components.
     */
    private final T _virtualConfigurationRoot;

    /**
     * The virtual tree root.
     */
    private ISubtreeNodeComponent<T> _virtualRoot;

    private Map<T, Map<String, INodeComponent<T>>> _cacheByTypeAndLdapName;

    private Map<T, Map<String, INodeComponent<T>>> _cacheByTypeAndSimpleName;

    private Map<String, INodeComponent<T>> _cacheByLdapName;

    /**
     * Constructor.
     * @param virtualConfigurationRoot .
     */
    public ContentModel(final T virtualConfigurationRoot) {
        _virtualConfigurationRoot = virtualConfigurationRoot;
        initFields(_virtualConfigurationRoot);
    }

    private void initFields(final T objectClassRoot) {
        _cacheByLdapName = new HashMap<String, INodeComponent<T>>();

        final Class<T> clazz = objectClassRoot.getDeclaringClass();

        _cacheByTypeAndLdapName = initCacheByType(clazz);
        _cacheByTypeAndSimpleName = initCacheByType(clazz);

        try {
            _virtualRoot = new TreeNodeComponent<T>("VirtualRoot",
                                                    objectClassRoot,
                                                    null,
                                                    null,
                                                    null);
        } catch (final InvalidNameException e) {
            LOG.log(Level.WARNING, "Error creating root node in content model.", e);
        }
    }


    private Map<T, Map<String, INodeComponent<T>>> initCacheByType(final Class<T> enumClass) {
        return new EnumMap<T, Map<String, INodeComponent<T>>>(enumClass);
    }


    /**
     * Adds a child node to a parent node. If the parent node does not yet exist in the model, it is
     * added below the virtual root. That may throw an IllegalArgumentException, if the <not yet present>
     * parent node is not configured to be root (i.e. immediately below the virtual root in this model).
     *
     * @param parent
     * @param newChild
     */
    public void addChild(final ISubtreeNodeComponent<T> parent,
                         final INodeComponent<T> newChild) {

        if (parent.equals(_virtualRoot)) {
            parent.addChild(newChild);
        } else {
            final Map<String, INodeComponent<T>> byTypes = _cacheByTypeAndLdapName.get(parent.getType());
            if (!byTypes.containsKey(parent.getLdapName().toString())) { // parent does not yet exist in the model
                addChild(_virtualRoot, parent);               // add it first
            } else {
                parent.addChild(newChild); // add the child
            }
        }

        cacheNewChild(newChild);
    }

    private void cacheNewChild(final INodeComponent<T> newChild) {
        // CACHING
        _cacheByLdapName.put(newChild.getLdapName().toString(), newChild);

        // MORE CACHING
        final T type = newChild.getType();
        if (!_cacheByTypeAndLdapName.containsKey(type)) {
            _cacheByTypeAndLdapName.put(type, new HashMap<String, INodeComponent<T>>());
        }
        final Map<String, INodeComponent<T>> childrenByLdapName = _cacheByTypeAndLdapName.get(type);

        final String nameKey = newChild.getLdapName().toString();
        if (!childrenByLdapName.containsKey(nameKey)) {
            childrenByLdapName.put(nameKey, newChild); // updates the current map in cache by type
        }

        // AND EVEN MORE CACHING
        if (!_cacheByTypeAndSimpleName.containsKey(type)) {
            _cacheByTypeAndSimpleName.put(type, new HashMap<String, INodeComponent<T>>());
        }
        final Map<String, INodeComponent<T>> childrenBySimpleName = _cacheByTypeAndSimpleName.get(type);

        final String simpleName = newChild.getName();
        if (!childrenBySimpleName.containsKey(simpleName)) {
            childrenBySimpleName.put(simpleName, newChild); // updates the current map in cache by type
        }
    }

    /**
     * Accesses the type cache.
     * @param type the type of the children
     * @return a copy of the keys of the children featuring the given type
     */
    public Set<String> getSimpleNames(final T type) {
        final Map<String, INodeComponent<T>> children = _cacheByTypeAndSimpleName.get(type);

        return new HashSet<String>(children.keySet());
    }

    public Map<String, INodeComponent<T>> getChildrenByTypeAndLdapName(final T type) {

        final Map<String, INodeComponent<T>> map = _cacheByTypeAndLdapName.get(type);
        return map != null ? map : Collections.<String, INodeComponent<T>>emptyMap();
    }

    public Map<String, INodeComponent<T>> getChildrenByTypeAndSimpleName(final T type) {
        final Map<String, INodeComponent<T>> map = _cacheByTypeAndSimpleName.get(type);
        return map != null ? map : Collections.<String, INodeComponent<T>>emptyMap();
    }

    public INodeComponent<T> getByTypeAndLdapName(final T type, final LdapName key) {
        final Map<String, INodeComponent<T>> children = _cacheByTypeAndLdapName.get(type);
        return children != null ? children.get(key.toString()) : null;
    }


    public INodeComponent<T> getByTypeAndSimpleName(final T type, final String key) {
        final Map<String, INodeComponent<T>> children = _cacheByTypeAndSimpleName.get(type);
        return children != null ? children.get(key) : null;
    }

    public INodeComponent<T> getChildByLdapName(final String name) {
        return _cacheByLdapName.get(name);
    }

    public Map<String, INodeComponent<T>> getByType(final T type) {
        final Map<String, INodeComponent<T>> map = _cacheByTypeAndLdapName.get(type);
        return map != null ? map : Collections.<String, INodeComponent<T>>emptyMap();
    }

    /**
     * Delivers the virtual root of this tree model. This root yields the entry point into the
     * tree structure. It is not supposed to be depicted anywhere or to be structurally relevant for
     * the modeled content.
     * @return the virtual root of the model
     */
    public ISubtreeNodeComponent<T> getVirtualRoot() {
        return _virtualRoot;
    }

    /**
     * Clears the caches and removes any children below the virtual root.
     */
    public void clear() {
        _virtualRoot.removeAllChildren();
        _cacheByLdapName.clear();
        _cacheByTypeAndLdapName.clear();
        _cacheByTypeAndSimpleName.clear();
    }

    public boolean isEmpty() {
        return _cacheByLdapName.isEmpty() && _cacheByTypeAndLdapName.isEmpty() && _cacheByTypeAndSimpleName.isEmpty();
    }
}
