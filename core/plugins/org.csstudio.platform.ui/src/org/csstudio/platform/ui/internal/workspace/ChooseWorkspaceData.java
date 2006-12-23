/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui.internal.workspace;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.StringTokenizer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * This class stores the information behind the "Launch Workspace" dialog. The
 * class is able to read and write itself to a well known configuration file.
 * 
 * <p>
 * <b>Code is based upon
 * <code>org.eclipse.ui.internal.ide.ChooseWorkspaceData</code> in plugin
 * <code>org.eclipse.ui.ide</code>.</b>
 * </p>
 * 
 * @author Alexander Will
 * @version $Revision$
 */
public final class ChooseWorkspaceData {
	/**
	 * The default max length of the recent workspace mru list.
	 */
	private static final int RECENT_MAX_LENGTH = 5;

	/**
	 * The directory within the config area that will be used for the receiver's
	 * persisted data.
	 */
	private static final String PERS_FOLDER = "org.eclipse.ui.ide"; //$NON-NLS-1$

	/**
	 * The name of the file within the config area that will be used for the
	 * recever's persisted data.
	 * 
	 * @see PERS_FOLDER
	 */
	private static final String PERS_FILENAME = "recentWorkspaces.xml"; //$NON-NLS-1$

	/**
	 * In the past a file was used to store persist these values. This file was
	 * written with this value as its protocol identifier.
	 */
	private static final int PERS_ENCODING_VERSION = 1;

	/**
	 * This is the first version of the encode/decode protocol that uses the
	 * config area preference store for persistence. The only encoding done is
	 * to convert the recent workspace list into a comma-separated list.
	 */
	private static final int PERS_ENCODING_VERSION_CONFIG_PREFS = 2;

	/**
	 * <p>
	 * Stores a comma separated list of the recently used workspace paths.
	 * </p>
	 * 
	 * @since 3.1
	 */
	public static final String RECENT_WORKSPACES = "RECENT_WORKSPACES"; //$NON-NLS-1$

	/**
	 * <p>
	 * Stores the maximum number of workspaces that should be displayed in the
	 * ChooseWorkspaceDialog.
	 * </p>
	 * 
	 * @since 3.1
	 */
	public static final String MAX_RECENT_WORKSPACES = "MAX_RECENT_WORKSPACES"; //$NON-NLS-1$

	/**
	 * <p>
	 * Specifies whether or not the workspace selection dialog should be shown
	 * on startup.
	 * </p>
	 * <p>
	 * The default value for this preference is <code>true</code>.
	 * </p>
	 * 
	 * @since 3.1
	 */
	public static final String SHOW_WORKSPACE_SELECTION_DIALOG = "SHOW_WORKSPACE_SELECTION_DIALOG"; //$NON-NLS-1$

	/**
	 * <p>
	 * Stores the version of the protocol used to decode/encode the list of
	 * recent workspaces.
	 * </p>
	 * 
	 * @since 3.1
	 */
	public static final String RECENT_WORKSPACES_PROTOCOL = "RECENT_WORKSPACES_PROTOCOL"; //$NON-NLS-1$

	/**
	 * <p>
	 * Stores the "refresh workspace on startup" flag.
	 * </p>
	 */
	public static final String REFRESH_WORKSPACE_ON_STARTUP = "REFRESH_WORKSPACE_ON_STARTUP"; //$NON-NLS-1$

	/**
	 * <p>
	 * Stores the "exit prompt on close last window" flag.
	 * </p>
	 */
	public static final String EXIT_PROMPT_ON_CLOSE_LAST_WINDOW = "EXIT_PROMPT_ON_CLOSE_LAST_WINDOW"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>protocol</code>.
	 */
	public static final String XML_PROTOCOL = "protocol"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>version</code>.
	 */
	public static final String XML_VERSION = "version"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>alwaysAsk</code>.
	 */
	public static final String XML_ALWAYS_ASK = "alwaysAsk"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>showDialog</code>.
	 */
	public static final String XML_SHOW_DIALOG = "showDialog"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>workspace</code>.
	 */
	public static final String XML_WORKSPACE = "workspace"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>recentWorkspaces</code>.
	 */
	public static final String XML_RECENT_WORKSPACES = "recentWorkspaces"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>maxLength</code>.
	 */
	public static final String XML_MAX_LENGTH = "maxLength"; //$NON-NLS-1$

	/**
	 * XML tag definition for <code>path</code>.
	 */
	public static final String XML_PATH = "path"; //$NON-NLS-1$	

	/**
	 * Show the workspace choice dialog on startup.
	 */
	private boolean _showDialog = true;

	/**
	 * The initial default value.
	 */
	private String _initialDefault;

	/**
	 * The currently selected workspace.
	 */
	private String _selection;

	/**
	 * The recently used workspaces.
	 */
	private String[] _recentWorkspaces;

	/**
	 * Creates a new instance, loading persistent data if its found.
	 * 
	 * @param initialDefault
	 *            The initial default value.
	 */
	public ChooseWorkspaceData(final String initialDefault) {
		readPersistedData();
		setInitialDefault(initialDefault);
	}

	/**
	 * Creates a new instance, loading persistent data if its found.
	 * 
	 * @param instanceUrl
	 *            The instance URL.
	 */
	public ChooseWorkspaceData(final URL instanceUrl) {
		readPersistedData();
		if (instanceUrl != null) {
			setInitialDefault(new File(instanceUrl.getFile()).toString());
		}
	}

	/**
	 * Return the folder to be used as a default if no other information exists.
	 * Does not return null.
	 * 
	 * @return The initial default value.
	 */
	public String getInitialDefault() {
		if (_initialDefault == null) {
			setInitialDefault(System.getProperty("user.dir") //$NON-NLS-1$
					+ File.separator + "workspace"); //$NON-NLS-1$
		}
		return _initialDefault;
	}

	/**
	 * Set this data's initialDefault parameter to a properly formatted version
	 * of the argument directory string. The proper format is to the platform
	 * appropriate separator character without meaningless leading or trailing
	 * separator characters.
	 * 
	 * @param dir
	 *            The initial default value.
	 */
	private void setInitialDefault(final String dir) {
		if ((dir == null) || (dir.length() <= 0)) {
			_initialDefault = null;
			return;
		}

		String newDir = new Path(dir).toOSString();

		while (dir.charAt(dir.length() - 1) == File.separatorChar) {
			newDir = dir.substring(0, dir.length() - 1);
		}
		_initialDefault = newDir;
	}

	/**
	 * Return the currently selected workspace or null if nothing is selected.
	 * 
	 * @return The currently selected workspace.
	 */
	public String getSelection() {
		return _selection;
	}

	/**
	 * Return the "show dialog" flag.
	 * 
	 * @return The "show dialog" flag.
	 */
	public boolean getShowDialog() {
		return _showDialog;
	}

	/**
	 * Return an array of recent workspaces sorted with the most recently used
	 * at the start.
	 * 
	 * @return The recently used workspaces.
	 */
	public String[] getRecentWorkspaces() {
		return _recentWorkspaces;
	}

	/**
	 * The argument workspace has been selected, update the receiver. Does not
	 * persist the new values.
	 * 
	 * @param dir
	 *            The selected workspace.
	 */
	public void workspaceSelected(final String dir) {
		// this just stores the selection, it is not inserted and persisted
		// until the workspace is actually selected
		_selection = dir;
	}

	/**
	 * Toggle value of the showDialog persistent setting.
	 */
	public void toggleShowDialog() {
		_showDialog = !_showDialog;
	}

	/**
	 * Update the persistent store. Call this function after the currently
	 * selected value has been found to be ok.
	 */
	public void writePersistedData() {
		// 1. get config pref node
		Preferences node = new ConfigurationScope()
				.getNode(CSSPlatformUiPlugin.ID);

		// 2. get value for showDialog
		node.putBoolean(ChooseWorkspaceData.SHOW_WORKSPACE_SELECTION_DIALOG,
				_showDialog);

		// 3. use value of numRecent to create proper length array
		node.putInt(ChooseWorkspaceData.MAX_RECENT_WORKSPACES,
				_recentWorkspaces.length);

		// move the new selection to the front of the list
		if (_selection != null) {
			String oldEntry = _recentWorkspaces[0];
			_recentWorkspaces[0] = _selection;
			for (int i = 1; (i < _recentWorkspaces.length)
					&& (oldEntry != null); ++i) {
				if (_selection.equals(oldEntry)) {
					break;
				}
				String tmp = _recentWorkspaces[i];
				_recentWorkspaces[i] = oldEntry;
				oldEntry = tmp;
			}
		}

		// 4. store values of recent workspaces into array
		String encodedRecentWorkspaces = encodeStoredWorkspacePaths(_recentWorkspaces);
		node
				.put(ChooseWorkspaceData.RECENT_WORKSPACES,
						encodedRecentWorkspaces);

		// 5. store the protocol version used to encode the list
		node.putInt(ChooseWorkspaceData.RECENT_WORKSPACES_PROTOCOL,
				PERS_ENCODING_VERSION_CONFIG_PREFS);

		// 6. store the node
		try {
			node.flush();
		} catch (BackingStoreException e) {
			CentralLogger.getInstance().error(this, e);
		}
	}

	/**
	 * Look for and read data that might have been persisted from some previous
	 * run. Leave the receiver in a default state if no persistent data is
	 * found.
	 * 
	 * @return true if a file was successfully read and false otherwise
	 */
	private boolean readPersistedDataFile() {
		URL persUrl = null;

		Location configLoc = Platform.getConfigurationLocation();
		if (configLoc != null) {
			persUrl = getPersistenceUrl(configLoc.getURL(), false);
		}

		try {
			// inside try to get the safe default creation in the finally
			// clause
			if (persUrl == null) {
				return false;
			}

			// E.g.,
			// <launchWorkspaceData>
			// <protocol version="1"/>
			// <alwaysAsk showDialog="1"/>
			// <recentWorkspaces maxLength="5">
			// <workspace path="C:\eclipse\workspace0"/>
			// <workspace path="C:\eclipse\workspace1"/>
			// </recentWorkspaces>
			// </launchWorkspaceData>

			Reader reader = new FileReader(persUrl.getFile());
			XMLMemento memento = XMLMemento.createReadRoot(reader);
			if ((memento == null) || !compatibleFileProtocol(memento)) {
				return false;
			}

			IMemento alwaysAskTag = memento.getChild(XML_ALWAYS_ASK);
			_showDialog = alwaysAskTag == null ? true : alwaysAskTag
					.getInteger(XML_SHOW_DIALOG).intValue() == 1;

			IMemento recent = memento.getChild(XML_RECENT_WORKSPACES);
			if (recent == null) {
				return false;
			}

			Integer maxLength = recent.getInteger(XML_MAX_LENGTH);
			int max = RECENT_MAX_LENGTH;
			if (maxLength != null) {
				max = maxLength.intValue();
			}

			IMemento[] indices = recent.getChildren(XML_WORKSPACE);
			if ((indices == null) || (indices.length <= 0)) {
				return false;
			}

			// if a user has edited maxLength to be shorter than the listed
			// indices, accept the list (its tougher for them to retype a long
			// list of paths than to update a max number)
			max = Math.max(max, indices.length);

			_recentWorkspaces = new String[max];
			for (int i = 0; i < indices.length; ++i) {
				String path = indices[i].getString(XML_PATH);
				if (path == null) {
					break;
				}
				_recentWorkspaces[i] = path;
			}
		} catch (IOException e) {
			// cannot log because instance area has not been set
			return false;
		} catch (WorkbenchException e) {
			// cannot log because instance area has not been set
			return false;
		} finally {
			// create safe default if needed
			if (_recentWorkspaces == null) {
				_recentWorkspaces = new String[RECENT_MAX_LENGTH];
			}
		}

		return true;
	}

	/**
	 * Return the current (persisted) value of the "showDialog on startup"
	 * preference. Return the global default if the file cannot be accessed.
	 * 
	 * @return The current (persisted) value of the "showDialog on startup"
	 *         preference.
	 */
	public static boolean getShowDialogValue() {
		// TODO See the long comment in #readPersistedData -- when the
		// transition time is over this method can be changed to
		// read the preference directly.

		ChooseWorkspaceData data = new ChooseWorkspaceData(""); //$NON-NLS-1$

		// return either the value in the file or true, which is the global
		// default
		return data.readPersistedData() ? data._showDialog : true;
	}

	/**
	 * Set the current value of the "showDialog on startup" preference.
	 * 
	 * @param showDialog
	 *            The current value of the "showDialog on startup" preference.
	 */
	public static void setShowDialogValue(final boolean showDialog) {
		// TODO See the long comment in #readPersistedData -- when the
		// transition time is over this method can be changed to
		// read the preference directly.

		ChooseWorkspaceData data = new ChooseWorkspaceData(""); //$NON-NLS-1$

		// update the value and write the new settings
		data._showDialog = showDialog;
		data.writePersistedData();
	}

	/**
	 * Look in the config area preference store for the list of recently used
	 * workspaces.
	 * 
	 * NOTE: During the transition phase the file will be checked if no config
	 * preferences are found.
	 * 
	 * @return true if the values were successfully retrieved and false
	 *         otherwise
	 */
	public boolean readPersistedData() {
		IPreferenceStore store = new ScopedPreferenceStore(
				new ConfigurationScope(), CSSPlatformUiPlugin.ID);

		// The old way was to store this information in a file, the new is to
		// use the configuration area preference store. To help users with the
		// transition, this code always looks for values in the preference
		// store; they are used if found. If there aren't any related
		// preferences, then the file method is used instead. This class always
		// writes to the preference store, so the fall-back should be needed no
		// more than once per-user, per-configuration.

		// This code always sets the value of the protocol to a non-zero value
		// (currently at 2). If the value comes back as the default (0), then
		// none of the preferences were set, revert to the file method.

		int protocol = store
				.getInt(ChooseWorkspaceData.RECENT_WORKSPACES_PROTOCOL);
		if ((protocol == IPreferenceStore.INT_DEFAULT_DEFAULT)
				&& readPersistedDataFile()) {
			return true;
		}

		// 2. get value for showDialog
		_showDialog = store
				.getBoolean(ChooseWorkspaceData.SHOW_WORKSPACE_SELECTION_DIALOG);

		// 3. use value of numRecent to create proper length array
		int max = store.getInt(ChooseWorkspaceData.MAX_RECENT_WORKSPACES);
		max = Math.max(max, RECENT_MAX_LENGTH);

		// 4. load values of recent workspaces into array
		String workspacePathPref = store
				.getString(ChooseWorkspaceData.RECENT_WORKSPACES);
		_recentWorkspaces = decodeStoredWorkspacePaths(max, workspacePathPref);

		return true;
	}

	/**
	 * The the list of recent workspaces must be stored as a string in the
	 * preference node.
	 * 
	 * @param recent
	 *            The recently used workspace.
	 * @return The list of the currently used workspaced as one string.
	 */
	private static String encodeStoredWorkspacePaths(final String[] recent) {
		StringBuffer buff = new StringBuffer();

		String path = null;
		for (int i = 0; i < recent.length; ++i) {
			if (recent[i] == null) {
				break;
			}

			if (path != null) {
				buff.append(","); //$NON-NLS-1$
			}

			path = recent[i];
			buff.append(path);
		}

		return buff.toString();
	}

	/**
	 * The the preference for recent workspaces must be converted from the
	 * storage string into an array.
	 * 
	 * @param max
	 *            The maximum size of the recently used workspaces array.
	 * @param prefValue
	 *            A string that contains all recently used workspaces.
	 * @return An array with the recently used workspaces.
	 */
	private static String[] decodeStoredWorkspacePaths(final int max,
			final String prefValue) {
		String[] paths = new String[max];
		if ((prefValue == null) || (prefValue.length() <= 0)) {
			return paths;
		}

		StringTokenizer tokenizer = new StringTokenizer(prefValue, ","); //$NON-NLS-1$
		for (int i = 0; (i < paths.length) && tokenizer.hasMoreTokens(); ++i) {
			paths[i] = tokenizer.nextToken();
		}

		return paths;
	}

	/**
	 * Return true if the protocol used to encode the argument memento is
	 * compatible with the receiver's implementation and false otherwise.
	 * 
	 * @param memento
	 *            A memento.
	 * @return True if the protocol used to encode the argument memento is
	 *         compatible with the receiver's implementation and false
	 *         otherwise.
	 */
	private static boolean compatibleFileProtocol(final IMemento memento) {
		IMemento protocolMemento = memento.getChild(XML_PROTOCOL);
		if (protocolMemento == null) {
			return false;
		}

		Integer version = protocolMemento.getInteger(XML_VERSION);
		return (version != null)
				&& (version.intValue() == PERS_ENCODING_VERSION);
	}

	/**
	 * The workspace data is stored in the well known file pointed to by the
	 * result of this method.
	 * 
	 * @param baseUrl
	 *            The base URL.
	 * @param create
	 *            If the directory and file does not exist this parameter
	 *            controls whether it will be created.
	 * @return An url to the file and null if it does not exist or could not be
	 *         created.
	 */
	private static URL getPersistenceUrl(final URL baseUrl, final boolean create) {
		if (baseUrl == null) {
			return null;
		}

		try {
			// make sure the directory exists
			URL url = new URL(baseUrl, PERS_FOLDER);
			File dir = new File(url.getFile());
			if (!dir.exists() && (!create || !dir.mkdir())) {
				return null;
			}

			// make sure the file exists
			url = new URL(dir.toURL(), PERS_FILENAME);
			File persFile = new File(url.getFile());
			if (!persFile.exists() && (!create || !persFile.createNewFile())) {
				return null;
			}

			return persFile.toURL();
		} catch (IOException e) {
			// cannot log because instance area has not been set
			return null;
		}
	}
}
