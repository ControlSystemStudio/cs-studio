/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueIterator;
import org.csstudio.archive.reader.influxdb.raw.Preferences;
import org.diirt.vtype.VType;
import org.influxdb.dto.QueryResult;

/** Value Iterator that reads from the SAMPLE table.
 *  @author Kay Kasemir
 *  @author Megan Grodowitz (InfluxDB)
 */
public class SampleIterator extends AbstractInfluxDBValueIterator
{
    /** Queue of result chunks of the sample query */
    final BlockingQueue<QueryResult> sample_queue = new LinkedBlockingQueue<>();

    /** Queue of result chunks of the metadata query */
    final BlockingQueue<QueryResult> metadata_queue = new LinkedBlockingQueue<>();

    /** 'Current' value that <code>next()</code> will return,
     *  or <code>null</code>
     */
    private VType next_value = null;

    private final ChunkReader samples;

    final private int sample_chunk_size;
    final private int metadata_chunk_size;

    /** Initialize
     *  @param reader InfluxDBArchiveReader
     *  @param channel_name ID of channel
     *  @param start Start time
     *  @param end End time
     *  @throws Exception on error
     */
    public SampleIterator(final InfluxDBArchiveReader reader,
            final String channel_name, final Instant start,
            final Instant end) throws Exception
    {
        super(reader, channel_name);
        Instant sample_endtime, sample_starttime, metadata_endtime, metadata_starttime;
        // QueryResult results = null;

        sample_chunk_size = Preferences.getChunkSize();
        metadata_chunk_size = Preferences.getChunkSize();

        //Get the timestamp of the last sample at or before the indicated start time.
        sample_starttime = InfluxDBResults.getTimestamp(reader.getQueries().get_newest_channel_samples(channel_name, null, start, 1L));
        if (sample_starttime == null)
        {
            //No samples at or before start, find oldest sample in range
            sample_starttime = InfluxDBResults.getTimestamp(reader.getQueries().get_channel_samples(channel_name, start, end, 1L));

            //No samples before the end time. We are done
            if (sample_starttime == null)
            {
                samples = null;
                close();
                return;
            }
        }

        //Get the timestamp of the last sample in the range.
        sample_endtime = InfluxDBResults.getTimestamp(reader.getQueries().get_newest_channel_samples(channel_name, sample_starttime, end, 1L));

        //Find the last timestamp of the metadata before the end time
        metadata_endtime = InfluxDBResults.getTimestamp(reader.getQueries().get_newest_meta_data(channel_name, null, end, 1L));
        //Get the timestamp of the last metadata at or before the sample start time.
        metadata_starttime = InfluxDBResults.getTimestamp(reader.getQueries().get_newest_meta_data(channel_name, null, sample_starttime, 1L));

        reader.getQueries().chunk_get_channel_samples(sample_chunk_size, channel_name, sample_starttime, end, null,
                new Consumer<QueryResult>() {
            @Override
            public void accept(QueryResult result) {
                sample_queue.add(result);
                //Activator.getLogger().log(Level.FINE, () -> "Got chunk of sample vals size " + InfluxDBResults.getValueCount(result) + " for total size: " + sample_queue.size());
                sample_queue.add(result);
            }});

        reader.getQueries().chunk_get_channel_metadata(metadata_chunk_size, channel_name, metadata_starttime, end, null,
                new Consumer<QueryResult>() {
            @Override
            public void accept(QueryResult result) {
                metadata_queue.add(result);
            }});

        samples = new ChunkReader(sample_queue, sample_endtime, metadata_queue, metadata_endtime, reader.getTimeout(),
                new ArchiveDecoder.Factory());

        if (samples.step())
            next_value = samples.decodeSampleValue();
        else
            close();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return next_value != null;
    }


    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public VType next() throws Exception
    {
        // This should not happen...
        if (next_value == null)
            throw new Exception("SampleIterator.next(" + measurement + ") called after end");

        // Remember value to return...
        final VType result = next_value;

        // ... and prepare next value
        if (samples.step())
            next_value = samples.decodeSampleValue();
        else
            close();

        return result;
    }

    /** Release all database resources.
     *  OK to call more than once.
     */
    @Override
    public void close()
    {
        super.close();
        next_value = null;
    }
}
