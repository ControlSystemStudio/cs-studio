package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
//import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.vtype.ArchiveVType;
import org.csstudio.archive.writer.ArchiveWriter;
import org.csstudio.archive.writer.WriteChannel;
import org.diirt.vtype.VType;

/**
 * Implementation of ArchiveReader for channel archiver files.
 * Created to import archiver files to new archives via their
 * ArchiveWriter implementations.
 * @author Amanda Carpenter
 *
 */
public class ArchiveFileReader implements ArchiveReader
{
	private final ArchiveFileIndexReader indexReader;
	
	/**
	 * Construct an ArchiveFileReader.
	 * @param index Index file for archive.
	 * @throws IOException
	 */
	public ArchiveFileReader(File index) throws IOException
	{
		this.indexReader = new ArchiveFileIndexReader(index);
	}
	
	/**
	 * Construct an ArchiveFileReader.
	 * @param index Index file for archive
	 * @throws IOException
	 */
	public ArchiveFileReader(String index) throws IOException
	{
		this(new File(index));
	}
	
	static class DataFileEntry //package-private //(belongs with index reader?)
	{
		private final File file;
		private final Long offset;
		public DataFileEntry(final File file, final Long offset)
		{
			this.file = file;
			this.offset = offset;
		}
		public String toString()
		{
			return String.format("data in '%s' @ 0x%08x(%d)", file.getName(), offset, offset);
		}
	}
	
	/**
	 * Demonstrates ArchiveFileReader. Given a list of index files, prints all
	 * data for each file.
	 * @param args List of index files
	 */
	public static void main(String [] args)
	{
		//TODO: init writer
		for (String arg : args)
		{
			System.out.println(String.format("\n* * Index file=%s * *", arg));
			try
			{
				readAndWrite(arg, null);
			}
			catch (Exception e)
			{
				System.out.println("Exception reading file " + arg);
				e.printStackTrace();
			}
		}
		/*try {
			ArchiveFileReader reader = new ArchiveFileReader(args[0]);
			getAndPrint(reader, "BoolPV", Instant.MIN, Instant.MAX);
			getAndPrint(reader, "BoolPV", Instant.parse("2004-03-05T23:58:10.0Z"), Instant.parse("2004-03-05T23:58:30.0Z"));
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * Demonstrates getRawValues(). Calls getRawValues(), then iterates through all values and prints them.
	 * @param reader ArchiveFileReader
	 * @param channelName channel name
	 * @param min Start time for data
	 * @param max End time
	 * @throws UnknownChannelException
	 * @throws Exception
	 */
	private static void getAndPrint(ArchiveFileReader reader, String channelName, Instant min, Instant max) throws UnknownChannelException, Exception
	{
		ValueIterator it = reader.getRawValues(0, channelName, min, max);
		System.out.println(String.format("Getting %s from %s to %s", channelName, min.toString(), max.toString()));
		while (it.hasNext())
			System.out.println(it.next().toString());
	}

	/**
	 * Demonstration method. Get all data for all channels described in the given index file.
	 * If 'writer' is non-null, the data is then written to the writer;
	 * if it is null, the data is printed to System.out.
	 * @param indexFile Index file from which to get data.
	 * @param writer Writer with which to write data; if null, data is printed to System.out.
	 * @throws IOException
	 */
	//TODO: use value iterator from getRawValues()
	private static void readAndWrite(String indexFile, ArchiveWriter writer) throws IOException
	{
		ArchiveFileReader reader = new ArchiveFileReader(indexFile);
		HashMap<String, List<DataFileEntry>> indexMap = reader.indexReader.readLeftmostDataFileEntries();
		for (Map.Entry<String, List<DataFileEntry>> entry : indexMap.entrySet())
		{
			List<ArchiveVType> samples = new LinkedList<>();
			String channelName = entry.getKey();
			WriteChannel channel = null;
			if (writer != null)
			{
				try
				{
					channel = writer.getChannel(channelName);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
			else
			{
				System.out.println("Channel name = " + channelName);
			}
			for (DataFileEntry dataEntry : entry.getValue())
			{
				ArchiveFileSampleReader.readDataFileEntries(samples, dataEntry.file, dataEntry.offset);
				//System.out.print("Done. Have " + samples.size() + " samples\n   ");
			}
			if (writer != null)
			{
				for (ArchiveVType sample : samples)
				{
					try
					{
						writer.addSample(channel, sample);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else
			{
				if (samples.size() > 100)
				{
					System.out.print(samples.subList(0, 100).toString());
					System.out.println("...");
				}
				else
				{
					System.out.println(samples);
				}
			}
		}
		reader.close();
	}

	@Override
	public String getServerName()
	{
		return "";
	}

	@Override
	public String getURL()
	{
		return "";
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public int getVersion()
	{
		return 0;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos()
	{
		return new ArchiveInfo[0];
	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern) throws Exception
	{
		List<String> result = new ArrayList<>();
		for (String name : indexReader.getChannelNames())
		{
			if (false) //TODO: test matching
				result.add(name);
		}
		return result.toArray(new String [result.size()]);
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception
	{
		List<String> result = new ArrayList<>();
		for (String name : indexReader.getChannelNames())
		{
			if (name.matches(reg_exp))
				result.add(name);
		}
		return result.toArray(new String [result.size()]);
	}

	@Override
	public ValueIterator getRawValues(int key, String name, Instant start, Instant end)
			throws UnknownChannelException, Exception
	{
		List<DataFileEntry> entries = Collections.emptyList();
		entries = indexReader.getEntries(name, start, end);
		if (entries.size() < 1)
			return new ValueIterator()
			{
				@Override
				public VType next() throws Exception
				{
					throw new Exception("This iterator has no values");
				}
				
				@Override
				public boolean hasNext()
				{
					return false;
				}
				
				@Override
				public void close() {} //do nothing
			};
		//TODO: if entries.size() > 1 (complicated)
		DataFileEntry entry = entries.get(0);
		return new ArchiveFileSampleReader(start, end, entry.file, entry.offset);
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name, Instant start, Instant end, int count)
			throws UnknownChannelException, Exception
	{
		return getRawValues(key, name, start, end);
	}

	@Override
	public void cancel()
	{
		//no-op
	}

	@Override
	public void close()
	{
		try {
			indexReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
