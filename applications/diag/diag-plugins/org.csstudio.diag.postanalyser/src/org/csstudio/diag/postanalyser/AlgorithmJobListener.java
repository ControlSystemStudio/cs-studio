package org.csstudio.diag.postanalyser;

import org.csstudio.diag.postanalyser.model.Algorithm;

/** Listener interface for <code>AlgorithmJob</code>
 *  @author Kay Kasemir
 */
public interface AlgorithmJobListener
{
    /** Algorithm completed successfully
     *  @param algorithm The Algorithm
     */
    public void algorithmDone(final Algorithm algorithm);

    /** Algorithm failed
     *  @param algorithm The Algorithm
     *  @param ex The error
     */
    public void algorithmFailed(Algorithm algorithm, Throwable ex);
}
