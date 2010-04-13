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
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

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

    public static final int DEFAULT_SCOPE = SearchControls.SUBTREE_SCOPE;

    /**
     * Callback interface to invoke a custom method on a successful LDAP read.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 08.04.2010
     */
    public interface JobCompletedCallBack {
        /**
         * Callback method.
         */
        void onLdapReadComplete();
    }


    private int _defaultScope = DEFAULT_SCOPE;

    private static final Logger LOG = CentralLogger.getInstance().getLogger(LDAPReader.class.getName());

    private String _searchRoot;
    private String _filter;
    private final LdapSearchResult _searchResult;

    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param root the search root
     * @param filter the search filter
     * @param searchScope the search scope
     */
    public LDAPReader(@Nonnull final String root,
                      @Nonnull final String filter,
                      @Nonnull final int searchScope){
        super("LDAPReader");
        setBasics(root, filter);
        setDefaultScope(searchScope);
        _searchResult = new LdapSearchResult();
    }

    /**
     * Constructor.
     *
     * @param name the search root
     * @param filter the search filter
     * @param searchScope the search scope
     * @param result the search result, typically used in the callback method
     * @param callBack callback to invoke custom method on sucessful LDAP read
     */
    public LDAPReader(@Nonnull final String name,
                      @Nonnull final String filter,
                      final int searchScope,
                      @Nonnull final LdapSearchResult result,
                      @Nonnull final JobCompletedCallBack callBack){
        super("LDAPReader");
        setBasics(name, filter);
        setDefaultScope(searchScope);
        _searchResult = result;
        setJobCompletedCallBack(callBack);
    }

    /**
     * Constructor.
     *
     * @param name the search root
     * @param filter the search filter
     * @param result the result, typically used in the callback method
     * @param callBack callback to invoke custom method on sucessful LDAP read
     */
    public LDAPReader(@Nonnull final String name,
                      @Nonnull final String filter,
                      @Nonnull final LdapSearchResult result,
                      @Nonnull final JobCompletedCallBack callBack){
        this(name, filter, DEFAULT_SCOPE, result, callBack);
    }

    /**
     * @param callBack
     */
    private void setJobCompletedCallBack(@Nonnull final JobCompletedCallBack callBack) {
        addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(@Nonnull final IJobChangeEvent event) {
                if (event.getResult().isOK()) {
                    callBack.onLdapReadComplete();
                }
            }
        });
    }


    @Override
    protected final IStatus run(@Nonnull final IProgressMonitor monitor ) {
        monitor.beginTask("LDAP Reader", IProgressMonitor.UNKNOWN);

        final DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx != null){
            final SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(_defaultScope);
            try{
                final Set<SearchResult> answerSet = new HashSet<SearchResult>();

                final NamingEnumeration<SearchResult> answer = ctx.search(_searchRoot, _filter, ctrl);
                try {
                    while(answer.hasMore()){
                        final SearchResult result = answer.next();
                        answerSet.add(result);
                        if(monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                    }
                } catch (final NamingException e) {
                    LOG.info("LDAP Fehler " + e.getExplanation());
                }
                answer.close();

                _searchResult.setResult(_searchRoot, _filter, answerSet);

                monitor.done();
                return Status.OK_STATUS;

            } catch (final NameNotFoundException nnfe){
                Engine.getInstance().reconnectDirContext();
                LOG.info("Falscher LDAP Name oder so." + nnfe.getExplanation());
            } catch (final NamingException e) {
                Engine.getInstance().reconnectDirContext();
                LOG.info("Falscher LDAP Suchpfad. " + e.getExplanation());
            }

        }
        monitor.setCanceled(true);
        _searchResult.setResult(_searchRoot, _filter, Collections.<SearchResult>emptySet());
        return Status.CANCEL_STATUS;
    }

    private void setBasics(@Nonnull final String searchRoot,
                           @Nonnull final String filter) {
        _searchRoot = searchRoot;
        _filter = filter;
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
     *
     * @param searchRoot search root
     * @param filter search filter
     * @return the search result
     */
    @CheckForNull
    public static final LdapSearchResult getSynchronousSearchResult(@Nonnull final String searchRoot,
                                                                    @Nonnull final String filter) {

        final DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx !=null){
            final SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(DEFAULT_SCOPE);
            try{
                final LdapSearchResult result = new LdapSearchResult();
                final Set<SearchResult> answerSet = new HashSet<SearchResult>();

                final NamingEnumeration<SearchResult> answer = ctx.search(searchRoot, filter, ctrl);
                while(answer.hasMore()){
                    answerSet.add(answer.next());
                }
                answer.close();

                result.setResult(searchRoot, filter, answerSet);

                return result;

            } catch (final NameNotFoundException nnfe){
                Engine.getInstance().reconnectDirContext();
                LOG.info("Falscher LDAP Name oder so." + nnfe.getExplanation());
            } catch (final NamingException e) {
                Engine.getInstance().reconnectDirContext();
                LOG.info("Falscher LDAP Suchpfad. " + e.getExplanation());
            }
        }
        return null;
    }

    /**
     * Set the Scope. @link SearchControls.
     * @param defaultScope set the given Scope.
     */
    private void setDefaultScope(final int defaultScope) {
        _defaultScope = defaultScope;
    }
}
