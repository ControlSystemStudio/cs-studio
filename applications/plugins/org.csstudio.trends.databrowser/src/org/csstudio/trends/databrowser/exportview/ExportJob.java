package org.csstudio.trends.databrowser.exportview;

import java.io.PrintWriter;
import java.util.Iterator;

import org.csstudio.apputil.time.SecondsParser;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.archive.cache.ArchiveExceptionDialog;
import org.csstudio.archive.crawl.RawValueIterator;
import org.csstudio.archive.crawl.SpreadsheetIterator;
import org.csstudio.archive.crawl.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IModelSamples;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSampleIterator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Eclipse background job for searching names on a data server.
 *  @author Kay Kasemir
 */
class ExportJob extends Job
{
    private static final int PROGRESS_LINE_GRANULARITY = 100;
    private final Shell shell;
    private final Model model;
    private final ITimestamp start, end;

    /** From where to export the data */
    enum Source
    {
    	/** Use data from plot (i.e. current Model memory) */
        Plot,
        /** Request raw archive data */
        Raw,
        /** Request averaged archive data */
        Average
    };
    private final Source source;
    private final double seconds;
    private final boolean format_spreadsheet;
    private final boolean format_severity;
    private final IValue.Format format;
    private final int precision;

    private final String filename;
    
    /** Create job for exporting data.
     *  @param shell Shell used to display error message
     *  @param model The model from which to export
     *  @param start
     *  @param end Start and end time.
     *  @param source Where to get the data.
     *  @param seconds Seconds between averaged samples
     *  @param add_live_samples Include the live samples, or only archive?
     *  @param format_spreadsheet Spreadsheet, or plain list?
     *  @param format_severity Include severity/status/info, or omit?
     */     
    public ExportJob(final Shell shell,
                     final Model model,
                     final ITimestamp start,
                     final ITimestamp end,
                     final Source source,
                     final double seconds,
                     final boolean format_spreadsheet,
                     final boolean format_severity,
                     final IValue.Format format,
                     final int precision,
                     final String filename)
    {
        super(Messages.ExportJobTitle);
        if (shell == null ||
            model == null ||
            start == null ||
            end == null ||
            source == null ||
            format == null ||
            filename == null)
            throw new NullPointerException("Received null argument"); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.start = start;
        this.end = end;
        this.source = source;
        this.seconds = seconds;
        this.format_spreadsheet = format_spreadsheet;
        this.format_severity = format_severity;
        this.format = format;
        this.precision = precision;
        this.filename = filename;
    }

    /* @see org.eclipse.core.runtime.jobs.Job#run() */
    @SuppressWarnings("nls") //$NON-NLS-1$
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        Plugin.getLogger().debug("ExportJob starts");
        monitor.beginTask(Messages.ExportJobTask, IProgressMonitor.UNKNOWN);
        final ArchiveCache cache = ArchiveCache.getInstance();
        PrintWriter out = null;
        int line_count = 0;
        final int N = model.getNumItems();
        try
        {
            out = new PrintWriter(filename);
            printHeader(out);

            // Get sample iterator for each channel.
            // Either dump it ASAP, or keep it for spreadsheet-iteration.
            final ValueIterator iters[] = new ValueIterator[N];
            for (int i=0;  i<N  &&  !monitor.isCanceled();  ++i)
            {
                final IModelItem item = model.getItem(i);
                iters[i] = getValueIterator(item, monitor, out, cache);
                if (format_spreadsheet == false)
                {   // Plain list: dump this channel's samples...
                    line_count = dumpOneItem(monitor, line_count,
                                             out, iters[i]);
                    // ... then release iterator ASAP
                    iters[i] = null;
                }
            }

            // No spreadsheet?
            // Then we're done, since we already dumped each channel.
            // Otherwise, dump the Spreadsheed:
            if (format_spreadsheet)
            {
                // Spreadsheet Header
                out.println();
                out.print(Messages.Comment + Messages.TimeColHeader);
                for (int i=0;   i<N;   ++i)
                {
                    final IModelItem item = model.getItem(i);
                    out.print(Messages.ColSep + item.getName());
                    if (format_severity)
                        out.print(Messages.ColSep + Messages.InfoColHeader);
                }
                out.println();
                
                // Dump the spreadsheet lines
                SpreadsheetIterator sheet = new SpreadsheetIterator(iters);
                while (sheet.hasNext())
                {
                    final ITimestamp time = sheet.getTime();
                    final IValue line[] = sheet.next();
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
                        Plugin.getLogger().info("ExportJob cancelled");
                        break;
                    }
                }
            }
        }
        catch (final Exception ex)
        {
            Plugin.getLogger().error("ExportJob error", ex);
            if (out != null)
                out.write("# Error: " + ex.getMessage());
            monitor.setCanceled(true);
            ArchiveExceptionDialog.showArchiveException(
                            shell, Messages.Error,
                            Messages.ExportErrorMsg, ex);
            return Status.CANCEL_STATUS;
        }
        finally
        {
            if (out != null)
                out.close();
        }
        monitor.done();
        Plugin.getLogger().debug("ExportJob finishes");
        return Status.OK_STATUS;
    }

    /** Create iterator for given model item.
     *  @param item Item
     *  @param monitor Monitor gets updated with name of item
     *  @param out info about item gets written to output
     *  @param cache Cache from which data is fetched
     *  @return ValueIterator or null
     *  @throws Exception on error.
     */
    private ValueIterator getValueIterator(final IModelItem item,
                    final IProgressMonitor monitor,
                    final PrintWriter out,
                    final ArchiveCache cache) throws Exception
    {
        final String item_name = item.getName();

        monitor.subTask(Messages.Job_Fetching + item_name);
        
        out.println();
        out.println(Messages.Comment + Messages.PV + item_name);
        out.println(Messages.Comment);

        // Is data from plot requested?
        // It is this no PV, i.e. the only data is in the plot?
        if (source == Source.Plot  ||  !(item instanceof IPVModelItem))
        {
            out.println(Messages.Comment + Messages.SourceLabel
                        + Messages.Source_Plot);
            final IModelSamples samples = item.getSamples();
            // Limit samples to the start...end range
            final int start_index =
                ChartSampleSearch.findClosestSample(samples, start.toDouble());
            final int end_index =
                ChartSampleSearch.findClosestSample(samples, end.toDouble());
            return new ModelSampleIterator(samples, start_index, end_index);
        }
        // else: Get iterator for archive samples
        out.println(Messages.Comment + Messages.Archives);
        // Query PV item for its underlying data sources
        final IPVModelItem pv_item = (IPVModelItem) item;
        // RawSampleIterator handles reading from multiple archives.
        // Build arrays of servers & keys
        final IArchiveDataSource archives[] = pv_item.getArchiveDataSources();
        final ArchiveServer servers[] = new ArchiveServer[archives.length];
        final int keys[] = new int[archives.length];
        for (int i=0; i < archives.length; ++i)
        {
            out.println(Messages.Comment + (i+1)
                            + Messages.EnumerationSep
                            + archives[i].getName());
            servers[i] = cache.getServer(archives[i].getUrl());
            keys[i] = archives[i].getKey();
        }
        if (archives.length <= 0)
            return null;
        if (source == Source.Average)
            return new RawValueIterator(servers, keys, item_name, start, end,
                ArchiveServer.GET_AVERAGE, new Object[] { new Double(seconds) });
        //else
        return new RawValueIterator(servers, keys, item_name, start, end);
    }
    
    /** The very first entry in the export file, the overall header. */
    @SuppressWarnings("nls")
	private void printHeader(final PrintWriter out)
    {
    	// A bit of a hack since we re-use messages from the GUI
        out.println(Messages.Comment + Messages.DataBrowserExport);
        out.println(Messages.Comment + Messages.Version + Plugin.Version);
        out.println(Messages.Comment + Messages.StartLabel + start);
        out.println(Messages.Comment + Messages.EndLabel + end);
        if (source == Source.Average)
            out.println(NLS.bind(Messages.Comment_Averaging,
            		             SecondsParser.formatSeconds(seconds)));
        else
            out.println(Messages.Comment + Messages.SourceLabel + source);
        out.println(Messages.Comment + Messages.Spreadsheet + ": "+ format_spreadsheet);
        out.println(Messages.Comment + Messages.IncludeSeverity + format_severity);
        out.println(NLS.bind(Messages.Comment_Format, format, precision));
        out.println(Messages.ExportFileInfo);
    }
    
    /** Dump all the samples for one item.
     *  @param monitor
     *  @param line_count
     *  @param out
     *  @param channel_iter
     *  @return New line count
     */
    private int dumpOneItem(final IProgressMonitor monitor,
                            int line_count, final PrintWriter out,
                            final Iterator<IValue> channel_iter)
    {
        while (channel_iter.hasNext())
        {
            final IValue sample = channel_iter.next();
            out.println(sample.getTime() + Messages.ColSep + formatValue(sample));
            ++line_count;
            if ((line_count % PROGRESS_LINE_GRANULARITY) == 0)
                monitor.subTask(Messages.LinesWritten + line_count);
            if (monitor.isCanceled())
            {
                out.println(Messages.Comment + Messages.Cancelled);
                Plugin.getLogger().info("ExportJob cancelled"); //$NON-NLS-1$
                break;
            }
        }
        return line_count;
    }
    
    /** Format one value, according to the format/precision settings,
     *  maybe with severity/status info.
     */
    private String formatValue(IValue value)
    {
        String text;
        if (value == null)
            text = Messages.NoDataMarker;
        else
            text = value.format(format, precision);
        // value is set, maybe add severity/status.
        if (format_severity)
        {
            String info = (value == null) ? "" : ValueUtil.getInfo(value); //$NON-NLS-1$
            if (info == null)
                info = ""; //$NON-NLS-1$
            return text + Messages.ColSep + info;
        }
        return text;
    }
}