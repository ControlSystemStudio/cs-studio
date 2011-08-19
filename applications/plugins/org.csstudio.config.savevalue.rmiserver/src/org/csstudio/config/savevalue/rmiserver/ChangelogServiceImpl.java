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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import org.csstudio.config.savevalue.internal.changelog.ChangelogAppender;
import org.csstudio.config.savevalue.internal.changelog.ChangelogReader;
import org.csstudio.config.savevalue.service.ChangelogDeletionService;
import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the changelog service.
 *
 * @author Joerg Rathlev
 */
public class ChangelogServiceImpl implements ChangelogService, ChangelogDeletionService {

	/**
	 * The logger.
	 */
    private static final Logger LOG = LoggerFactory.getLogger(ChangelogServiceImpl.class);
    
	/**
	 * {@inheritDoc}
	 */
	@Override
    public final ChangelogEntry[] readChangelog(final String iocName)
			throws SaveValueServiceException, RemoteException {
		LOG.debug("Reading changelog for: {}", iocName);
		IocFiles files = new IocFiles(iocName);
		ChangelogEntry[] entries = readChangelog(files.getChangelog());
		return entries;
	}

	/**
	 * Reads the entries in the given changelog file.
	 *
	 * @param changelog
	 *            the changelog file to read.
	 * @return the entries.
	 * @throws SaveValueServiceException
	 *             if an error occurs.
	 */
	private ChangelogEntry[] readChangelog(final File changelog) throws SaveValueServiceException {
		if (changelog.exists()) {
			ChangelogReader reader = null;
			try {

				reader = new ChangelogReader(new FileReader(changelog));
				Collection<ChangelogEntry> entries = reader.readLatestEntries();
				return entries.toArray(
						new ChangelogEntry[entries.size()]);

			} catch (FileNotFoundException e) {
				LOG.error("File exists but could not be opened: {}",changelog, e);
				throw new SaveValueServiceException("Could not open changelog file", e);
			} catch (IOException e) {
				LOG.error("Error reading changelog file", e);
				throw new SaveValueServiceException("Error reading changelog file: " + e.getMessage(), e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						LOG.warn("Error closing changelog file", e);
					}
				}
			}
		} else {
			return new ChangelogEntry[0];
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void deleteEntries(final String iocName, final String pvName)
			throws SaveValueServiceException, RemoteException {
		LOG.debug("deleteEntries called with iocName={}, pvName={}",iocName, pvName);
		IocFiles files = new IocFiles(iocName);
		File changelogFile = files.getChangelog();
		deleteEntries(changelogFile, pvName);
	}

	/**
	 * Deletes all entries for the given PV from the given changelog file.
	 *
	 * @param changelog
	 *            the changelog file.
	 * @param pvName
	 *            the name of the PV.
	 */
	private void deleteEntries(final File changelog, final String pvName)
			throws SaveValueServiceException {
		if (changelog.exists()) {
			try {
				Collection<ChangelogEntry> entries = readAllEntries(changelog);
				filter(entries, pvName);
				overwriteChangelog(changelog, entries);
			} catch (IOException e) {
				LOG.error("Error accessing changelog file", e);
				throw new SaveValueServiceException(
						"Error accessing changelog file", e);
			}
		}
	}

	/**
	 * Overwrites the given changelog file with a new file which contains the
	 * entries from the given collection.
	 *
	 * @param changelog
	 *            the changelog file.
	 * @param entries
	 *            the collection of entries.
	 * @throws IOException
	 *             if an error occurs.
	 */
	private void overwriteChangelog(final File changelog,
			final Collection<ChangelogEntry> entries) throws IOException {
		ChangelogAppender appender = null;
		try {
			appender = new ChangelogAppender(new FileWriter(changelog, false));
			for (ChangelogEntry entry : entries) {
				appender.append(entry);
			}
		} finally {
			if (appender != null) {
				try {
					appender.close();
				} catch (IOException e) {
					LOG.warn("Error closing changelog file", e);
				}
			}
		}
	}

	/**
	 * Removes the entries with the given PV name from the collection.
	 *
	 * @param entries
	 *            the collection.
	 * @param pvName
	 *            the name.
	 */
	private void filter(final Collection<ChangelogEntry> entries, final String pvName) {
		for (Iterator<ChangelogEntry> i = entries.iterator(); i.hasNext();) {
			ChangelogEntry entry = i.next();
			if (entry.getPvName().equals(pvName)) {
				i.remove();
			}
		}
	}

	/**
	 * @param changelog
	 * @return
	 */
	private Collection<ChangelogEntry> readAllEntries(final File changelog)
			throws IOException {
		ChangelogReader reader = null;
		try {
			reader = new ChangelogReader(new FileReader(changelog));
			return reader.readEntries();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
