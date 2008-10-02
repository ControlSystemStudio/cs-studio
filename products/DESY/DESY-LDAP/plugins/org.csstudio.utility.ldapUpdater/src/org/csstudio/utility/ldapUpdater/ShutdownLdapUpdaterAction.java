package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.libs.dcf.actions.IAction;

public class ShutdownLdapUpdaterAction implements IAction {

		public Object run(Object param) {
			LdapUpdaterServer.getRunningServer().stop();
			return "DONE";
		}
	}
