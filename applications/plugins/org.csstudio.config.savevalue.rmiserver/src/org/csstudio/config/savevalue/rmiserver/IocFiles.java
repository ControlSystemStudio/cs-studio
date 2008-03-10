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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Class that returns the file names for an IOC.
 * 
 * @author Joerg Rathlev
 */
class IocFiles {
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
				CaPutService.FILE_PATH_PREFERENCE, "", null);
		_cafile = new File(path, iocName + CA_FILE_EXTENSION);
		_backup = new File(path, iocName + CA_BACKUP_FILE_EXTENSION);
		_changelog = new File(path, iocName + CHANGELOG_FILE_EXTENSION);
	}

	/**
	 * Returns the ca file.
	 * @return the ca file.
	 */
	File getCafile() {
		return _cafile;
	}

	/**
	 * Returns the backup file.
	 * @return the backup file.
	 */
	File getBackup() {
		return _backup;
	}

	/**
	 * Returns the changelog file.
	 * @return the changelog file.
	 */
	File getChangelog() {
		return _changelog;
	}
}
