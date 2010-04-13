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

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldapUpdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapUpdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.mail.NotificationMail;
import org.csstudio.utility.ldapUpdater.mail.NotificationType;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey;

import service.LdapService;
import service.impl.LdapServiceImpl;

/**
 * LDAP Updater access class to encapsulate specific updater access.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 */
public final class LdapAccess {

    private static final LdapService SERVICE = LdapServiceImpl.getInstance();

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

    private static final Logger LOGGER = CentralLogger.getInstance().getLogger(LdapAccess.class.getCanonicalName());


    /**
     * Don't instantiate.
     */
    private LdapAccess() {
        // Empty
    }


    /**
     * TODO (bknerr) : should be encapsulated in a file access class - does not belong here.
     * @param pathToFile the file with records
     */
    @Nonnull
    private static Set<Record> getRecordsFromFile(@Nonnull final String pathToFile) {
        final Set<Record> records = new HashSet<Record>();
        try {
            final BufferedReader br = new BufferedReader(new FileReader(pathToFile));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                records.add(new Record(strLine));
            }
            return records;
        } catch (final FileNotFoundException e) {
            LOGGER.error("Could not find file: " + pathToFile + "\n" + e.getMessage());
        } catch (final IOException e) {
            LOGGER.error("Error while reading from file: " + e.getMessage());
        }
        return Collections.emptySet();
    }

    private static boolean isIOCFileNewerThanHistoryEntry(@Nonnull final IOC ioc, @Nonnull final HistoryFileContentModel historyFileModel) {
        final long timeFromFile = ioc.getDateTime().getTimeInMillis() / 1000;
        final long timeFromHistoryFile = historyFileModel.getTimeForRecord(ioc.getName());
        return timeFromFile > timeFromHistoryFile;
    }


    /**
     * Tidies LDAP conservatively.
     * Gets an IOC map of valid existing IOCs and removes any entry in LDAP which is not contained in this map.
     *
     * @param ldapContentModel current LDAP contents
     * @param iocMapFromFS valid IOCs
     */
    public static void tidyUpLDAPFromIOCList(@Nonnull final LdapContentModel ldapContentModel,
                                             @Nonnull final Map<String, IOC> iocMapFromFS){

        for (final String iocNameFromLdap : ldapContentModel.getIOCNames()) {

            final IOC iocFromLdap = ldapContentModel.getIOC(iocNameFromLdap);
            if (iocMapFromFS.containsKey(iocNameFromLdap)) {

                SERVICE.tidyUpIocEntryInLdap(Engine.getInstance().getLdapDirContext(),
                                              iocFromLdap.getName(),
                                              iocFromLdap.getGroup(),
                                              LdapAccess.getValidRecordsForIOC(iocFromLdap.getName()));

            } else { // LDAP entry is not contained in current IOC directory - is considered obsolete!
                SERVICE.removeIocEntryFromLdap(Engine.getInstance().getLdapDirContext(),
                                                iocFromLdap);
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
        final Set<Record> fileRecords = getRecordsFromFile(iocFilePath + iocName);
        return fileRecords;
    }

    @Nonnull
    private static UpdateIOCResult updateIOC(@Nonnull final LdapContentModel model,
                                             @Nonnull final IOC ioc) {

        final String iocName = ioc.getName();
        int numOfRecsWritten = 0;

        final String iocFilePath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        final Set<Record> recordsFromFile = getRecordsFromFile(iocFilePath + iocName);

        final StringBuilder forbiddenRecords = new StringBuilder();

        LOGGER.info( "Process IOC " + iocName + "\t\t #records " +  recordsFromFile.size());
        for (final Record record : recordsFromFile) {
            final String recordName = record.getName();

            if (model.getRecord(ioc.getGroup(), iocName, recordName) == null) { // does not yet exist
                LOGGER.info("New Record: " + iocName + " " + recordName);

                if (!LdapUtils.filterLDAPNames(recordName)) {
                    // TODO (bknerr) : Stopping or proceeding? Transaction rollback? Hist file update ?
                    if (!SERVICE.createLDAPRecord(Engine.getInstance().getLdapDirContext(), ioc, recordName)) {
                        LOGGER.error("Error while updating LDAP record for " + recordName +
                        "\nProceed with next record.");
                    } else {
                        numOfRecsWritten++;
                    }
                } else {
                    LOGGER.warn("Record " + recordName + " could not be written. Unallowed characters!");
                    forbiddenRecords.append(recordName + "\n");
                }
            }
        }
        if (forbiddenRecords.length() > 0) {
            NotificationMail.sendMail(NotificationType.UNALLOWED_CHARS,
                                      ioc.getResponsible(),
                                      "\nIn IOC " + iocName + ":\n\n" + forbiddenRecords.toString());
        }

        // TODO (bknerr) : what to do with success variable ?
        return new UpdateIOCResult(recordsFromFile.size(),
                                   numOfRecsWritten,
                                   model.getRecords(iocName).size(), true);
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
    public static void updateLDAPFromIOCList(@Nonnull final LdapContentModel model,
                                             @Nonnull final Map<String, IOC> iocMap,
                                             @Nonnull final HistoryFileContentModel historyFileModel) throws InterruptedException {

        for (final Entry<String, IOC> iocFromFS : iocMap.entrySet()) {

            final String iocName = iocFromFS.getKey();

            if (historyFileModel.contains(iocName)) {
                if (!isIOCFileNewerThanHistoryEntry(iocFromFS.getValue(), historyFileModel)) {
                    LOGGER.debug("IOC file for " + iocName
                                 + " is not newer than history file time stamp.");
                    continue;
                }
            } // else means 'new IOC file in directory'

            final IOC iocFromLDAP = model.getIOC(iocName);
            if (iocFromLDAP == null) {
                LOGGER.warn("IOC "
                            + iocName
                            + " (from file system) does not exist in LDAP - no facility/group association possible.\n"
                            + "No LDAP Update! Generate an LDAP entry for this IOC manually!");
                continue;
            }

            final LdapSearchResult searchResult = SERVICE.retrieveRecords(iocFromLDAP.getGroup(), iocFromLDAP.getName());
            model.addSearchResult(searchResult);

            final UpdateIOCResult updateResult = updateIOC(model, iocFromLDAP);

            // TODO (bknerr) : does only make sense when the update process has been stopped
            if (updateResult.hasNoError()) {
                try {
                    HistoryFileAccess.appendLineToHistfile(iocName,
                                                           updateResult.getNumOfRecsWritten(),
                                                           updateResult.getNumOfRecsInFile(),
                                                           updateResult.getNumOfRecsInLDAP() );
                } catch (final IOException e) {
                    LOGGER.error("I/O-Exception while trying to append a line to "
                                 + getValueFromPreferences(LdapUpdaterPreferenceKey.LDAP_HIST_PATH) + "history.dat");
                }
            }
        }
    }
}
