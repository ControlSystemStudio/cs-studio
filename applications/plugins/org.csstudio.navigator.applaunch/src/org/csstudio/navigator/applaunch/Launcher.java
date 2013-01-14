/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Util;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorLauncher;

/** Launcher for an (external) application
 *
 *  <p>plugin.xml associates this with application files.
 *
 *  @author Kay Kasemir
 */
public class Launcher implements IEditorLauncher
{
	/** Invoked by Eclipse with the path to the launcher config {@inheritDoc} */
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
			MessageDialog.openError(null, Messages.Error, NLS.bind(
					Messages.ConfigFileErrorFmt, path, ex.getMessage()));
		}
	}

	/** Execute a command
	 *  @param command
	 */
	@SuppressWarnings("nls")
    private void launchCommand(final String command)
	{
	    // Remove unnecessary space
	    final String commandToLaunch = command.trim();

	    // Modified by Laurent PHILIPPE GANIL in April 2012:
		// GANIL need to use this for java app with arguments,
		// and Program mechanism would require wrapper script.

		// If the command has no argument and it is a normal file use Program mechanism
	    // (Rely on operating system  file extension/program association).
		// But if there are arguments or the command is not a file use Runtime.exec mechanism
		// that allows argument.

	    // On OS X there is the special case of "/Applications/Utilities/Terminal.app".
	    // It is technically a directory, but the Program.launch() mechanism understands
	    // how to open it as an application.
	    if (Util.isMac()  &&  commandToLaunch.contains(".app"))
	    {
	        if (!Program.launch(command))
	            MessageDialog.openError(null, Messages.Error,
	                    NLS.bind(Messages.LaunchErrorApp, command));
            return;
	    }

	    // Check if single command or comamnd with arguments
	    final String[] argv = commandToLaunch.split(" +");
	    if (argv.length == 1)
		{
			// If no argument and command is a file use OS properties to launch the appropriate program
		    final File file = new File(argv[0]);
			if (file.isFile())
			{
			    final int ext_index = argv[0].lastIndexOf(".");
			    final String extension = ext_index > 0 ? argv[0].substring(ext_index) : "";
				if (!Program.launch(command))
				{
					MessageDialog.openError(null, Messages.Error, NLS.bind(
							Messages.LaunchErrorProgram, commandToLaunch,
							Program.findProgram(extension).getName()));
				}
				return;
			}
		}

		//ELSE it is a command
		//Use a runnable due to the output of the command
		final Display display = Display.getCurrent();
		final Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final Process p = Runtime.getRuntime().exec(argv);

					String line;
					BufferedReader is = new BufferedReader(
							new InputStreamReader(p.getInputStream()));

					//Display output of the cmd in the console
					while ((line = is.readLine()) != null)
						System.out.println(argv[0] + "=>" + line);

					System.out.flush();

					p.waitFor(); // wait for process to complete

					if (p.exitValue() != 0)
					{
						display.syncExec(new Runnable()
						{
							@Override
							public void run() {
								MessageDialog.openError(null, Messages.Error, NLS.bind(
										Messages.LaunchErrorCmd, commandToLaunch,
										p.exitValue()));
							}
						});
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};

		Thread t = new Thread(r);
		t.start();
	}
}
