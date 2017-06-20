package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.csstudio.archive.reader.channelarchiver.file.ArchiveFileReader.DataFileEntry;

/**
 * Helper class for reading ChannelArchiver index files (both master index files
 * and sub-archive index files).
 * 
 * @author Amanda Carpenter
 */
public class ArchiveFileIndexReader implements AutoCloseable
{
	private final ArchiveFileBuffer buffer;
	
	public ArchiveFileIndexReader(File indexFile) throws IOException
	{
		buffer = new ArchiveFileBuffer(indexFile);
	}
	
	private Queue<Long> readHashTable() throws IOException
	{
		buffer.offset(4);
		long start = buffer.getUnsignedInt();
		long size = buffer.getUnsignedInt();

		Queue<Long> ret = new ArrayDeque<Long>();
		buffer.offset(start);
		while (--size > 0)
		{
			long res = buffer.getUnsignedInt();
			if (res != 0)
				ret.add(res);
		}
		return ret;
	}
	
	/*private HashMap<String, Long> readHashEntries(Queue<Long> offsets) throws IOException
	{
		//Hash table entries are stored as follows:
		// long next - offset of next hash entry on the table
		// long ID - offset of RTree anchor for channel name
		// short name_len - length of channel name
		// short id_text_len - length of channel id text
		// char name [name_name] - channel name (without '/0' terminator)
		// char id_text [id_text_len] - id text (without '/0' terminator)
		HashMap<String, Long> ret = new HashMap<>();
		while (!offsets.isEmpty())
		{
			long offset = offsets.poll();
			ArchiveFileReader.prepBuffer(offset, 10, buffer, file);
			
			//read next, "id", and name_len
			long next_offset = Integer.toUnsignedLong(buffer.getInt());
			long anchor_offset = Integer.toUnsignedLong(buffer.getInt());
			int nameLen = buffer.getShort();

			//make sure all of name is in buffer
			ArchiveFileReader.prepBuffer(offset+12, nameLen, buffer, file);
			
			byte name [] = new byte [nameLen];
			buffer.get(name);
			ret.put(new String(name), anchor_offset);
			if (next_offset != 0)
				offsets.add(next_offset);
		}
		return ret;
	}*/

	//Returns a map from channel names to their leftmost (earliest) data file entries
	public HashMap<String, List<DataFileEntry>> readLeftmostDataFileEntries() throws IOException
	{
		//Hash table entries are stored as follows:
		// long next - offset of next hash entry on the table
		// long ID - offset of RTree anchor for channel name
		// short name_len - length of channel name
		// short id_text_len - length of channel id text
		// char name [name_name] - channel name (without '/0' terminator)
		// char id_text [id_text_len] - id text (without '/0' terminator)
		HashMap<String, List<DataFileEntry>> ret = new HashMap<>();
		Queue<Long> offsets = readHashTable();
		while (!offsets.isEmpty())
		{
			long offset = offsets.poll();
			buffer.offset(offset);
			
			//read next, "id", and name_len
			offset = buffer.getUnsignedInt();
			long anchor_offset = buffer.getUnsignedInt();
			short nameLen = buffer.getShort();
			buffer.skip(2);

			byte name [] = new byte [nameLen];
			buffer.get(name);
			ret.put(new String(name), readLeftmostDatablocks(anchor_offset));
			if (offset != 0)
				offsets.add(offset);
		}
		return ret;
	}

	
	//Given the offset of an RTree Anchor, returns a list of all data file entries
	//attached to the RTree's leftmost non-empty record.
	private List<DataFileEntry> readLeftmostDatablocks(long anchor_offset) throws IOException
	{
		//An RTree Anchor is laid out as follows:
		//	long root - offset of RTree root
		//	long numRecords - number of records per RTree node
		buffer.offset(anchor_offset);
		
		long root = buffer.getUnsignedInt();
		long numRecords = buffer.getUnsignedInt(); //number of records per RTree node
		
		long datablock = readLeftmostDescendant(root, numRecords);
		List<DataFileEntry> ret = readDatablocks(datablock);
		return ret;
	}
	
	//Return the offset of the node's leftmost (least) descendant.
	//Affects buffer contents and file position
	private long readLeftmostDescendant(long node, long numRecords) throws IOException
	{
		//An RTree Node is laid out as follows:
		// byte isLeaf (if false, 0; otherwise, true)
		// long parent (if root, 0; otherwise, offset of parent node)
		// Record[M] records, where a Record is 20 bytes
		buffer.offset(node);
		boolean isLeaf = buffer.get() != 0;
		buffer.skip(4);
		//read all currently available (in-buffer) records
		Deque<Long> records = new ArrayDeque<>();
		long numRecordsRem = readLeftmostRecords(records, numRecords, isLeaf);
		while (true)
		{
			while (records.isEmpty())
			{ // read more records
				buffer.prepareGet(20); //prepare to read a least 1 record
				numRecordsRem = readLeftmostRecords(records, numRecords, isLeaf);
				if (numRecordsRem == 0)
				{
					if (records.isEmpty()) return 0;
					else break;
				}
			}
			if (isLeaf) return records.poll();
			// Need to save current file offset before reading the child node's descendants
			long records_offset = buffer.offset();
			long result = readLeftmostDescendant(records.poll(), numRecords);
			if (result > 0) return result;
			buffer.offset(records_offset);
		}
	}

	//Looks for all records with a non-null child, puts them on queue.
	//Stops when no records are available in buffer. If stopLeftmost is true,
	//stops when first non-zero record is found, if any are found.
	//Preconditions: buffer's position is at first record.
	//Returns number of records remaining to be read.
	private long readLeftmostRecords(Queue<Long> records, long numRecords, boolean stopLeftmost) throws IOException
	{
		//A Record in an RTree Node is 20 bytes, arranged as follows:
		//	EpicsTime start time, where an EpicsTime is 8 bytes long
		//	EpicsTime end time
		//	long child - if empty, 0; if Node is not leaf, offset of child node; if Node is leaf, offset of child Datablock
		//We're just looking for non-zero children.
		while (buffer.remaining() >= 20)
		{
			buffer.skip(16);
			long child = buffer.getUnsignedInt();
			numRecords--;
			if (child != 0)
			{
				records.add(child);
				if (stopLeftmost)
					return numRecords;
			}
		}
		return numRecords;
	}
	
	//given the offset of an RTree datablock, gets the location of the entry in
		//the data file for that datablock as well as any other datablocks for that
		//time-frame
	private List<DataFileEntry> readDatablocks(long offset) throws IOException
	{
		//Datablocks (RTree entries) are stored as follows:
		// long next_ID - offset of next datablock
		// long data_offset - offset of data block in data file
		// short name_size - size of name
		// char name [name_size] - file name for data file (w/o '\0')
		List<DataFileEntry> ret = new ArrayList<>();
		while (offset != 0)
		{
			long name_offset = offset + 10;
			buffer.offset(offset);
			
			offset = buffer.getUnsignedInt();
			final long dataOffset = buffer.getUnsignedInt();
			int nameSize = buffer.getShort();
			
			byte name [] = new byte [nameSize];
			buffer.get(name);
			ret.add(new DataFileEntry(new String(name), dataOffset));
		}
		return ret;
	}

	@Override
	public void close() throws Exception
	{
		buffer.close();
	}
}
