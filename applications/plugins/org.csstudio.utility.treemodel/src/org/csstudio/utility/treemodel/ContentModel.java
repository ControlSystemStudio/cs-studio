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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

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

    private static final Logger LOG = CentralLogger.getInstance().getLogger(ContentModel.class);

    /**
     * A type object to give access to the type specific functionality of the tree components.
     */
    private final T _configurationRoot;

    /**
     * The virtual tree root.
     */
    private ISubtreeNodeComponent<T> _virtualRoot;

    private Map<T, Map<String, ISubtreeNodeComponent<T>>> _cacheByTypeAndLdapName;

    private Map<T, Map<String, ISubtreeNodeComponent<T>>> _cacheByTypeAndSimpleName;

    private Map<String, ISubtreeNodeComponent<T>> _cacheByLdapName;

    /**
     * Constructor.
     * @param configurationRoot .
     * @throws InvalidNameException
     */
    public ContentModel(@Nonnull final T configurationRoot) throws InvalidNameException {
        _configurationRoot = configurationRoot;
        initFields(_configurationRoot);
    }

    /**
     * @param objectClassRoot
     * @param rootValue
     * @throws InvalidNameException
     */
    private void initFields(@Nonnull final T objectClassRoot) throws InvalidNameException {
        _cacheByLdapName = new HashMap<String, ISubtreeNodeComponent<T>>();

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
            LOG.error("Error creating root node in content model.", e);
        }
    }


    @Nonnull
    private Map<T, Map<String, ISubtreeNodeComponent<T>>> initCacheByType(@Nonnull final Class<T> enumClass) {
        return new EnumMap<T, Map<String, ISubtreeNodeComponent<T>>>(enumClass);
    }


    /**
     * Adds a child node to a parent node. If the parent node does not yet exist in the model, it is
     * added below the virtual root. That may throw an IllegalArgumentException, if the <not yet present>
     * parent node is not configured to be root (i.e. immediately below the virtual root in this model).
     *
     * @param parent
     * @param newChild
     */
    public void addChild(@Nonnull final ISubtreeNodeComponent<T> parent,
                         @Nonnull final ISubtreeNodeComponent<T> newChild) {

        if (parent.equals(_virtualRoot)) {
            parent.addChild(newChild);
        } else {
            final Map<String, ISubtreeNodeComponent<T>> byTypes = _cacheByTypeAndLdapName.get(parent.getType());
            if (!byTypes.containsKey(parent.getLdapName().toString())) { // parent does not yet exist in the model
                addChild(_virtualRoot, parent);               // add it first
            } else {
                parent.addChild(newChild); // add the child
            }
        }

        cacheNewChild(newChild);
    }

    private void cacheNewChild(final ISubtreeNodeComponent<T> newChild) {
        // CACHING
        _cacheByLdapName.put(newChild.getLdapName().toString(), newChild);

        // MORE CACHING
        final T type = newChild.getType();
        if (!_cacheByTypeAndLdapName.containsKey(type)) {
            _cacheByTypeAndLdapName.put(type, new HashMap<String, ISubtreeNodeComponent<T>>());
        }
        final Map<String, ISubtreeNodeComponent<T>> childrenByLdapName = _cacheByTypeAndLdapName.get(type);

        final String nameKey = newChild.getLdapName().toString();
        if (!childrenByLdapName.containsKey(nameKey)) {
            childrenByLdapName.put(nameKey, newChild); // updates the current map in cache by type
        }

        // AND EVEN MORE CACHING
        if (!_cacheByTypeAndSimpleName.containsKey(type)) {
            _cacheByTypeAndSimpleName.put(type, new HashMap<String, ISubtreeNodeComponent<T>>());
        }
        final Map<String, ISubtreeNodeComponent<T>> childrenBySimpleName = _cacheByTypeAndSimpleName.get(type);

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
    @Nonnull
    public Set<String> getSimpleNames(@Nonnull final T type) {
        final Map<String, ISubtreeNodeComponent<T>> children = _cacheByTypeAndSimpleName.get(type);

        return new HashSet<String>(children.keySet());
    }

    @Nonnull
    public Map<String, ISubtreeNodeComponent<T>> getChildrenByTypeAndLdapName(@Nonnull final T type) {

        final Map<String, ISubtreeNodeComponent<T>> map = _cacheByTypeAndLdapName.get(type);
        return map != null ? map : Collections.<String, ISubtreeNodeComponent<T>>emptyMap();
    }

    @Nonnull
    public Map<String, ISubtreeNodeComponent<T>> getChildrenByTypeAndSimpleName(@Nonnull final T type) {
        return _cacheByTypeAndSimpleName.get(type);
    }

    @CheckForNull
    public ISubtreeNodeComponent<T> getByTypeAndLdapName(@Nonnull final T type, @Nonnull final LdapName key) {
        final Map<String, ISubtreeNodeComponent<T>> children = _cacheByTypeAndLdapName.get(type);
        return children != null ? children.get(key.toString()) : null;
    }


    @CheckForNull
    public ISubtreeNodeComponent<T> getByTypeAndSimpleName(@Nonnull final T type, @Nonnull final String key) {
        final Map<String, ISubtreeNodeComponent<T>> children = _cacheByTypeAndSimpleName.get(type);
        return children != null ? children.get(key) : null;
    }

    @CheckForNull
    public ISubtreeNodeComponent<T> getChildByLdapName(@Nonnull final String name) {
        return _cacheByLdapName.get(name);
    }

    @CheckForNull
    public Map<String, ISubtreeNodeComponent<T>> getByType(@Nonnull final T type) {
        return _cacheByTypeAndLdapName.get(type);
    }

    /**
     * Delivers the virtual root of this tree model. This root yields the entry point into the
     * tree structure. It is not supposed to be depicted anywhere or to be structurally relevant for
     * the modeled content.
     * @return the virtual root of the model
     */
    @Nonnull
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
