/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.export;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.jmatio.io.MatFileIncrementalWriter;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;

/** Eclipse Job for exporting data from Model to Matlab-format file.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MatlabFileExportJob extends ExportJob
{
    final private String filename;

    public MatlabFileExportJob(final Model model, final ITimestamp start,
            final ITimestamp end, final Source source,
            final int optimize_count, final String filename,
            final ExportErrorHandler error_handler)
    {
        super("", model, start, end, source, optimize_count, null, error_handler);
        this.filename = filename;
    }

    /** {@inheritDoc} */
    @Override
    protected void performExport(final IProgressMonitor monitor,
                                 final PrintStream out) throws Exception
    {
        // This exporter does its own file handling
        if (out != null)
            throw new IllegalStateException();

        final MatFileIncrementalWriter writer = new MatFileIncrementalWriter(filename);
        for (int i=0; i<model.getItemCount(); ++i)
        {
            final ModelItem item = model.getItem(i);
            // Get data
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getName()));
            final ValueIterator iter = createValueIterator(item);
            final List<ITimestamp> times = new ArrayList<ITimestamp>();
            final List<Double> values = new ArrayList<Double>();
            final List<ISeverity> severities = new ArrayList<ISeverity>();
            while (iter.hasNext()  &&  !monitor.isCanceled())
            {
                final IValue value = iter.next();
                times.add(value.getTime());
                values.add(ValueUtil.getDouble(value));
                severities.add(value.getSeverity());
                if (values.size() % PROGRESS_UPDATE_LINES == 0)
                    monitor.subTask(NLS.bind("{0}: Obtained {1} samples", item.getName(), values.size()));
            }
            // Add to Matlab file
            final MLStructure struct = createMLStruct(i, item.getName(), times, values, severities);
            writer.write(struct);
        }

        writer.close();
    }

    /** Set element of cell array to text
     *  @param cell Cell array to update
     *  @param index Index of cell element
     *  @param text Text to place in cell element
     */
    private void setCellText(final MLCell cell, final int index, final String text)
    {
        cell.set(new MLChar(null, text), index);
    }

    /** Create ML Structure with data for a channel
     *  @param index Index of channel in model
     *  @param name Channel name
     *  @param times Time stamps
     *  @param values Values
     *  @param severities Severities
     *  @return {@link MLStructure}
     */
    private MLStructure createMLStruct(final int index, final String name,
            final List<ITimestamp> times,
            final List<Double> values,
            final List<ISeverity> severities)
    {
        final MLStructure struct = new MLStructure("channel" + index, new int[] { 1, 1 });
        final int N = values.size();
        final int[] dims = new int[] { N, 1 };
        final MLCell time = new MLCell(null, dims);
        final MLDouble value = new MLDouble(null, dims);
        final MLCell severity = new MLCell(null, dims);
        for (int i=0; i<N; ++i)
        {
            setCellText(time, i, times.get(i).toString());
            value.set(values.get(i), i);
            setCellText(severity, i, severities.get(i).toString());
        }
        struct.setField("name", new MLChar(null, name));
        struct.setField("time", time);
        struct.setField("value", value);
        struct.setField("severity", severity);
        return struct;
    }
}
