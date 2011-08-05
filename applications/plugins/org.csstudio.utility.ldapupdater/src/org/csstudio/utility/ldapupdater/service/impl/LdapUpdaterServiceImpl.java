/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapupdater.service.impl;

import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.utility.ldap.service.util.LdapNameUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.UpdaterLdapConstants;
import org.csstudio.utility.ldapupdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapupdater.mail.NotificationMailer;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.csstudio.utility.ldapupdater.model.Record;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.csstudio.utility.ldapupdater.service.ILdapFacade;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterFileService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapupdater.service.LdapFacadeException;
import org.csstudio.utility.ldapupdater.service.LdapUpdaterServiceException;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.INodeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;


/**
 * LDAP Updater access class to encapsulate specific updater access.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 */
public final class LdapUpdaterServiceImpl implements ILdapUpdaterService {


    private static final Logger LOG = LoggerFactory.getLogger(LdapUpdaterServiceImpl.class);

    /**
     * Update Result.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 13.04.2010
     */
    private static class UpdateIOCResult {
        private final int _numOfRecsWritten;
        private final boolean _noError;
        private final int _numOfRecsInFile;
        private final int _numOfRecsInLDAP;


        public UpdateIOCResult(final int numOfRecsInFile,
                               final int numOfRecsWritten,
                               final int numOfRecordsInLDAP,
                               final boolean noError) {
            _numOfRecsInFile = numOfRecsInFile;
            _numOfRecsWritten = numOfRecsWritten;
            _numOfRecsInLDAP = numOfRecordsInLDAP;
            _noError = noError;
        }

        public int getNumOfRecsInFile() {
            return _numOfRecsInFile;
        }

        public int getNumOfRecsWritten() {
            return _numOfRecsWritten;
        }

        public boolean hasNoError() {
            return _noError;
        }

        public int getNumOfRecsInLDAP() {
            return _numOfRecsInLDAP;
        }
    }

    private final ILdapFacade _facade;
    private final ILdapUpdaterFileService _fileService;
    private final LdapUpdaterPreferencesService _prefsService;

    /**
     * Don't instantiate.
     */
    @Inject
    public LdapUpdaterServiceImpl(@Nonnull final ILdapFacade facade,
                                  @Nonnull final ILdapUpdaterFileService fileService,
                                  @Nonnull final LdapUpdaterPreferencesService prefService) {
        _facade = facade;
        _fileService = fileService;
        _prefsService = prefService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpLDAPFromIOCList(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdap,
                                      @Nonnull final Map<String, IOC> iocMapFromFS) throws LdapUpdaterServiceException {

        for (final INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap : iocsFromLdap.values()) {

            final String iocFromLdapName = iocFromLdap.getName();
            final String facFromLdapName =
                LdapNameUtils.getValueOfRdnType(iocFromLdap.getLdapName(), FACILITY.getNodeTypeName());

            if (facFromLdapName == null) {
                LOG.warn("Facility name could not be retrieved for " + iocFromLdap.getLdapName().toString());
                continue;
            }

            try {
                if (iocMapFromFS.containsKey(iocFromLdapName)) {

                    final Set<Record> validRecords = _fileService.getBootRecordsFromIocFile(iocFromLdapName);
                    _facade.tidyUpIocEntryInLdap(iocFromLdapName,
                                                 facFromLdapName,
                                                 validRecords);

                } else { // LDAP entry is not contained in current IOC directory - is considered obsolete!
                    _facade.removeIocEntryFromLdap(iocFromLdapName, facFromLdapName);
                }
            } catch (final LdapFacadeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Nonnull
    private UpdateIOCResult updateIocInLdapWithRecordsFromBootFile(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> recordMapFromLdap,
                                                                   @Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP) throws LdapUpdaterServiceException {

        final String iocName = iocFromLDAP.getName();

        final Set<Record> recordsFromFile = _fileService.getBootRecordsFromIocFile(iocName);

        final StringBuilder forbiddenRecords = new StringBuilder();
        LOG.info( "Process IOC " + iocName + "\t\t #records " +  recordsFromFile.size());
        int numOfRecsWritten = 0;
        try {
            for (final Record recFromFile : recordsFromFile) {
                if (!recordMapFromLdap.containsKey(recFromFile.getName())) {
                    numOfRecsWritten += createNewRecordEntry(iocFromLDAP,
                                                             forbiddenRecords,
                                                             recFromFile);
                }
            }
            NotificationMailer.sendUnallowedCharsNotification(iocFromLDAP, iocName, forbiddenRecords);
        } catch (final NamingException e) {
            throw new LdapUpdaterServiceException("LDAP name creation failed on updating records for IOC " + iocName, e);
        } catch (final LdapFacadeException e) {
            throw new LdapUpdaterServiceException("Creating new records failed on updating IOC " + iocName, e);
        }

        return new UpdateIOCResult(recordsFromFile.size(),
                                   numOfRecsWritten,
                                   -1,
                                   true);
    }

    private int createNewRecordEntry(@Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
                                     @Nonnull final StringBuilder forbiddenRecords,
                                     @Nonnull final Record recordFromFS) throws InvalidNameException, LdapFacadeException {

        final String recordFromFSName = recordFromFS.getName();

        LOG.info("New record for LDAP: " + recordFromFSName);

        if (!LdapNameUtils.filterName(recordFromFSName)) {

            final LdapName newLdapName = new LdapName(iocFromLDAP.getLdapName().getRdns());
            newLdapName.add(new Rdn(RECORD.getNodeTypeName(), recordFromFSName));

            if (!_facade.createLdapRecord(newLdapName)) {
                LOG.error("Error while updating LDAP record for " + recordFromFSName +
                "\nProceed with next record.");
            }
            return 1;
        }
        LOG.warn("Record " + recordFromFSName + " could not be written. Unallowed characters!");
        forbiddenRecords.append(recordFromFSName + "\n");
        return 0;
    }


    /**
     * This method compares the contents of the current LDAP hierarchy with the contents found in
     * the directory, where the IOC files reside. The contents of the ioc list are firstly checked
     * whether they are more recent than those stored in the history file, if not so the ioc file
     * has already been processed. If so, the LDAP is updated with the newer content of the ioc
     * files conservatively, i.e. by adding references to records, but not removing entries
     * from the LDAP in case the corresponding file does not exist in the ioc directory.

     * @param iocs the current LDAP content model
     * @param iocMap
     *            the list of ioc filenames as found in the ioc directory
     * @param historyFileModel
     *            the contents of the history file
     * @throws LdapUpdaterServiceException
     */
    @Override
    public void updateLDAPFromIOCList(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocMapFromLdap,
                                      @Nonnull final Map<String, IOC> iocMapFromFS,
                                      @Nonnull final TimeInstant lastHeartBeat) throws LdapUpdaterServiceException {

        for (final Entry<String, IOC> entry : iocMapFromFS.entrySet()) {

            final String iocFromFSName = entry.getKey();
            final IOC iocFromFS = entry.getValue();

            if (iocFromFS.getLastBootTime().isBefore(lastHeartBeat)) {
                LOG.debug("IOC file for " + iocFromFSName
                          + " is not newer than history file time stamp.");
                continue;
            }
            createOrUpdateIocInLdap(iocMapFromLdap, iocFromFS);
        }
    }

    private void createOrUpdateIocInLdap(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocMapFromLdap,
                                         @Nonnull final IOC iocFromFS) throws LdapUpdaterServiceException {
        INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap = iocMapFromLdap.get(iocFromFS.getName());
        if (iocFromLdap == null) {
            iocFromLdap = createIocInLdap(iocFromFS, iocMapFromLdap);
        }

        final LdapName iocFromLdapName = iocFromLdap.getLdapName();

        Map<String, INodeComponent<LdapEpicsControlsConfiguration>> recordMapFromLdap;
        try {
            recordMapFromLdap = _facade.retrieveRecordsForIOC(iocFromLdapName);
        } catch (final LdapFacadeException e) {
            throw new LdapUpdaterServiceException("Exception in LDAP facade on creating or updating IOC in LDAP.", e);
        }

        final UpdateIOCResult updateResult =
            updateIocInLdapWithRecordsFromBootFile(recordMapFromLdap, iocFromLdap);

        // TODO (bknerr) : does only make sense when the update process has been stopped
        if (updateResult.hasNoError()) {
            HistoryFileAccess.appendLineToHistfile(_prefsService.getHistoryDatFilePath(),
                                                   iocFromFS.getName(),
                                                   updateResult.getNumOfRecsWritten(),
                                                   updateResult.getNumOfRecsInFile(),
                                                   updateResult.getNumOfRecsInLDAP() );
        }
    }

    @Nonnull
    private INodeComponent<LdapEpicsControlsConfiguration>
        createIocInLdap(@Nonnull final IOC iocFromFS,
                        @Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocMapFromLdap) throws LdapUpdaterServiceException {

        final String iocName = iocFromFS.getName();
        LOG.info("IOC " + iocName +
                 " (from file system) does not yet exist in LDAP - added to facility MISC.\n");

        final IpAddress ipAddress = iocFromFS.getIpAddress();
        if (ipAddress != null) {
            validateAndUpdateIpAddressAttribute(ipAddress, iocMapFromLdap.values());
        }

        final LdapName middleName = createLdapName(COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                                   FACILITY.getNodeTypeName(), UpdaterLdapConstants.FACILITY_MISC_FIELD_VALUE);

        LdapName iocFromLdapName;
        try {
            iocFromLdapName = (LdapName) new LdapName(middleName.getRdns()).add(new Rdn(IOC.getNodeTypeName(), iocName));

            final LdapName fullLdapName =
                (LdapName) new LdapName(iocFromLdapName.getRdns()).add(0, new Rdn(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()));

            _facade.createLdapIoc(fullLdapName, iocFromFS);

            return _facade.retrieveIOC(fullLdapName);
        } catch (final InvalidNameException e) {
            throw new LdapUpdaterServiceException("Invalid name on creating new LDAP IOC.", e);
        } catch (final LdapFacadeException e) {
            throw new LdapUpdaterServiceException("Exception in LDAP facade on creating IOC in LDAP.", e);
        }
    }


    private void validateAndUpdateIpAddressAttribute(@Nonnull final IpAddress ipAddress,
                                                     @Nonnull final Collection<INodeComponent<LdapEpicsControlsConfiguration>> values)
                                                     throws LdapUpdaterServiceException {

        final List<String> iocsWithoutAttribute = new LinkedList<String>();

        for (final INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap : values) {
            final Attribute attribute = iocFromLdap.getAttribute(LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS);
            try {
                if (attribute == null) {
                    iocsWithoutAttribute.add(iocFromLdap.toString());
                    continue;
                }
                final String ipAddressFromLdap = (String) attribute.get();
                if (Strings.isNullOrEmpty(ipAddressFromLdap)) {
                    iocsWithoutAttribute.add(iocFromLdap.toString());
                    continue;
                }
                if (ipAddress.toString().equals(ipAddressFromLdap)) {
                    NotificationMailer.sendIpAddressNotUniqueNotification(ipAddress, iocFromLdap);
                    _facade.modifyIpAddressAttribute(iocFromLdap.getLdapName(), null);
                }
            } catch (final NamingException e) {
                throw new LdapUpdaterServiceException("Attribute creation for IP Address modification in LDAP failed.", e);
            } catch (final LdapFacadeException e) {
                throw new LdapUpdaterServiceException("IP Address modification in LDAP failed.", e);
            }
        }
        if (!iocsWithoutAttribute.isEmpty()) {
            NotificationMailer.sendIpAddressNotSetInLDAP(iocsWithoutAttribute);
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ContentModel<LdapEpicsControlsConfiguration> retrieveIOCs() throws LdapUpdaterServiceException {
        try {
            return _facade.retrieveIOCs();
        } catch (final LdapFacadeException e) {
            throw new LdapUpdaterServiceException("Exception in LDAP facade on retrieval of IOCs.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIocEntryFromLdap(@Nonnull final String iocName,
                                       @Nonnull final String facilityName) throws LdapUpdaterServiceException {
        try {
            _facade.removeIocEntryFromLdap(iocName, facilityName);
        } catch (final LdapFacadeException e) {
            throw new LdapUpdaterServiceException("Exception in LDAP facade on removal of IOC in LDAP.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpIocEntryInLdap(@Nonnull final String iocName,
                                     @Nonnull final String facilityName,
                                     @Nonnull final Set<Record> validRecords) throws LdapUpdaterServiceException {
        try {
            _facade.tidyUpIocEntryInLdap(iocName, facilityName, validRecords);
        } catch (final LdapFacadeException e) {
            throw new LdapUpdaterServiceException("Exception in LDAP facade on tidying LDAP.", e);
        }
    }



}
