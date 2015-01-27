/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.io.File;

import org.csstudio.java.string.StringSplitter;

/** Thread for executing a (system) command.
 *  On Unix, that could be anything in the PATH.
 *  <p>
 *  Several things can happen:
 *  <ul>
 *  <li>Command finishes OK right away
 *  <li>Command gives error right away
 *  <li>Command runs for a long time, eventually giving error or OK.
 *  </ul>
 *  The command executor waits a little time to see if the command
 *  finishes, and calls back in case of an error.
 *  When the command finishes right away OK or runs longer,
 *  we leave it be.
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
abstract public class CommandExecutorThread extends Thread
{
    /** Command to run. Format depends on OS */
    public enum CommandState
    {
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

    /**  Directory where to run the command */
    final private String dir_name;

    /** Command to run. Format depends on OS */
    final private String command;

    /**  Time to wait for completion in seconds */
    final private int wait;

    /** Command to run. Format depends on OS */
    private CommandState state = CommandState.UNKNOWN;

    /** Initialize
     *  @param command Command to run. Format depends on OS.
     *  @param dir_name Directory where to run the command
     *  @param wait Time to wait for completion in seconds
     */
    public CommandExecutorThread(final String command, final String dir_name,
            final int wait)
    {
        super("CommandExecutor");
        this.command = command;
        this.dir_name = dir_name;
        this.wait = wait;
    }

    /** @return Command state */
    public CommandState getCommandState()
    {
        return state;
    }

    /** Derived class must implement this callback that's invoked
     *  in case of an error within the 'wait' time.
     *  <p>
     *  Will be invoked from CommandExecutor thread.
     *  @param exit_code Exit code of program
     *  @param stderr Standard error output of program
     */
    abstract public void error(final int exit_code, final String stderr);

    @Override
    public void run()
    {
        // Execute command in a certain directory
        final File dir = new File(dir_name);
        final Process process;
        try
        {
   	     	final String[] cmd = StringSplitter.splitIgnoreInQuotes(command, ' ', true);
            process = new ProcessBuilder(cmd).directory(dir).start();
        }
        catch (Throwable ex)
        {   // Cannot execute command at all
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
        for (int w=0; w<wait; ++w)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {   // Ignore
            }
            try
            {
                exit_code = process.exitValue();
                break;
            }
            catch (IllegalThreadStateException ex)
            {   // Process still runs, there is no exit code. Try again.
            }
        }
        // Process runs so long that we no longer care
        if (exit_code == null)
        {
            state = CommandState.LEFT_RUNNING;
            return;
        }
        // Process ended
        if (exit_code == 0)
        {
            state = CommandState.FINISHED_OK;
            return;
        }
        // .. with error; check error output
        state = CommandState.FINISHED_ERROR;
        error(exit_code, stderr.getText());
    }
}
