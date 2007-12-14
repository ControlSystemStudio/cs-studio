package org.csstudio.platform.internal.usermanagement;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.security.ILoginModule;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * The LoginContext performs the authentication by invoking
 * the Login module and stores the authenticated user.
 *
 */
public final class LoginContext {
	private String _name;
	private User _user = null;
	
	public LoginContext(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public void login(ILoginCallbackHandler handler) {
		ILoginModule loginModule = getLoginModule();
		if (loginModule != null) {
			_user = loginModule.login(handler);
			if (_user != null) {
				CentralLogger.getInstance().info(this,
						"User logged in: " + _user.getUsername());
				new InstanceScope().getNode(CSSPlatformPlugin.ID).put(SecurityFacade.LOGIN_LAST_USER_NAME, _user.getUsername());
				RightsManagementService.getInstance().readRightsForUser(_user);
			} else {
				CentralLogger.getInstance().info(this, "Using anonymously");
			}
		} else {
			_user = null;
		}
	}
	
	private ILoginModule getLoginModule() {
		IExtension[] extension = Platform.getExtensionRegistry()
			.getExtensionPoint("org.csstudio.platform.loginModule")
			.getExtensions();
		if (extension.length > 0) {
			IExtension lmExtension = extension[0];
			IConfigurationElement lmConfigElement = lmExtension
					.getConfigurationElements()[0];
			try {
				return (ILoginModule) lmConfigElement
						.createExecutableExtension("class");
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public void logout() {
		
	}
	
	public User getUser() {
		return _user;
	}
	
	public boolean isLoginAvailable() {
		return (getLoginModule() != null);
	}
}
