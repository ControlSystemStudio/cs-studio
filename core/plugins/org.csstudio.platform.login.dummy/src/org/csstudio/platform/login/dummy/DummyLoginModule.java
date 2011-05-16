package org.csstudio.platform.login.dummy;

import org.csstudio.auth.security.ILoginCallbackHandler;
import org.csstudio.auth.security.ILoginModule;
import org.csstudio.auth.security.User;

public class DummyLoginModule implements ILoginModule {

	public User login(ILoginCallbackHandler handler) {
		User user = new User(handler.getCredentials().getUsername());
		return user;
	}

	public void logout() {

	}

}
