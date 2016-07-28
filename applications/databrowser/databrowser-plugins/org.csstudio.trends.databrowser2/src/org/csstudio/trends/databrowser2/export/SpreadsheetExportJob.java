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
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.SpreadsheetIterator;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Ecipse Job for exporting data from Model to file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SpreadsheetExportJob extends PlainExportJob
{
    public SpreadsheetExportJob(final  Model model,
            final Instant start, final Instant end, final Source source,
            final int optimize_parameter, final ValueFormatter formatter,
            final String filename,
            final ExportErrorHandler error_handler)
    {
        super(model, start, end, source, optimize_parameter, formatter, filename, error_handler);
    }

    /** {@inheritDoc} */
    @Override
    protected void performExport(final IProgressMonitor monitor,
                                 final PrintStream out) throws Exception
    {
        // Item header
        for (ModelItem item : model.getItems())
            printItemInfo(out, item);
        out.println();
        // Spreadsheet Header
        out.print("# " + Messages.TimeColumn);
        for (ModelItem item : model.getItems())
            out.print(Messages.Export_Delimiter + item.getResolvedName() + " " + formatter.getHeader());
        out.println();

        // Create speadsheet interpolation
        final List<ValueIterator> iters = new ArrayList<>();
        for (ModelItem item : model.getItems())
        {
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getResolvedName()));
            iters.add(createValueIterator(item));
        }
        final SpreadsheetIterator sheet = new SpreadsheetIterator(iters.toArray(new ValueIterator[iters.size()]));
        // Dump the spreadsheet lines
        long line_count = 0;

        while (sheet.hasNext()  &&  !monitor.isCanceled())
        {
            final Instant time = sheet.getTime();
            final VType line[] = sheet.next();
            out.print(TimestampHelper.format(time));

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
