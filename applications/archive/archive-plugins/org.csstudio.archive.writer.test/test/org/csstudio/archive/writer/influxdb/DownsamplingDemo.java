package org.csstudio.archive.writer.influxdb;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.influxdb.InfluxDBArchiveReader;
import org.csstudio.archive.reader.rdb.AveragedValueIterator;
import org.csstudio.archive.reader.rdb.RDBArchiveReader;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.archive.writer.ArchiveWriter;
import org.csstudio.archive.writer.WriteChannel;

public class DownsamplingDemo
{
	private static final int SAMPLE_COUNT = 8_000;
	
	//rdb args should be (space-separated) url, username, password, schema (optional), stored procedure (optional)
	//influx args should be (space-separated) url, username (optional), password (optional)
	public static void main(String [] args) throws Exception
	{
		if (args.length == 4)
		{
			//RDB server args (space-separated, surrounded by ""):
			//jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=snsappa.sns.ornl.gov)(PORT=1610))(ADDRESS=(PROTOCOL=TCP)(HOST=snsappb.sns.ornl.gov)(PORT=1610))(CONNECT_DATA=(SERVICE_NAME=prod_controls)))
			//css_arch_user
			//???
			//chan_arch
			//1000
			//chan_arch.archive_reader_pkg.get_browser_data,
			String rdb_server_args[] = args[1].split(" ");

			//Influx server args (space-separated, surrounded by ""):
			//"localhost:8086"
			String influx_server_args [] = args[2].split(" ");
			
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
		throw new IllegalArgumentException("Usage: (copy | time) \"<rdb args>\" \"<influx args>\" <channel_glob_pattern>");
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
		ArchiveWriter writer = getInfluxWriter(influx_server_args);
		
		final String names [] = reader.getNamesByPattern(1, channel_pattern);
		for (String name : names)
		{
			WriteChannel channel = writer.getChannel(name);
			ValueIterator it = reader.getRawValues(1, name, Instant.MIN, Instant.MAX);
			while(it.hasNext())
				writer.addSample(channel, it.next());
		}
	}
	
	private static void time(final String rdb_server_args [], final String influx_server_args [], final String channel_pattern) throws Exception
	{
		final ArchiveReader influx = getInfluxReader(influx_server_args);
		final ArchiveReader rdb = getRdbReader(rdb_server_args);
		
		final Duration durations [] = {Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO,
										null, null, null, null};
		//durations: rdb optimized, averaged rdb raw, influx optimized, averaged influx raw,
		try
		{
			final String channel_names [] = influx.getNamesByPattern(1, channel_pattern);
			for (final String name : channel_names)
			{
				final Instant start = VTypeHelper.getTimestamp(
						influx.getRawValues(1, name, Instant.MIN, Instant.MAX).next());
				ValueIterator its [] = {
						rdb.getOptimizedValues(1, name, start, Instant.MAX, SAMPLE_COUNT),
						new AveragedValueIterator(rdb.getRawValues(1, name, start, Instant.MAX), SAMPLE_COUNT),
						influx.getOptimizedValues(1, name, start, Instant.MAX, SAMPLE_COUNT),
						new AveragedValueIterator(influx.getRawValues(1, name, start, Instant.MAX), SAMPLE_COUNT)
				};
				for (int i = 0; i < 4; ++i)
				{
					ValueIterator it = its[i];
					try
					{
						durations[i] = durations[i].plus(timeValueIterator(it));
					}
					catch (Exception e) {}
					finally
					{
						it.close();
					}
				}
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
		
		String msg = String.format("Rdb optimized = %s (time per sample = %s)\n"+
				"Avg. rdb raw = %s (time per sample = %s)\n"+
				"Influx optimized = %s (time per sample = %s)\n"+
				"Avg. influx raw = %s (time per sample = %s)\n",
				format(durations[0]), format(durations[4]),
				format(durations[1]), format(durations[5]),
				format(durations[2]), format(durations[6]),
				format(durations[3]), format(durations[7]));
		System.out.println(msg);
	}
	
	private static String format(Duration duration)
	{
		return Objects.toString(duration).replace("PT","").replace("H", " hours ").replace("M", " minutes ")
				.replace("S", " seconds");
	}

	private static Duration timeValueIterator(ValueIterator it) throws Exception
	{
		Duration ret = Duration.ZERO;
		while (it.hasNext())
		{
			Instant start = Instant.now();
			it.hasNext();
			it.next();
			Instant end = Instant.now();
			ret = ret.plus(Duration.between(start, end));
		}
		return ret;
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

    private static ArchiveWriter getInfluxWriter(String influx_server_args []) throws Exception
    {
    	String args [] = new String [3];
    	extend(influx_server_args, args, null);
    	return new InfluxDBArchiveWriter(args[0], args[1], args[2]);
    }
}
