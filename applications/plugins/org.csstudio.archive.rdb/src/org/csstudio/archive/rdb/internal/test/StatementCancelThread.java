package org.csstudio.archive.rdb.internal.test;

import java.sql.Statement;

import org.eclipse.core.runtime.IProgressMonitor;

/** Background thread that cancels a SQL statement when the progress monitor
 *  indicates that the user requested cancellation.
 *  <p>
 *  Some RDB statements can take a long time.
 *  Oracle and probably other RDBs support a <code>cancel</code>
 *  call for RDB statements, resulting in an exception for the ongoing
 *  statement.
 *  However, the task that's currently stuck in a long running query can of
 *  course not invoke this cancel method itself.
 *  This helper thread polls the Eclipse progress monitor for
 *  user requested cancellations and cancels the RDB statement. 
 *  
 *  @author kasemirk@ornl.gov
 */
public class StatementCancelThread extends Thread
{
    /** The progress monitor to check */
    private final IProgressMonitor progress;
    
    /** The statement to cancel */
    private final Statement statement;

    /** Should this thread still run? */
    private boolean run = true;
    
    /** Constructor
     *  @param name Task name
     *  @param progress The progress monitor to check
     *  @param statement The statement to cancel
     *  @see #end()
     */
    public StatementCancelThread(final String name,
            final IProgressMonitor progress,
            final Statement statement)
    {
        super(name);
        this.progress = progress;
        this.statement = statement;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        while (run)
        {
            // If user requests cancellation...
            if (progress.isCanceled())
            {
                // ... cancel the RDB statement
                try
                {
                    System.out.println(getName() + " cancels statement...");
                    statement.cancel();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                return;
            }
            // Else: Wait a little bit - maybe cut short by call to end()
            try
            {
                synchronized (this)
                {
                    this.wait(1000);
                }
            }
            catch (Exception ex)
            {
                // NOP
            }
        }
        System.out.println(getName() + " ends normally");
    }
    
    /** Ask the cancel thread to end, because the statement ran OK. */
    public void end()
    {
        run = false;
        // Notify thread to end right away, not waiting to the end
        // of a wait(1000).
        synchronized (this)
        {
            this.notify();
        }
    }
}
