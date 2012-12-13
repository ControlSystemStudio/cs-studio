/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.trends.databrowser2.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse background job for connecting to an archive data reader
 *  and obtaining the list of archives.
 *  @author Kay Kasemir
 */
abstract public class ConnectJob extends Job
{
    final private String url;
    private ArchiveReader reader;
    private ArchiveInfo infos[];
    
    /** Create job that connects to given URL, then notifies view when done. */
    public ConnectJob(final String url)
    {
        super(Messages.Connecting);
        this.url = url;
        this.reader = null;
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        monitor.beginTask(url, IProgressMonitor.UNKNOWN);
        try
        {
            monitor.subTask(url);
            reader = ArchiveRepository.getInstance().getArchiveReader(url);
            infos = reader.getArchiveInfos();
            archiveServerConnected(reader, infos);
        }
        catch (final Exception ex)
        {
            archiveServerError(url, ex);
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    /** Invoked when the job connected to the server and retrieved
     *  the available archives.
     *  @param reader ArchiveReader
     *  @param infos List of archives on server
     */
    abstract protected void archiveServerConnected(ArchiveReader reader,
            ArchiveInfo infos[]);

    /** Invoked when the job failed
     *  @param url ArchiveServer URL that resulted in error
     *  @param ex Exception
     */
    abstract protected void archiveServerError(String url, Exception ex);
}
