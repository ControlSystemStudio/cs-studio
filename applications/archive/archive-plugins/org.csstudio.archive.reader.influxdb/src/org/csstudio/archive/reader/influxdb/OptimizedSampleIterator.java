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

/**
 * Iterator for optimized (downsampled/aggregated) values.
 * @author Amanda Carpenter ("optimized")
 * @author Megan Grodowitz (SampleIterator (influxdb))
 */
public class OptimizedSampleIterator extends AbstractInfluxDBValueIterator
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
    public OptimizedSampleIterator(final InfluxDBArchiveReader reader,
            final String channel_name, final Instant start,
            final Instant end, final long count) throws Exception
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

        //TODO: More efficient to get total sample count for whole time range, then
        //get counts by time for last_sample_time?
        //Get the sample intervals ("buckets") in the range
        final QueryResult interval_results = reader.getQueries().get_newest_channel_sample_count_in_intervals(channel_name, sample_starttime, end, count, count);
        // Does the data fill 'count' intervals ("buckets"), or is the number of samples
        //        at least twice the desired number of "buckets"?
        final boolean isEnoughValues = InfluxDBResults.getValueCount(interval_results) >= count ||
                InfluxDBResults.getValueSum(interval_results) >= count*2;

        //Find the last timestamp of the metadata before the end time
        metadata_endtime = InfluxDBResults.getTimestamp(reader.getQueries().get_newest_meta_data(channel_name, null, end, 1L));
        //Get the timestamp of the last metadata at or before the sample start time.
        metadata_starttime = InfluxDBResults.getTimestamp(reader.getQueries().get_newest_meta_data(channel_name, null, sample_starttime, 1L));

        final Consumer<QueryResult> sample_consumer = (result) -> sample_queue.add(result);
        final Consumer<QueryResult> meta_consumer = (result) -> metadata_queue.add(result);

        reader.getQueries().chunk_get_channel_metadata(metadata_chunk_size, channel_name, metadata_starttime,
                end, null, meta_consumer);

        if (isEnoughValues)
        {
            final boolean stdDev = Preferences.getUseStdDev();

            sample_endtime = InfluxDBResults.getTimestamp(interval_results);

            reader.getQueries().chunk_get_channel_sample_stats(sample_chunk_size, channel_name, sample_starttime,
                    end, count, stdDev, sample_consumer);

            samples = new AggregatedChunkReader(sample_queue, sample_endtime, metadata_queue, metadata_endtime,
                    reader.getTimeout(), new ArchiveStatisticsDecoder.Factory(stdDev));
        }
        else
        {
            sample_endtime = InfluxDBResults.getTimestamp(
                    reader.getQueries().get_newest_channel_samples(channel_name, sample_starttime, end, 1L));

            reader.getQueries().chunk_get_channel_samples(sample_chunk_size, channel_name, sample_starttime,
                    end, null, sample_consumer);

            samples = new ChunkReader(sample_queue, sample_endtime, metadata_queue, metadata_endtime,
                    reader.getTimeout(), new ArchiveDecoder.Factory());
        }

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
            throw new Exception("OptimizedSampleIterator.next(" + measurement + ") called after end");

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
