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

package org.csstudio.config.savevalue.rmiserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.config.savevalue.service.SaveValueRequest;
import org.csstudio.config.savevalue.service.SaveValueResult;
import org.csstudio.config.savevalue.service.SaveValueService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Save value service that saves to a ca file.
 * 
 * @author Joerg Rathlev
 */
public class CaPutService implements SaveValueService {

	/**
	 * The logger.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();
	
	/**
	 * Preference key for the ca file path preference.
	 */
	private static final String FILE_PATH_PREFERENCE = "caFilePath";
	
	/**
	 * The character used to separate channel and value in ca file entries.
	 */
	private static final String CA_SEPARATOR = " ";
	
	/**
	 * The character used to separate the columns (channel, value, user, host,
	 * date) of changelog file entries.
	 */
	private static final String CHANGELOG_SEPARATOR = " ";

	/**
	 * The date format to use for changelog entries.
	 */
	private static final SimpleDateFormat CHANGELOG_DATE_FORMAT =
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	/**
	 * Class that returns the file names for an IOC.
	 */
	private static class IocFiles {
		/**
		 * File extension for ca files.
		 */
		private static final String CA_FILE_EXTENSION = ".ca";
		
		/**
		 * File extension for ca files.
		 */
		private static final String CA_BACKUP_FILE_EXTENSION = ".ca~";

		/**
		 * File extension for changelog files.
		 */
		private static final String CHANGELOG_FILE_EXTENSION = ".changelog";

		/**
		 * The ca file.
		 */
		private final File _cafile;
		
		/**
		 * The backup file.
		 */
		private final File _backup;
		
		/**
		 * The changelog file.
		 */
		private final File _changelog;
		
		/**
		 * Creates the set of file names for the given IOC.
		 * 
		 * @param iocName
		 *            the name of the IOC.
		 */
		IocFiles(final String iocName) {
			IPreferencesService prefs = Platform.getPreferencesService();
			String path = prefs.getString(Activator.PLUGIN_ID,
					FILE_PATH_PREFERENCE, "", null);
			_cafile = new File(path, iocName + CA_FILE_EXTENSION);
			_backup = new File(path, iocName + CA_BACKUP_FILE_EXTENSION);
			_changelog = new File(path, iocName + CHANGELOG_FILE_EXTENSION);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final synchronized SaveValueResult saveValue(
			final SaveValueRequest request) throws SaveValueServiceException,
			RemoteException {
		_log.info(this, "saveValue called with: " + request);
		if (request.isValid()) {
			IocFiles files = new IocFiles(request.getIocName());
			makeBackupCopy(files);
			String replacedValue = updateValueInFile(files._cafile, request.getPvName(), request.getValue());
			writeChangelog(files._changelog, request);
			return new SaveValueResult(replacedValue);
		} else {
			_log.warn(this, "Invalid request.");
			throw new SaveValueServiceException("Invalid request", null);
		}
	}

	/**
	 * Writes a changelog entry to the given file.
	 * 
	 * @param file the changelog file.
	 * @param request the request that was executed.
	 */
	private void writeChangelog(final File file, final SaveValueRequest request) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file, true)));
			String date = CHANGELOG_DATE_FORMAT.format(new Date());
			writer.println(
					request.getPvName() + CHANGELOG_SEPARATOR
					+ request.getValue() + CHANGELOG_SEPARATOR
					+ request.getUsername() + CHANGELOG_SEPARATOR
					+ request.getHostname() + CHANGELOG_SEPARATOR
					+ date);
		} catch (IOException e) {
			_log.warn(this, "Error writing to changelog file", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Updates the value for the given PV in the given file. If the file has no
	 * entry for the given PV, a new entry is added, otherwise, the existing
	 * entry is replaced. Returns the old value that was replaced, or
	 * <code>null</code> if a new entry was created.
	 * 
	 * @param file
	 *            the file in which to update the value.
	 * @param pvName
	 *            the name of the process variable.
	 * @param newValue
	 *            the new value.
	 * @return the previous value which was replaced by this method, or
	 *         <code>null</code> if a new entry was added to the file.
	 * @throws SaveValueServiceException
	 *             if the file could not be updated.
	 */
	private String updateValueInFile(final File file, final String pvName, final String newValue)
			throws SaveValueServiceException {
		String replacedValue = null;
		Map<String, String> entries = parseFile(file);
		if (entries.containsKey(pvName)) {
			replacedValue = entries.get(pvName);
		}
		entries.put(pvName, newValue);
		replaceFile(file, entries);
		return replacedValue;
	}

	/**
	 * Replaces the contents of the given ca file with the given entries. The
	 * entries are first written to a temporary file and that file is than
	 * moved, so that the replacement operation is atomic. If the file to be
	 * replaced does not exist yet, it is created.
	 * 
	 * @param file
	 *            the file to replace.
	 * @param entries
	 *            the new entries to write.
	 * @throws SaveValueServiceException
	 *             if the file cannot be written.
	 */
	private void replaceFile(final File file, final Map<String, String> entries)
			throws SaveValueServiceException {
		try {
			File directory = file.getAbsoluteFile().getParentFile();
			// create a temporary file to write into
			File temp = File.createTempFile(file.getName(), null, directory);
			_log.debug(this, "Writing to temporary file: " + temp);
			PrintWriter writer = null;
			try {
				// write the file
				writer = new PrintWriter(new BufferedWriter(
						new FileWriter(temp)));
				for (String channel : entries.keySet()) {
					String value = entries.get(channel);
					writer.println(channel + CA_SEPARATOR + value);
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
			
			_log.debug(this, "Renaming temporary file to " + file);
			File target = file.getAbsoluteFile();
			if (!temp.renameTo(target)) {
				// On Windows, the rename fails if the target file exists. Try
				// again, but this time, delete the target file first.
				_log.warn(this, "Could not rename file, trying to delete target first");
				if (!(target.delete() && temp.renameTo(target))) {
					// Failed again...
					_log.error(this, "Could not rename file");
					throw new SaveValueServiceException(
							"Existing file could not be replaced.", null);
				}
			}
			
		} catch (IOException e) {
			_log.error(this, "Error creating temporary file for writing", e);
			throw new SaveValueServiceException(
					"Could not create the replacement file", e);
		}
	}

	/**
	 * Creates a backup copy of the cafile of the given files. If the file does
	 * not exist, no backup copy is created.
	 * 
	 * @param files
	 *            the files.
	 */
	private void makeBackupCopy(final IocFiles files) {
		if (files._cafile.exists()) {
			FileChannel in = null;
			FileChannel out = null;
			try {
				in = new FileInputStream(files._cafile).getChannel();
				out = new FileOutputStream(files._backup).getChannel();

				// see http://forum.java.sun.com/thread.jspa?threadID=439695&messageID=2917510
				int maxCount = (64 * 1024 * 1024) - (32 * 1024);

				long size = in.size();
				long position = 0;
				while (position < size) {
					position += in.transferTo(position, maxCount, out);
				}
				_log.debug(this, "Created backup copy of " + files._cafile);
			} catch (FileNotFoundException e) {
				_log.warn(this, "Backup failed with FileNotFoundException", e);
			} catch (IOException e) {
				_log.warn(this, "Backup failed with IOException", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						_log.warn(this, "Error closing input file, backup may have failed", e);
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						_log.warn(this, "Error closing output file, backup may have failed", e);
					}
				}
			}
		}
	}

	/**
	 * Parses the given file and returns its entries.
	 * 
	 * @param file
	 *            the file to parse
	 * @return a map of the entries found in the file. Returns an empty map if
	 *         the file does not exist or does not contain any entries.
	 * @throws SaveValueServiceException
	 *             if the file exists but cannot be read or a parse error
	 *             occurs.
	 */
	private Map<String, String> parseFile(final File file)
			throws SaveValueServiceException {
		Map<String, String> entries = new HashMap<String, String>();
		if (file.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					parseLine(line, entries);
				}
			} catch (FileNotFoundException e) {
				_log.error(this,
						"File exists but could not be opened: " + file, e);
				throw new SaveValueServiceException(
						"Could not open existing ca file: " + file, e);
			} catch (IOException e) {
				_log.error(this, "Error reading from file: " + file, e);
				throw new SaveValueServiceException(
						"Error reading from existing ca file: " + file, e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						_log.warn(this, "Error closing input file", e);
					}
				}
			}
		}
		return entries;
	}

	/**
	 * Parses a single line from a ca file. The result will be stored into the
	 * given map.
	 * 
	 * @param line
	 *            the line to parse.
	 * @param entries
	 *            the map into which the result should be stored.
	 * @throws SaveValueServiceException
	 *             if a parse error occurs.
	 */
	private void parseLine(final String line, final Map<String, String> entries)
			throws SaveValueServiceException {
		String[] tokens = line.split(CA_SEPARATOR, 2);
		if (tokens.length != 2) {
			throw new SaveValueServiceException(
					"Error parsing ca file, syntax error in the following line:\n"
					+ line, null);
		}
		entries.put(tokens[0], tokens[1]);
	}

}
