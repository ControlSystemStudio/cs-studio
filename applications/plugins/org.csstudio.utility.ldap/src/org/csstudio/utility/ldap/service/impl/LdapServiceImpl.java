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


import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.attributesForLdapEntry;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapFieldsAndAttributes;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.LdapService;


/**
 * Service implementation for the LDAP access.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 09.04.2010
 */
public final class LdapServiceImpl implements LdapService {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    /**
     * Service holder for lazy synchronized instantiation.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 20.04.2010
     */
    private static final class LdapServiceImplHolder {

        private static final LdapService INSTANCE = new LdapServiceImpl();

        private LdapServiceImplHolder() {
            // Empty
        }
    }

    /**
     * Constructor.
     */
    private LdapServiceImpl() {
        // Empty
    }

    @Override
    @CheckForNull
    public LdapSearchResult retrieveSearchResult(@Nonnull final String searchRoot,
                                                 @Nonnull final String filter,
                                                 final int searchScope) {

        final LDAPReader job = createLdapReaderAndScheduleJob(searchRoot, filter, searchScope);

        try {
            // FIXME (bknerr) : timeout !!!
            job.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        return job.getSearchResult();
    }



    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IOC getIOCForRecordName(@Nonnull final String recordName) {
        if (recordName.isEmpty()) {
            return null;
        }

        final LdapSearchResult result =
            retrieveSearchResult(LdapFieldsAndAttributes.OU_FIELD_NAME + LdapFieldsAndAttributes.FIELD_ASSIGNMENT + LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE,
                                 LdapFieldsAndAttributes.EREN_FIELD_NAME + LdapFieldsAndAttributes.FIELD_ASSIGNMENT + recordName,
                                 SearchControls.SUBTREE_SCOPE);
        final LdapContentModel model = new LdapContentModel(result);

        final Set<IOC> iocs = model.getIOCs();

        if (iocs.size() > 1) {
            throw new IllegalStateException("For record name " + recordName + " more than one IOC could be identified!");
        }
        if (iocs.isEmpty()) {
            return null;
        }
        return iocs.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LdapSearchResult retrieveRecords(@Nonnull final String facilityName,
                                            @Nonnull final String iocName) throws InterruptedException {

        final String query = createLdapQuery(LdapFieldsAndAttributes.ECON_FIELD_NAME, iocName,
                                             LdapFieldsAndAttributes.ECOM_FIELD_NAME, LdapFieldsAndAttributes.ECOM_FIELD_VALUE,
                                             LdapFieldsAndAttributes.EFAN_FIELD_NAME, facilityName,
                                             LdapFieldsAndAttributes.OU_FIELD_NAME, LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE);

        return retrieveSearchResult(query, any(LdapFieldsAndAttributes.EREN_FIELD_NAME), SearchControls.ONELEVEL_SCOPE);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLDAPRecord(@Nonnull final DirContext context,
                                    @Nonnull final IOC ioc,
                                    @Nonnull final String recordName) {

        final String query = createLdapQuery(LdapFieldsAndAttributes.EREN_FIELD_NAME, recordName,
                                             LdapFieldsAndAttributes.ECON_FIELD_NAME, ioc.getName(),
                                             LdapFieldsAndAttributes.ECOM_FIELD_NAME, LdapFieldsAndAttributes.ECOM_FIELD_VALUE,
                                             LdapFieldsAndAttributes.EFAN_FIELD_NAME, ioc.getGroup(),
                                             LdapFieldsAndAttributes.OU_FIELD_NAME, LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE);

        final Attributes afe =
            attributesForLdapEntry(LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS, LdapFieldsAndAttributes.ATTR_VAL_OBJECT_CLASS,
                                   LdapFieldsAndAttributes.EREN_FIELD_NAME, recordName);
        try {
            context.bind(query, null, afe);
            _log.info( "Record written: " + query);
        } catch (final NamingException e) {
            _log.warn( "Naming Exception while trying to bind: " + query);
            _log.warn(e.getExplanation());
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIocEntryFromLdap(@Nonnull final DirContext context, @Nonnull final IOC ioc) {
        removeIocEntryFromLdap(context, ioc.getName(), ioc.getGroup());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpIocEntryInLdap(@Nonnull final DirContext context,
                                     @Nonnull final String iocName,
                                     @Nonnull final String facilityName,
                                     @Nonnull final Set<Record> validRecords)  {

        try {
            final LdapSearchResult searchResult = retrieveRecords(facilityName, iocName);
            final LdapContentModel model = new LdapContentModel(searchResult);

            final IOC ioc = model.getIOC(facilityName, iocName);
            final Set<Record> ldapRecords = ioc.getRecordValues();

            ldapRecords.removeAll(validRecords); // removes all that are valid (that are in the IOC file)

            // for all remaining records
            for (final Record record : ldapRecords) {
                removeRecordEntryFromLdap(Engine.getInstance().getLdapDirContext(), ioc, record);
                _log.info("Tidying: Record " + record.getName() + " removed.");
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("null")
    @Override
    public void removeIocEntryFromLdap(@Nonnull final DirContext context,
                                       @Nonnull final String iocName,
                                       @Nonnull final String facilityName) {


        LdapContentModel model = null;
        try {
            model = new LdapContentModel(retrieveRecords(facilityName, iocName));
        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }


        final IOC ioc = model.getIOC(facilityName, iocName);

        final Map<String, Record> records = ioc.getRecords();
        for (final Record record : records.values()) {
            removeRecordEntryFromLdap(context, ioc , record);
        }

        final String query = createLdapQuery(LdapFieldsAndAttributes.ECON_FIELD_NAME, iocName,
                                             LdapFieldsAndAttributes.ECOM_FIELD_NAME, LdapFieldsAndAttributes.ECOM_FIELD_VALUE,
                                             LdapFieldsAndAttributes.EFAN_FIELD_NAME, facilityName,
                                             LdapFieldsAndAttributes.OU_FIELD_NAME, LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE);
        removeEntryFromLdap(context, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRecordEntryFromLdap(@Nonnull final DirContext context,
                                          @Nonnull final IOC ioc,
                                          @Nonnull final Record record) {

        final String query = createLdapQuery(LdapFieldsAndAttributes.EREN_FIELD_NAME, record.getName(),
                                             LdapFieldsAndAttributes.ECON_FIELD_NAME, ioc.getName(),
                                             LdapFieldsAndAttributes.ECOM_FIELD_NAME, LdapFieldsAndAttributes.ECOM_FIELD_VALUE,
                                             LdapFieldsAndAttributes.EFAN_FIELD_NAME, ioc.getGroup(),
                                             LdapFieldsAndAttributes.OU_FIELD_NAME, LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE);
        removeEntryFromLdap(context, query);
    }

    /**
     * @param context
     * @param query
     */
    private void removeEntryFromLdap(@Nonnull final DirContext context,
                                     @Nonnull final String query) {
        try {
            context.unbind(query);
            _log.info("Entry removed from LDAP: " + query);
        } catch (final NamingException e) {
            _log.warn("Naming Exception while trying to unbind: " + query);
            _log.warn(e.getExplanation());
        }
    }


    @Nonnull
    private LDAPReader createLdapReaderAndScheduleJob(@Nonnull final String searchRoot,
                                                      @Nonnull final String filter,
                                                      final int searchScope) {
        final LDAPReader ldapr =
            new LDAPReader(searchRoot,
                           filter,
                           searchScope
            );
        ldapr.schedule();

        return ldapr;
    }


    /**
     * Returns the singleton.
     * @return The service instance.
     */
    @Nonnull
    public static LdapService getInstance() {
        return LdapServiceImplHolder.INSTANCE;

    }



}
