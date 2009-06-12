package org.csstudio.startuphelper.module;

import java.util.Map;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.ui.dialogs.LoginDialog;
import org.csstudio.startup.module.LoginExtPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * <code>DefaultLoginPrompt</code> is the default implementation of the
 * login prompt extension point which uses the org.csstudio.platform.security
 * plugin to login to the application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultLoginPrompt implements LoginExtPoint {
    /** {@inheritDoc} */
	public Object login(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
		SecurityFacade sf = SecurityFacade.getInstance();
		String lastUser = Platform.getPreferencesService().getString(CSSPlatformPlugin.ID,SecurityFacade.LOGIN_LAST_USER_NAME , "", null); //$NON-NLS-1$
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
