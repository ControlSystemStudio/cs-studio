package org.csstudio.alarm.beast.notifier.test;

import org.csstudio.alarm.beast.notifier.actions.DefaultNotificationAction;
import org.csstudio.alarm.beast.notifier.util.CommandExecutorThread;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link DefaultNotificationAction}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class DefaultActionUnitTest {

	private String command;

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
			super(command, dir, 10);
		}

		@Override
		public void error(final int exit_code, final String stderr) {
			System.out.println("Exit code: " + exit_code + "\nstderr: " + stderr);
		}
	}
	
	@Test
	public void testFakeCommand() {
		final String dir = "/home/ITER/arnaudf/";
		command = "dtc";
		ExecuteActionThread eat = new ExecuteActionThread(dir);
		eat.run();
		Assert.assertTrue(eat.getCommandState().equals(ExecuteActionThread.CommandState.ERROR));
	}
	
	@Test
	public void testSimpleCommand() {
		final String dir = "/home/ITER/arnaudf/";
		command = "dirname .";
		ExecuteActionThread eat = new ExecuteActionThread(dir);
		eat.run();
		Assert.assertTrue(eat.getCommandState().equals(ExecuteActionThread.CommandState.FINISHED_OK));
	}
}
