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

import static org.csstudio.utility.ldap.LdapConstants.ECOM_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapConstants.ECOM_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapConstants.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapConstants.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapConstants.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapConstants.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapConstants.LDAP_ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapConstants.LDAP_ATTR_VAL_OBJECT_CLASS;
import static org.csstudio.utility.ldap.LdapConstants.LDAP_OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapConstants.attributesForLdapEntry;
import static org.csstudio.utility.ldap.LdapConstants.createLdapQuery;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapConstants;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.reader.LdapResultList;
import org.csstudio.utility.ldap.reader.LdapService;
import org.csstudio.utility.ldapUpdater.model.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.model.IOC;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;
import org.csstudio.utility.ldapUpdater.model.Record;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey;


public class LdapAccess {
    
    private static class UpdateIOCResult {
        private final int _numOfRecsWritten;
        private final boolean _noError;
        private final int _numOfRecsInFile;
        
        public UpdateIOCResult(final int numOfRecsInFile, final int numOfRecsWritten, final boolean noError) {
            _numOfRecsInFile = numOfRecsInFile;
            _numOfRecsWritten = numOfRecsWritten;
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
    }
    
    private final Logger LOGGER = CentralLogger.getInstance().getLogger(this);
    
    
    /**
     * Constructor.
     */
    public LdapAccess() {
        // Empty
    }
    
    
    // /**
    // * Returns the attributes for a new entry with the given object class and
    // * name.
    // *
    // * @param objectClass
    // * the object class of the new entry.
    // * @param rdnAttr
    // * ? // TODO (someone) :
    // * @param name
    // * the name of the new entry.
    // * @return the attributes for the new entry.
    // */
    // private Attributes attributesForEntry(final String objectClass,
    // final String rdnAttr, final String name) {
    // final BasicAttributes result = new BasicAttributes();
    // result.put("objectClass", objectClass);
    // result.put(rdnAttr, name);
    // // result.put("epicsCssType", objectClass.getCssType());
    // return result;
    // }
    
    private String fillModelWithLdapRecordsForIOC(final LdapService service,
                                                  final ReadLdapObserver ldapDataObserver,
                                                  final IOC iocFromLDAP)
    throws InterruptedException {
        final String efanName = iocFromLDAP.getGroup();
        
        ldapDataObserver.setReady(false);
        final String query = createLdapQuery(ECON_FIELD_NAME, iocFromLDAP.getName(),
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, iocFromLDAP.getGroup(),
                                             LDAP_OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        
        final LdapResultList result = service.readLdapEntries(query, LdapConstants.any(EREN_FIELD_NAME), ldapDataObserver);
        ldapDataObserver.setResult(result);
        
        while (!ldapDataObserver.isReady()) {
            Thread.sleep(100);
        }
        return efanName;
    }
    
    
    /**
     * TODO (bknerr) : should be encapsulated in a file access class - does not belong here.
     */
    private List<Record> getRecordsFromFile(final String pathToFile) {
        final List<Record> records = new ArrayList<Record>();
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
        return Collections.emptyList();
    }
    
    private boolean isIOCFileNewerThanHistoryEntry(final IOC ioc, final HistoryFileContentModel historyFileModel) {
        final long timeFromFile = ioc.getDateTime().getTimeInMillis() / 1000;
        final long timeFromHistoryFile = historyFileModel.getTimeForRecord(ioc.getName());
        return timeFromFile > timeFromHistoryFile;
    }
    
    
    private void removeIocEntryFromLdap(final IOC iocFromLdap) {
        final DirContext directory = Engine.getInstance().getLdapDirContext();
        
        final String query = createLdapQuery(ECON_FIELD_NAME, iocFromLdap.getName(),
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, iocFromLdap.getGroup(),
                                             LDAP_OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        try {
            directory.unbind(query);
            LOGGER.info("IOC removed from LDAP: " + query);
        } catch (final NamingException e) {
            LOGGER.warn("Naming Exception while trying to unbind: " + query);
            LOGGER.warn(e.getExplanation());
        }
    }
    
    private void removeRecordEntryFromLdap(final IOC ioc, final Record record) {
        final DirContext directory = Engine.getInstance().getLdapDirContext();
        
        final String query = createLdapQuery(EREN_FIELD_NAME, record.getName(),
                                             ECON_FIELD_NAME, ioc.getName(),
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, ioc.getGroup(),
                                             LDAP_OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        try {
            directory.unbind(query);
            LOGGER.info("Record removed from LDAP: " + query);
        } catch (final NamingException e) {
            LOGGER.warn("Naming Exception while trying to unbind: " + query);
            LOGGER.warn(e.getExplanation());
        }
    }
    
    /**
     * @param service
     * @param ldapDataObserver
     * @param ldapContentModel
     * @param iocFilePath
     * @param iocList
     */
    public void tidyUpLDAPFromIOCList(final LdapService service, final ReadLdapObserver ldapDataObserver,
                                      final LDAPContentModel ldapContentModel,
                                      final Map<String, IOC> iocMapFromFS) {
        
        
        for (final String iocNameFromLdap : ldapContentModel.getIOCNames()) {
            
            final IOC iocFromLdap = ldapContentModel.getIOC(iocNameFromLdap);
            
            if (iocMapFromFS.containsKey(iocNameFromLdap)) {
                
                updateIOC(ldapContentModel, iocFromLdap, true);
                
            } else { // LDAP entry is not contained in current IOC directory - is considered
                // obsolete!
                removeIocEntryFromLdap(iocFromLdap);
            }
            
            
        }
        
    }
    
    
    
    private UpdateIOCResult updateIOC(final LDAPContentModel ldapContentModel,
                                      final IOC ioc,
                                      final boolean tidyUp) {
        
        final String iocName = ioc.getName();
        int numOfRecsWritten = 0;
        
        final String iocFilePath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        final List<Record> recordsFromFile = getRecordsFromFile(iocFilePath + iocName);
        
        // just in case LDAP shall be cleaned
        Set<Record> obsoleteRecords = null;
        if (tidyUp) {
            obsoleteRecords = ldapContentModel.getRecords(iocName);
        }
        
        LOGGER.info( "Process IOC " + iocName + "\t\t #records " +  recordsFromFile.size());
        for (final Record record : recordsFromFile) {
            final String recordName = record.getName();
            
            if (ldapContentModel.getRecord(ioc.getGroup(), iocName, recordName) == null) { // does not yet exist
                LOGGER.info("New Record: " + iocFilePath + " " + recordName);
                
                if (!LdapConstants.filterLDAPNames(recordName)) {
                    // TODO (bknerr) : Stopping or proceeding? Transaction rollback? Hist file update ?
                    if (!updateLDAPRecord(ioc, recordName)) {
                        LOGGER.error("Error while updating LDAP record for " + recordName +
                        "\nProceed with next record.");
                    } else {
                        numOfRecsWritten++;
                    }
                } else {
                    LOGGER.warn("Record " + recordName + " could not be written. Unallowed characters!");
                }
            }
            if (tidyUp && (obsoleteRecords != null)) { // TODO (bknerr) : javax.annotations !!!
                obsoleteRecords.remove(record);
            }
        }
        
        if (tidyUp && !obsoleteRecords.isEmpty()) {
            for (final Record record : obsoleteRecords) {
                removeRecordEntryFromLdap(ioc, record);
            }
        }
        
        // TODO (bknerr) : what to do with success variable ?
        return new UpdateIOCResult(recordsFromFile.size(), numOfRecsWritten, true);
    }
    
    /**
     * This method compares the contents of the current LDAP hierarchy with the contents found in
     * the directory, where the IOC files reside. The contents of the ioc list are firstly checked
     * whether they are more recent than those stored in the history file, if not so the ioc file
     * has already been processed. If so, the LDAP is updated with the newer content of the ioc
     * files conservatively, i.e. by adding references to existing files, but not removing entries
     * from the LDAP in case the corresponding file does not exist in the ioc directory.
     * 
     * @param service
     * @param ldapDataObserver
     * @param ldapContentModel
     * @param iocMap
     *            the list of ioc filenames as found in the ioc directory
     * @param historyFileModel
     *            the contents of the history file
     * @throws InterruptedException
     */
    public void updateLDAPFromIOCList(final LdapService service,
                                      final ReadLdapObserver ldapDataObserver,
                                      final LDAPContentModel ldapContentModel,
                                      final Map<String, IOC> iocMap,
                                      final HistoryFileContentModel historyFileModel) throws InterruptedException {
        
        for (final Entry<String, IOC> iocFromFS : iocMap.entrySet()) {
            
            final String iocName = iocFromFS.getKey();
            
            if (historyFileModel.contains(iocName)) {
                if (!isIOCFileNewerThanHistoryEntry(iocFromFS.getValue(), historyFileModel)) {
                    LOGGER.info("IOC file for " + iocName
                                + " is not newer than history file time stamp.");
                    continue;
                }
            } // else means 'new IOC file in directory'
            
            final IOC iocFromLDAP = ldapContentModel.getIOC(iocName);
            if (iocFromLDAP == null) {
                LOGGER
                .warn("IOC "
                      + iocName
                      + " (from file system) does not exist in LDAP - no facility/group association possible.\n"
                      + "No LDAP Update! Generate an LDAP entry for this IOC manually!");
                continue;
            }
            
            fillModelWithLdapRecordsForIOC(service, ldapDataObserver, iocFromLDAP);
            
            final UpdateIOCResult updateResult = updateIOC(ldapContentModel, iocFromLDAP, false);
            
            // TODO (bknerr) : does only make sense when the update process has been stopped
            if (updateResult.hasNoError()) {
                try {
                    HistoryFileAccess.appendLineToHistfile(iocName, updateResult
                                                           .getNumOfRecsWritten(), updateResult.getNumOfRecsInFile());
                } catch (final IOException e) {
                    LOGGER.error("I/O-Exception while trying to append a line to "
                                 + getValueFromPreferences(LdapUpdaterPreferenceKey.LDAP_HIST_PATH) + "history.dat");
                }
            }
        }
    }
    
    private boolean updateLDAPRecord(final IOC ioc,
                                     final String recordName) {
        
        final DirContext directory = Engine.getInstance().getLdapDirContext();
        
        final String query = createLdapQuery(EREN_FIELD_NAME, recordName,
                                             ECON_FIELD_NAME, ioc.getName(),
                                             ECOM_FIELD_NAME, ECOM_FIELD_VALUE,
                                             EFAN_FIELD_NAME, ioc.getGroup(),
                                             LDAP_OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE);
        
        final Attributes afe =
            attributesForLdapEntry(LDAP_ATTR_FIELD_OBJECT_CLASS, LDAP_ATTR_VAL_OBJECT_CLASS,
                                   EREN_FIELD_NAME, recordName);
        try {
            directory.bind(query, null, afe);
            LOGGER.info( "Record written: " + query);
        } catch (final NamingException e) {
            LOGGER.warn( "Naming Exception while trying to bind: " + query);
            LOGGER.warn(e.getExplanation());
            return false;
        }
        return true;
    }
}
