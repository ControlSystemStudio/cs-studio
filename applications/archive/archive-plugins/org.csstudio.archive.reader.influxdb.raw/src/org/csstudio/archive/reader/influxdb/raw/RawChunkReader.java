/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.reader.influxdb.raw;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.MetaTypes.MetaObject;
import org.diirt.vtype.VType;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;

/** Manager of chunked data coming from both sample and metadata queries simultaneously
 *  @author Megan Grodowitz
 */

public class RawChunkReader extends AbstractInfluxDBValueLookup
{
    /** Queue of result chunks of the sample query */
    final BlockingQueue<QueryResult> sample_queue;

    /** Expected last timestamp for samples */
    final Instant last_sample_time;

    /** Current sample timestamp or null for none */
    protected Instant cur_sample_time;

    /** Max time to wait for a chunk of data to arrive */
    protected final int timeout_secs;

    /** Column labels of current series of samples to process */
    public String[] cur_sample_columns;

    /** Map of label strings to column index */
    final protected Map<String, Integer> cur_column_map = new HashMap<String, Integer>();

    /** Values of current sample series */
    public List<Object> cur_sample_values;

    /** Remaining values of current series of samples to process */
    final protected Queue<List<Object>> next_sample_values = new LinkedList<List<Object>>();

    /** Remaining sample_series in the current sample chunk */
    final protected Queue<Series> next_sample_series = new LinkedList<Series>();

    private int step_count;

    private int recv_vals;

    /** Decode samples into Vtype **/
    private final AbstractInfluxDBValueDecoder decoder;

    protected RawChunkReader(final BlockingQueue<QueryResult> sample_queue, final Instant last_sample_time,
            final int timeout_secs, final AbstractInfluxDBValueDecoder.Factory decoder_factory)
    {
        this.sample_queue = sample_queue;
        this.timeout_secs = timeout_secs;

        this.last_sample_time = last_sample_time;
        this.cur_sample_time = Instant.MIN;

        this.cur_sample_columns = new String[1];
        this.cur_sample_values = null;

        this.step_count = 0;
        this.recv_vals = 0;

        this.decoder = decoder_factory.create(this);
    }

    public VType decodeSampleValue() throws Exception {
        return decoder.decodeSampleValue();
    }

    private boolean poll_next_sample_series() throws Exception
    {
        Series next_series = next_sample_series.poll();

        while (next_series == null)
        {
            try
            {
                Activator.getLogger().log(Level.FINER, "Polling for next chunk of samples");
                final QueryResult results = sample_queue.poll(timeout_secs, TimeUnit.SECONDS);
                //Activator.getLogger().log(Level.FINEST, () -> "Got sample chunk : " + InfluxDBResults.toString(results) );
                next_sample_series.addAll(InfluxDBResults.getSeries(results));
            }
            catch (Exception e)
            {
                return false;
            }
            next_series = next_sample_series.poll();
        }

        //        if (!next_series.getName().equals(parent.channel_name))
        //        {
        //            throw new Exception("Got series result with name " + next_series.getName() + ", expected channel name " + parent.channel_name);
        //        }

        int col_count = InfluxDBResults.getColumnCount(next_series);
        int val_count = InfluxDBResults.getValueCount(next_series);

        recv_vals += val_count;
        Activator.getLogger().log(Level.FINE, "Polled for next series of samples (cols = {0}, vals = {1}, total vals = {2})", new Object[] {col_count, val_count, recv_vals});


        if ((col_count < 1) || (val_count < 1))
            return poll_next_sample_series();

        if (col_count != cur_sample_columns.length)
            cur_sample_columns = new String[col_count];

        cur_sample_columns = next_series.getColumns().toArray(cur_sample_columns);
        cur_column_map.clear();
        int i = 0;
        for (String col : cur_sample_columns)
        {
            cur_column_map.put(col, i);
            i++;
        }

        next_sample_values.addAll(next_series.getValues());

        return true;
    }

    public boolean step() throws Exception
    {
        // if the current sample is the same time as or after the end time stamp, we are done
        if (!cur_sample_time.isBefore(last_sample_time))
        {
            return false;
        }

        if (next_sample_values.isEmpty())
        {
            if (!poll_next_sample_series())
            {
                Activator.getLogger().log(Level.WARNING, () -> "Unable to poll next set of sample results. Possible timeout? Vals recieved = " + recv_vals + ", Step count = "
                        + step_count + ", last sample time = " + last_sample_time + " (" + InfluxDBUtil.toNanoLong(last_sample_time)
                        + ") cur sample time " + cur_sample_time + " (" + InfluxDBUtil.toNanoLong(cur_sample_time) + ")");
                return false;
            }
        }

        final List<Object> vals = next_sample_values.poll();
        if (vals.size() != cur_sample_columns.length)
        {
            throw new Exception ("Sample result encountered with wrong number of values != " + cur_sample_columns.length + ": " + vals);
        }

        cur_sample_values = vals;
        cur_sample_time = InfluxDBUtil.fromInfluxDBTimeFormat(this.getValue("time"));

        Activator.getLogger().log(Level.FINER, () -> "sample step success: " + this.toString());
        step_count++;
        return true;
    }

    public boolean containsColumn(String key)
    {
        return cur_column_map.containsKey(key);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(InfluxDBResults.makeSeriesTable(Arrays.asList(cur_sample_columns), Arrays.asList(cur_sample_values)));
        return sb.toString();
    }

    @Override
    public Object getValue(final String colname) throws Exception
    {
        Integer idx = cur_column_map.get(colname);
        if (idx == null)
        {
            throw new Exception ("Tried to access sample value in nonexistant column " + colname);
        }
        return cur_sample_values.get(idx);
    }

    @Override
    public boolean hasValue(final String colname)
    {
        return cur_column_map.containsKey(colname);
    }

    @Override
    public MetaObject getMeta() {
        return null;
    }

}
