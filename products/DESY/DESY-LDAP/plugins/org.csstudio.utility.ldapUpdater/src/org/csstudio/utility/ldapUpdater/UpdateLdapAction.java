package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.libs.dcf.actions.IAction;

public class UpdateLdapAction implements IAction {

	public Object run(Object param) {
    	LdapUpdater ldapUpdater=LdapUpdater.getInstance();
    	try {
			if (!ldapUpdater.busy){
				ldapUpdater.start();
			}else{
				return ("busy for max. 2 minutes");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ("ok");
	}

}
