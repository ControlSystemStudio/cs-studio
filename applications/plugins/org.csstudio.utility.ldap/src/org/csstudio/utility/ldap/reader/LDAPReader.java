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
import java.util.Hashtable;
import java.util.Set;

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

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    private String _searchRoot;
    private String _filter;

    private LdapSearchResult _resultList;

    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param name
     * @param filter
     * @param searchScope
     * @param resultList the list for the result {@link LdapSearchResult}
     */
    public LDAPReader(final String name,
                      final String filter,
                      final int searchScope,
                      final LdapSearchResult resultList){
        super("LDAPReader");
        setBasics(name, filter, resultList);
        setDefaultScope(searchScope);
    }


    /**
     * Constructor.
     *
     * @param name the search root
     * @param filter the search filter
     * @param resultList the list for the result {@link LdapSearchResult}
     * @param callBack callback to invoke custom method on sucessful LDAP read
     */
    public LDAPReader(final String name,
                      final String filter,
                      final LdapSearchResult resultList,
                      @Nonnull final JobCompletedCallBack callBack){
        super("LDAPReader");
        setBasics(name, filter, resultList);
        setJobCompletedCallBack(callBack);
    }

    /**
     * @param callBack
     */
    private void setJobCompletedCallBack(final JobCompletedCallBack callBack) {
        addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(@Nonnull final IJobChangeEvent event) {
                if (event.getResult().isOK()) {
                    callBack.onLdapReadComplete();
                }
            }
        });
    }



    /**
     * Need connection settings. (For Headless use)
     *
     * @param name
     * @param filter
     * @param searchScope
     * @param resultList the list for the result {@link LdapSearchResult}
     * @param env connection settings.
     */

    public LDAPReader(final String name, final String filter, final int searchScope, final LdapSearchResult resultList, final Hashtable<Object,String> env){
        super("LDAPReader");
        setBasics(name, filter, resultList);
        setDefaultScope(searchScope);
    }

    /**
     *
     * @param name
     * @param filter
     * @param searchScope
     * @param resultList
     * @param env value for<br>
     * 	0: Context.PROVIDER_URL<br>
     *  1: Context.SECURITY_PROTOCOL<br>
     *  2: Context.SECURITY_AUTHENTICATION<br>
     *  3: Context.SECURITY_PRINCIPAL<br>
     *  4: Context.SECURITY_CREDENTIALS<br>
     *
     */
    public LDAPReader(final String searchRoot, final String filter, final int searchScope, final LdapSearchResult resultList, final String[] env){
        super("LDAPReader");
        setBasics(searchRoot, filter, resultList);
        setDefaultScope(searchScope);
    }

    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param name
     * @param filter
     * @param ldapResultList the list for the result {@link LdapSearchResult}
     */
    public LDAPReader(final String name, final String filter, final LdapSearchResult ldapResultList){
        super("LDAPReader");
        setBasics(name, filter, ldapResultList);
    }

    /**
     * Need connection settings. (For Headless use)
     *
     * @param name
     * @param filter
     * @param resultList
     * @param env value for<br>
     * 	0: Context.PROVIDER_URL<br>
     *  1: Context.SECURITY_PROTOCOL<br>
     *  2: Context.SECURITY_AUTHENTICATION<br>
     *  3: Context.SECURITY_PRINCIPAL<br>
     *  4: Context.SECURITY_CREDENTIALS<br>
     *
     */
    public LDAPReader(final String name, final String filter,  final LdapSearchResult resultList, final String[] env){
        super("LDAPReader");
        setBasics(name, filter, resultList);
    }
    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param nameUFilter<br> 0: name<br>1: = filter<br>
     * @param searchScope set the Scope {@link SearchControls}
     * @param ergebnisListe the list for the result {@link LdapSearchResult}
     */

    public LDAPReader(final String[] nameUFilter, final int searchScope, final LdapSearchResult resultList){

        super("LDAPReader");

        setBasics(nameUFilter[0], nameUFilter[1], resultList);
        setDefaultScope(searchScope);
    }
    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param nameUFilter<br> 0: name<br>1: = filter<br>
     * @param resultList
     */
    public LDAPReader(final String[] nameUFilter, final LdapSearchResult resultList){
        super("LDAPReader");
        setBasics(nameUFilter[0], nameUFilter[1], resultList);
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor ) {
        monitor.beginTask("LDAP Reader", IProgressMonitor.UNKNOWN);
        final DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx !=null){
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
                    _log.info("LDAP Fehler " + e.getExplanation());
                }
                answer.close();

                _resultList.setResult(_searchRoot, _filter, answerSet);

                monitor.done();
                return Status.OK_STATUS;

            } catch (final NameNotFoundException nnfe){
                Engine.getInstance().reconnectDirContext();
                _log.info("Falscher LDAP Name oder so." + nnfe.getExplanation());
            } catch (final NamingException e) {
                Engine.getInstance().reconnectDirContext();
                _log.info("Falscher LDAP Suchpfad. " + e.getExplanation());
            }

        }
        monitor.setCanceled(true);
        _resultList.setResult(_searchRoot, _filter, Collections.<SearchResult>emptySet());
        return Status.CANCEL_STATUS;
    }

    /**
     * @param searchRoot
     * @param filter
     * @param resultList
     * @param ctx
     */
    private void setBasics(final String searchRoot, final String filter, final LdapSearchResult resultList) {
        _resultList = resultList;
        _searchRoot = searchRoot;
        _filter = filter;
    }

    public LdapSearchResult getResultList() {
        return _resultList;
    }

    /**
     * Set the Scope. @link SearchControls.
     * @param defaultScope set the given Scope.
     */
    private void setDefaultScope(final int defaultScope) {
        _defaultScope = defaultScope;
    }
}
