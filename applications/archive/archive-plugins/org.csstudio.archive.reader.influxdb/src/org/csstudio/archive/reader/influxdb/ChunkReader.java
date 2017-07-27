/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.MetaTypes;
import org.csstudio.archive.influxdb.MetaTypes.MetaObject;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueDecoder;
import org.csstudio.archive.reader.influxdb.raw.Activator;
import org.csstudio.archive.reader.influxdb.raw.RawChunkReader;
import org.diirt.vtype.VType;
import org.influxdb.dto.QueryResult;

/** Manager of chunked data coming from both sample and metadata queries simultaneously
 *  @author Megan Grodowitz
 */

public class ChunkReader extends RawChunkReader
{
    /** Queue of result chunks of the metadata query */
    final BlockingQueue<QueryResult> metadata_queue;

    /** Expected last timestamp for metadata */
    final Instant last_metadata_time;

    /** Current meta data to process and next metadata if such exists */
    public MetaObject cur_meta, next_meta;

    /** Remaining metadata in the current metadata chunk */
    final protected Queue<MetaObject> next_metadata = new LinkedList<MetaObject>();


    ChunkReader(final BlockingQueue<QueryResult> sample_queue, final Instant last_sample_time,
            final BlockingQueue<QueryResult> metadata_queue, final Instant last_metadata_time,
            final int timeout_secs, final AbstractInfluxDBValueDecoder.Factory decoder_factory)
    {
        super(sample_queue, last_sample_time, timeout_secs, decoder_factory);
        this.metadata_queue = metadata_queue;
        this.last_metadata_time = last_metadata_time;

        this.cur_meta = null;
        this.next_meta = null;
    }

    @Override
    public VType decodeSampleValue() throws Exception
    {
        VType result;
        do
        {
            result = super.decodeSampleValue();
        } while (result == null && step());
        return result;
    }

    private void step_next_metadata() throws Exception
    {
        next_meta = next_metadata.poll();
        while (next_meta == null)
        {
            try
            {
                final QueryResult results = metadata_queue.poll(timeout_secs, TimeUnit.SECONDS);
                //Activator.getLogger().log(Level.FINEST, () -> "Got metadata chunk " + InfluxDBResults.toString(results) );
                next_metadata.addAll(MetaTypes.toMetaObjects(results));
            }
            catch (Exception e)
            {
                throw new Exception ("failed to poll metadata queue for next metadata results ", e);
            }
            next_meta = next_metadata.poll();
        }
        Activator.getLogger().log(Level.FINER, () -> "Stepped next metadata " + next_meta.toString());
    }

    private void update_meta() throws Exception
    {
        if (cur_meta == null)
        {
            try
            {
                step_next_metadata();
            }
            catch (Exception e)
            {
                throw new Exception ("Could not set initial metadata object", e);
            }

            cur_meta = next_meta;
            Activator.getLogger().log(Level.FINE, "Set current metadata {0}, last timestamp is {1}", new Object[] {cur_meta, last_metadata_time});

            if (cur_meta.timestamp.isBefore(last_metadata_time))
            {
                // There should be more metadata, because we haven't hit the end timestamp
                step_next_metadata();
            }
            else {
                next_meta = null;
                return;
            }
        }

        if (!cur_meta.timestamp.isBefore(last_metadata_time))
            return;

        // This is the last metadata value for this sample range
        if (next_meta == null)
            return;

        // Update to next metadata until we run out or the sample is the same or later timestamp
        // while (current sample is the same time as or after the next metadata)
        while (!cur_sample_time.isBefore(next_meta.timestamp))
        {
            cur_meta = next_meta;
            Activator.getLogger().log(Level.FINE, "Set current metadata {0}, last timestamp is {1}", new Object[] {cur_meta, last_metadata_time});

            if (cur_meta.timestamp.isBefore(last_metadata_time))
            {
                //We expect more metadata
                step_next_metadata();
            }
            else
            {
                next_meta = null;
                return;
            }
        }
    }

    @Override
    public boolean step() throws Exception
    {
        if (super.step())
        {
            update_meta();
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(cur_meta.toString()).append("\n");
        sb.append(InfluxDBResults.makeSeriesTable(Arrays.asList(cur_sample_columns), Arrays.asList(cur_sample_values)));
        return sb.toString();
    }



    @Override
    public boolean hasValue(final String colname)
    {
        return cur_column_map.containsKey(colname);
    }

    @Override
    public MetaObject getMeta()
    {
        return cur_meta;
    }

}
