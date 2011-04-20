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
package org.csstudio.utility.ldapUpdater;

import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.service.util.LdapUtils.filterLDAPNames;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.csstudio.utility.ldapUpdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapUpdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.files.IOCFilesDirTree;
import org.csstudio.utility.ldapUpdater.mail.NotificationMail;
import org.csstudio.utility.ldapUpdater.mail.NotificationType;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapUpdater.service.impl.LdapUpdaterServiceImpl;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.csstudio.utility.treemodel.TreeNodeComponent;


/**
 * LDAP Updater access class to encapsulate specific updater access.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 */
public final class LdapAccess {

    public static final Logger LOG = CentralLogger.getInstance().getLogger(LdapAccess.class);


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


    private static final ILdapUpdaterService LDAP_UPDATER_SERVICE = LdapUpdaterServiceImpl.INSTANCE;

    /**
     * Don't instantiate.
     */
    private LdapAccess() {
        // Empty
    }


    private static boolean isIOCFileNewerThanHistoryEntry(@Nonnull final IOC ioc, @Nonnull final HistoryFileContentModel historyFileModel) {
        final GregorianCalendar lastUpdated = ioc.getLastUpdated();
        if (lastUpdated != null) {
            final long timeFromFile = lastUpdated.getTimeInMillis() / 1000;
            final Long timeFromHistoryFile = historyFileModel.getTimeForRecord(ioc.getName());
            if (timeFromHistoryFile != null) {
                return timeFromFile > timeFromHistoryFile;
            }
        }
        return true;
    }


    /**
     * Tidies LDAP conservatively.
     * Gets an IOC map of valid existing IOCs and removes any entry in LDAP which is not contained in this map.
     *
     * @param contentModel current LDAP contents
     * @param iocMapFromFS valid IOCs
     * @throws InterruptedException
     * @throws InvalidNameException
     * @throws CreateContentModelException
     * @throws ServiceUnavailableException
     */
    public static void tidyUpLDAPFromIOCList(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> contentModel,
                                             @Nonnull final Map<String, IOC> iocMapFromFS) throws InvalidNameException, InterruptedException, CreateContentModelException, ServiceUnavailableException{

        final Set<Entry<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>>> childrenByTypeSet =
            contentModel.getChildrenByTypeAndSimpleName(IOC).entrySet();

        for (final Entry<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> entry : childrenByTypeSet) {

            final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLdap = entry.getValue();

            final String iocName = iocFromLdap.getName();
            final String facName =
                LdapNameUtils.getValueOfRdnType(iocFromLdap.getLdapName(), FACILITY.getNodeTypeName());

            if (facName == null) {
                LOG.warn("Facility name could not be retrieved for " + iocFromLdap.getLdapName().toString());
                continue;
            }

            if (iocMapFromFS.containsKey(entry.getKey())) {

                LDAP_UPDATER_SERVICE.tidyUpIocEntryInLdap(iocName,
                                                          facName,
                                                          LdapAccess.getValidRecordsForIOC(iocFromLdap.getName()));

            } else { // LDAP entry is not contained in current IOC directory - is considered obsolete!
                LDAP_UPDATER_SERVICE.removeIocEntryFromLdap(iocName, facName);
            }
        }
    }

    /**
     * Retrieves valid records for an IOC from the IOC file.
     * @param iocName the ioc file name
     * @return a set of contained records
     */
    @Nonnull
    public static Set<Record> getValidRecordsForIOC(@Nonnull final String iocName) {
        final String iocFilePath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        final Set<Record> fileRecords = IOCFilesDirTree.getRecordsFromFile(iocFilePath + iocName);
        return fileRecords;
    }

    @Nonnull
    private static UpdateIOCResult updateIOC(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> model,
                                             @Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP) throws NamingException {

        final String iocName = iocFromLDAP.getName();
        int numOfRecsWritten = 0;

        final String iocFilePath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        final Set<Record> recordsFromFile = IOCFilesDirTree.getRecordsFromFile(iocFilePath + iocName);

        final StringBuilder forbiddenRecords = new StringBuilder();

        LOG.info( "Process IOC " + iocName + "\t\t #records " +  recordsFromFile.size());
        for (final Record record : recordsFromFile) {
            numOfRecsWritten = processRecordEntry(model,
                                                  iocFromLDAP,
                                                  iocName,
                                                  numOfRecsWritten,
                                                  forbiddenRecords,
                                                  record);
        }
        sendUnallowedCharsNotification(iocFromLDAP, iocName, forbiddenRecords);

        // TODO (bknerr) : what to do with success variable ?
        final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iLdapComponent =
            model.getByTypeAndSimpleName(LdapEpicsControlsConfiguration.IOC, iocName);

        int numOfChildren = -1;
        if (iLdapComponent != null) {
            numOfChildren = iLdapComponent.getDirectChildren().size();
        }

        return new UpdateIOCResult(recordsFromFile.size(),
                                   numOfRecsWritten,
                                   numOfChildren, true);
    }


    private static int processRecordEntry(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> model,
                                          @Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
                                          @Nonnull final String iocName,
                                          final int numOfRecsWritten,
                                          @Nonnull final StringBuilder forbiddenRecords,
                                          @Nonnull final Record record) throws InvalidNameException, ServiceUnavailableException {
        final String recordName = record.getName();
        int number = numOfRecsWritten;
        final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> recordComponent =
            model.getByTypeAndSimpleName(RECORD, recordName);

        if (recordComponent == null) { // does not yet exist
            LOG.info("New Record: " + iocName + " " + recordName);

            if (!filterLDAPNames(recordName)) {

                final LdapName newLdapName = new LdapName(iocFromLDAP.getLdapName().getRdns());
                newLdapName.add(new Rdn(RECORD.getNodeTypeName(), recordName));

                if (!LDAP_UPDATER_SERVICE.createLdapRecord(newLdapName)) {
                    LOG.error("Error while updating LDAP record for " + recordName +
                    "\nProceed with next record.");
                } else {
                    number++;
                }
            } else {
                LOG.warn("Record " + recordName + " could not be written. Unallowed characters!");
                forbiddenRecords.append(recordName + "\n");
            }
        }
        return number;
    }



    private static void sendUnallowedCharsNotification(@Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
                                                       @Nonnull final String iocName,
                                                       @Nonnull final StringBuilder forbiddenRecords) throws NamingException {
        if (forbiddenRecords.length() > 0) {
            final Attribute attr = iocFromLDAP.getAttribute(LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
            String person;
            if (attr != null && !StringUtil.hasLength((String) attr.get())) {
                person = (String) attr.get();
            } else {
                person = LdapUpdater.DEFAULT_RESPONSIBLE_PERSON;
            }
            NotificationMail.sendMail(NotificationType.UNALLOWED_CHARS,
                                      person,
                                      "\nIn IOC " + iocName + ":\n\n" + forbiddenRecords.toString());
        }
    }


    /**
     * This method compares the contents of the current LDAP hierarchy with the contents found in
     * the directory, where the IOC files reside. The contents of the ioc list are firstly checked
     * whether they are more recent than those stored in the history file, if not so the ioc file
     * has already been processed. If so, the LDAP is updated with the newer content of the ioc
     * files conservatively, i.e. by adding references to records, but not removing entries
     * from the LDAP in case the corresponding file does not exist in the ioc directory.

     * @param model the current LDAP content model
     * @param iocMap
     *            the list of ioc filenames as found in the ioc directory
     * @param historyFileModel
     *            the contents of the history file
     * @throws InterruptedException
     */
    public static void updateLDAPFromIOCList(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> model,
                                             @Nonnull final Map<String, IOC> iocMap,
                                             @Nonnull final HistoryFileContentModel historyFileModel) throws InterruptedException {

        for (final Entry<String, IOC> iocFromFS : iocMap.entrySet()) {

            final String iocName = iocFromFS.getKey();

            if (historyFileModel.contains(iocName)) {
                if (!isIOCFileNewerThanHistoryEntry(iocFromFS.getValue(), historyFileModel)) {
                    LOG.debug("IOC file for " + iocName
                              + " is not newer than history file time stamp.");
                    continue;
                }
            } // else means 'new IOC file in directory'

            try {
                createIocAndUpdate(model, iocFromFS, iocName);
            } catch (final InvalidNameException e1) {
                LOG.error("Invalid LDAP name.", e1);
            } catch (final NamingException e) {
                LOG.error("Update of IOC threw naming exception.", e);
            } catch (final CreateContentModelException e) {
                LOG.error("Error creating content model from LDAP.");
            }
        }
    }

    private static void createIocAndUpdate(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> model,
                                           @Nonnull final Entry<String, IOC> iocFromFS,
                                           @Nonnull final String iocName) throws InterruptedException, NamingException, CreateContentModelException {
        final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLdap =
            getOrCreateIocFromLdap(model,
                                   iocFromFS,
                                   iocName);
        if (iocFromLdap != null) {
            final LdapName iocFromLdapName = iocFromLdap.getLdapName();

            final ILdapSearchResult searchResult =
                LDAP_UPDATER_SERVICE.retrieveRecordsForIOC(iocFromLdapName);
            if (searchResult != null) {
                final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
                    LDAP_UPDATER_SERVICE.getLdapContentModelBuilder(model);
                builder.setSearchResult(searchResult);
                builder.build();
            }
            final UpdateIOCResult updateResult = updateIOC(model, iocFromLdap);
            // TODO (bknerr) : does only make sense when the update process has been stopped
            if (updateResult.hasNoError()) {
                HistoryFileAccess.appendLineToHistfile(iocName,
                                                       updateResult.getNumOfRecsWritten(),
                                                       updateResult.getNumOfRecsInFile(),
                                                       updateResult.getNumOfRecsInLDAP() );
            }
        }
    }

    @CheckForNull
    private static ISubtreeNodeComponent<LdapEpicsControlsConfiguration>
        getOrCreateIocFromLdap(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> model,
                               @Nonnull final Entry<String, IOC> iocFromFS,
                               @Nonnull final String iocName)  throws InvalidNameException, ServiceUnavailableException {

        ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLdap =
            model.getByTypeAndSimpleName(LdapEpicsControlsConfiguration.IOC, iocName);

        LdapName iocFromLdapName;
        if (iocFromLdap == null) {
            LOG.info("IOC " + iocName + " (from file system) does not yet exist in LDAP - added to facility MISC.\n");

            final LdapName middleName = createLdapName(COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                                        FACILITY.getNodeTypeName(), UpdaterLdapConstants.FACILITY_MISC_FIELD_VALUE);

            iocFromLdapName =
                (LdapName) new LdapName(middleName.getRdns()).add(new Rdn(IOC.getNodeTypeName(), iocName));

            final LdapName fullLdapName =
                (LdapName) new LdapName(iocFromLdapName.getRdns()).add(0, new Rdn(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()));

            LDAP_UPDATER_SERVICE.createLdapIoc(fullLdapName, iocFromFS.getValue().getLastUpdated());

            final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> parent = model.getChildByLdapName(middleName.toString());
            if (parent != null) {
                iocFromLdap =
                    new TreeNodeComponent<LdapEpicsControlsConfiguration>(
                            iocName,
                            LdapEpicsControlsConfiguration.IOC,
                            parent,
                            null,
                            iocFromLdapName);
                model.addChild(parent, iocFromLdap);
            } else {
                LOG.warn("Parent " + middleName.toString() + " could not be retrieved from the model. No IOC added.");
            }
        }
        return iocFromLdap;
    }

}
