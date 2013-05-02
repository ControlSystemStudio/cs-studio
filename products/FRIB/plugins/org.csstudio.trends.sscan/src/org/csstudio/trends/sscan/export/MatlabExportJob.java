/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.export;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Eclipse Job for exporting data from Model to Matlab-format file.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MatlabExportJob extends ExportJob
{
    public MatlabExportJob(final Model model, final Source source,
            final int optimize_count, final String filename,
            final ExportErrorHandler error_handler)
    {
        super("% ", model, source, optimize_count, filename, error_handler);
    }

    /** {@inheritDoc} */
    @Override
    protected void printExportInfo(final PrintStream out)
    {
        super.printExportInfo(out);
        out.println(comment);
        out.println(comment + "This file can be loaded into Matlab");
        out.println(comment);
        out.println(comment + "It defines a 'Time Series' object for each channel");
        out.println(comment + "which can be displayed via the 'plot' command.");
        out.println(comment + "Time series can be analyzed further with the Matlab");
        out.println(comment + "Time Series Tools, see Matlab manual.");
        out.println();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void performExport(final IProgressMonitor monitor,
                                 final PrintStream out) throws Exception
    {
        final DateFormat date_format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
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
            MatlabQualityHelper qualities = new MatlabQualityHelper();
            long line_count = 0;
            out.println("clear t;");
            out.println("clear v;");
            out.println("clear q;");
        

            out.println(comment + "Convert time stamps into 'date numbers'");
            out.println("tn=datenum(t, 'yyyy/mm/dd HH:MM:SS.FFF');");
            out.println(comment + "Prepare patched data because");
            out.println(comment + "timeseries() cannot handle duplicate time stamps");
            out.println("[xx, idx]=unique(tn, 'last');");
            out.println("pt=tn(idx);");
            out.println("pv=v(idx);");
            out.println("pq=q(idx);");
            out.println("clear xx idx");
            out.println(comment + "Convert into time series and plot");
            // Patch "_" in name because Matlab plot will interprete it as LaTeX sub-script
            final String channel_name = item.getDisplayName().replace("_", "\\_");
            out.println("channel"+i+"=timeseries(pv', pt', pq', 'IsDatenum', true, 'Name', '"+channel_name+"');");

            out.print("channel"+i+".QualityInfo.Code=[");
            for (int q=0; q<qualities.getNumCodes(); ++q)
                out.print(" " + q);
            out.println(" ];");

            out.print("channel"+i+".QualityInfo.Description={");
            for (int q=0; q<qualities.getNumCodes(); ++q)
                out.print(" '" + qualities.getQuality(q) + "'");
            out.println(" };");
            
            out.println();
        }
        out.println(comment + "Example for plotting the data");
        for (int i=0; i<model.getItemCount(); ++i)
        {
            out.println("subplot(1, " + model.getItemCount() + ", " + (i+1) + ");");
            out.println("plot(channel" + i + ");");
        }
    }
}
