package org.csstudio.trends.databrowser.plotpart;

/** Interface to notifications sent by ArchiveFetchJob
 *  @author Kay Kasemir
 */
public interface ArchiveFetchJobListener
{
    /** ArchiveFetchJob completed
     *  @param job The job
     */
    public void fetchCompleted(ArchiveFetchJob job);
}
