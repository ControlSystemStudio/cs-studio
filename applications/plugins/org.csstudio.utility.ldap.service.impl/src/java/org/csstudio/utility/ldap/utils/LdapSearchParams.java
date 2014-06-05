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
 */
package org.csstudio.utility.ldap.utils;

import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.csstudio.utility.ldap.service.ILdapSearchParams;

/**
 * Search specifier for an LDAP lookup.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 26.04.2010
 */
public final class LdapSearchParams implements ILdapSearchParams {

    private final LdapName _searchRoot;

    private final String _filter;

    private int _scope;


    /**
     * Constructor.
     * Default search scope is <code>SearchControls.SUBTREE_SCOPE</code>.
     *
     * @param searchRoot .
     * @param filter .
     */
    public LdapSearchParams(final LdapName searchRoot,
                            final String filter) {
        this(searchRoot, filter, SearchControls.SUBTREE_SCOPE);
    }

    /**
     * Constructor.
     * @param searchRoot .
     * @param filter .
     * @param scope the search controls
     */
    public LdapSearchParams(final LdapName searchRoot,
                            final String filter,
                            final int scope) {
        _searchRoot = searchRoot;
        _filter = filter;
        _scope = scope;
    }

    @Override
    public LdapName getSearchRoot() {
        return _searchRoot;
    }
    @Override
    public String getFilter() {
        return _filter;
    }
    @Override
    public int getScope() {
        return _scope;
    }
    public void setScope(final int scope) {
        _scope = scope;
    }

    @Override
    public String toString() {
        return "Root: " +_searchRoot + ", filter: " + _filter + ", scope: " + _scope;
    }
}
