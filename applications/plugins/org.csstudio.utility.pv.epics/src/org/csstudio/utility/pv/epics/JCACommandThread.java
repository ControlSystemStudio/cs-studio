package org.csstudio.utility.pv.epics;

import java.util.LinkedList;


/** JCA command pump, added for two reasons:
 *  <ol>
 *  <li>JCA callbacks can't directly send JCA commands
 *      without danger of a deadlock, at least not with JNI
 *      and the "DirectRequestDispatcher"
 *  <li>Instead of calling 'flushIO' after each command,
 *      this thread allows for a few requests to queue up,
 *      then periodically pumps them out with only a final
 *      'flush'
 *  </ol>
 *  @author Kay Kasemir
 *  TODO Use this!
 */
@SuppressWarnings("nls")
class JCACommandThread extends Thread
{
    final private static long DELAY_MILLIS = 100;
    
    final private LinkedList<Runnable> command_queue =
        new LinkedList<Runnable>();
    
    private boolean run;
    
    public JCACommandThread()
    {
        super("JCA Command Thread");
    }
    
    /** Start the thread */
    @Override
    public void start()
    {
        run = true;
        super.start();
    }

    /** Stop the thread and wait for it to finish */
    void shutdown()
    {
        run = false;
        try
        {
            join();
        }
        catch (InterruptedException ex)
        {
            Activator.logException("JCACommandThread shutdown", ex);
        }
    }
    
    /** Add a command to the queue.
     *  <p>
     *  In case the caller wants to wait for the command:
     *  After running the command, it will be notified.
     *  @param command
     */
    void addCommand(final Runnable command)
    {
        synchronized (command_queue)
        {
            command_queue.addLast(command);
        }
    }
    
    /** @return Oldest queued command or <code>null</code> */
    private Runnable getCommand()
    {           
        synchronized (command_queue)
        {
            if (command_queue.size() > 0)
                return command_queue.removeFirst();
        }
        return null;
    }
    
    @Override
    public void run()
    {
        while (run)
        {
            // Execute all the commands currently queued...
            Runnable command = getCommand();
            while (command != null)
            {
                try
                {
                    command.run();
                }
                catch (Throwable ex)
                {
                    Activator.logException("JCACommandThread exception", ex);
                }
                synchronized (command)
                {
                    command.notifyAll();
                }
                command = getCommand();
            }
            // Flush once
            PVContext.flush();
            // Then wait.
            try
            {
                Thread.sleep(DELAY_MILLIS);
            }
            catch (InterruptedException ex)
            { /* don't even ignore */ }
        }
    }

}
