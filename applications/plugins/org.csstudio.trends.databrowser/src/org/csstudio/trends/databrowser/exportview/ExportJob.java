/**
 * 
 */
package org.csstudio.trends.databrowser.exportview;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Iterator;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.EnumSample;
import org.csstudio.archive.Sample;
import org.csstudio.archive.StringSample;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.archive.crawl.RawSampleIterator;
import org.csstudio.archive.crawl.SampleIterator;
import org.csstudio.archive.crawl.SpreadsheetIterator;
import org.csstudio.archive.util.SampleUtil;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSamples;
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
    enum Format
    {
        Default, Decimal, Exponential
    };
    private final Source source;
    private final boolean format_spreadsheet;
    private final boolean format_severity;
    private final Format format;
    private final int precision;

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
                    boolean format_spreadsheet,
                    boolean format_severity,
                    Format format,
                    int precision,
                    String filename)
    {
        super(Messages.ExportJobTitle);
        this.model = model;
        this.start = start;
        this.end = end;
        this.source = source;
        this.format_spreadsheet = format_spreadsheet;
        this.format_severity = format_severity;
        this.format = format;
        this.precision = precision;
        this.filename = filename;
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @SuppressWarnings("nls") //$NON-NLS-1$
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.ExportJobTask, IProgressMonitor.UNKNOWN);
        ArchiveCache cache = ArchiveCache.getInstance();
        try
        {
            int line_count = 0;
            PrintWriter out = new PrintWriter(filename);
            printHeader(out);

            // Get sample iterator for each channel.
            // Either dump it ASAP, or keep it for spreadsheet-iteration.
            int N = model.getNumItems();
            SampleIterator iters[] = new SampleIterator[N];
            for (int ch_idx=0;  ch_idx<N  &&  !monitor.isCanceled(); ++ch_idx)
            {
                IModelItem item = model.getItem(ch_idx);
                String item_name = item.getName();

                monitor.subTask(Messages.Job_Fetching + item_name);
                
                out.println();
                out.println(Messages.Comment);
                out.println(Messages.Comment + Messages.PV + item_name);
                out.println(Messages.Comment);
                out.println(Messages.Comment + Messages.Archives);

                if (source == Source.Plot)
                {
                    // TODO: Rethink the synchronization?
                    // Use lock/unlock semaphore, so samples
                    // can stay locked for the duration of the export job?
                    ModelSamples samples = item.getSamples();
                    iters[ch_idx] = new ModelSampleIterator(samples);
                }
                else
                {
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
                    iters[ch_idx] = new RawSampleIterator(
                                    servers, keys, item_name, start, end);
                }
                if (format_spreadsheet == false)
                {   // Plain list: dump this channel's samples...
                    line_count = dumpOneItem(monitor, line_count,
                                             out, iters[ch_idx]);
                    // ... then release iterator ASAP
                    iters[ch_idx] = null;
                }
            }

            // No spreadsheet? Then we're done.
            // Spreadsheed: Dump it
            if (format_spreadsheet)
            {
                // Spreadsheet Header
                out.println();
                out.print(Messages.Comment + Messages.TimeColHeader);
                for (int item_idx=0;  item_idx<N;   ++item_idx)
                {
                    IModelItem item = model.getItem(item_idx);
                    out.print(Messages.ColSep + item.getName());
                    if (format_severity)
                        out.print(Messages.ColSep + Messages.InfoColHeader);
                }
                out.println();
                
                // Dump the spreadsheet lines
                SpreadsheetIterator sheet = new SpreadsheetIterator(iters);
                while (sheet.hasNext())
                {
                    ITimestamp time = sheet.getTime();
                    Sample line[] = sheet.next();
                    out.print(time);
                    for (int i=0; i<line.length; ++i)
                        out.print(Messages.ColSep + formatValue(line[i]));
                    out.println();
                    
                    ++line_count;
                    if ((line_count % PROGRESS_LINE_GRANULARITY) == 0)
                        monitor.subTask(Messages.LinesWritten + line_count);
                    if (monitor.isCanceled())
                    {
                        out.println(Messages.Comment + Messages.Cancelled);
                        break;
                    }
                }
            }
            
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

    /** The very first entry in the export file, the overall header. */
    private void printHeader(PrintWriter out)
    {
        out.println(Messages.Comment + Messages.DataBrowserExport);
        out.println(Messages.Comment + Messages.Version + Plugin.Version);
        out.println(Messages.Comment + Messages.StartLabel + start);
        out.println(Messages.Comment + Messages.EndLabel + end);
        out.println(Messages.Comment + Messages.SourceLabel + source);
        out.println(Messages.Comment + Messages.OutputLabel + format_spreadsheet);
        out.println(Messages.Comment + Messages.FormatLabel + format);
        out.println(Messages.Comment + Messages.IncludeSeverity + format_severity);
    }
    
    /** Dump all the samples for one item.
     *  @param monitor
     *  @param line_count
     *  @param out
     *  @param channel_iter
     *  @return New line count
     */
    private int dumpOneItem(IProgressMonitor monitor,
                            int line_count, PrintWriter out,
                            Iterator<Sample> channel_iter)
    {
        while (channel_iter.hasNext())
        {
            Sample sample = channel_iter.next();
            out.println(sample.getTime() + Messages.ColSep + formatValue(sample));
            ++line_count;
            if ((line_count % PROGRESS_LINE_GRANULARITY) == 0)
                monitor.subTask(Messages.LinesWritten + line_count);
            if (monitor.isCanceled())
            {
                out.println(Messages.Comment + Messages.Cancelled);
                break;
            }
        }
        return line_count;
    }
    
    /** Format one value, according to the format/precision settings,
     *  maybe with severity/status info.
     */
    private String formatValue(Sample sample)
    {
        String value;
        if (sample == null)
            value = Messages.NoDataMarker;
        else
        {
            if (format == Format.Default  ||
                sample instanceof StringSample || sample instanceof EnumSample)
            {
                value = sample.format();
            }
            else
            {
                final double number = SampleUtil.getDouble(sample);
                if (Double.isInfinite(number))
                    value = Messages.NoDataMarker;
                else if (format == Format.Decimal)
                {
                    NumberFormat fmt = NumberFormat.getNumberInstance();
                    fmt.setMinimumFractionDigits(precision);
                    fmt.setMaximumFractionDigits(precision);
                    value = fmt.format(number);
                }
                else // format == Format.Exponential
                {
                    final String fmt = "%." + precision + "e"; //$NON-NLS-1$//$NON-NLS-2$
                    value = String.format(fmt, number);
                }
            }
        }
        // value is set, maybe add severity/status.
        if (format_severity)
        {
            String info = (sample == null) ? "" : SampleUtil.getInfo(sample); //$NON-NLS-1$
            if (info == null)
                info = ""; //$NON-NLS-1$
            return value + Messages.ColSep + info;
        }
        return value;
    }
}