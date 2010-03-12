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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapConstants;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.reader.LdapService;
import org.csstudio.utility.ldapUpdater.model.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.model.IOC;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;
import org.csstudio.utility.ldapUpdater.model.Record;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey;


public class UpdateComparator {

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
	public UpdateComparator() {
		// Empty
	}


	/**
	 * Returns the attributes for a new entry with the given object class and
	 * name.
	 * 
	 * @param objectClass
	 *            the object class of the new entry.
	 * @param rdnAttr
	 * 			  ? // TODO (someone) :
	 * @param name
	 *            the name of the new entry.
	 * @return the attributes for the new entry.
	 */
	private Attributes attributesForEntry(final String objectClass,
			final String rdnAttr, final String name) {
		final BasicAttributes result = new BasicAttributes();
		result.put("objectClass", objectClass);
		result.put(rdnAttr, name);
		// result.put("epicsCssType", objectClass.getCssType());
		return result;
	}


	/**d
	 * TODO (bknerr) : centralize filtering in ldap read/write utils
	 * @param recordName
	 * @return
	 */
	private boolean filterLDAPNames(final String recordName) {

		if (recordName.contains("+"))
			return false;
		else if (recordName.contains("/"))
			return false;
		return true;
	}



	private List<Record> getRecordsFromFile(final String pathToFile) {
		final List<Record> records = new ArrayList<Record>();
		try {
			final BufferedReader br = new BufferedReader(new FileReader(pathToFile));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				records.add(new Record(strLine));
			}
			return records;
		} catch (final Exception e){ //Catch exception if any
			LOGGER.error("Error while reading from file: " + e.getMessage());
		}
		return Collections.emptyList();
	}


	private boolean isIOCFileNewerThanHistoryEntry(final IOC ioc, final HistoryFileContentModel historyFileModel) {
		final long timeFromFile = ioc.getDateTime().getTimeInMillis() / 1000;
		final long timeFromHistoryFile = historyFileModel.getTimeForRecord(ioc.getName());
		return timeFromFile > timeFromHistoryFile;
	}



	public UpdateIOCResult updateIOC(
			final LdapService service, final ReadLdapObserver ldapDataObserver, final DirContext directory,
			final LDAPContentModel ldapContentModel,
			final String iocFilePath,
			final IOC ioc) {

		final String iocName = ioc.getName();
		int numOfRecsWritten = 0;
		final List<Record> recordsFromFile = getRecordsFromFile(iocFilePath + iocName);

		LOGGER.info( "Process IOC " + iocName + "\t\t #records " +  recordsFromFile.size());
		for (final Record record : recordsFromFile) {
			final String recordName = record.getName();
			
			if (ldapContentModel.getRecord(ioc.getGroup(), iocName, recordName) == null) { // does not yet exist
				LOGGER.info("New Record: " + iocFilePath + " " + recordName);
				
				if (!LdapConstants.filterLDAPNames(recordName)) {
					// TODO (bknerr) : Stopping or proceeding? Transaction rollback? Hist file update ?
					if (!updateLDAPRecord(directory, ioc, recordName)) {
						LOGGER.error("Error while updating LDAP record for " + recordName +
						"\nProceed with next record.");
					}
					numOfRecsWritten++;
				} else {
					LOGGER.warn("Record " + recordName + " could not be written. Unallowed characters!");
				}
			}


		}
		// TODO (bknerr) : what to do with success variable ?
		return new UpdateIOCResult(recordsFromFile.size(), numOfRecsWritten, true);
	}

	/**
	 * This method compares the contents of the current LDAP hierarchy with the contents
	 * found in the directory, where the IOC files reside.
	 * The contents of the ioc list are firstly checked whether they are more recent than those stored
	 * in the history file, if not so the ioc file has already been processed.
	 * If so, the LDAP is updated with the newer content of the ioc files conservatively,
	 * i.e. by adding references to existing files, but not removing entries from the LDAP in case
	 * the corresponding file does not exist in the ioc directory.
	 * @param service
	 * @param ldapDataObserver
	 * @param ldapContentModel
	 * @param iocList the list of ioc filenames as found in the ioc directory
	 * @param historyFileModel the contents of the history file
	 * @throws InterruptedException
	 */
	public void updateLDAPFromIOCList(
			final LdapService service,
			final ReadLdapObserver ldapDataObserver,
			final LDAPContentModel ldapContentModel,
			final List<IOC> iocList,
			final HistoryFileContentModel historyFileModel) throws InterruptedException {

		final DirContext directory = Engine.getInstance().getLdapDirContext();
		final String iocFilePath = getValueFromPreferences(IOC_DBL_DUMP_PATH);

		for (final IOC iocFromFS : iocList) {

			final String iocName = iocFromFS.getName();

			if (historyFileModel.contains(iocName)) {
				if (!isIOCFileNewerThanHistoryEntry(iocFromFS, historyFileModel)) {
					LOGGER.info( "IOC file for " + iocName + " is not newer than history file time stamp.");
					continue;
				}
			}

			final IOC iocFromLDAP = ldapContentModel.getIOC(iocName);
			if (iocFromLDAP == null) {
				LOGGER.warn("IOC file for " + iocName +
						    " does not exist in LDAP - no facility association possible.\n" +
				            "Hence, records could not be updated in LDAP.");
				continue;
			}

			final String efanName = iocFromLDAP.getGroup();

			
			ldapDataObserver.setReady(false);
			ldapDataObserver.setResult(service.readLdapEntries("econ="+iocName+",ecom=EPICS-IOC,efan="+efanName+",ou=EpicsControls", "eren=*", ldapDataObserver));
			while(!ldapDataObserver.isReady()){ Thread.sleep(100);}

			
			final UpdateIOCResult updateResult = updateIOC(service, ldapDataObserver, directory, ldapContentModel, iocFilePath, iocFromLDAP);

			// TODO (bknerr) : does only make sense when the update process has been stopped
			if (updateResult.hasNoError()) {
				try {
					HistoryFileAccess.appendLineToHistfile(iocName, updateResult.getNumOfRecsWritten(), updateResult.getNumOfRecsInFile());
				} catch (final IOException e) {
					LOGGER.error ("I/O-Exception while trying to append a line to " + LdapUpdaterPreferenceKey.LDAP_HIST_PATH + "" + null + "history.dat");
				}
			}
		}
	}



	public boolean updateLDAPRecord(
			final DirContext directory,
			final IOC ioc,
			final String recordName) {

		final String iocName = ioc.getName();
		final String facName = ioc.getGroup();
		
		String query = String.format("eren=%s, econ=%s, ecom=EPICS-IOC, efan=%s, ou=EpicsControls",
				                     recordName, iocName, facName);
		final Attributes afe = attributesForEntry("epicsRecord", "eren", recordName);
		try {
			directory.bind(query, null, afe); // = Record schreiben
			LOGGER.info( "Record written: " + query);
		} catch (final NamingException e) {
			LOGGER.warn( "Naming Exception while trying to bind: " + query);
			System.out.println(e.getExplanation());
			return false;
		}
		return true;
	}
}
