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

    private final T _objectClassRoot;

    private ILdapTreeComponent<T> _treeRoot;

//    private final Map<T, Boolean> _cacheTypeDirty;

    private final Map<T, Map<String, ILdapComponent<T>>> _cacheByType;

    private Map<String, ILdapComponent<T>> _cacheByLdapName;


    /**
     * Constructor.
     * @param searchResult .
     * @param objectClassRoot .
     */
    public ContentModel(@Nonnull final LdapSearchResult searchResult,
                        @Nonnull final T objectClassRoot) {

//        _cacheTypeDirty = initCacheDirtyMap(objectClassRoot.getDeclaringClass());

        _cacheByType = initCacheByType(objectClassRoot.getDeclaringClass());


        _objectClassRoot = objectClassRoot;

        try {
            _treeRoot = new LdapTreeComponent<T>(objectClassRoot.getDescription(),
                                                 objectClassRoot,
                                                 objectClassRoot.getNestedContainerClasses() ,
                                                 null,
                                                 null,
                                                 null);
        } catch (final InvalidNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        addSearchResult(searchResult);
    }


    @Nonnull
    private Map<T, Map<String, ILdapComponent<T>>> initCacheByType(@Nonnull final Class<T> enumClass) {
        return new EnumMap<T, Map<String, ILdapComponent<T>>>(enumClass);
    }


//    @Nonnull
//    private Map<T, Boolean> initCacheDirtyMap(@Nonnull final Class<T> enumClass) {
//        final EnumMap<T, Boolean> dirtyMap = new EnumMap<T, Boolean>(enumClass);
//        for (final T type : enumClass.getEnumConstants()) {
//            dirtyMap.put(type, Boolean.TRUE);
//        }
//        return dirtyMap;
//    }


    /**
     * Adds a given search result to the current LDAP content model.
     *
     * @param searchResult the search result .
     */
    public void addSearchResult(@Nonnull final LdapSearchResult searchResult) {

        _cacheByLdapName = new HashMap<String, ILdapComponent<T>>();

        final Set<SearchResult> answerSet = searchResult.getAnswerSet();
        try {
            for (final SearchResult row : answerSet) {

                final LdapName fullName = LdapNameUtils.removeRdns(LdapNameUtils.parseSearchResult(row),
                                                                   LdapFieldsAndAttributes.EFAN_FIELD_NAME,
                                                                   Direction.FORWARD);

                createLdapComponent(row, fullName, _treeRoot);
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


        final LdapName currentFullName = new LdapName("");


        for (int i = 0; i < fullName.size(); i++) {

            final Rdn rdn = fullName.getRdn(i);
            currentFullName.add(rdn);


            // Check whether this component exists already
            if (_cacheByLdapName.containsKey(currentFullName.toString())) {
                if (i < fullName.size() - 1) { // another name component follows => has children
                    parent = (ILdapTreeComponent<T>) _cacheByLdapName.get(currentFullName.toString());
                }
                System.out.println("EXISTS");
                continue; // YES
            }
            // NO

            final ILdapComponent<T> newChild;
            final T oc = _objectClassRoot.getObjectClassByRdnType(rdn.getType());

            if (i == fullName.size() - 1) { // does not have children components
                newChild = new LdapComponent<T>((String) rdn.getValue(),
                                                oc,
                                                parent,
                                                row.getAttributes(),
                                                currentFullName);
                addChild(parent, newChild);
            } else {
                newChild = new LdapTreeComponent<T>((String) rdn.getValue(),
                        oc,
                        oc.getNestedContainerClasses(),
                        parent,
                        row.getAttributes(),
                        currentFullName);
                addChild(parent, newChild);

                parent = (ILdapTreeComponent<T>) newChild;
            }
            // CACHING
            _cacheByLdapName.put(newChild.getLdapName().toString(), newChild);
        }
    }


    private void addChild(@Nonnull final ILdapTreeComponent<T> parent, @Nonnull final ILdapComponent<T> newChild) {

        parent.addChild(newChild);

        // MORE CACHING
        final T type = newChild.getType();
        if (!_cacheByType.containsKey(type)) {
            _cacheByType.put(type, new HashMap<String, ILdapComponent<T>>());
        }
        final Map<String, ILdapComponent<T>> childrenByType = _cacheByType.get(type);

        final String nameKey = newChild.getLdapName().toString();
        if (!childrenByType.containsKey(nameKey)) {
            childrenByType.put(nameKey, newChild); // updates the current map in cache by type
        }
    }



    /**
     * Performs a recursive search over the full tree and gathers all children by the same type.
     * Attention, the children are mapped by their name identifier. It has to be unique over the
     * complete tree, otherwise this function hashes children with equal names to a single map entry.
     * @param type the type of the children
     * @return a copy of the keys of the children featuring the given type
     */
    @Nonnull
    public Set<String> getKeys(@Nonnull final T type) {
        final Map<String, ILdapComponent<T>> children = getChildrenByType(type);
        return new HashSet<String>(children.keySet());
    }


    @CheckForNull
    public ILdapComponent<T> get(@Nonnull final T type, @Nonnull final String key) {
        final Map<String, ILdapComponent<T>> childrenByType = getChildrenByType(type);
        return childrenByType.get(key);
    }


    @Nonnull
    public Map<String, ILdapComponent<T>> getChildrenByType(@Nonnull final T type) {
//        if (_cacheTypeDirty.get(type)) {
//            _cacheByType.put(type, _treeRoot.getChildrenByType(type));
//            _cacheTypeDirty.put(type, Boolean.FALSE);
//        }

        final Map<String, ILdapComponent<T>> children = _cacheByType.get(type);
        return children;
    }

    @Nonnull
    public ILdapTreeComponent<T> getRoot() {
        return _treeRoot;

    }
}
