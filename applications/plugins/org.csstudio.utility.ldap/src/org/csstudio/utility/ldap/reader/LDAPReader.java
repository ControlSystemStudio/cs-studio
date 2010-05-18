/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.reader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * The LDAP read job. Creates a {@link LdapSearchResult} for a given query.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 08.04.2010
 */
public class LDAPReader extends Job {

    /**
     * Search specifier for an LDAP lookup
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 26.04.2010
     */
    public static final class LdapSearchParams {

        private final LdapName _searchRoot;

        private final String _filter;

        private int _scope;


        /**
         * Constructor.
         * @param searchRoot .
         * @param filter .
         */
        public LdapSearchParams(@Nonnull final LdapName searchRoot,
                                @Nonnull final String filter) {
            this(searchRoot, filter, DEFAULT_SCOPE);
        }

        /**
         * Constructor.
         * @param searchRoot .
         * @param filter .
         * @param scope the search controls
         */
        public LdapSearchParams(@Nonnull final LdapName searchRoot,
                                @Nonnull final String filter,
                                @Nonnull final int scope) {
            _searchRoot = searchRoot;
            _filter = filter;
            _scope = scope;
        }
        @Nonnull
        public LdapName getSearchRoot() {
            return _searchRoot;
        }
        @Nonnull
        public String getFilter() {
            return _filter;
        }
        @Nonnull
        public int getScope() {
            return _scope;
        }

        public void setScope(final int scope) {
            _scope = scope;
        }
    }


    /**
     * Callback interface to invoke a custom method on a successful LDAP read.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 08.04.2010
     */
    public interface IJobCompletedCallBack {
        /**
         * Callback method.
         */
        void onLdapReadComplete();
    }

    public static final int DEFAULT_SCOPE = SearchControls.SUBTREE_SCOPE;


    private static final Logger LOG = CentralLogger.getInstance().getLogger(LDAPReader.class.getName());

    private final LdapSearchParams _searchParams;

    private final LdapSearchResult _searchResult;

    /**
     * The builder for the LDAPReader class.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 28.04.2010
     */
    public static class Builder {

        private final LdapSearchParams _searchParams;
        private LdapSearchResult _searchResult;
        private IJobCompletedCallBack _callBack;

        /**
         * Constructor with required parameters.
         *
         * @param searchRoot the root
         * @param filter the filter
         */
        public Builder(@Nonnull final LdapName searchRoot,
                       @Nonnull final String filter) {
            _searchParams = new LdapSearchParams(searchRoot, filter);
        }

        /**
         * Setter.
         * @param scope the search scope of {@link javax.naming.directory.SearchControls}
         * @return
         */
        @Nonnull
        public Builder setScope(final int scope) {
            _searchParams.setScope(scope);
            return this;
        }

        /**
         * Setter.
         * @param result reference to an existing search result
         * @return the builder for chaining
         */
        @Nonnull
        public Builder setSearchResult(@Nonnull final LdapSearchResult result) {
            _searchResult = result;
            return this;
        }

        /**
         * Setter.
         * @param callBack called on job completion
         * @return the builder for chaining
         */
        @Nonnull
        public Builder setJobCompletedCallBack(@Nonnull final IJobCompletedCallBack callBack) {
            _callBack = callBack;
            return this;
        }

        /**
         * Eventually constructs an LDAPReader instance.
         * @return the LDAP reader job
         */
        @Nonnull
        public LDAPReader build() {
            return new LDAPReader(this);
        }
    }


    private LDAPReader(@Nonnull final Builder builder){
        super("LDAPReader");
        _searchParams = builder._searchParams;
        _searchResult = builder._searchResult;
        setJobCompletedCallBack(builder._callBack);
    }



    private void setJobCompletedCallBack(@Nullable final IJobCompletedCallBack callBack) {
        if (callBack != null) {
            addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(@Nonnull final IJobChangeEvent event) {
                    if (event.getResult().isOK()) {
                        callBack.onLdapReadComplete();
                    }
                }
            });
        }
    }


    @Override
    protected final IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask("LDAP Reader", IProgressMonitor.UNKNOWN);

        final DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx != null){
            final SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(_searchParams.getScope());

            NamingEnumeration<SearchResult> answer = null;
            try {
                answer = ctx.search(_searchParams.getSearchRoot(),
                                    _searchParams.getFilter(),
                                    ctrl);
                final Set<SearchResult> answerSet = new HashSet<SearchResult>();
                while(answer.hasMore()){
                    final SearchResult result = answer.next();
                    answerSet.add(result);
                    if(monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                }
                _searchResult.setResult(_searchParams, answerSet);

                monitor.done();
                return Status.OK_STATUS;

            } catch (final NameNotFoundException nnfe){
                Engine.getInstance().reconnectDirContext();
                LOG.info("Falscher LDAP Name oder so." + nnfe.getExplanation());
            } catch (final NamingException e) {
                Engine.getInstance().reconnectDirContext();
                LOG.info("LDAP Suche Fehler: " + e.getExplanation());
            } finally {
                try {
                    if (answer != null) {
                        answer.close();
                    }
                } catch (final NamingException e) {
                    LOG.warn("Error closing search results", e);
                }
            }
        }
        _searchResult.setResult(_searchParams, Collections.<SearchResult>emptySet());

        monitor.setCanceled(true);
        return Status.CANCEL_STATUS;
    }

    /**
     * Returns the search result of the last LDAP lookup.
     * @return the search result object, might be null.
     */
    @CheckForNull
    public LdapSearchResult getSearchResult() {
        return _searchResult;
    }


    /**
     * Retrieves a search result from LDAP synchronously (without being scheduled as job).
     * This method blocks until the LDAP read has been performed.
     *
     * @param searchRoot search root
     * @param filter search filter
     * @return the search result
     */
    @CheckForNull
    public static final LdapSearchResult getSearchResultSynchronously(@Nonnull final LdapSearchParams params) {

        final DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx != null){
            final SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(params.getScope());
            NamingEnumeration<SearchResult> answer = null;
            try {
                answer = ctx.search(params.getSearchRoot(),
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
                LOG.info("Falscher LDAP Name oder so." + nnfe.getExplanation());
            } catch (final NamingException e) {
                Engine.getInstance().reconnectDirContext();
                LOG.info("Falscher LDAP Suchpfad. " + e.getExplanation());
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
}
