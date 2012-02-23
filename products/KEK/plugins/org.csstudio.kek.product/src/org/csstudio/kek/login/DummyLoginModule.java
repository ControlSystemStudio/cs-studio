package org.csstudio.kek.login;

import org.csstudio.auth.security.ILoginCallbackHandler;
import org.csstudio.auth.security.ILoginModule;
import org.csstudio.auth.security.User;

/**
 * This login module always returns null as the user name, which
 * finally results in anonymous user at the end. The main
 * purpose of using this dummy login module is to suppress
 * warning that complains about nonexistence of login module.
 * 
 * @author Takashi Nakamoto
 */
public class DummyLoginModule implements ILoginModule {

	@Override
	public User login(ILoginCallbackHandler handler) {
		return null;
	}

	@Override
	public void logout() {
	}
}
