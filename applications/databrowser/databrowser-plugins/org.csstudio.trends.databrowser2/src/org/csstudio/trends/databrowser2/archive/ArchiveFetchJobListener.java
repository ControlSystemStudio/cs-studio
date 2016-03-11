/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;

/** Listener to an ArchiveFetchJob
 *  @author Kay Kasemir
 */
public interface ArchiveFetchJobListener
{
    /** Invoked when the job completed successfully
     *  @param job Job that completed
     */
    void fetchCompleted(ArchiveFetchJob job);

    /** Invoked when the job failed to complete
     *  @param job Job that had error
     *  @param archive Archive that job was currently accessing
     *  @param error Error description
     */
    void archiveFetchFailed(ArchiveFetchJob job, ArchiveDataSource archive, Exception error);

    /**
     *  Invoked when the channel was not found in at least one of the archive sources, regardless of whether in the end
     *  the data were loaded or not. Default implementation forwards the call to
     *  {@link #archiveFetchFailed(ArchiveFetchJob, ArchiveDataSource, Exception)} for each of the archive sources. The
     *  default implementation is for backward compatibility and should be overridden, unless you want to be notified
     *  multiple times for every channel (once for each archive source, where the channel was not found).
     *
     *  @param job Job that had error
     *  @param channelFoundAtLeastOnce if the channel was found in at least one data source, this parameter is
     *            <code>true</code>, if it was not found in any source it is <code>false</code>
     *  @param archivesThatFailed archive sources in which the channel was not found
     */
    default void channelNotFound(ArchiveFetchJob job, boolean channelFoundAtLeastOnce,
        ArchiveDataSource[] archivesThatFailed) {
        UnknownChannelException e = new UnknownChannelException(job.getPVItem().getResolvedName());
        for (ArchiveDataSource s : archivesThatFailed) {
            archiveFetchFailed(job, s, e);
        }
    }
}
