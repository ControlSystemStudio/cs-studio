/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorLauncher;

/** Launcher for an (external) application
 * 
 *  <p>plugin.xml associates this with application files.
 * 
 *  @author Kay Kasemir
 */
public class Launcher implements IEditorLauncher
{
	/** Invoked by Eclipse with the path to the launcher config
	 *  {@inheritDoc}
	 */
	@Override
	public void open(final IPath path)
	{
		final File file = path.toFile();
		
		// Ignore empty files
		// as they are created initially
		// when adding a new file to the navigator
		if (file.length() <= 0)
			return;
		try
		{
			final LaunchConfig config = new LaunchConfig(file);
			launchCommand(config.getCommand());
		}
		catch (Exception ex)
		{
			MessageDialog.openError(null,
					Messages.Error,
					NLS.bind(Messages.ConfigFileErrorFmt,
							path, ex.getMessage()));
		}
	}

	/** Execute a command
	 *  @param command
	 */
	private void launchCommand(final String command)
    {
		// Is that really all?
        Program.launch(command);
        
//        // More ideas from org.eclipse.ui.internal.misc.ExternalEditor 
//        if (Util.isMac()) {
//			Runtime.getRuntime().exec(
//					new String[] { "open", "-a", programFileName, path });
//		} else {
//			Runtime.getRuntime().exec(
//					new String[] { programFileName, path });
    }
}
