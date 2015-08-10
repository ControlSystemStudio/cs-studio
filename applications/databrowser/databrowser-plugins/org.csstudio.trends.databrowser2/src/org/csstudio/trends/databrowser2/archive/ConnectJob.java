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

    /** Create job that connects to given URL, then notifies view when done. */
    public ConnectJob(final String url)
    {
        super(Messages.Connecting);
        this.url = url;
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        monitor.beginTask(url, IProgressMonitor.UNKNOWN);
        monitor.subTask(url);
        try
        (
            final ArchiveReader reader = ArchiveRepository.getInstance().getArchiveReader(url);
        )
        {
            final StringBuilder buf = new StringBuilder();
            buf.append("Archive Data Server: " + reader.getServerName() + "\n\n");
            buf.append("URL:\n" + reader.getURL() + "\n\n");
            buf.append("Version: " + reader.getVersion() + "\n\n");
            buf.append("Description:\n" + reader.getDescription());
            final ArchiveInfo infos[] = reader.getArchiveInfos();
            archiveServerConnected(buf.toString(), infos);
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
     *  @param server_info Human-readable server info, multi-line
     *  @param infos List of archives on server
     */
    abstract protected void archiveServerConnected(String server_info, ArchiveInfo infos[]);

    /** Invoked when the job failed
     *  @param url ArchiveServer URL that resulted in error
     *  @param ex Exception
     */
    abstract protected void archiveServerError(String url, Exception ex);
}
