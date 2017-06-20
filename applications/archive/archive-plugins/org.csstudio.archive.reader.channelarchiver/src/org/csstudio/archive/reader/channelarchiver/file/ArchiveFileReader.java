package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.vtype.ArchiveVType;

public class ArchiveFileReader implements AutoCloseable //implements ArchiveReader
{
	private final ArchiveFileIndexReader index;
	private final File parent;
	
	private ArchiveFileBuffer buffer;
	
	public ArchiveFileReader(File index) throws IOException
	{
		parent = index.getParentFile();
		final FileChannel indexFile = FileChannel.open(index.toPath());
		this.index = new ArchiveFileIndexReader(indexFile);
		buffer = null;
	}
	
	public ArchiveFileReader(String string) throws IOException
	{
		this(new File(string));
	}
	
	static class DataFileEntry //package-private //(belongs with index reader?)
	{
		private final String filename;
		private final Long offset;
		public DataFileEntry(final String filename, final Long offset)
		{
			this.filename = filename;
			this.offset = offset;
		}
		public String toString()
		{
			return String.format("in '%s' @ %08x(%d)", filename, offset, offset);
		}
	}
	
	
	// For this, a single sample is represented as a byte []. The size of the samples are calculated
	// based on the number of bytes used by the data file entry and the number of samples in the entry.
	// Given the dbrType and dbrCount of the data, it should be possible to both determine size (bytes)
	// of each sample and extract its attributes (timestamp, alarm severity, alarm status, and value) from
	// the file data.
	/**
	 * Get all samples associated with a channel name, given the filename and offset of its first entry.
	 * @param filename Filename (DataFile name)
	 * @param offset Offset (of data header)
	 * @param dataParams an array of at least two; on return, dataParams[0] and dataParams[1] contain
	 * 		the DbrType and DbrCount of the data, respectively, as shorts
	 * @return A list of all samples associated with the channel, in bytes
	 * @throws IOException
	 * @throws {@link ArrayIndexOutOfBoundsException} if dataParams.length < 2
	 */
	@SuppressWarnings("unused")
	private List<ArchiveVType> readDataFileEntries(String filename, long offset) throws IOException
	{
		File dataFile = new File(parent, filename);
		// Note on parents: The parent directory of the data file might be different from the parent
		// of the index file if the index file is a master file, and the data file associated with the
		// entry is in a sub-archive. 
		File dataParent = dataFile.getParentFile();
		List<ArchiveVType> ret = new LinkedList<>();
		byte nameBytes [] = new byte [40];
		FileChannel file = FileChannel.open(dataFile.toPath());
		buffer = new ArchiveFileBuffer(file);
		CtrlInfoReader ctrlInfo = new CtrlInfoReader(0);

		// prepare to read header
		buffer.offset(offset+4); //(skip first 4 bytes)
		do {
			// first part of data file header:
			//	4 bytes directory_offset (skipped)
			//	"		next_offset (offset of next entry in its file)
			//	"		prev_offset (offset of previous entry in its file)
			//	"		cur_offset (used by FileAllocator writing file)
			//	"		num_samples (number of samples in the buffer)
			//	"		ctrl_info_offset (offset in this file of control info header (units, limits, etc.))
			//	"		buff_size (bytes allocated for this entry, including header)
			//	"		buff_free (number of un-used, allocated bytes for this header)
			//  2 bytes DbrType (type of data stored in buffer)
			//	2 bytes	DbrCount (count of values for each buffer element, i.e. 1 for scalar types, >1 for array types)
			offset = buffer.getUnsignedInt();
			buffer.skip(8);
			long numSamples = buffer.getUnsignedInt();
			long ctrlInfoOffset = buffer.getUnsignedInt();
			if (!ctrlInfo.isOffset(ctrlInfoOffset))
				ctrlInfo = new CtrlInfoReader(ctrlInfoOffset);
			// compute amount of data in this data file entry: (bytes allocated) - (bytes free) - (bytes in header)
			long buffDataSize = buffer.getUnsignedInt() - buffer.getUnsignedInt() - 152;
			short dbrType = buffer.getShort();
			short dbrCount = buffer.getShort();
			// last part of data file header:
			//	4 bytes padding (used to align the period)
			//	8 bytes (double) period
			//	8 bytes (epicsTimeStamp) begin_time
			//	"						next_file_time
			//	"						end_time
			//	char [40] prev_file						total: 76 bytes
			//	char [40] next_file						total: 116 bytes
			buffer.skip(76);
			buffer.get(nameBytes);
			
			while (numSamples -- > 0)
			{
				ArchiveVType sample = ArchiveFileSampleReader.getSample(dbrType, dbrCount, ctrlInfo, buffer);
				ret.add(sample);
			}

			// Is there a next entry?
			if (offset == 0)
			{
				file.close();
				return ret;
			}
			// Prepare to get the next entry
			offset += 4; // skip first 4 bytes
			String nextName = new String(nameBytes);
			nextName = nextName.substring(0, nextName.indexOf('\0'));
			if (!filename.equals(nextName))
			{	//close the file and open the next one
				filename = nextName;
				buffer.close();
				file = FileChannel.open(new File(parent, nextName).toPath());
				buffer.setFile(file);
			}
			buffer.offset(offset);
		} while (true);
	}
	
	/*public void addRawBufferData(List<byte[]> list, long numSamples, long buffDataSize) throws IOException
	{
		assert buffDataSize % numSamples == 0 : "Error reading file: numSamples does not divide size of data";
		final int rawValueSize = (int) (buffDataSize / numSamples);
		while (numSamples-- > 0)
		{
			byte rawValue [] = new byte [rawValueSize];
			buffer.get(rawValue);
			list.add(rawValue);
		}
	}*/
	
	public static void main(String [] args) throws IOException
	{
		//TODO: In a master archive, the leftmost data file entries might not include
		//all sub-archives. The data file entries in sub-archives can't point to entries
		//in other sub-archives. Thus, to dump a master archive, we have to identify and
		//enter all sub-archives.
		ArchiveFileReader reader = new ArchiveFileReader(args[0]);
		HashMap<String, List<DataFileEntry>> channelsMap = reader.index.readLeftmostDataFileEntries(); 
		System.out.println(channelsMap);
		for (Map.Entry<String, List<DataFileEntry>> entry : channelsMap.entrySet())
		{
			System.out.print(entry.getKey() + "\n   ");
			List<ArchiveVType> samples = new LinkedList<>();
			for (DataFileEntry dataEntry : entry.getValue())
			{
				samples.addAll(reader.readDataFileEntries(dataEntry.filename, dataEntry.offset));
			}
			System.out.println(samples);
		}
		reader.close();
	}

	@Override
	public void close() throws IOException
	{
		if (buffer != null)
			buffer.close();
	}
}
