/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.desy.startuphelper.login;

import java.util.Map;

import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.dialogs.LoginDialog;
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
public class DesyLoginPrompt implements LoginExtPoint {
    /** {@inheritDoc} */
    @Override
    public Object login(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
        SecurityFacade sf = SecurityFacade.getInstance();
//        String lastUser = Platform.getPreferencesService().getString(CSSPlatformPlugin.ID,SecurityFacade.LOGIN_LAST_USER_NAME , "", null); //$NON-NLS-1$
//        LoginDialog dialog = new LoginDialog(null,lastUser);
        LoginDialog dialog = new LoginDialog(null, null);
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
