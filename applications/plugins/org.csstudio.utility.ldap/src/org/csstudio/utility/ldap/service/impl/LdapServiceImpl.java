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


import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
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

    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapServiceImpl.class);

    private static DirContext CONTEXT = Engine.getInstance().getLdapDirContext();

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

        final LdapSearchParams params = new LdapSearchParams(searchRoot, filter, searchScope);
        if(CONTEXT != null){
            final SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(params.getScope());
            NamingEnumeration<SearchResult> answer = null;
            try {
                answer = CONTEXT.search(params.getSearchRoot(),
                                         params.getFilter(),
                                         ctrl);

                final Set<SearchResult> answerSet = new HashSet<SearchResult>();
                while(answer.hasMore()){
                    answerSet.add(answer.next());
                }

                final LdapSearchResult result = new LdapSearchResult();
                result.setResult(params, answerSet);
                return result;

            } catch (final NameNotFoundException nnfe){
                Engine.getInstance().reconnectDirContext();
                LOG.info("Wrong LDAP name?" + nnfe.getExplanation());
            } catch (final NamingException e) {
                Engine.getInstance().reconnectDirContext();
                LOG.info("Wrong LDAP query. " + e.getExplanation());
            } finally {
                try {
                    if (answer != null) {
                        answer.close();
                    }
                } catch (final NamingException e) {
                    LOG.warn("Error closing search results: ", e);
                }
            }
        }
        return null;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createComponent(@Nonnull final LdapName newComponentName,
                                   @Nullable final Attributes attributes) {
        try {
            CONTEXT.bind(newComponentName, null, attributes);
            LOG.info( "New LDAP Component: " + newComponentName.toString());
        } catch (final NamingException e) {
            LOG.warn( "Naming Exception while trying to bind: " + newComponentName.toString());
            LOG.warn(e.getExplanation());
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean removeLeafComponent(@Nonnull final LdapName component) {
        try {
            LOG.debug("Unbind entry from LDAP: " + component);
            CONTEXT.unbind(component);
        } catch (final NamingException e) {
            LOG.warn("Naming Exception while trying to unbind: " + component);
            LOG.warn(e.getExplanation());
            return false;
        }
        return true;
    }

//    /**
//     * {@inheritDoc}}
//     */
//    @Override
//    public boolean removeComponent(@Nonnull final LdapName component) {
//        try {
//            LOG.debug("Unbind entry from LDAP incl. its subtree: " + component);
//            final Object object = CONTEXT.lookup(component);
//
//
//            CONTEXT.unbind(component);
//        } catch (final NamingException e) {
//            LOG.warn("Naming Exception while trying to unbind: " + component);
//            LOG.warn(e.getExplanation());
//            return false;
//        }
//        return true;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyAttributes(@Nonnull final LdapName name,
                                 @Nonnull final ModificationItem[] mods) throws NamingException {
        LOG.debug("Modify entry for: " + name);
        CONTEXT.modifyAttributes(name, mods);
    }

    /**
     * {@inheritDoc}
     * @throws NamingException
     */
    @Override
    public void rename(@Nonnull final LdapName oldLdapName,
                       @Nonnull final LdapName newLdapName) throws NamingException {
        LOG.info("Rename entry from:\n" + oldLdapName.toString() + "\nto\n" + newLdapName.toString());
        CONTEXT.rename(oldLdapName, newLdapName);
    }

    /**
     * {@inheritDoc}
     * @throws NamingException
     */
    @Override
    public void move(@Nonnull final LdapName oldLdapName,
                     @Nonnull final LdapName newLdapName) throws NamingException {
        LOG.info("Move entry from:\n" + oldLdapName.toString() + "\nto\n" + newLdapName.toString());
        final Object o = CONTEXT.lookup(oldLdapName);
        CONTEXT.bind(newLdapName, o);
        removeLeafComponent(oldLdapName); // unbind not allowed on non-leaf
    }



    /**
     * {@inheritDoc}
     * @throws NamingException
     */
    @Override
    @CheckForNull
    public Attributes getAttributes(@Nonnull final LdapName ldapName) throws NamingException {
        return CONTEXT.getAttributes(ldapName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Object lookup(@Nonnull final LdapName name) throws NamingException {
        return CONTEXT.lookup(name);
    }


}
