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

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;
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

    public MatlabFileExportJob(final Model model, final Instant start,
            final Instant end, final Source source,
            final int optimize_parameter, final String filename,
            final ExportErrorHandler error_handler)
    {
        super("", model, start, end, source, optimize_parameter, null, error_handler);
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
        int i = 0;
        for (ModelItem item : model.getItems())
        {   // Get data
            monitor.subTask(NLS.bind("Fetching data for {0}", item.getResolvedName()));
            final ValueIterator iter = createValueIterator(item);
            final List<Instant> times = new ArrayList<Instant>();
            final List<Double> values = new ArrayList<Double>();
            final List<AlarmSeverity> severities = new ArrayList<AlarmSeverity>();
            while (iter.hasNext()  &&  !monitor.isCanceled())
            {
                final VType value = iter.next();
                times.add(VTypeHelper.getTimestamp(value));
                values.add(VTypeHelper.toDouble(value));
                severities.add(VTypeHelper.getSeverity(value));
                if (values.size() % PROGRESS_UPDATE_LINES == 0)
                    monitor.subTask(NLS.bind("{0}: Obtained {1} samples", item.getResolvedName(), values.size()));
            }
            // Add to Matlab file
            final MLStructure struct = createMLStruct(i++, item.getResolvedName(), times, values, severities);
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
            final List<Instant> times,
            final List<Double> values,
            final List<AlarmSeverity> severities)
    {
        final MLStructure struct = new MLStructure("channel" + index, new int[] { 1, 1 });
        final int N = values.size();
        final int[] dims = new int[] { N, 1 };
        final MLCell time = new MLCell(null, dims);
        final MLDouble value = new MLDouble(null, dims);
        final MLCell severity = new MLCell(null, dims);
        for (int i=0; i<N; ++i)
        {
            setCellText(time, i, TimestampHelper.format(times.get(i)));
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
