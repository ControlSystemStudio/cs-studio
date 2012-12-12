/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.startuphelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.csstudio.platform.workspace.WorkspaceIndependentStore;
import org.csstudio.platform.workspace.WorkspaceInfo;
import org.csstudio.startup.module.LoginExtPoint;
import org.csstudio.startup.module.WorkspaceExtPoint;
import org.csstudio.utility.product.StartupParameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 *
 * <code>WorkspacePromptExtPointImpl</code> uses the startup parameters which
 * define the default workspace and tries to set that url as the workspace for
 * the application. This implementation expects the following parameters
 * {@value LoginExtPoint#USERNAME}, {@value LoginExtPoint#PASSWORD},
 * {@value StartupParameters#FORCE_WORKSPACE_PROMPT_PARAM}, and
 * {@link WorkspaceExtPoint#WORKSPACE}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WorkspacePrompt implements WorkspaceExtPoint
{
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.WorkspacePromptExtPoint#promptForWorkspace(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	@Override
    public Object promptForWorkspace(Display display,
			IApplicationContext context, Map<String, Object> parameters)
	{
		Object o = parameters.get(StartupParameters.LOGIN_PROMPT_PARAM);
        final boolean login = o != null ? (Boolean)o : false;

        o = parameters.get(LoginExtPoint.USERNAME);
		final String username = o != null ? (String)o : null;

		o = parameters.get(LoginExtPoint.PASSWORD);
		final String password = o != null ? (String)o : null;

		o = parameters.get(StartupParameters.FORCE_WORKSPACE_PROMPT_PARAM);
		final boolean force_workspace_prompt = o != null ? (Boolean)o : false;

		o = parameters.get(WorkspaceExtPoint.WORKSPACE);
		final URL default_workspace = o != null ? (URL)o : null;

		if (! checkInstanceLocation(login, force_workspace_prompt,
				default_workspace, username, password, parameters)) {
			// The <code>stop()</code> routine of many UI plugins writes
			// the current settings to the workspace.
			// Even though we have not yet opened any workspace, that would
			// open, even create the default workspace.
			// So exit right away:
			System.exit(0);
			// .. instead of:
			//Platform.endSplash();
			return IApplication.EXIT_OK;
		}
		return null;
	}

	 /** Check or select the workspace.
     *  <p>
     *  See IDEApplication code from org.eclipse.ui.internal.ide.application
     *  in version 3.3.
     *  That example uses a "Shell" argument, but also has a comment about
     *  bug 84881 and thus not using the shell to force the dialogs to be
     *  top-level, so we skip the shell altogether.
     *  <p>
     *  Note that we must be very careful with anything that sets the workspace.
     *  For example, initializing a logger from preferences
     *  activates the default workspace, after which we can no
     *  longer change it...
     *  @param show_login Show the login (user/password) dialog?
     *  @param force_prompt Set <code>true</code> in a Control Room
     *         setting where the initial suggestion is always the "default"
     *         workspace, and there is no way to suppress the "ask again" option.
     *         Set <code>false</code> in an Office setting where users can
     *         uncheck the "ask again" option and use the last workspace as
     *         a default.
     *  @param  default_workspace Default to use
     *  @param username the username to access the workspace
     *  @param password the password for the given username
     *  @return <code>true</code> if all OK
     */
    private boolean checkInstanceLocation(boolean show_login,
            final boolean force_prompt,
    		URL default_workspace, String username, String password,
    		Map<String, Object> parameters)
    {
        // Was "-data @none" specified on command line?
        final Location instanceLoc = Platform.getInstanceLocation();

        if (instanceLoc == null)
        {
            MessageDialog.openError(null,
                    "No workspace", //$NON-NLS-1$
                    "Cannot run without a workspace"); //$NON-NLS-1$
            return false;
        }

        // -data "/some/path" was provided...
        if (instanceLoc.isSet())
        {
            try
            {   // Lock
                if (instanceLoc.lock())
                    return true;
                // Two possibilities:
                // 1. directory is already in use
                // 2. directory could not be created
                final File ws_dir = new File(instanceLoc.getURL().getFile());
                if (ws_dir.exists())
                    MessageDialog.openError(null,
                            org.csstudio.platform.workspace.Messages.Workspace_InUseErrorTitle,
                            NLS.bind(org.csstudio.platform.workspace.Messages.Workspace_InUseError,
                                    ws_dir.getCanonicalPath()));
                else
                    MessageDialog.openError(null,
                            org.csstudio.platform.workspace.Messages.Workspace_DirectoryErrorTitle,
                            org.csstudio.platform.workspace.Messages.Workspace_DirectoryError);
            }
            catch (IOException ex)
            {
                MessageDialog.openError(null,
                        org.csstudio.platform.workspace.Messages.Workspace_LockErrorTitle,
                        org.csstudio.platform.workspace.Messages.Workspace_LockError
                        + ex.getMessage());
            }
            return false;
        }

        // -data @noDefault or -data not specified, prompt and set
        if (default_workspace == null)
            default_workspace = instanceLoc.getDefault();

        final WorkspaceInfo workspace_info =
            new WorkspaceInfo(default_workspace, !force_prompt);

        // Prompt in any case? Or did user decide to be asked again?
        boolean show_Workspace = force_prompt | workspace_info.getShowDialog();

        //if no user name provided, display last login user.
        if(username == null)
        	username = WorkspaceIndependentStore.readLastLoginUser();

        //initialize startupHelper
        StartupHelper startupHelper = new StartupHelper(null, force_prompt,
        		workspace_info, username, password, show_login, show_Workspace);

        while (true)
        {
        	startupHelper.setShow_Login(show_login);
        	startupHelper.setShow_Workspace(show_Workspace);

            if (show_Workspace || show_login)
            {
                if (! startupHelper.openStartupDialog())
                    return false; // canceled

                //get user name and password from startup dialog
                if(show_login) {
                	username = startupHelper.getUserName();
                	password = startupHelper.getPassword();
                }
            }
            // In case of errors, we will have to ask the workspace,
            // but don't bother to ask user name and password again.
            show_Workspace = true;
            show_login = false;

            try
            {
                // the operation will fail if the url is not a valid
                // instance data area, so other checking is unneeded
                URL workspaceUrl = new URL("file:" + workspace_info.getSelectedWorkspace()); //$NON-NLS-1$
                if (instanceLoc.set(workspaceUrl, true)) // set & lock
                {
                    workspace_info.writePersistedData();
                    parameters.put(WORKSPACE, workspaceUrl);
                    return true;
                }
            }
            catch (Exception ex)
            {
                MessageDialog.openError(null,
                        org.csstudio.platform.workspace.Messages.Workspace_GenericErrorTitle,
                        org.csstudio.platform.workspace.Messages.Workspace_GenericError + ex.getMessage());
                return false;
            }
            // by this point it has been determined that the workspace is
            // already in use -- force the user to choose again
            show_login = false;
            MessageDialog.openError(null,
                    org.csstudio.platform.workspace.Messages.Workspace_InUseErrorTitle,
                    NLS.bind(org.csstudio.platform.workspace.Messages.Workspace_InUseError,
                            workspace_info.getSelectedWorkspace()));
        }
    }
}
