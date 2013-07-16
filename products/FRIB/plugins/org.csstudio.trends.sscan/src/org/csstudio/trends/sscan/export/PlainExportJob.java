/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.export;

import java.io.PrintStream;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
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
    		final Source source,
            final int optimize_count, final ValueFormatter formatter,
            final String filename,
            final ExportErrorHandler error_handler)
    {
        super("# ", model, source, optimize_count, filename, error_handler);
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
        for (int i=0; i<model.getItemCount(); ++i)
        {
            final ModelItem item = model.getItem(i);
            // Item header
            if (i > 0)
                out.println();
            printItemInfo(out, item);
            // Get data
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getName()));

            // Dump all values
            out.println(comment + Messages.TimeColumn + Messages.Export_Delimiter + formatter.getHeader());
            long line_count = 0;

        }
    }
}
