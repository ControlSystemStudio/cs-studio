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

import javax.naming.NamingException;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.diag.interconnectionServer.server.IocConnection;
import org.csstudio.diag.interconnectionServer.server.IocConnectionManager;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.eclipse.core.runtime.Platform;

/**
 * Enables or diables message handling for an IOC.
 *
 * @author Joerg Rathlev
 */
public class EnableDisableIoc implements IManagementCommand {

	/**
	 * {@inheritDoc}
	 */
	public CommandResult execute(final CommandParameters parameters) {
		if (parameters == null) {
			return CommandResult.createFailureResult("Paramters required");
		}
		final String ioc = (String) parameters.get("ioc");
		final String action = (String) parameters.get("action");
		if (ioc == null || action == null) {
			return CommandResult.createFailureResult("Parameters required");
		}

		final int dataPort = Integer.parseInt(Platform.getPreferencesService().getString(Activator.getDefault().getPluginId(),
				PreferenceConstants.DATA_PORT_NUMBER, "", null));
		IocConnection iocConnection;
        try {
            iocConnection = IocConnectionManager.INSTANCE.getIocConnection(IocConnectionManager.INSTANCE.getIocInetAdressByName(ioc), dataPort);
        } catch (final NamingException e) {
            return CommandResult.createFailureResult("LDAP name composition of IOC lookup failed for" + ioc);
        }
		boolean enabled = true;
		if (action.equals("disable")) {
			enabled = false;
		}
		iocConnection.setDisabled(!enabled);

		return CommandResult.createSuccessResult();
	}

}
