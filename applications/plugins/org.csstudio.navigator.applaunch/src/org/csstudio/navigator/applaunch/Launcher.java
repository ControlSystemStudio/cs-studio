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
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorLauncher;

/**
 * Launcher for an (external) application
 * 
 * <p>
 * plugin.xml associates this with application files.
 * 
 * @author Kay Kasemir
 */
public class Launcher implements IEditorLauncher {
	/**
	 * Invoked by Eclipse with the path to the launcher config {@inheritDoc}
	 */
	@Override
	public void open(final IPath path) {
		final File file = path.toFile();

		// Ignore empty files
		// as they are created initially
		// when adding a new file to the navigator
		if (file.length() <= 0)
			return;
		try {
			final LaunchConfig config = new LaunchConfig(file);
			launchCommand(config.getCommand());
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(null, Messages.Error, NLS.bind(
					Messages.ConfigFileErrorFmt, path, ex.getMessage()));
		}
	}

	/**
	 * Execute a command
	 * 
	 * @param command
	 */
	private void launchCommand(final String command) {
	
		// Modify by Laurent PHILIPPE GANIL on Avril 2012
		// GANIL need to use this for java app with arguments,
		// and Program mechanism would require wrapper script
 
		//If the command have no argument and it is a normal file use Program mechanism (Rely on operating system  file extension/program association).
		//But if there are arguments or the command is not a file use Runtime.exec mechanism 
		//that allows argument.
		
		
		// Remove unnecessary space
		final String commandToLaunch = command.trim().replaceAll(
				"( )+", " ");
		//System.out.println("Launcher.launchCommand() " + commandToLaunch);

		final String[] argv = commandToLaunch.split(" ");
		if (argv.length == 1) {
			//If no argument and command is a file use OS properties to launch the appropriate program  
			File file = new File(argv[0]);
			if (file.isFile()) {
				//System.out.println("Launcher.launchCommand() FILE");

				String extension = argv[0].substring(argv[0].lastIndexOf("."));
			
				if (!Program.launch(command)) {

					MessageDialog.openError(null, Messages.Error, NLS.bind(
							Messages.LaunchErrorProgram, commandToLaunch,
							Program.findProgram(extension).getName()));
				}
				return;
			}
		}

		//ELSE it is a command
		//Use a runnable due to the output of the command 
		
		//System.out.println("Launcher.launchCommand() CMD");
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Process p = Runtime.getRuntime().exec(argv);

					String line;
					BufferedReader is = new BufferedReader(
							new InputStreamReader(p.getInputStream()));

					//Display output of the cmd in the console
					while ((line = is.readLine()) != null)
						System.out.println(argv[0] + "=>" + line);

					// System.out.println("In Main after EOF");
					System.out.flush();
					
					p.waitFor(); // wait for process to complete
					

					if (p.exitValue() != 0) {
						MessageDialog.openError(null, Messages.Error, NLS.bind(
								Messages.LaunchErrorCmd, commandToLaunch,
								p.exitValue()));
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		Thread t = new Thread(r);
		t.start();

		// // More ideas from org.eclipse.ui.internal.misc.ExternalEditor
		// if (Util.isMac()) {
		// Runtime.getRuntime().exec(
		// new String[] { "open", "-a", programFileName, path });
		// } else {
		// Runtime.getRuntime().exec(
		// new String[] { programFileName, path });
	}

}
