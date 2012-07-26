package org.csstudio.alarm.beast.notifier.actions;

import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.model.AbstractNotificationAction;
import org.csstudio.alarm.beast.notifier.util.CommandExecutorThread;

/**
 * Action which handle unrecognized actions (undefined scheme).
 * Simply run a command in a thread.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class CommandNotificationAction extends AbstractNotificationAction {

	protected String command;

	/**
	 * CommandExecutorThread for the command, using the wait time from
	 * preferences, displaying errors as dialog.
	 * 
	 * JProfiler shows that this gets removed by the GC, but a
	 * java.lang.UnixProcess for the external process remains until the external
	 * program exits.
	 */
	private class ExecuteActionThread extends CommandExecutorThread 
	{
		public ExecuteActionThread(final String dir) {
			super(command, dir, Preferences.getCommandCheckTime());
		}

		@Override
		public void error(final int exit_code, final String stderr) {
			Activator.getLogger().log(Level.WARNING,
							"Failed to execute command: {0}, exit code: {1}, stderr: {2}",
							new Object[] { command, exit_code, stderr });
		}
	}
	
	public void init(AlarmNotifier notifier, ActionID id, ItemInfo item, int delay,
			String details) {
		super.init(notifier, id, item, delay, details);
		this.command = details;
	}
	
	/** {@inheritDoc} */
	@Override
	public void execute() {
		final String dir;
		try {
			dir = Preferences.getCommandDirectory();
		} catch (Exception ex) {
			Activator.getLogger().log(Level.SEVERE,
					"Can not find command directory: {0}", ex.getMessage());
			return;
		}
		new ExecuteActionThread(dir).start();
	}

	/** {@inheritDoc} */
	@Override
	public void dump() {
		System.out.println("DefaultNotificationAction [\n\tcmd= " + command + "\n]");
	}

}
