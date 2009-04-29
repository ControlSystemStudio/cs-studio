package org.csstudio.startuphelper.extensions.impl;

import java.util.Map;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.ui.dialogs.LoginDialog;
import org.csstudio.startuphelper.extensions.LoginExtPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <code>DefaultLoginPromptExtPoint</code> is the default implementation of the
 * login prompt extension point which uses the org.csstudio.platform.security
 * plugin to login to the application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultLoginPromptExtPoint implements LoginExtPoint {


	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.LoginPromptExtPoint#login(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object login(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
		SecurityFacade sf = SecurityFacade.getInstance();
		String lastUser = Platform.getPreferencesService().getString(CSSPlatformPlugin.ID,SecurityFacade.LOGIN_LAST_USER_NAME , "", null);
		LoginDialog dialog = new LoginDialog(null,lastUser);
		sf.setLoginCallbackHandler(dialog);
		if (sf.isLoginOnStartupEnabled()) {
			sf.authenticateApplicationUser();
		}
		Credentials credentials = dialog.getLoginCredentials();
		if (credentials != null) {
			parameters.put(USERNAME, credentials.getUsername());
			parameters.put(PASSWORD, credentials.getPassword());
		}
		return null;
	}
}
