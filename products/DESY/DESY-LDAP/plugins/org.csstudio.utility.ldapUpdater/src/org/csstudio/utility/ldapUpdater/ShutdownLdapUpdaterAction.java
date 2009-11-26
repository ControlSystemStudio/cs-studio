package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;

public class ShutdownLdapUpdaterAction implements IManagementCommand {

	/**
	 * {@inheritDoc}
	 */
	public CommandResult execute(CommandParameters parameters) {
			LdapUpdaterServer.getRunningServer().stop();
			return CommandResult.createSuccessResult();
		}
	}