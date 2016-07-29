/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.export;

import java.io.PrintStream;
import java.time.Instant;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Eclipse Job for exporting data from Model to file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlainExportJob extends ExportJob
{
    final protected ValueFormatter formatter;

    public PlainExportJob(final Model model,
            final Instant start, final Instant end, final Source source,
            final double optimize_parameter, final ValueFormatter formatter,
            final String filename,
            final ExportErrorHandler error_handler)
    {
        super("# ", model, start, end, source, optimize_parameter, filename, error_handler);
        this.formatter = formatter;
    }

    /** {@inheritDoc} */
    @Override
    protected void printExportInfo(final PrintStream out)
    {
        super.printExportInfo(out);
        out.println(comment + "Format     : " + formatter.toString());
        out.println(comment);
        out.println(comment + "Data is in TAB-delimited columns, should import into e.g. Excel");
        out.println();
    }

    /** {@inheritDoc} */
    @Override
    protected void performExport(final IProgressMonitor monitor,
                                 final PrintStream out) throws Exception
    {
        int count = 0;
        for (ModelItem item : model.getItems())
        {   // Item header
            if (count > 0)
                out.println();
            printItemInfo(out, item);
            // Get data
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getResolvedName()));
            final ValueIterator values = createValueIterator(item);
            // Dump all values
            out.println(comment + Messages.TimeColumn + Messages.Export_Delimiter + formatter.getHeader());
            long line_count = 0;
            while (values.hasNext()  &&  !monitor.isCanceled())
            {
                final VType value = values.next();

                final String time = TimestampHelper.format(VTypeHelper.getTimestamp(value));
                out.println(time + Messages.Export_Delimiter + formatter.format(value));
                ++line_count;
                if (++line_count % PROGRESS_UPDATE_LINES == 0)
                    monitor.subTask(NLS.bind("{0}: Wrote {1} samples", item.getResolvedName(), line_count));
            }
            ++count;
        }
    }
}
