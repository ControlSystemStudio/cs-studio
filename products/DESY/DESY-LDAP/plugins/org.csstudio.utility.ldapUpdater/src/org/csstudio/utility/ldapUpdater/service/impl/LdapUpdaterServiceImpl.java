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
package org.csstudio.utility.ldapUpdater.service.impl;

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ATTR_VAL_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECOM_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.attributesForLdapEntry;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapNameUtils;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.model.LdapEpicsControlsObjectClass;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldapUpdater.Activator;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;

/**
 * Implements access to LDAP Service for LdapUpdater application.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 05.05.2010
 */
public class LdapUpdaterServiceImpl implements ILdapUpdaterService {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    private final ILdapService _ldapService = Activator.getDefault().getLdapService();


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLDAPRecord(@Nonnull final DirContext context,
                                    @Nonnull final LdapName newLdapName) {

        final String recordName =
            LdapNameUtils.getValueOfRdnType(newLdapName, LdapEpicsControlsObjectClass.RECORD.getRdnType());

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_OBJECT_CLASS,
                                   EREN_FIELD_NAME, recordName);

        return _ldapService.createComponent(context, newLdapName, afe);
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
            _ldapService.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
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
    @CheckForNull
    public LdapSearchResult retrieveRecordsForIOC(@Nullable final LdapName ldapSuffix, @Nonnull final LdapName fullIocName) throws InterruptedException, InvalidNameException {
        if (fullIocName.size() > 0) {
            final LdapName query = new LdapName(fullIocName.getRdns());
            if (ldapSuffix != null) {
                query.addAll(0, ldapSuffix.getRdns());
            }
            return _ldapService.retrieveSearchResultSynchronously(query,
                                                                  any(EREN_FIELD_NAME),
                                                                  SearchControls.ONELEVEL_SCOPE);

        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LdapSearchResult retrieveRecordsForIOC(@Nonnull final String facilityName,
                                                  @Nonnull final String iocName) throws InterruptedException {

        final LdapName query = createLdapQuery(ECON_FIELD_NAME, iocName,
                                               ECOM_FIELD_NAME, ECOM_EPICS_IOC_FIELD_VALUE,
                                               EFAN_FIELD_NAME, facilityName,
                                               OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);

        return _ldapService.retrieveSearchResultSynchronously(query,
                                                              any(EREN_FIELD_NAME),
                                                              SearchControls.ONELEVEL_SCOPE);
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
            model = new LdapContentModel(retrieveRecordsForIOC(facilityName, iocName));
        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }


        final IOC ioc = model.getIOC(facilityName, iocName);

        final Map<String, Record> records = ioc.getRecords();
        for (final Record record : records.values()) {
            removeRecordEntryFromLdap(context, ioc , record);
        }

        final LdapName query = createLdapQuery(ECON_FIELD_NAME, iocName,
                                               ECOM_FIELD_NAME, ECOM_EPICS_IOC_FIELD_VALUE,
                                               EFAN_FIELD_NAME, facilityName,
                                               OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        _ldapService.removeComponent(context, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRecordEntryFromLdap(@Nonnull final DirContext context,
                                          @Nonnull final IOC ioc,
                                          @Nonnull final Record record) {

        final LdapName query = createLdapQuery(EREN_FIELD_NAME, record.getName(),
                                               ECON_FIELD_NAME, ioc.getName(),
                                               ECOM_FIELD_NAME, ECOM_EPICS_IOC_FIELD_VALUE,
                                               EFAN_FIELD_NAME, ioc.getGroup(),
                                               OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        _ldapService.removeComponent(context, query);
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
            final LdapSearchResult searchResult = retrieveRecordsForIOC(facilityName, iocName);
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
}
