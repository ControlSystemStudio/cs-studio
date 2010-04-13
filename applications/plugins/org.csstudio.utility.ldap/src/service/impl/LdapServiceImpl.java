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
package service.impl;

import static org.csstudio.utility.ldap.LdapUtils.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapUtils.ATTR_VAL_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapUtils.ECOM_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.ECOM_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapUtils.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.attributesForLdapEntry;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;

import service.LdapService;

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

    private static LdapService INSTANCE;


    /**
     * Constructor.
     */
    private LdapServiceImpl() {
        // Empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapSearchResult retrieveSearchResult(final String searchRoot,
                                                 final String filter,
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




    @Override
    public IOC getIOCForRecordName(final String recordName) {
        if ((recordName == null) || recordName.isEmpty()) {
            return null;
        }

        final LdapSearchResult result =
            retrieveSearchResult(OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
                                 EREN_FIELD_NAME + FIELD_ASSIGNMENT + recordName,
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
    public LdapSearchResult retrieveRecords(final String facilityName,
                                            final String iocName) throws InterruptedException {

        final String query = createLdapQuery(ECON_FIELD_NAME, iocName,
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, facilityName,
                                             OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);

        return retrieveSearchResult(query, any(EREN_FIELD_NAME), SearchControls.ONELEVEL_SCOPE);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLDAPRecord(final DirContext context,
                                    final IOC ioc,
                                    final String recordName) {

        final String query = createLdapQuery(EREN_FIELD_NAME, recordName,
                                             ECON_FIELD_NAME, ioc.getName(),
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, ioc.getGroup(),
                                             OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_OBJECT_CLASS,
                                   EREN_FIELD_NAME, recordName);
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
    public void removeIocEntryFromLdap(final DirContext context, final IOC ioc) {
        removeIocEntryFromLdap(context, ioc.getName(), ioc.getGroup());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpIocEntryInLdap(final DirContext context,
                                     final String iocName,
                                     final String facilityName,
                                     final Set<Record> validRecords)  {

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
    public void removeIocEntryFromLdap(final DirContext context, final String iocName, final String facilityName) {


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

        final String query = createLdapQuery(ECON_FIELD_NAME, iocName,
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, facilityName,
                                             OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        removeEntryFromLdap(context, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRecordEntryFromLdap(final DirContext context, final IOC ioc, final Record record) {

        final String query = createLdapQuery(EREN_FIELD_NAME, record.getName(),
                                             ECON_FIELD_NAME, ioc.getName(),
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, ioc.getGroup(),
                                             OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        removeEntryFromLdap(context, query);
    }

    /**
     * @param context
     * @param query
     */
    private void removeEntryFromLdap(final DirContext context, final String query) {
        try {
            context.unbind(query);
            _log.info("Entry removed from LDAP: " + query);
        } catch (final NamingException e) {
            _log.warn("Naming Exception while trying to unbind: " + query);
            _log.warn(e.getExplanation());
        }
    }


    private LDAPReader createLdapReaderAndScheduleJob(final String searchRoot,
                                                 final String filter,
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
     * @return The service instance.
     */
    public static LdapService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LdapServiceImpl();
        }
        return INSTANCE;

    }



}
