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

import static org.csstudio.utility.ldap.service.util.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.service.util.LdapFieldsAndAttributes.ATTR_VAL_IOC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.service.util.LdapFieldsAndAttributes.ATTR_VAL_REC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.attributesForLdapEntry;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.VIRTUAL_ROOT;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.csstudio.utility.ldapUpdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;

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


    @Nonnull
    private ILdapService getLdapService() throws ServiceUnavailableException {
        final ILdapService service = LdapUpdaterActivator.getDefault().getLdapService();
        if (service == null) {
            throw new ServiceUnavailableException("LDAP service is unavailable.");
        }
        return service;
    }

    /**
     * {@inheritDoc}
     * @throws ServiceUnavailableException
     */
    @Override
    public boolean createLdapRecord(@Nonnull final LdapName newLdapName) throws ServiceUnavailableException {

        final String recordName =
            LdapNameUtils.getValueOfRdnType(newLdapName, RECORD.getNodeTypeName());

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_REC_OBJECT_CLASS,
                                   RECORD.getNodeTypeName(), recordName);

        final ILdapService service = getLdapService();
        return service.createComponent(newLdapName, afe);
    }


    /**
     * {@inheritDoc}
     * @throws ServiceUnavailableException
     */
    @Override
    public boolean createLdapIoc(@Nonnull final LdapName newLdapName, @Nullable final GregorianCalendar cal) throws ServiceUnavailableException {

//        final String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime());

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_IOC_OBJECT_CLASS);
        // TODO (bknerr) : modify schema and add new attribute
//        ,
//                                   ATTR_FIELD_LAST_UPDATED, time,
//                                   ATTR_FIELD_LAST_UPDATED_IN_MILLIS, String.valueOf(cal.getTimeInMillis()));

        final ILdapService service = getLdapService();
        return service.createComponent(newLdapName, afe);
    }


    /**
     * {@inheritDoc}
     * @throws ServiceUnavailableException
     */
    @Override
    @CheckForNull
    public ILdapSearchResult retrieveRecordsForIOC(@Nonnull final LdapName fullIocName)
        throws InterruptedException, InvalidNameException, ServiceUnavailableException {

        if (fullIocName.size() > 0) {
            final LdapName query = new LdapName(fullIocName.getRdns());
            final ILdapService service = getLdapService();
            return service.retrieveSearchResultSynchronously(query,
                                                             any(RECORD.getNodeTypeName()),
                                                             SearchControls.ONELEVEL_SCOPE);

        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws ServiceUnavailableException
     */
    @Override
    @CheckForNull
    public ILdapSearchResult retrieveRecordsForIOC(@Nonnull final String facilityName,
                                                   @Nonnull final String iocName) throws InterruptedException, ServiceUnavailableException {

        final LdapName query = createLdapName(IOC.getNodeTypeName(), iocName,
                                               COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                               FACILITY.getNodeTypeName(), facilityName,
                                               UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

        final ILdapService service = getLdapService();
        return service.retrieveSearchResultSynchronously(query,
                                                         any(RECORD.getNodeTypeName()),
                                                         SearchControls.ONELEVEL_SCOPE);
    }


    /**
     * {@inheritDoc}
     * @throws InvalidNameException
     * @throws InterruptedException
     * @throws ServiceUnavailableException
     */
    @Override
    public void removeIocEntryFromLdap(@Nonnull final String iocName,
                                       @Nonnull final String facilityName) throws InvalidNameException,
                                                                                  InterruptedException,
                                                                                  ServiceUnavailableException {

        final ILdapSearchResult recordsSearchResult = retrieveRecordsForIOC(iocName, facilityName);

        final ILdapService service = getLdapService();
        if (recordsSearchResult != null) {

            final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
                service.getLdapContentModelBuilder(VIRTUAL_ROOT, recordsSearchResult);

            try {
                builder.build();
            } catch (final CreateContentModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();
            if (model == null) {
                LOG.warn("LDAP Content model could not be filled by search result. Removing cancelled.");
                return;
            }

            final Collection<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> records =
                model.getChildrenByTypeAndLdapName(RECORD).values();

            for (final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> record : records) {
                final LdapName ldapName = record.getLdapName();

                service.removeLeafComponent(ldapName);

            }
        }

        service.removeLeafComponent(createLdapName(IOC.getNodeTypeName(), iocName,
                                                    COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                                    FACILITY.getNodeTypeName(), facilityName,
                                                    UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()));
    }

    /**
     * {@inheritDoc}
     * @throws ServiceUnavailableException
     */
    @Override
    public void tidyUpIocEntryInLdap(@Nonnull final String iocName,
                                     @Nonnull final String facilityName,
                                     @Nonnull final Set<Record> validRecords) throws ServiceUnavailableException {

        try {
            final ILdapSearchResult searchResult = retrieveRecordsForIOC(facilityName, iocName);

            if (searchResult != null) {
                final ILdapService service = getLdapService();
                final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder = 
                    service.getLdapContentModelBuilder(VIRTUAL_ROOT, searchResult);

                builder.build();
                final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();

                if (model == null) {
                    LOG.info("Empty content model for this searchRestult - no tidying possible.");
                    return;
                }

                removeLeafComponents(validRecords, model);
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(@Nonnull final ContentModel<T> model) throws ServiceUnavailableException {

        final ILdapService service = getLdapService();
        return service.getLdapContentModelBuilder(model);
    }

    private void removeLeafComponents(@Nonnull final Set<Record> validRecords,
                                      @Nonnull final ContentModel<LdapEpicsControlsConfiguration> model)
            throws InvalidNameException,
                   ServiceUnavailableException {

        final Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> recordsInLdap =
            model.getChildrenByTypeAndSimpleName(RECORD);

        final ILdapService service = getLdapService();

        for (final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> record : recordsInLdap.values()) {
            if (!validRecords.contains(new Record(record.getName()))) {

                final LdapName ldapName = record.getLdapName();

                service.removeLeafComponent(ldapName);
                LOG.info("Tidying: Record " + record.getName() + " removed.");
            }
        }
    }
}
