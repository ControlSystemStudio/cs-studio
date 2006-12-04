/**
 * 
 */
package org.csstudio.trends.databrowser.exportview;

import java.io.PrintWriter;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.Sample;
import org.csstudio.archive.cache.ArchiveCache;
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
    private final Model model;
    private final ITimestamp start, end;

    enum Source
    {
        Plot, Raw, Average
    };
    private final Source source;
    private final boolean add_live_samples;
    private final boolean format_spreadsheet;
    private final boolean format_severity;

    private final String filename;
    
    /** Create job for exporting data.
     *  @param start
     *  @param end Start and end time.
     *  @param source Where to get the data.
     *  @param add_live_samples Include the live samples, or only archive?
     *  @param format_spreadsheet Spreadsheet, or plain list?
     *  @param format_severity Include severity/status/info, or omit?
     */     
    public ExportJob(Model model,
                    ITimestamp start, ITimestamp end,
                    Source source,
                    boolean add_live_samples,
                    boolean format_spreadsheet,
                    boolean format_severity,
                    String filename)
    {
        super(Messages.ExportJobTitle);
        this.model = model;
        this.start = start;
        this.end = end;
        this.source = source;
        this.add_live_samples = add_live_samples;
        this.format_spreadsheet = format_spreadsheet;
        this.format_severity = format_severity;
        this.filename = filename;
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @SuppressWarnings("nls")
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        {
            System.out.println("Export:");
            System.out.println("Start                : " + start);
            System.out.println("End                  : " + end);
            System.out.println("Source               : " + source);
            System.out.println("Add live samples     : " + add_live_samples);
            System.out.println("Format as Spreadsheet: " + format_spreadsheet);
            System.out.println("Format with info     : " + format_severity);
        }
        
        
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
                Iterable<Sample> iter = new RawSampleIterator(
                                servers, keys, item_name, start, end);
                for (Sample sample : iter)
                {
                    out.println(sample);
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