/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.FileOutputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.PlatformUI;

/** Invoke configuration editor for {@link LaunchConfig}
 *  @author Kay Kasemir
 */
public class Editor implements IEditorLauncher
{
	/** Invoked by Eclipse with the path to the launcher config
	 *  {@inheritDoc}
	 */
	@Override
	public void open(final IPath path)
	{
		LaunchConfig config;
		try
		{
			config = new LaunchConfig(path.toFile());
		}
		catch (Exception ex)
		{
			config = new LaunchConfig();
		}

		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final LaunchConfigDialog dlg = new LaunchConfigDialog(shell, config);
		if (dlg.open() != Window.OK)
			return;
		
		// Update file
		try
        {
			final FileOutputStream stream = new FileOutputStream(path.toFile());
			stream.write(dlg.getConfig().getXML().getBytes());
			stream.close();
			
			// Since we changed a file in the workspace, refresh it.
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
        }
        catch (Exception ex)
        {
        	MessageDialog.openError(shell, Messages.Error,
        			NLS.bind(Messages.LaunchConfigUpdateErrorFmt,
        					ex.getMessage()));
        }
	}
}
