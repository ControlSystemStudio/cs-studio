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
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;

import org.csstudio.config.savevalue.internal.changelog.ChangelogReader;
import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Implementation of the changelog service.
 * 
 * @author Joerg Rathlev
 */
public class ChangelogServiceImpl implements ChangelogService {

	/**
	 * The logger.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * {@inheritDoc}
	 */
	public final ChangelogEntry[] readChangelog(final String iocName)
			throws SaveValueServiceException, RemoteException {
		_log.debug(this, "Reading changelog for: " + iocName);
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
				Collection<ChangelogEntry> entries = reader.readEntries();
				return (ChangelogEntry[]) entries.toArray(
						new ChangelogEntry[entries.size()]);
				
			} catch (FileNotFoundException e) {
				_log.error(this,
						"File exists but could not be opened: " + changelog, e);
				throw new SaveValueServiceException("Could not open changelog file", e);
			} catch (IOException e) {
				_log.error(this, "Error reading changelog file", e);
				throw new SaveValueServiceException("Error reading changelog file: " + e.getMessage(), e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						_log.warn(this, "Error closing changelog file", e);
					}
				}
			}
		} else {
			return new ChangelogEntry[0];
		}
	}
}
