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
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ATTR_VAL_IOC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ATTR_VAL_RECORD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECOM_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.attributesForLdapEntry;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapNameUtils;
import org.csstudio.utility.ldap.model.LdapEpicsControlsObjectClass;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldapUpdater.Activator;
import org.csstudio.utility.ldapUpdater.LdapAccess;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;

/**
 * Implements access to LDAP Service for LdapUpdater application.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 05.05.2010
 */
public enum LdapUpdaterServiceImpl implements ILdapUpdaterService {

    INSTANCE;

    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUpdaterServiceImpl.class);

    private final ILdapService _ldapService = Activator.getDefault().getLdapService();


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLdapRecord(@Nonnull final LdapName newLdapName) {

        final String recordName =
            LdapNameUtils.getValueOfRdnType(newLdapName, LdapEpicsControlsObjectClass.RECORD.getNodeTypeName());

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_RECORD_OBJECT_CLASS,
                                   EREN_FIELD_NAME, recordName);

        return _ldapService.createComponent(newLdapName, afe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLdapIoc(@Nonnull final LdapName newLdapName, @Nullable final GregorianCalendar cal) {

        final String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime());

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_IOC_OBJECT_CLASS);
        // TODO (bknerr) : modify schema and add new attribute
//        ,
//                                   ATTR_FIELD_LAST_UPDATED, time,
//                                   ATTR_FIELD_LAST_UPDATED_IN_MILLIS, String.valueOf(cal.getTimeInMillis()));

        return _ldapService.createComponent(newLdapName, afe);
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
     * @throws InvalidNameException
     * @throws InterruptedException
     */
    @Override
    public void removeIocEntryFromLdap(@Nonnull final String iocName,
                                       @Nonnull final String facilityName) throws InvalidNameException,
                                                                                  InterruptedException {

        final LdapSearchResult recordsSearchResult = retrieveRecordsForIOC(iocName, facilityName);

        if (recordsSearchResult != null) {
            final LdapContentModelBuilder<LdapEpicsControlsObjectClass> builder =
                new LdapContentModelBuilder<LdapEpicsControlsObjectClass>(LdapEpicsControlsObjectClass.ROOT, recordsSearchResult);
            try {
                builder.build();
            } catch (final CreateContentModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final ContentModel<LdapEpicsControlsObjectClass> model = builder.getModel();

            final Collection<ISubtreeNodeComponent<LdapEpicsControlsObjectClass>> records =
                model.getChildrenByTypeAndLdapName(LdapEpicsControlsObjectClass.RECORD).values();

            for (final ISubtreeNodeComponent<LdapEpicsControlsObjectClass> record : records) {
                final LdapName ldapName = record.getLdapName();
                ldapName.addAll(0, LdapAccess.NAME_SUFFIX);

                _ldapService.removeComponent(ldapName);

            }
        }

        _ldapService.removeComponent(createLdapQuery(ECON_FIELD_NAME, iocName,
                                                     ECOM_FIELD_NAME, ECOM_EPICS_IOC_FIELD_VALUE,
                                                     EFAN_FIELD_NAME, facilityName,
                                                     OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpIocEntryInLdap(@Nonnull final String iocName,
                                     @Nonnull final String facilityName,
                                     @Nonnull final Set<Record> validRecords) {

        try {
            final LdapSearchResult searchResult = retrieveRecordsForIOC(facilityName, iocName);

            if (searchResult != null) {
                final LdapContentModelBuilder<LdapEpicsControlsObjectClass> builder =
                    new LdapContentModelBuilder<LdapEpicsControlsObjectClass>(LdapEpicsControlsObjectClass.ROOT, searchResult);
                builder.build();
                final ContentModel<LdapEpicsControlsObjectClass> model = builder.getModel();

                final Map<String, ISubtreeNodeComponent<LdapEpicsControlsObjectClass>> recordsInLdap =
                    model.getChildrenByTypeAndSimpleName(LdapEpicsControlsObjectClass.RECORD);

                for (final ISubtreeNodeComponent<LdapEpicsControlsObjectClass> record : recordsInLdap.values()) {
                    if (!validRecords.contains(new Record(record.getName()))) {

                        final LdapName ldapName = record.getLdapName();
                        ldapName.addAll(0, LdapAccess.NAME_SUFFIX);

                        _ldapService.removeComponent(ldapName);
                        LOG.info("Tidying: Record " + record.getName() + " removed.");
                    }
                }
            }
        } catch (final InterruptedException e) {
            LOG.error("Interrupted.", e);
            Thread.currentThread().interrupt();
        } catch (final CreateContentModelException e) {
            LOG.error("Error creating content model.");
        } catch (final InvalidNameException e) {
            LOG.error("Invalid name exception while adding name suffix on removal query.", e);
        }

    }
}
