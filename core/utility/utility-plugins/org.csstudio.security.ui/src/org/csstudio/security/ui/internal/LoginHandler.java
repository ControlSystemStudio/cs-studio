/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui.internal;

import org.csstudio.security.authentication.LoginJob;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/** Perform login via {@link LoginDialog}
 *  @author Kay Kasemir
 */
public class LoginHandler extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        try
        {
            final LoginDialog dlg = new LoginDialog(HandlerUtil.getActiveShell(event));
            dlg.open();
        }
        catch (Exception ex)
        {
            LoginJob.logout();
        }

        return null;
    }
}
