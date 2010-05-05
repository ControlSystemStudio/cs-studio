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
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.ILdapObjectClass;
import org.csstudio.utility.ldap.LdapFieldsAndAttributes;
import org.csstudio.utility.ldap.engine.Engine;
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

    private final ILdapTreeComponent<T> _treeRoot;

//    private final Map<T, Boolean> _cacheTypeDirty;

    private final Map<T, Map<String, ILdapComponent<T>>> _cacheByType;


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

        _treeRoot = new LdapTreeComponent<T>(objectClassRoot.getDescription(),
                                             objectClassRoot,
                                             objectClassRoot.getNestedContainerClasses() ,
                                             null,
                                             null,
                                             null);

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

        final Set<SearchResult> answerSet = searchResult.getAnswerSet();
        NameParser nameParser;
        try {
            nameParser = Engine.getInstance().getLdapDirContext().getNameParser(new CompositeName());
            for (final SearchResult row : answerSet) {
                final LdapName fullName = (LdapName) nameParser.parse(row.getNameInNamespace());
                // TODO (bknerr) : remove from here to LdapNameUtils in package LDAP
                // remove any hierarchy level before 'efan=...'
                while ((fullName.size() > 0) && !fullName.get(0).startsWith(LdapFieldsAndAttributes.EFAN_FIELD_NAME)) {
                    fullName.remove(0);
                }

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

    private void createLdapComponent(final SearchResult row,
                                                      final LdapName fullName,
                                                      final ILdapTreeComponent<T> root) {
        ILdapTreeComponent<T> lastComponent = root;

        for (int i = 0; i < fullName.size(); i++) {
            final Rdn rdn = fullName.getRdn(i);
            final T oc = _objectClassRoot.getObjectClassByRdnType(rdn.getType());

            final ILdapComponent<T> newChild;
            if (oc.getNestedContainerClasses().isEmpty()) {
                newChild = new LdapComponent<T>((String) rdn.getValue(),
                                                oc,
                                                lastComponent,
                                                row.getAttributes(),
                                                fullName);
                addChild(lastComponent, newChild);
                break;
            }
            newChild = new LdapTreeComponent<T>((String) rdn.getValue(),
                                                oc,
                                                oc.getNestedContainerClasses(),
                                                lastComponent,
                                                row.getAttributes(),
                                                fullName);
            addChild(lastComponent, newChild);

            lastComponent = (ILdapTreeComponent<T>) newChild;
        }
    }


    private void addChild(@Nonnull final ILdapTreeComponent<T> lastComponent, @Nonnull final ILdapComponent<T> newChild) {

        final T type = newChild.getType();
        Map<String, ILdapComponent<T>> childrenByType = _cacheByType.get(type);

        if (childrenByType == null) {
            childrenByType = new HashMap<String, ILdapComponent<T>>();
        }

        final String nameKey = newChild.getName().toUpperCase();
        if (childrenByType.get(nameKey) == null) {
            childrenByType.put(nameKey, newChild);
            lastComponent.addChild(newChild);
            _cacheByType.put(type, childrenByType);
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
}
