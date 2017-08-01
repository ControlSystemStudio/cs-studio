package org.csstudio.archive.writer.influxdb;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.influxdb.InfluxDBArchiveReader;
import org.csstudio.archive.reader.rdb.AveragedValueIterator;
import org.csstudio.archive.reader.rdb.RDBArchiveReader;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.archive.writer.WriteChannel;

/**
 * Not really a test, just a "demo" class for rough timing of Archive Reader downsampling.
 * Depending on the arguments, the main() method can do two things:
 * <ol>
 * <li>copy: Copy data from an RDB archive to an InfluxDB archive.</li>
 * <li>time: Time iteration through different {@link ArchiveReader}.getOptimizedValues() implementations
 * in RDB and InfluxDB for identical time periods of (presumably) identical data.</li>
 * </ol>
 * <p>With a little tweaking of constants, the "copy" method could be used to copy any amount
 * (time period) of data for any start time.
 *
 * @author Amanda Carpenter
 *
 */
public class DownsamplingDemo
{
    private static final int SAMPLE_COUNT = 800;
    private static final int NUM_ITERATIONS = 3;
    private static final boolean PRINT_PROGRESS = false;
    private static final TemporalAmount COPY_AMOUNT = Duration.ofDays(1);
    private static final Instant COPY_START = Instant.now().minus(COPY_AMOUNT);

    /**
     * Compare times for getOptimizedValues() implementations for rdb and influx archive readers, or
     * copy data from an rdb archive to an influx archive.
     *
     * <p>Command line arguments should be of the form<pre>
     *         (copy | time) "&lt;rdb, args&gt;" "&lt;influx, args&gt;" &lt;channel_name_glob_pattern&gt;<\pre>.
     *
     * <p>The "rdb args" and "influx args" arguments are the parameters for their respective archive readers; each
     * parameter should be separated by a comma and space (the string ", "). Of course, these should be quoted
     * or the space escaped with '/' so that they are parsed as single arguments.
     * The rdb parameters are url, username, password, schema (optional), stored procedure (optional).
     * The influx parameters are url, username (optional), password (optional).
    //both kinds of "args" arguments should be separated by comma and space (", "), surrounded by " chars
     * @param args
     * @throws Exception
     */
    public static void main(String [] args) throws Exception
    {
        if (args.length == 4)
        {
            //RDB server args:
            //jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=snsappb.sns.ornl.gov)(PORT=1610))(CONNECT_DATA=(SERVICE_NAME=prod_controls)))
            //css_arch_user
            //??????? //password
            //chan_arch
            //chan_arch.archive_reader_pkg.get_browser_data
            String rdb_server_args[] = args[1].split(", ");

            //Influx server args:
            //"http://localhost:8086"
            String influx_server_args [] = args[2].split(", ");

            //Channel pattern
            //"*LLRF:IOC*:Load"
            String channel_pattern = args[3];

            if ("copy".equals(args[0]))
            {
                copy(rdb_server_args, influx_server_args, channel_pattern);
                return;
            }
            else if ("time".equals(args[0]))
            {
                time(rdb_server_args, influx_server_args, channel_pattern);
                return;
            }
        }
        throw new IllegalArgumentException("Usage: (copy | time) \"<rdb, args>\" \"<influx, args>\" <channel_glob_pattern>");
    }

    private static void extend(String [] src, String [] dst, String def)
    {
        int i = 0;
        while (i < src.length)
            dst[i] = src[i++];
        while (i < dst.length)
            dst[i++] = def;
    }

    private static void copy(final String rdb_server_args [], final String influx_server_args [], final String channel_pattern) throws Exception
    {
        ArchiveReader reader = getRdbReader(rdb_server_args);
        InfluxDBArchiveWriter writer = getInfluxWriter(influx_server_args);
        writer.getQueries().initDatabases(writer.getConnectionInfo().influxdb);

        final String names [] = reader.getNamesByPattern(1, channel_pattern);
        //names = Arrays.copyOf(names, 3);
        final Instant start = COPY_START;
        final Instant end = start.plus(COPY_AMOUNT);
        for (String name : names)
        {
            WriteChannel channel = writer.getChannel(name);
            ValueIterator it = reader.getRawValues(1, name, start, end);
            int num_its = 0;
            while(it.hasNext())
            {
                writer.addSample(channel, it.next());
                ++num_its;
            }
            writer.flush();
            System.out.println(String.format("Added %d samples to channel %s", num_its, name));
        }
        System.out.println("Done copying " + names.length + " channels for " + start.toString() + " to " + end.toString());
        writer.close();
        reader.close();
    }

    static void time(final String rdb_server_args [], final String influx_server_args [], final String channel_pattern) throws Exception
    {
        final ArchiveReader influx = getInfluxReader(influx_server_args);
        final ArchiveReader rdb = getRdbReader(rdb_server_args);

        final Duration first_durations [] = {Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO};
        Duration total_durations [] = {Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO};
        int total_num_samples [] = {0, 0, 0, 0};
        //durations: rdb optimized, averaged rdb raw, influx optimized, averaged influx raw,
        int num_channels = 0;
        try
        {
            final String channel_names [] = influx.getNamesByPattern(1, channel_pattern);
            final Instant now = Instant.now();
            for (final String name : channel_names)
            {
                final Instant start = VTypeHelper.getTimestamp(
                        influx.getRawValues(1, name, Instant.EPOCH, now).next());
                final Instant end = start.plus(COPY_AMOUNT);
                getResults(rdb, influx, name, start, end, first_durations, total_num_samples);
                final String msg = "Got results for 1/"+NUM_ITERATIONS+" tests of channel "+name+"("+(num_channels+1)+"/"+channel_names.length+")";
                if (PRINT_PROGRESS)
                    System.out.println(msg);
                for (int i = 1; i < NUM_ITERATIONS; ++i)
                {
                    getResults(rdb, influx, name, start, end, total_durations, total_num_samples);
                    if (PRINT_PROGRESS)
                        System.out.println(msg.replaceFirst("1", ""+(i+1)));
                }
                ++num_channels;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            influx.close();
            rdb.close();
        }

        Object results [] = new Object [12];
        for (int i = 0; i < 4; ++i)
        {
            total_durations[i] = total_durations[i].plus(first_durations[i]);
            results[3*i] = getSecondsDouble(first_durations[i]);
            results[3*i+1] = getSecondsDouble(total_durations[i].dividedBy(NUM_ITERATIONS));
            results[3*i+2] = ((double) total_num_samples[i]) / (num_channels * NUM_ITERATIONS);
        }
        String msg = String.format(
                "Iterator\tFirst time (s)\tAvg. time (s)\tAvg. samples per request\n"+
                "Rdb optimized\t%f\t%f\t%f\n"+
                "Avg'd rdb raw\t%f\t%f\t%f\n"+
                "Influx opt.\t%f\t%f\t%f\n"+
                "Avg'd influx\t%f\t%f\t%f",
                results);
        System.out.println(msg);
    }

    private static void getResults(final ArchiveReader rdb, final ArchiveReader influx, final String name,
            final Instant start, final Instant end, final Duration durations [], final int num_samples [])
                    throws Exception
    {
        final ValueIterator its [] = {
                rdb.getOptimizedValues(1, name, start, end, SAMPLE_COUNT),
                new AveragedValueIterator(rdb.getRawValues(1, name, start, end), SAMPLE_COUNT),
                influx.getOptimizedValues(1, name, start, end, SAMPLE_COUNT),
                new AveragedValueIterator(influx.getRawValues(1, name, start, end), SAMPLE_COUNT)
        };
        for (int i = 0; i < 4; ++i)
        {
            ValueIterator it = its[i];
            try
            {
                Duration duration = timeValueIterator(it, num_samples, i);
                durations[i] = durations[i].plus(duration);
                //TODO: what else?
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally
            {
                it.close();
            }
        }
    }

    private static double getSecondsDouble(Duration duration)
    {
        return duration.getSeconds() + duration.getNano()/1_000_000_000.0;
    }

    private static Duration timeValueIterator(ValueIterator it, int total_num_samples [], int index) throws Exception
    {
        Duration result = Duration.ZERO;
        while (it.hasNext())
        {
            Instant start = Instant.now();
            it.hasNext();
            it.next();
            Instant end = Instant.now();
            result = result.plus(Duration.between(start, end));
            ++total_num_samples[index];
        }
        return result;
    }

    private static ArchiveReader getRdbReader(String rdb_server_args []) throws Exception
    {
        String args [] = new String [5];
        extend(rdb_server_args, args, "");
        return new RDBArchiveReader(args[0], args[1], args[2], args[3], args[4]);
    }

    private static ArchiveReader getInfluxReader(String influx_server_args []) throws Exception
    {
        String args [] = new String [3];
        extend(influx_server_args, args, null);
        return new InfluxDBArchiveReader(args[0], args[1], args[2]);
    }

    private static InfluxDBArchiveWriter getInfluxWriter(String influx_server_args []) throws Exception
    {
        String args [] = new String [3];
        extend(influx_server_args, args, null);
        return new InfluxDBArchiveWriter(args[0], args[1], args[2]);
    }
}
