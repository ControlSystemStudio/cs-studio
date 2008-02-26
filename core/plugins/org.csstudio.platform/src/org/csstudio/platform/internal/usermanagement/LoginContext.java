/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
