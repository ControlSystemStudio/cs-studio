/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.util.StreamStringReaderThread;
import org.csstudio.alarm.beast.notifier.util.StreamSwallowThread;
import org.csstudio.java.string.StringSplitter;

/**
 * Action which handle unrecognized actions (undefined scheme).
 * Simply run a command in a thread.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class CommandActionImpl implements IAutomatedAction {

	/** Command to run. Format depends on OS */
	public static enum CommandState {
		/** Command has not been started, no idea how it will work out */
		UNKNOWN,
		/** Command produced an immediate error */
		ERROR,
		/** Command was still running at the end of the wait period */
		LEFT_RUNNING,
		/** Command finished without error within the wait period */
		FINISHED_OK,
		/** Command ended with an error code within the wait period */
		FINISHED_ERROR
	}

	/** Command to run. Format depends on OS */
	private String command;

	/** Time to wait for completion in seconds */
	private int wait;

	/** Command to run. Format depends on OS */
	private CommandState state = CommandState.UNKNOWN;


    /** {@inheritDoc} */
	@Override
	public void init(ItemInfo item, AAData auto_action, IActionHandler handler)
			throws Exception {
		String details = auto_action.getDetails();
		if (details != null && details.startsWith("cmd:"))
			this.command = details.substring(4);
		else this.command = details;
		this.wait = Preferences.getCommandCheckTime();
	}

	/** {@inheritDoc} */
	@Override
	public void execute(List<PVSnapshot> pvs) throws Exception {
		final String dir;
		try {
			dir = Preferences.getCommandDirectory();
		} catch (Exception ex) {
			Activator.getLogger().log(Level.SEVERE,
					"Can not find command directory: {0}", ex.getMessage());
			return;
		}
		if (command.contains("*")) { // List PVs and their alarm severity
			final StringBuilder buf = new StringBuilder();
			for (PVSnapshot pv : pvs) {
				if (buf.length() > 0) buf.append(" ");
				buf.append(pv.getPath());
				buf.append(" ");
				buf.append(pv.getSeverity().name());
			}
			final String expanded_command = command.replace("*", buf.toString());
			execCmd(dir, expanded_command, wait);
		} else execCmd(dir, command, wait);
	}
	
	public void execCmd(String dir_name, String command, int wait) {
		// Execute command in a certain directory
		final File dir = new File(dir_name);
		final Process process;
		try {
			final String[] cmd = StringSplitter.splitIgnoreInQuotes(command, ' ', true);
			process = new ProcessBuilder(cmd).directory(dir).start();
		} catch (Throwable ex) { // Cannot execute command at all
			state = CommandState.ERROR;
			error(-1, ex.getMessage());
			return;
		}
		// Ignore stdout, but capture stderr
		new StreamSwallowThread(process.getInputStream()).start();
		final StreamStringReaderThread stderr = new StreamStringReaderThread(process.getErrorStream());
		stderr.start();

		// Could use process.waitFor() to wait for the command to exit,
		// but that can take a long time, and then the user has probably
		// forgotten about the command and no longer needs to know the result.
		// So poll for exit code during 'wait' time:
		Integer exit_code = null;
		for (int w = 0; w < wait; ++w) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Ignore
			}
			try {
				exit_code = process.exitValue();
				break;
			} catch (IllegalThreadStateException ex) {
				// Process still runs, there is no exit code. Try again.
			}
		}
		// Process runs so long that we no longer care
		if (exit_code == null) {
			state = CommandState.LEFT_RUNNING;
			return;
		}
		// Process ended
		if (exit_code == 0) {
			state = CommandState.FINISHED_OK;
			return;
		}
		// .. with error; check error output
		state = CommandState.FINISHED_ERROR;
		error(exit_code, stderr.getText());
	}
	
	private void error(final int exit_code, final String stderr) {
		Activator.getLogger().log(Level.WARNING,
						"Failed to execute command: {0}, exit code: {1}, stderr: {2}",
						new Object[] { command, exit_code, stderr });
	}
	
	/** @return Command state */
	public CommandState getCommandState() {
		return state;
	}

	public void dump() {
		System.out.println("CommandActionImpl [\n\tcmd= " + command + "\n]");
	}

}
