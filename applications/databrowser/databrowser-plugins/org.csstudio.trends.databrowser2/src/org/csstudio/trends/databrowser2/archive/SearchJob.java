/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import java.util.ArrayList;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/** Eclipse background job for searching names on archive data server
 *  @author Kay Kasemir
 */
abstract public class SearchJob extends Job
{
    final private ArchiveDataSource archives[];
    final private String pattern;
    final boolean pattern_is_glob;

    /** Create job that connects to given URL, then notifies view when done. */
    public SearchJob(final ArchiveDataSource archives[],
            final String pattern, final boolean pattern_is_glob)
    {
        super(NLS.bind(Messages.SearchChannelFmt, pattern));
        this.archives = archives;
        this.pattern = pattern;
        this.pattern_is_glob = pattern_is_glob;
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        final ArrayList<ChannelInfo> channels = new ArrayList<>();
        monitor.beginTask(Messages.Search, archives.length);
        for (ArchiveDataSource archive : archives)
        {
            try
            (
                final ArchiveReader reader = ArchiveRepository.getInstance().getArchiveReader(archive.getUrl());
            )
            {
                if (monitor.isCanceled()) {
                    monitor.done();
                    return Status.CANCEL_STATUS;
                }
                monitor.subTask(archive.getName());
                final String[] names;
                if (pattern_is_glob)
                    names = reader.getNamesByPattern(archive.getKey(), pattern);
                else
                    names = reader.getNamesByRegExp(archive.getKey(), pattern);
                for (String name : names)
                    channels.add(new ChannelInfo(name, archive));
                monitor.worked(1);
            }
            catch (final Exception ex)
            {
                monitor.setCanceled(true);
                monitor.done();
                archiveServerError(archive.getUrl(), ex);
                return Status.CANCEL_STATUS;
            }
        }
        receivedChannelInfos(
            (ChannelInfo[]) channels.toArray(new ChannelInfo[channels.size()]));
        monitor.done();
        return Status.OK_STATUS;
    }

    /** Invoked when the job located names on the server
     *  @param channels List of names found on server
     */
    abstract protected void receivedChannelInfos(ChannelInfo channels[]);

    /** Invoked when the job failed
     *  @param url ArchiveServer URL that resulted in error
     *  @param ex Exception
     */
    abstract protected void archiveServerError(String url, Exception ex);
}
