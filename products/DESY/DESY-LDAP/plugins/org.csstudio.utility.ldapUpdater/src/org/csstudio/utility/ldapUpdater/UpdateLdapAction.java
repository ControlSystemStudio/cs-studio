package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.logging.CentralLogger;

public class UpdateLdapAction implements IAction {

	public Object run(Object param) {
    	LdapUpdater ldapUpdater=LdapUpdater.getInstance();
    	try {
			if (!ldapUpdater.busy){
				ldapUpdater.start();
			}else{
				return ("ldapUpdater is busy for max. 150 s (was started by timer). Try later!");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			CentralLogger.getInstance().error (this, "Exception while try to start run" );		
		}
		return ("ok");
	}
}
