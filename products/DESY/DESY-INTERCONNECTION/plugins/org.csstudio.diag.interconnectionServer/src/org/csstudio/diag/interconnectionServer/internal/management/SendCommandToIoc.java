/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.internal.management;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.diag.interconnectionServer.server.IocCommandSender;
import org.csstudio.diag.interconnectionServer.server.IocConnectionManager;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Management command to send a command to an IOC.
 *
 * @author Joerg Rathlev
 */
public class SendCommandToIoc implements IManagementCommand {

	/**
	 * {@inheritDoc}
	 */
	public CommandResult execute(final CommandParameters parameters) {
		final String ioc = (String) parameters.get("ioc");
		final int port = commandPort();
		final String command = (String) parameters.get("command");

		try {
		    final IocCommandSender sender =
		        new IocCommandSender(IocConnectionManager.INSTANCE.getIocInetAdressByName(ioc), port, command);
		    InterconnectionServer.getInstance().getCommandExecutor().execute(sender);
		} catch (final IllegalArgumentException e) {
		    return CommandResult.createFailureResult("Creation of command sender failed:\n" + e.getMessage());
		}
		return CommandResult.createSuccessResult();
	}

	/**
	 * Returns the command port from the preferences.
	 *
	 * @return the port.
	 */
	private int commandPort() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		return prefs.getInt(Activator.PLUGIN_ID,
				PreferenceConstants.COMMAND_PORT_NUMBER, 0, null);
	}

}
