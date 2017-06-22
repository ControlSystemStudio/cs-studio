package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
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

public class ArchiveFileReader implements ArchiveReader
{
	private final ArchiveFileIndexReader indexReader;
	private final File parent;
	
	public ArchiveFileReader(File index) throws IOException
	{
		parent = index.getParentFile();
		this.indexReader = new ArchiveFileIndexReader(index);
	}
	
	public ArchiveFileReader(String string) throws IOException
	{
		this(new File(string));
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
	
	public static void main(String [] args)
	{
		//TODO: init writer
		for (String arg : args)
		{
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
	}

	//if writer is null, just prints to screen
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
				System.out.print(entry.getKey() + "\n   ");
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
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueIterator getRawValues(int key, String name, Instant start, Instant end)
			throws UnknownChannelException, Exception
	{
		File file = null;
		long offset = -1;
		//TODO: get data file entry from index
		return new ArchiveFileSampleReader(start, end, file, offset);
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name, Instant start, Instant end, int count)
			throws UnknownChannelException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
