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
package org.csstudio.utility.ldapUpdater.service.impl;

import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.csstudio.utility.ldapUpdater.LdapUpdaterUtil;
import org.csstudio.utility.ldapUpdater.UpdaterLdapConstants;
import org.csstudio.utility.ldapUpdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapUpdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.files.RecordsFileContentParser;
import org.csstudio.utility.ldapUpdater.service.ILdapFacade;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapUpdater.service.LdapFacadeException;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.INodeComponent;

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

    public static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUpdaterServiceImpl.class);


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

    /**
     * Don't instantiate.
     */
    @Inject
    public LdapUpdaterServiceImpl(@Nonnull final ILdapFacade facade) {
        _facade = facade;
    }


    private boolean isIOCFileNewerThanHistoryEntry(@Nonnull final IOC ioc,
                                                  @Nonnull final HistoryFileContentModel historyFileModel) {
        final TimeInstant lastBootTime = ioc.getLastBootTime();
        if (lastBootTime != null) {
            final TimeInstant timeFromHistoryFile = historyFileModel.getTimeForRecord(ioc.getName());
            if (timeFromHistoryFile != null) {
                return lastBootTime.isAfter(timeFromHistoryFile);
            }
        }
        return true;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpLDAPFromIOCList(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdap,
                                      @Nonnull final Map<String, IOC> iocMapFromFS) throws LdapFacadeException {

        for (final INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap : iocsFromLdap.values()) {

            final String iocFromLdapName = iocFromLdap.getName();
            final String facFromLdapName =
                LdapNameUtils.getValueOfRdnType(iocFromLdap.getLdapName(), FACILITY.getNodeTypeName());

            if (facFromLdapName == null) {
                LOG.warn("Facility name could not be retrieved for " + iocFromLdap.getLdapName().toString());
                continue;
            }

            if (iocMapFromFS.containsKey(iocFromLdapName)) {

                final Set<Record> validRecords = getBootRecordsFromIocFile(iocFromLdapName);
                _facade.tidyUpIocEntryInLdap(iocFromLdapName,
                                             facFromLdapName,
                                             validRecords);

            } else { // LDAP entry is not contained in current IOC directory - is considered obsolete!
                _facade.removeIocEntryFromLdap(iocFromLdapName, facFromLdapName);
            }
        }
    }


    @Nonnull
    private UpdateIOCResult updateIocInLdapWithRecordsFromBootFile(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> recordMapFromLdap,
                                                                   @Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP) throws LdapFacadeException {

        final String iocName = iocFromLDAP.getName();

        final Set<Record> recordsFromFile = getBootRecordsFromIocFile(iocName);

        final StringBuilder forbiddenRecords = new StringBuilder();
        LOG.info( "Process IOC " + iocName + "\t\t #records " +  recordsFromFile.size());
        int numOfRecsWritten = 0;
        try {
            for (final Record recFromFile : recordsFromFile) {
                numOfRecsWritten += processRecordEntry(recordMapFromLdap.get(recFromFile),
                                                       iocFromLDAP,
                                                       forbiddenRecords,
                                                       recFromFile);
            }
            LdapUpdaterUtil.sendUnallowedCharsNotification(iocFromLDAP, iocName, forbiddenRecords);
        } catch (final NamingException e) {
            throw new LdapFacadeException("LDAP name creation failed on updating records for IOC " + iocName, e);
        }

        return new UpdateIOCResult(recordsFromFile.size(),
                                   numOfRecsWritten,
                                   -1,
                                   true);
    }

    /**
     * Retrieves valid records for an IOC from the IOC file.
     * @param iocName the ioc file name
     * @return a set of contained records
     * @throws LdapFacadeException
     */
    @Override
    @Nonnull
    public SortedSet<Record> getBootRecordsFromIocFile(@Nonnull final String iocName) throws LdapFacadeException {
        final File dumpPath = IOC_DBL_DUMP_PATH.getValue();

        final RecordsFileContentParser parser = new RecordsFileContentParser();
        try {
            parser.parseFile(new File(dumpPath, iocName + UpdaterLdapConstants.RECORDS_FILE_SUFFIX));
        } catch (final IOException e) {
            throw new LdapFacadeException("Failure on parsing contents of record file " + iocName , e);
        }
        return parser.getRecords();
    }

    private int processRecordEntry(@Nullable final INodeComponent<LdapEpicsControlsConfiguration> recFromLdap,
                                   @Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
                                   @Nonnull final StringBuilder forbiddenRecords,
                                   @Nonnull final Record recordFromFS) throws InvalidNameException, LdapFacadeException {
        final String recordFromFSName = recordFromFS.getName();

        if (recFromLdap == null) { // does not yet exist in LDAP
            LOG.info("New record for LDAP: " + recordFromFSName);

            if (!LdapNameUtils.filterName(recordFromFSName)) {

                final LdapName newLdapName = new LdapName(iocFromLDAP.getLdapName().getRdns());
                newLdapName.add(new Rdn(RECORD.getNodeTypeName(), recordFromFSName));

                if (!_facade.createLdapRecord(newLdapName)) {
                    LOG.error("Error while updating LDAP record for " + recordFromFSName +
                    "\nProceed with next record.");
                }
                return 1;
            } else {
                LOG.warn("Record " + recordFromFSName + " could not be written. Unallowed characters!");
                forbiddenRecords.append(recordFromFSName + "\n");
            }
        }
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
     * @throws LdapFacadeException
     */
    @Override
    public void updateLDAPFromIOCList(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocMapFromLdap,
                                      @Nonnull final Map<String, IOC> iocMapFromFS,
                                      @Nonnull final HistoryFileContentModel historyFileModel) throws LdapFacadeException {

        for (final Entry<String, IOC> entry : iocMapFromFS.entrySet()) {

            final String iocFromFSName = entry.getKey();
            final IOC iocFromFS = entry.getValue();

            if (historyFileModel.contains(iocFromFSName)) {
                if (!isIOCFileNewerThanHistoryEntry(iocFromFS, historyFileModel)) {
                    LOG.debug("IOC file for " + iocFromFSName
                              + " is not newer than history file time stamp.");
                    continue;
                }
            } // else means 'new IOC file in directory'

            createOrUpdateIocInLdap(iocMapFromLdap.get(iocFromFSName), iocFromFS);
        }
    }

    private void createOrUpdateIocInLdap(@Nullable final INodeComponent<LdapEpicsControlsConfiguration> pIocFromLdap,
                                         @Nonnull final IOC iocFromFS) throws LdapFacadeException {
        INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap = pIocFromLdap;
        if (iocFromLdap == null) {
            iocFromLdap = createIocInLdap(iocFromFS);
        }

        final LdapName iocFromLdapName = iocFromLdap.getLdapName();

        final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> recordMapFromLdap =
            _facade.retrieveRecordsForIOC(iocFromLdapName);

        final UpdateIOCResult updateResult = updateIocInLdapWithRecordsFromBootFile(recordMapFromLdap, iocFromLdap);

        // TODO (bknerr) : does only make sense when the update process has been stopped
        if (updateResult.hasNoError()) {
            HistoryFileAccess.appendLineToHistfile(iocFromFS.getName(),
                                                   updateResult.getNumOfRecsWritten(),
                                                   updateResult.getNumOfRecsInFile(),
                                                   updateResult.getNumOfRecsInLDAP() );
        }
    }

    @Nonnull
    private INodeComponent<LdapEpicsControlsConfiguration>
        createIocInLdap(@Nonnull final IOC iocFromFS) throws LdapFacadeException {

        final String iocName = iocFromFS.getName();
        LOG.info("IOC " + iocName +
                 " (from file system) does not yet exist in LDAP - added to facility MISC.\n");

        final LdapName middleName = createLdapName(COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                                   FACILITY.getNodeTypeName(), UpdaterLdapConstants.FACILITY_MISC_FIELD_VALUE);

        LdapName iocFromLdapName;
        try {
            iocFromLdapName = (LdapName) new LdapName(middleName.getRdns()).add(new Rdn(IOC.getNodeTypeName(), iocName));

            final LdapName fullLdapName =
                (LdapName) new LdapName(iocFromLdapName.getRdns()).add(0, new Rdn(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()));

            _facade.createLdapIoc(fullLdapName, iocFromFS.getLastBootTime());

            return _facade.retrieveIOC(fullLdapName);
        } catch (final InvalidNameException e) {
            throw new LdapFacadeException("Invalid name on creating new LDAP IOC.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ContentModel<LdapEpicsControlsConfiguration> retrieveIOCs() throws LdapFacadeException {
        return _facade.retrieveIOCs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIocEntryFromLdap(@Nonnull final String iocName,
                                       @Nonnull final String facilityName) throws LdapFacadeException {
        _facade.removeIocEntryFromLdap(iocName, facilityName);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpIocEntryInLdap(@Nonnull final String iocName,
                                     @Nonnull final String facilityName,
                                     @Nonnull final Set<Record> validRecords) throws LdapFacadeException {
        _facade.tidyUpIocEntryInLdap(iocName, facilityName, validRecords);
    }

}
