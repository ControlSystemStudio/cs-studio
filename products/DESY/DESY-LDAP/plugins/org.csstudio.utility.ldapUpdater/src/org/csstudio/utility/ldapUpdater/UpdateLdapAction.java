package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.libs.dcf.actions.IAction;

public class UpdateLdapAction implements IAction {

	public Object run(Object param) {
    	LdapUpdater ldapUpdater=LdapUpdater.getInstance();
    	try {
			ldapUpdater.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
