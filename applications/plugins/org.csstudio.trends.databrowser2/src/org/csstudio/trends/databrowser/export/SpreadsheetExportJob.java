package org.csstudio.trends.databrowser.export;

import java.io.PrintStream;

import org.csstudio.archivereader.SpreadsheetIterator;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Ecipse Job for exporting data from Model to file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SpreadsheetExportJob extends PlainExportJob
{
    public SpreadsheetExportJob(final  Model model, 
            final ITimestamp start, final ITimestamp end, final Source source,
            final int optimize_count, final ValueFormatter formatter,
            final String filename,
            final ExportErrorHandler error_handler)
    {
        super(model, start, end, source, optimize_count, formatter, filename, error_handler);
    }

    /** {@inheritDoc} */
    @Override
    protected void performExport(final IProgressMonitor monitor,
                                 final PrintStream out) throws Exception
    {
        // Item header
        for (int i=0; i<model.getItemCount(); ++i)
            printItemInfo(out, model.getItem(i));
        out.println();
        // Spreadsheet Header
        out.print("# " + Messages.TimeColumn);
        for (int i=0; i<model.getItemCount(); ++i)
            out.print(Messages.Export_Delimiter + model.getItem(i).getName() + " " + formatter.getHeader());
        out.println();
        
        // Create speadsheet interpolation
        final ValueIterator iters[] = new ValueIterator[model.getItemCount()];
        for (int i=0; i<model.getItemCount(); ++i)
        {
            final ModelItem item = model.getItem(i);
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getName()));
            iters[i] = createValueIterator(item);
        }
        final SpreadsheetIterator sheet = new SpreadsheetIterator(iters);
        // Dump the spreadsheet lines
        long line_count = 0;
        while (sheet.hasNext()  &&  !monitor.isCanceled())
        {
            final ITimestamp time = sheet.getTime();
            final IValue line[] = sheet.next();
            out.print(time);
            for (int i=0; i<line.length; ++i)
                out.print(Messages.Export_Delimiter + formatter.format(line[i]));
            out.println();
            ++line_count;
            if ((line_count % PROGRESS_UPDATE_LINES) == 0)
                monitor.subTask(NLS.bind("Wrote {0} samples", line_count));
            if (monitor.isCanceled())
                break;
        }
    }
}
