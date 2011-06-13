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


import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote action for shutting down the server.
 * 
 * @author Joerg Rathlev
 */
public class ShutdownAction implements IManagementCommand {

	/**
	 * The logger used by this class.
	 */
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownAction.class);
    
	/**
	 * Shuts down the server.
	 * 
	 * @param param
	 *            the parameter supplied by the client. This must be a
	 *            <code>CommandParamters</code> containing a password string.
	 * @return a message.
	 */
	@Override
    public CommandResult execute(CommandParameters parameters) {
		String password = null;
		password = (String) parameters.get("PASSWORD");
		
		
		if (isCorrectPassword(password)) {
			LOG.info("Received shutdown command, shutting down now.");
			SaveValueServer.getRunningServer().stop();
			return CommandResult.createSuccessResult();
		} else {
			LOG.warn("Received shutdown command with invalid password.");
			return CommandResult.createFailureResult("Invalid password");
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
