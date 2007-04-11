package org.csstudio.diag.interconnectionServer;

import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;

public class LoginCallbackHandler1 implements ILoginCallbackHandler {

	public Credentials getCredentials() {
		return new Credentials("icserver", "icserver");
	}

}
