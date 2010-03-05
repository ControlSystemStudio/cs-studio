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

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants.createFileNameFromPreferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;

/**
 * Reads the file with the list of the IOCs.
 * 
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2008
 */

public class IocGroupAndRecordFactory {

	private static final CentralLogger LOGGER = CentralLogger.getInstance();


	public IocGroupAndRecordFactory() {
	}
	


	public List<IOC> readRecordsForIOCs(List<IOC> iocList) {
		
		for (IOC ioc : iocList) {
			String iocName = ioc.getName();

			LOGGER.debug(this, "Read records from IOC file: " + ioc.getName());		 						

			BufferedReader fr = null;
		
			try {
				String iocDblDumpPath = 
					createFileNameFromPreferences(IOC_DBL_DUMP_PATH);

				fr = new BufferedReader(new FileReader(iocDblDumpPath + "\\" + iocName));
				String line;
				List<String> records = new ArrayList<String>();
				while ((line = fr.readLine()) != null) {
					if (line.length() > 0) {
						records.add(line);
					}
				}
				ioc.setIocRecordNames(records);
			} catch (FileNotFoundException e) {
				LOGGER.error(this, "File not Found : " + iocName);
			} catch (IOException e) {
				LOGGER.error (this, "I/O-Execption while trying to read " + iocName);
			} finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						LOGGER.error (this, "I/O-Execption while trying to close " + iocName);
					}
				}
			}
			LOGGER.debug(this, "All record names read.");

		}
		return iocList;
	}

	public List<IOC> createGroupAffiliation(
			List<IOC> iocList,
			final Map<String, String> econToEfanMap) {

		for (IOC ioc : iocList) {
			String iocName = ioc.getName();
			if (econToEfanMap.containsKey(iocName)) {
				ioc.setGroup(econToEfanMap.get(iocName));
			} else {
				LOGGER.info(this, "'For IOC " + iocName + " there has not been any entry in LDAP.");
				econToEfanMap.put(iocName, IOC.NO_GROUP);
				// TODO (kvalett) : add the newly found ioc name to the datamodel
				// or send mail
			}
		}
		return iocList;
	}

	
//	/**
//	 * Reads the list of all IOCs from the IOC list file.
//	 * @param iocFileNames the names of all ioc files that has been found in the file system
//	 * @param map 
//	 * @param model the datamodel in which the results are stored
//	 * 
//	 * @return the list of all IOCs.
//	 */
//	public final List<IOC> createIOCList(
//			List<String> iocFileNames, 
//			Map<String, String> econToEfanMap) {
//		
//		List<IOC> result = new ArrayList<IOC>();
//		
//		BufferedReader fr = null;
//		try {
//			fr = new BufferedReader(
//					new FileReader(_prefs.getString(Activator.getDefault().getPluginId(),
//								   LdapUpdaterPreferenceConstants.IOC_LIST_FILE, "", 
//								   null)));
//			String line;
//			while ((line = fr.readLine()) != null) {
//				if (line.length() > 0) {
//					IOC ioc = parseLine(line);
//					String iocName = ioc.getName();
//					if (econToEfanMap.containsKey(iocName)) {
//						ioc.setGroup(econToEfanMap.get(iocName));
//					} else {
//						LOGGER.info(this, "'For IOC " + iocName + " there isn't any entry in LDAP yet.");
//						econToEfanMap.put(iocName, IOC.NO_GROUP);
//						// TODO (kvalett) : add the newly found ioc name to the datamodel
//					}
//					result.add(ioc);
//				}
//			}
//		} catch (FileNotFoundException e) {
//			LOGGER.error(this, "File not Found : " + e.getMessage() ); 
//		} catch (IOException e) {
//			LOGGER.error(this, "IOExeption : " + e.getMessage() ); 
//		} finally {
//			if (fr != null) {
//				try {
//					fr.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//					LOGGER.error(this, "Error: " + e.getMessage() + e.toString()); 
//				}
//			}
//		}
//		return result;
//	}

//	/**
//	 * Parses a single line from the IOC list file.
//	 * 
//	 * @param line
//	 *            the line to parse.
//	 * @return the parsed IOC information.
//	 */
//	private IOC parseLine(final String line) {
//
//		String iocName = line;
//		GregorianCalendar dateTime = new GregorianCalendar();
//		String prefString = _prefs.getString(
//				Activator.getDefault().getPluginId(),
//		        LdapUpdaterPreferenceConstants.IOC_DBL_DUMP_PATH, 
//		        "", 
//		        null);
//		File filePath = new File(prefString);
//		dateTime.setTimeInMillis(new File(filePath,iocName).lastModified());
//		
//		IOC result = new IOC(iocName, dateTime);
//		return result;
//	}
}
