/**
 * 
 */
package org.csstudio.trends.databrowser.exportview;

import java.io.PrintWriter;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.archive.crawl.RawSampleInfo;
import org.csstudio.archive.crawl.RawSampleIterator;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse background job for searching names on a data server.
 *  @author Kay Kasemir
 */
class ExportJob extends Job
{
    private static final int PROGRESS_LINE_GRANULARITY = 100;
    Model model;
    ITimestamp start, end;
    String filename;
    
    /** Create job that searches given server's keys for pattern,
     *  then notifies view about received names.
     */
    public ExportJob(Model model,
                    ITimestamp start, ITimestamp end, String filename)
    {
        super(Messages.ExportJobTitle);
        this.model = model;
        this.start = start;
        this.end = end;
        this.filename = filename;
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.ExportJobTask, IProgressMonitor.UNKNOWN);
        ArchiveCache cache = ArchiveCache.getInstance();
        try
        {
            int lines = 0;
            PrintWriter out = new PrintWriter(filename);
            
            // Overall Header
            out.println(Messages.Comment + Messages.DataBrowserExport);
            out.println(Messages.Comment + Messages.Version + Plugin.Version);
            out.println(Messages.Comment + Messages.Start + start);
            out.println(Messages.Comment + Messages.End + end);
            int N = model.getNumItems();
            for (int item_idx=0;
                item_idx<N  &&  !monitor.isCanceled(); 
                ++item_idx)
            {
                IModelItem item = model.getItem(item_idx);
                String item_name = item.getName();

                out.println();
                out.println(Messages.Comment);
                out.println(Messages.Comment + Messages.PV + item_name);
                out.println(Messages.Comment);
                out.println(Messages.Comment + Messages.Archives);

                // RawSampleIterator handles reading from multiple archives.
                // Build arrays of servers & keys
                IArchiveDataSource archives[] = item.getArchiveDataSources();
                ArchiveServer servers[] = new ArchiveServer[archives.length];
                int keys[] = new int[archives.length];
                for (int i=0; i < archives.length; ++i)
                {
                    out.println(Messages.Comment + (i+1)
                                    + Messages.EnumerationSep
                                    + archives[i].getName());
                    servers[i] = cache.getServer(archives[i].getUrl());
                    keys[i] = archives[i].getKey();
                }
                // Actual sample export
                RawSampleIterator iter = new RawSampleIterator(
                                servers, keys, item_name, start, end);
                for (RawSampleInfo sample : iter)
                {
                    out.println(sample.toString());
                    ++lines;
                    if ((lines % PROGRESS_LINE_GRANULARITY) == 0)
                        monitor.subTask(Messages.LinesWritten + lines);
                    if (monitor.isCanceled())
                    {
                        out.println(Messages.Comment + Messages.Cancelled);
                        break;
                    }
                }
            }
            // TODO: spreadsheet formatter.
            // Can't use the one from the data server, since this
            // request might span archive servers!
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}