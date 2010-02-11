package org.csstudio.trends.databrowser.archive;

import org.csstudio.trends.databrowser.model.ArchiveDataSource;

/** Listener to an ArchiveFetchJob
 *  @author Kay Kasemir
 */
public interface ArchiveFetchJobListener
{
    /** Invoked when the job completed successfully
     *  @param job Job that completed
     */
    void fetchCompleted(ArchiveFetchJob job);

    /** Invoked when the job completed successfully
     *  @param job Job that had error
     *  @param archive Archive that job was currently accessing
     *  @param error Error description
     */
    void archiveFetchFailed(ArchiveFetchJob job, ArchiveDataSource archive, Exception error);
}
