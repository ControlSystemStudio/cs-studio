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
package org.csstudio.utility.ldap.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.ILdapObjectClass;
import org.csstudio.utility.ldap.LdapFieldsAndAttributes;
import org.csstudio.utility.ldap.LdapNameUtils;
import org.csstudio.utility.ldap.LdapNameUtils.Direction;
import org.csstudio.utility.ldap.reader.LdapSearchResult;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 03.05.2010
 * @param <T> the object class type for which a tree shall be created
 */
public class ContentModel<T extends Enum<T> & ILdapObjectClass<T>> {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    private T _objectClassRoot;

    private ILdapTreeComponent<T> _treeRoot;

    private Map<T, Map<String, ILdapComponent<T>>> _cacheByTypeAndLdapName;

    private Map<T, Map<String, ILdapComponent<T>>> _cacheByTypeAndSimpleName;

    private Map<String, ILdapComponent<T>> _cacheByLdapName;



    /**
     * Constructor.
     * @param searchResult .
     * @param objectClassRoot .
     */
    public ContentModel(@Nonnull final LdapSearchResult searchResult,
                        @Nonnull final T objectClassRoot) {

        initFields(objectClassRoot);
        addSearchResult(searchResult);
    }


    /**
     * Constructor.
     * @param objectClassRoot .
     */
    public ContentModel(@Nonnull final T objectClassRoot) {
        initFields(objectClassRoot);
    }


    /**
     * @param objectClassRoot
     */
    private void initFields(@Nonnull final T objectClassRoot) {
        _cacheByLdapName = new HashMap<String, ILdapComponent<T>>();

        _cacheByTypeAndLdapName = initCacheByType(objectClassRoot.getDeclaringClass());

        _cacheByTypeAndSimpleName = initCacheByType(objectClassRoot.getDeclaringClass());

        _objectClassRoot = objectClassRoot;

        try {
            _treeRoot = new LdapTreeComponent<T>(objectClassRoot.getDescription(),
                                                 objectClassRoot,
                                                 objectClassRoot.getNestedContainerClasses() ,
                                                 null,
                                                 null,
                                                 objectClassRoot.getRootValue());
        } catch (final InvalidNameException e) {
            _log.error("Error creating root node in content model.", e);
        }
    }


    @Nonnull
    private Map<T, Map<String, ILdapComponent<T>>> initCacheByType(@Nonnull final Class<T> enumClass) {
        return new EnumMap<T, Map<String, ILdapComponent<T>>>(enumClass);
    }


    /**
     * Adds a given search result to the current LDAP content model.
     *
     * @param searchResult the search result .
     */
    public void addSearchResult(@Nonnull final LdapSearchResult searchResult) {


        final Set<SearchResult> answerSet = searchResult.getAnswerSet();
        try {
            for (final SearchResult row : answerSet) {

                createLdapComponent(row, LdapNameUtils.parseSearchResult(row), _treeRoot);
            }
        } catch (final IndexOutOfBoundsException iooe) {
          _log.error("Tried to remove a name component with index out of bounds.", iooe);
        } catch (final InvalidNameException ie) {
            _log.error("Search result row could not be parsed by NameParser or removal of name component violates the syntax rules.", ie);
        } catch (final NamingException e) {
            _log.error("NameParser could not be obtained for LDAP Engine and CompositeName.", e);
        }
    }

    private void createLdapComponent(@Nonnull final SearchResult row,
                                     @Nonnull final LdapName fullName,
                                     @Nonnull final ILdapTreeComponent<T> root) throws InvalidNameException {
        ILdapTreeComponent<T> parent = root;

        final LdapName partialName = LdapNameUtils.removeRdns(fullName,
                                                              LdapFieldsAndAttributes.EFAN_FIELD_NAME,
                                                              Direction.FORWARD);

        final LdapName currentPartialName = new LdapName("");


        for (int i = 0; i < partialName.size(); i++) {

            final Rdn rdn = partialName.getRdn(i);
            currentPartialName.add(rdn);


            // Check whether this component exists already
            if (_cacheByLdapName.containsKey(currentPartialName.toString())) {
                if (i < partialName.size() - 1) { // another name component follows => has children
                    parent = (ILdapTreeComponent<T>) _cacheByLdapName.get(currentPartialName.toString());
                }
                continue; // YES
            }
            // NO

            final T oc = _objectClassRoot.getObjectClassByRdnType(rdn.getType());

            final ILdapTreeComponent<T> newChild =
                new LdapTreeComponent<T>((String) rdn.getValue(),
                                        oc,
                                        oc.getNestedContainerClasses(),
                                        parent,
                                        row.getAttributes(),
                                        currentPartialName);
            addChild(parent, newChild);

            parent = newChild;
            // CACHING
            _cacheByLdapName.put(newChild.getLdapName().toString(), newChild);
        }
    }


    private void addChild(@Nonnull final ILdapTreeComponent<T> parent, @Nonnull final ILdapComponent<T> newChild) {

        parent.addChild(newChild);

        // MORE CACHING
        final T type = newChild.getType();
        if (!_cacheByTypeAndLdapName.containsKey(type)) {
            _cacheByTypeAndLdapName.put(type, new HashMap<String, ILdapComponent<T>>());
        }
        final Map<String, ILdapComponent<T>> childrenByLdapName = _cacheByTypeAndLdapName.get(type);

        final String nameKey = newChild.getLdapName().toString();
        if (!childrenByLdapName.containsKey(nameKey)) {
            childrenByLdapName.put(nameKey, newChild); // updates the current map in cache by type
        }

        // AND EVEN MORE CACHING
        if (!_cacheByTypeAndSimpleName.containsKey(type)) {
            _cacheByTypeAndSimpleName.put(type, new HashMap<String, ILdapComponent<T>>());
        }
        final Map<String, ILdapComponent<T>> childrenBySimpleName = _cacheByTypeAndSimpleName.get(type);

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
        final Map<String, ILdapComponent<T>> children = _cacheByTypeAndSimpleName.get(type);

        return new HashSet<String>(children.keySet());
    }

    @CheckForNull
    public Map<String, ILdapComponent<T>> getChildrenByTypeAndLdapName(@Nonnull final T type) {
        return _cacheByTypeAndLdapName.get(type);
    }


    @CheckForNull
    public Map<String, ILdapComponent<T>> getChildrenByTypeAndSimpleName(@Nonnull final T type) {
        return _cacheByTypeAndSimpleName.get(type);
    }


    @CheckForNull
    public ILdapComponent<T> getByTypeAndLdapName(@Nonnull final T type, @Nonnull final String key) {
        final Map<String, ILdapComponent<T>> children = _cacheByTypeAndLdapName.get(type);
        return children != null ? children.get(key) : null;
    }


    @CheckForNull
    public ILdapComponent<T> getByTypeAndSimpleName(@Nonnull final T type, @Nonnull final String key) {
        final Map<String, ILdapComponent<T>> children = _cacheByTypeAndSimpleName.get(type);
        return children != null ? children.get(key) : null;
    }


    @Nonnull
    public ILdapTreeComponent<T> getRoot() {
        return _treeRoot;
    }

    public void clear() {
        _treeRoot = null;
        _cacheByLdapName.clear();
        _cacheByTypeAndLdapName.clear();
        _cacheByTypeAndSimpleName.clear();
    }
}
