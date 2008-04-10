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

import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Remote action for shutting down the server.
 * 
 * @author Joerg Rathlev
 */
public class ShutdownAction implements IAction {

	/**
	 * The logger used by this class.
	 */
	private CentralLogger _log = CentralLogger.getInstance();
	
	/**
	 * Shuts down the server.
	 * 
	 * @param param
	 *            the parameter supplied by the client. This must be a
	 *            <code>Map</code> containing a password string.
	 * @return a message.
	 */
	public final Object run(final Object param) {
		String password = null;
		if (!(param instanceof Map)) {
			return "Parameter not available.";
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> params = (Map<String, String>) param;
		password = params.get("password");
		
		if (isCorrectPassword(password)) {
			_log.info(this, "Received shutdown command, shutting down now.");
			SaveValueServer.getRunningServer().stop();
			return "Save Value RMI Server is shutting down...";
		} else {
			_log.warn(this, "Received shutdown command with invalid password.");
			return "Incorrect password.";
		}
	}

	/**
	 * Checks whether the given password is correct.
	 * 
	 * @param password
	 *            the password to check.
	 * @return <code>true</code> if the password is correct,
	 *         <code>false</code> otherwise.
	 */
	private boolean isCorrectPassword(final String password) {
		IPreferencesService prefs = Platform.getPreferencesService();
		String correctPassword = prefs.getString(Activator.PLUGIN_ID, 
				PreferenceConstants.SHUTDOWN_PASSWORD, "", null);
		return correctPassword.equals(password);
	}

}
