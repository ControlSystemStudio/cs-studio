/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.service.impl;


import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.reader.LDAPReader.IJobCompletedCallBack;
import org.csstudio.utility.ldap.reader.LDAPReader.LdapSearchParams;
import org.csstudio.utility.ldap.service.ILdapService;


/**
 * Service implementation for the LDAP access.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 09.04.2010
 */
public final class LdapServiceImpl implements ILdapService {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LDAPReader createLdapReaderJob(@Nonnull final LdapSearchParams params,
                                          @Nullable final LdapSearchResult result,
                                          @Nullable final IJobCompletedCallBack callBack) {

            final LDAPReader ldapr =
                new LDAPReader.Builder(params.getSearchRoot(), params.getFilter()).
                                       setScope(params.getScope()).
                                       setSearchResult(result).
                                       setJobCompletedCallBack(callBack).
                                       build();

            return ldapr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LdapSearchResult retrieveSearchResultSynchronously(@Nonnull final LdapName searchRoot,
                                                              @Nonnull final String filter,
                                                              final int searchScope) {

        return LDAPReader.getSearchResultSynchronously(new LdapSearchParams(searchRoot, filter, searchScope));

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createComponent(@Nonnull final DirContext context,
                                   @Nonnull final LdapName newComponentName,
                                   @Nullable final Attributes attributes) {
        try {
            context.bind(newComponentName, null, attributes);
            _log.info( "Record written: " + newComponentName.toString());
        } catch (final NamingException e) {
            _log.warn( "Naming Exception while trying to bind: " + newComponentName.toString());
            _log.warn(e.getExplanation());
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean removeComponent(@Nonnull final DirContext context,
                                   @Nonnull final LdapName query) {
        try {
            context.unbind(query);
            _log.info("Entry removed from LDAP: " + query);
        } catch (final NamingException e) {
            _log.warn("Naming Exception while trying to unbind: " + query);
            _log.warn(e.getExplanation());
            return false;
        }
        return true;
    }

}
