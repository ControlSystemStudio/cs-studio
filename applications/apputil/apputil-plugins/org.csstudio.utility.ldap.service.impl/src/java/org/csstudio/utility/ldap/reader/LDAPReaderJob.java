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
package org.csstudio.utility.ldap.reader;

import javax.naming.ldap.LdapName;

import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.ILdapReadCompletedCallback;
import org.csstudio.utility.ldap.service.ILdapReaderJob;
import org.csstudio.utility.ldap.service.ILdapSearchParams;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.utils.LdapSearchParams;
import org.csstudio.utility.ldap.utils.LdapSearchResult;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LDAP read job. Creates a {@link LdapSearchResult} for a given query.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 08.04.2010
 */
public final class LDAPReaderJob extends Job implements ILdapReaderJob {

    private static final Logger LOG = LoggerFactory.getLogger(LDAPReaderJob.class);

    private final ILdapSearchParams _searchParams;

    private final ILdapSearchResult _searchResult;

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
        private ILdapSearchResult _searchResult;
        private ILdapReadCompletedCallback _callBack;

        /**
         * Constructor with required parameters.
         *
         * @param searchRoot the root
         * @param filter the filter
         */
        public Builder(final LdapName searchRoot,
                       final String filter) {
            _searchParams = new LdapSearchParams(searchRoot, filter);
            _searchResult = new LdapSearchResult();
        }

        /**
         * Setter.
         * @param scope the search scope of {@link javax.naming.directory.SearchControls}
         * @return
         */
            public Builder setScope(final int scope) {
            _searchParams.setScope(scope);
            return this;
        }

        /**
         * Setter.
         * @param callBack called on job completion
         * @return the builder for chaining
         */
            public Builder setJobCompletedCallBack(final ILdapReadCompletedCallback callBack) {
            _callBack = callBack;
            return this;
        }

        /**
         * Eventually constructs an LDAPReader instance.
         * @return the LDAP reader job
         */
            public LDAPReaderJob build() {
            return new LDAPReaderJob(this);
        }
    }


    @SuppressWarnings("synthetic-access")
    LDAPReaderJob(final Builder builder){
        super("LDAPReader");
        _searchParams = builder._searchParams;
        _searchResult = builder._searchResult;
        setJobCompletedCallBack(builder._callBack);
    }


    private void setJobCompletedCallBack(final ILdapReadCompletedCallback callBack) {
        if (callBack != null) {
            addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(final IJobChangeEvent event) {
                    if (event.getResult().isOK()) {
                        callBack.onLdapReadComplete();
                    }
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask("LDAP Reader", IProgressMonitor.UNKNOWN);

        final ILdapService service = ServiceLocator.getService(ILdapService.class);
        if (service == null) {
            return new Status(IStatus.ERROR, "LDAP service unavailable.", null);
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(_searchParams.getSearchRoot(),
                                                      _searchParams.getFilter(),
                                                      _searchParams.getScope());
        if (result == null || result.getAnswerSet().isEmpty()) {
            LOG.info("No results for LDAP search query.\n{}", _searchParams.toString());
        } else {
            _searchResult.setResult(_searchParams, result.getAnswerSet());
        }

        monitor.done();
        return Status.OK_STATUS;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public ILdapSearchResult getSearchResult() {
        return _searchResult;
    }
}
