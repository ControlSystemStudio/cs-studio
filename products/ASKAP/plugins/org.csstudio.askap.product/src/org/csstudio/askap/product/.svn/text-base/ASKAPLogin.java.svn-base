package org.csstudio.askap.product;

import java.util.Map;

import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.dialogs.LoginDialog;
import org.csstudio.startup.module.LoginExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

public class ASKAPLogin implements LoginExtPoint {

	public ASKAPLogin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object login(Display display, IApplicationContext context,
			Map<String, Object> parameters) {
		
		try {
			SecurityFacade sf = SecurityFacade.getInstance();
			LoginDialog dialog = new LoginDialog(null, null);
			sf.setLoginCallbackHandler(dialog);
			if (sf.isLoginOnStartupEnabled()) {
				sf.authenticateApplicationUser();
			}
			Credentials credentials = dialog.getLoginCredentials();
			if (credentials != null) {
				parameters.put(USERNAME, credentials.getUsername());
				parameters.put(PASSWORD, credentials.getPassword());
	
				return null;
			}
		} catch (Exception e) {
			System.err.println("Could not login " + e.getMessage());
			e.printStackTrace();
//			return IApplication.EXIT_OK;
			
			// if user can't log in, can still start the application
			return null;
		}
		
		// if user can't log in, can still start the application
		System.err.println("Not logged in");
//		return IApplication.EXIT_OK;
		return null;
	}

}
