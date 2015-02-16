/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.junit.Test;
import static org.junit.Assert.*;

/** Used in the 'main()' routine test because this class name
 *  is easier to spot in the profiler than an anonymous inner class
 */
@SuppressWarnings("nls")
class CommandExecutorForSleep extends CommandExecutorThread
{
	/** Initialize Command Executor Thread
	 *  @param long_running Create long-running 'xclock' or 'sleep 10'?
	 */
    public CommandExecutorForSleep(final boolean long_running)
    {
        super(long_running ? "/usr/bin/xclock" : "sleep 10", CommandExecutorTest.dir, 5);
    }

    @Override
    public void error(int exitCode, String stderr)
    {
        System.out.println("Should not get an error, but got " + stderr);
    }
}

/** Unit test for CommandExecutor
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandExecutorTest
{
    final static String dir = "/tmp";

    // In all these tests, the CommandExecutorThread finishes OK,
    // but the result of the executed command varies
    @Test
    public void testError() throws Exception
    {
        final CommandExecutorThread cmd = new CommandExecutorThread("cat /etc/not_there", dir, 2)
        {
            @Override
            public void error(int exit_code, String stderr)
            {
                System.out.println("Good, caught code " + exit_code + ":");
                System.out.println(stderr);
            }
        };
        cmd.start();
        Thread.sleep(2000);
        System.out.println("Thread: " + cmd.getState());
        System.out.println("Command:" + cmd.getCommandState());
        assertEquals(Thread.State.TERMINATED, cmd.getState());
        assertEquals(CommandExecutorThread.CommandState.FINISHED_ERROR, cmd.getCommandState());
    }

    @Test
    public void testOK()  throws Exception
    {
        final CommandExecutorThread cmd = new CommandExecutorThread("sleep 2", dir, 4)
        {
            @Override
            public void error(int exit_code, String stderr)
            {
                fail("Should not get an error!");
            }
        };
        cmd.start();
        Thread.sleep(6000);
        System.out.println("Thread: " + cmd.getState());
        System.out.println("Command:" + cmd.getCommandState());
        assertEquals(Thread.State.TERMINATED, cmd.getState());
        assertEquals(CommandExecutorThread.CommandState.FINISHED_OK, cmd.getCommandState());
    }
    
    @Test
    public void testLong() throws Exception
    {
        final CommandExecutorThread cmd = new CommandExecutorThread("sleep 20", dir, 5)
        {
            @Override
            public void error(int exit_code, String stderr)
            {
                fail("Should not get an error!");
            }
        };
        cmd.start();
        Thread.sleep(7000);
        System.out.println("Thread: " + cmd.getState());
        System.out.println("Command:" + cmd.getCommandState());
        assertEquals(Thread.State.TERMINATED, cmd.getState());
        assertEquals(CommandExecutorThread.CommandState.LEFT_RUNNING, cmd.getCommandState());
    }
    
    // Long running stand-alone test to check for memory leaks.
    // JProfiler on OS X shows a java.lang.UnixProcess for each
    // CommandExecutorForSleep.
    // On Linux, the UnixProcess implementation is
    // a little different but same end result:
    // One UnixProcess per actual external process.
    // 
    // The UnixProcess instances may linger until a garbage collection
    // is forced, but a GC removes all of them with both
    // JDK 1.5.0_09 and JDK 1.6.0_13.
    public static void main(String[] args) throws Exception
    {
    	final int N = 1000;
    	for (int i=0; i<N; ++i)
    	{
    	    // Command sleeps for 10 seconds.
    	    // Wait only 5 seconds for command, i.e. leave it running.
    	    // Do this every 5 seconds, i.e. about one sleep active all the time
    		System.out.println("Run " + (i+1));
            new CommandExecutorForSleep((i % 100) == 0).start();
            Thread.sleep(5*1000);
    	}
	}
}
