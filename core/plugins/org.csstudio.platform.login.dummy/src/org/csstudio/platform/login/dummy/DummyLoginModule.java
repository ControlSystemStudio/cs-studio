package org.csstudio.platform.login.dummy;

import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.security.ILoginModule;
import org.csstudio.platform.security.User;

public class DummyLoginModule implements ILoginModule {

	public User login(ILoginCallbackHandler handler) {
		User user = new User(handler.getCredentials().getUsername());
		return user;
	}

	public void logout() {

	}

}
