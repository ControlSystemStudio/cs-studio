package org.csstudio.archive.engine.scanner;

import org.csstudio.archive.engine.Activator;

/** Thread that runs a Scanner.
 *  @author Kay Kasemir
 */
public class ScanThread implements Runnable
{
    /** Scanner to execute */
    final private Scanner scanner;
    
    /** The thread */
    private Thread thread;
    
    /** Flag, set <code>false</code> to cause thread to exit */
    private boolean do_run;

    /** Construct thread which runs scanner */
    public ScanThread(final Scanner scanner)
    {
        this.scanner = scanner;
    }
    
    /** Start the scan thread */
    public void start()
    {
        thread = new Thread(this, "ScanThread"); //$NON-NLS-1$
        thread.start();
    }
    
    /** Stop the scan thread
     *  @see #join()
     */
    public void stop()
    {
        do_run = false;
    }
    
    @SuppressWarnings("nls")
    public void run()
    {
        Activator.getLogger().info("Scan Thread runs");
        do_run = true;
        while (do_run)
        {
            scanner.run();
        }
        Activator.getLogger().info("Scan Thread ends");
    }

    /** Wait for thread to exit */
    public void join()
    {
        if (do_run)
            throw new Error("ScanThread still running"); //$NON-NLS-1$
        try
        {
            thread.join();
        }
        catch (InterruptedException ex)
        {
            Activator.getLogger().error("Scan Thread join attempt", ex); //$NON-NLS-1$
        }
    }
}
