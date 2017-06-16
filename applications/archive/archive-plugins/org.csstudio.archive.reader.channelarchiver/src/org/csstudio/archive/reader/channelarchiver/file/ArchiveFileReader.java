package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.reader.ArchiveReader;

public class ArchiveFileReader implements AutoCloseable //implements ArchiveReader
{
	private final ArchiveFileIndexReader index;
	private final File parent;
	
	public ArchiveFileReader(File index) throws IOException
	{
		parent = index.getParentFile();
		final FileChannel indexFile = FileChannel.open(index.toPath());
		int buffSize = (int) Math.min(indexFile.size(), Integer.MAX_VALUE);
		final ByteBuffer buffer = ByteBuffer.allocateDirect(buffSize);
		this.index = new ArchiveFileIndexReader(indexFile, buffer);
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
	
	/**
	 * Utility method for continuously getting bytes from a ByteBuffer and associated FileChannel.
	 * 
	 * Checks if the ByteBuffer has the desired number of bytes remaining; if not, the remaining bytes are moved to the
	 * front of the buffer, and the file is read into the buffer from its current position, erasing any bytes that were
	 * previously stored before the buffer's original position.
	 * Upon return, the buffer is positioned at the same sequence of bytes it was originally positioned at,
	 * buffer.limit() is equal to the number of bytes read,
	 * buffer contains exactly the buffer.limit() bytes from the file that are located before file.position(),
	 * and there are at least 'numBytes' bytes remaining in the buffer.
	 * 
	 * Useful for loops.
	 * @param buffer ByteBuffer from which to get data; should be loaded with data from the file
	 * @param file FileChannel from which to read new data, if needed; position should be just after the
	 * 			position (in the file) of the last byte in the buffer
	 * @return Number of bytes in buffer
	 * @throws IOException
	 */
	//package-private
	static void prepContinousGet(ByteBuffer buffer, FileChannel file, int numBytes) throws IOException
	{
		if (buffer.remaining() < numBytes)
		{
			if (buffer.position() - buffer.capacity() < numBytes)
			{
				buffer.compact();
			}
			else
			{
				buffer.position(buffer.limit());
				buffer.limit(buffer.capacity());
			}
			file.read(buffer);
			buffer.limit(buffer.position()); //use limit to mark extent of read
			buffer.position(0);
		}
	}
	
	/**
	 * Utility method for getting bytes from a ByteBuffer and associated FileChannel, continously.
	 * 
	 * Calls prepContinousRead(buffer, file, dst.length), then buffer.get(dst).
	 * @param buffer ByteBuffer from which to get data; should be loaded with data from the file
	 * @param file FileChannel from which to read new data, if needed; position should be just after the
	 * 			position (in the file) of the last byte in the buffer
	 * @param dst Destination for the bytes gotten from the ByteBuffer
	 * @throws IOException
	 */
	//package-private
	static void getContinousBytes(ByteBuffer buffer, FileChannel file, byte dst []) throws IOException
	{
		prepContinousGet(buffer, file, dst.length);
		buffer.get(dst);
	}
	
	/**
	 * Utility method for getting unsigned 16-bit integer values from a ByteBuffer and associated FileChannel, continously.
	 * 
	 * Calls prepContinousRead(buffer, file, 4) and then gets 4 bytes from the buffer,
	 * converting them to an unsigned integer value (of type long) before returning.
	 * @param buffer ByteBuffer from which to get data
	 * @param file FileChannel from which to read new data, if needed; position should be just after the
	 * 			position (in the file) of the last byte in the buffer
	 * @return long representing the unsigned int value of the next 4 bytes in the buffer and/or file
	 * @throws IOException
	 */
	//package-private
	static long getContinuousUnsigned16(ByteBuffer buffer, FileChannel file) throws IOException
	{
		prepContinousGet(buffer, file, 4);
		return Integer.toUnsignedLong(buffer.getInt());
	}
	
	/**
	 * Prepare to get data from FileChannel at given offset within file, by means of a ByteBuffer.
	 * Uses buffer.limit() to determine the amount of data currently in the buffer.
	 * @param offset Offset of desired data
	 * @param mincount Minimum number of bytes to prepare (read into buffer)
	 * @param buffer Buffer to prepare
	 * @param file FileChannel to retrieve data from
	 * @throws IOException
	 */
	//package-private
	static void prepBuffer(long offset, int mincount, ByteBuffer buffer, FileChannel file) throws IOException
	{
 		if (offset < 0)
		{
			prepContinousGet(buffer, file, mincount);
			return;
		}
		else if (offset + mincount > file.size())
		{
			//throw new RuntimeException("Offset exceeds file size.")
			return;
		}
		//check if buffer contains the data
		//(Buffer always represents a contiguous portion of the file's contents)
		long buffer_start_offset = file.position() - buffer.limit();
		boolean doesNotContain = buffer_start_offset > offset || file.position() < offset+mincount;
		if (doesNotContain)
		{
			file.position(offset);
			buffer.clear();
			//ensure amount read is at least mincount
			int numBytes = file.read(buffer);
			while (numBytes < mincount)
				numBytes += file.read(buffer);
			//place buffer at position corresponding to the desired offset in file
			buffer.position(0);
			buffer.limit(numBytes);
		}
		else
		{   //place buffer at position corresponding to the desired offset in file
			buffer.position((int)(offset - buffer_start_offset));
		}		
	}
	
	private FileChannel file;
	private ByteBuffer buffer;
	
	// For this, a single sample is represented as a byte []. The size of the samples are calculated
	// based on the number of bytes used by the data file entry and the number of samples in the entry.
	// Given the dbrType and dbrCount of the data, it should be possible to both determine size (bytes)
	// of each sample and extract its attributes (timestamp, alarm severity, alarm status, and value) from
	// the raw data. However, all this is determined by EPICS Channel Access API.
	/**
	 * Get all samples associated with a channel name, given the filename and offset of its first entry.
	 * @param filename Filename (DataFile name)
	 * @param offset Offset (of data header)
	 * @param dataParams an array of at least two; on return, dataParams[0] and dataParams[1] contain
	 * 		with the DbrType and DbrCount of the data, respectively, as shorts
	 * @return A list of all raw samples associated with the 
	 * @throws IOException
	 * @throws {@link ArrayIndexOutOfBoundsException} if dataParams.length < 2
	 */
	@SuppressWarnings("unused")
	//TODO: Can type and/or count change between data headers? What about info? Have to keep all of those with their data.
		//an idea: A sample class instead of byte []. Let it reference a ControlInfo instance for its control info.
	private List<byte[]> readDataFileEntries(String filename, long offset, short dataParams []) throws IOException
	{
		File dataFile = new File(parent, filename);
		// Note on parents: The parent directory of the data file might be different from the parent
		// of the index file if the index file is a master file, and the data file associated with the
		// entry is in a sub-archive. 
		File dataParent = dataFile.getParentFile();
		List<byte []> ret = new LinkedList<>();
		byte nameBytes [] = new byte [40];
		file = FileChannel.open(dataFile.toPath());
		buffer = ByteBuffer.allocateDirect((int)Math.min(file.size(), Integer.MAX_VALUE));

		// prepare to read header
		file.position(offset+4); //(skip first 4 bytes)
		buffer.limit(file.read(buffer));
		buffer.position(0);
		
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
			long nextOffset = getContinuousUnsigned16(buffer, file);
			getContinuousUnsigned16(buffer, file); //prev_offset
			getContinuousUnsigned16(buffer, file); //curr_offset
			long numSamples = getContinuousUnsigned16(buffer, file);
			long ctrlInfoOffset = getContinuousUnsigned16(buffer, file);
			// compute amount of data this data file entry: (bytes allocated) - (bytes free) - (bytes in header)
			long buffDataSize = getContinuousUnsigned16(buffer, file) - getContinuousUnsigned16(buffer, file) - 152;
			prepContinousGet(buffer, file, 4);
			dataParams[0] = buffer.getShort();
			dataParams[1] = buffer.getShort();
			// last part of data file header:
			//	4 bytes padding (used to align the period)
			//	8 bytes (double) period
			//	8 bytes (epicsTimeStamp) begin_time
			//	"						next_file_time
			//	"						end_time
			//	char [40] prev_file						total: 76 bytes
			//	char [40] next_file						total: 116 bytes
			prepContinousGet(buffer, file, 116);
			buffer.position(buffer.position() + 76);
			buffer.get(nameBytes);
			
			// Get raw sample data
			addRawBufferData(ret, numSamples, buffDataSize);

			// Is there a next entry?
			if (nextOffset == 0)
			{
				file.close();
				return ret;
			}
			// Prepare to get the next entry
			nextOffset += 4; // skip first 4 bytes
			String nextName = new String(nameBytes);
			if (filename.equals(nextName))
			{
				prepBuffer(nextOffset, 0, buffer, file);
			}
			else
			{
				filename = nextName;
				file.close();
				file = FileChannel.open(new File(parent, nextName).toPath());
				buffer.position(0).limit(0);
			}
		} while (true);
	}
	
	public void addRawBufferData(List<byte[]> list, long numSamples, long buffDataSize) throws IOException
	{
		assert buffDataSize % numSamples == 0 : "Error reading file: numSamples does not divide size of data";
		final int rawValueSize = (int) (buffDataSize / numSamples);
		while (numSamples-- > 0)
		{
			byte rawValue [] = new byte [rawValueSize];
			getContinousBytes(buffer, file, rawValue);
			list.add(rawValue);
		}
	}

	public static void main(String [] args) throws Exception
	{
		//TODO: In a master archive, the leftmost data file entries might not include
		//all sub-archives. The data file entries in sub-archives can't point to entries
		//in other sub-archives. Thus, to dump a master archive, we have to identify and
		//enter all sub-archives.
		ArchiveFileReader reader = new ArchiveFileReader(args[0]);
		HashMap<String, List<DataFileEntry>> channelsMap = reader.index.readLeftmostDataFileEntries(); 
		System.out.println(channelsMap);
		List<byte[]> data = new LinkedList<>();
		for (DataFileEntry dataEntry : channelsMap.get("DoublePV"))
		{
			data.addAll(reader.readDataFileEntries(dataEntry.filename, dataEntry.offset, new short [2]));
		}
		System.out.println(data);
		reader.close();
	}

	@Override
	public void close() throws Exception
	{
		file.close();
	}
}
