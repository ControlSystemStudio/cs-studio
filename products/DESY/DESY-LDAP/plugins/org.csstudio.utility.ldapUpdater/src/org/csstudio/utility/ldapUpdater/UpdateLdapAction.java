package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;

public class UpdateLdapAction implements IManagementCommand {

	@Override
	public CommandResult execute(CommandParameters parameters) {
    	LdapUpdater ldapUpdater=LdapUpdater.getInstance();
    	try {
			if (!ldapUpdater.busy){
				ldapUpdater.start();
			}else{
				return CommandResult.createMessageResult("ldapUpdater is busy for max. 150 s (was probably started by timer). Try later!");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			CentralLogger.getInstance().error (this, "Exception while try to start run" );		
		}
		return CommandResult.createSuccessResult();
	}
}
