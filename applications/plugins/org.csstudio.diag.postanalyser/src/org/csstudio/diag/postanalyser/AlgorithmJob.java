package org.csstudio.diag.postanalyser;

import org.csstudio.diag.postanalyser.model.Algorithm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse Job that processes an Algorithm.
 *  @author Kay Kasemir
 */
public class AlgorithmJob extends Job
{
    final private Algorithm algorithm;
    private AlgorithmJobListener listener;

    /** Construct job.
     *  <p>
     *  Called still needs to schedule the job!
     *  @param algorithm Algorithm to process
     *  @param listener Listener to notify about completion or error
     */
    public AlgorithmJob(final Algorithm algorithm,
            final AlgorithmJobListener listener)
    {
        super(algorithm.getName());
        this.algorithm = algorithm;
        this.listener = listener;
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        try
        {
            algorithm.process();
            if (monitor.isCanceled())
                Activator.getLogger().info(algorithm.getName() + " was cancelled"); //$NON-NLS-1$
            else
                listener.algorithmDone(algorithm);
        }
        catch (Exception ex)
        {
            listener.algorithmFailed(algorithm, ex);
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }
}
