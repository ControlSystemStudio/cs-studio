/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver.file;

import static org.csstudio.archive.reader.channelarchiver.file.ArchiveFileReader.logger;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.channelarchiver.file.ArchiveFileReader.DataFileEntry;

/**
 * Helper class for reading ChannelArchiver index files (both master index files
 * and sub-archive index files).
 * @author Amanda Carpenter
 */
public class ArchiveFileIndexReader implements AutoCloseable
{
	//TODO: Right now, "find least time" (find leftmost) and "find a specific time" are
	//handled separately and differently, adding an unnecessary level of complexity.
	//However, it works, and it's not inefficient, so it's okay for now.

	private final ArchiveFileBuffer buffer;
	private final File indexParent;
	private final HashMap<String, TreeAnchor> anchors;

	/** Anchor of an RTree
	 *
	 */
	private class TreeAnchor
	{
        public final String name;
		public final long root;
		public final int numRecords;

		public TreeAnchor(final String name, final long offset) throws IOException
		{
		    this.name = name;
			buffer.offset(offset);
			this.root = buffer.getUnsignedInt();
			this.numRecords = (int) buffer.getUnsignedInt();
		}

		@Override
		public String toString()
		{
		    return "RTree(" + name + "): " + numRecords + " records @ 0x" + Long.toHexString(root);
		}
	}

	public ArchiveFileIndexReader(final File indexFile) throws IOException
	{
		buffer = new ArchiveFileBuffer(indexFile);
		indexParent = indexFile.getParentFile();
		anchors = getAnchors();
		logger.fine(() -> "Opened " + indexFile + ", " + anchors.size() + " channels");
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

	private HashMap<String, TreeAnchor> getAnchors() throws IOException
	{
		//Hash table entries are stored as follows:
		// long next - offset of next hash entry on the table
		// long ID - offset of RTree anchor for channel name
		// short name_len - length of channel name
		// short id_text_len - length of channel id text
		// char name [name_name] - channel name (without '/0' terminator)
		// char id_text [id_text_len] - id text (without '/0' terminator)
		HashMap<String, TreeAnchor> ret = new HashMap<>();
		Queue<Long> offsets = readHashTable();
		while (!offsets.isEmpty())
		{
			long offset = offsets.poll();
			buffer.offset(offset);

			// read next, "id" (offset to RTree), and name_len
			offset = buffer.getUnsignedInt();
			long anchor_offset = buffer.getUnsignedInt();
			short nameLen = buffer.getShort();
			buffer.skip(2);

			byte name_buf [] = new byte [nameLen];
			buffer.get(name_buf);
			final String name = new String(name_buf);
			ret.put(name, new TreeAnchor(name, anchor_offset));
			if (offset != 0)
				offsets.add(offset);
		}
		return ret;
	}

	/**
	 * Get all data file entries (filename + offset) associated with the first RTree record
	 * for the given channel name and the given start and end times.
	 * If the index file has data for the given channel name, but no data for any time
	 * at or before the given end time, an empty list will be returned.
	 * For a sub-archive, at most one data file entry will be returned.
	 * For a master archive, there may be multiple entries,
	 * if multiple sub-archives contain data for the given start time.
	 * @param channelName Channel name
	 * @param startTime
	 * @param endTime
	 * @return data file entries (file + offset) for the given time range
	 * @throws UnknownChannelException If the index has no data for the given channel name.
	 * @throws Exception on error
	 */
	public List<DataFileEntry> getEntries(String channelName, Instant startTime, Instant endTime) throws Exception, UnknownChannelException
	{
	    final TreeAnchor anchor = anchors.get(channelName);
		if (anchor == null)
			throw new UnknownChannelException(channelName);
		final long result = searchRTreeNodes(anchor.root, anchor.numRecords, startTime);
		if (result == 0)
		    return Collections.emptyList();
		// check end time: if record starts after end time, it's no good.
		buffer.offset(buffer.offset() - 20);
		final Instant recordStartTime = buffer.getEpicsTime();
		if (recordStartTime.compareTo(endTime) > 0)
			return Collections.emptyList();
		return readDatablocks(result);
	}

	/**
	 * Find the leftmost data file entries (filename + offset) for all channels listed in the reader's
	 * index file. For sub-archives, each channel will have one data file entry in its leftmost
	 * record. For master archives, which collect multiple sub-archives, there may be more than one.
	 * @return A map from channel names to the list of data file entries in its leftmost record.
	 * @throws IOException
	 */
	public HashMap<String, List<DataFileEntry>> readLeftmostDataFileEntries() throws IOException
	{
		HashMap<String, List<DataFileEntry>> ret = new HashMap<>();
		for (Map.Entry<String, TreeAnchor> entry : anchors.entrySet())
		{
			ret.put(entry.getKey(), readLeftmostDatablocks(entry.getValue()));
		}
		return ret;
	}

	//Given an RTree Anchor, returns a list of all data file entries
	//attached to the RTree's leftmost non-empty record.
	private List<DataFileEntry> readLeftmostDatablocks(TreeAnchor anchor) throws IOException
	{
		long datablock = readLeftmostDescendant(anchor.root, anchor.numRecords);
		List<DataFileEntry> ret = readDatablocks(datablock);
		return ret;
	}

	//Return the offset of the node's leftmost (least) descendant.
	private long readLeftmostDescendant(long node, long numRecords) throws IOException
	{
		//An RTree Node is laid out as follows:
		// byte isLeaf (if false, 0; otherwise, true)
		// long parent (if root, 0; otherwise, offset of parent node)
		// Record[M] records, where a Record is 20 bytes
		buffer.offset(node);
		boolean isLeaf = buffer.get() != 0;

		buffer.skip(4);
		//read all currently available (in-buffer) records; or, if is leaf,
			//continue until non-zero record
		Deque<Long> records = new ArrayDeque<>();
		long numRecordsRem = numRecords;
		long initOffset = buffer.offset();
		while (true)
		{
			while(records.isEmpty())
			{
				if (isLeaf)
					readLeftmostRecords(records, numRecordsRem, 1, 1);
				else
					readLeftmostRecords(records, numRecordsRem, 0, 0);
				numRecordsRem -= (buffer.offset() - initOffset)/20;
				if (numRecordsRem == 0)
				{
					if (records.isEmpty()) return 0;
					else break;
				}
				initOffset = buffer.offset();
			}
			if (isLeaf) return records.poll();
			long result = readLeftmostDescendant(records.poll(), numRecords);
			if (result > 0) return result;
			buffer.offset(initOffset); //need to return to offset from before reading descendants
		}
	}

	//Looks for records with non-zero child offsets, adds the offset(s) to the collection.
	//Continues adding non-zero offsets until one of the following:
		//(1) maxRecordsLimit != 0, and maxRecordsLimit records have been added
		//(2) minRecordsLimit != 0, minRecordsLimit records have been added,
			//and no records remain in the buffer
		//(3) numRecords records have been processed (including those not added)
	//Preconditions: buffer's position is at first record to be searched.
	//Returns last child found, if return was due to (1) or (2); otherwise, 0.
	//Postconditions: buffer's position is immediately after last record read.
	private long readLeftmostRecords(Collection<Long> records, long numRecords, int minRecordsLimit, int maxRecordsLimit) throws IOException
	{
		//A Record in an RTree Node is 20 bytes, arranged as follows:
		//	EpicsTime start time, where an EpicsTime is 8 bytes long
		//	EpicsTime end time
		//	long child
		do
		{
			buffer.prepareGet(20);
			while (buffer.remaining() >= 20)
			{
				buffer.skip(16);
				long child = buffer.getUnsignedInt();
				numRecords--;
				if (child != 0)
				{
					records.add(child);
					if (--maxRecordsLimit == 0 || --minRecordsLimit == 0)
						return child;
				}
			}
		} while (minRecordsLimit > 0 && numRecords > 0);
		return 0;
	}

	/**
	 * Finds the leaf-node record whose start time is the largest start time
	 * at or below the given Instant. Returns the offset of that record's child,
	 * if the offset is non-zero; or else the first non-zero child offset of the
	 * records that follow, if there is one; or zero if no non-zero child offsets
	 * can be found.
	 * @param root Offset in index file of RTree's root node
	 * @param numRecords Number of records per RTree node
	 * @param time Start time to search for
	 * @return Offset of datablock which is at or before, or 0 if there is no data block
	 * @throws Exception on error
	 */
	public long searchRTreeNodes(final long root, final int numRecords, final Instant time) throws Exception
	{
	    RTreeNode node = new RTreeNode(buffer, root, numRecords);
	    while (true)
	    {
	        // System.out.println(node);
	        long child = 0;
	        for (int i=numRecords-1;  i>=0;  --i)
	        {
	            child = node.records[i].child;
	            if (child != 0  &&
	                node.records[i].start.compareTo(time) <= 0)
	                break;
	        }
	        if (node.isLeaf)
	            return child;
	        if (child == 0)
	            return 0;
            node = new RTreeNode(buffer, child, numRecords);
	    }
	}

	/**
	 * Given the offset of an RTree datablock, gets the filename and offset
	 * associated with that datablock, and the same for any child datablocks.
	 * The returned values correspond to the same time frame, but should be in
	 * different sub-archives, if there are more than one.
	 * @param offset Offset of RTree datablock (child_id of leaf node's record)
	 * @return List<DataFileEntry> corresponding to the respective files and offsets of
	 * 			all datablocks associated with the RTree record which contains the given offset
	 * @throws IOException
	 */
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
			buffer.offset(offset);

			offset = buffer.getUnsignedInt();
			final long dataOffset = buffer.getUnsignedInt();
			int nameSize = buffer.getShort();

			byte name [] = new byte [nameSize];
			buffer.get(name);
			ret.add(new DataFileEntry(new File(indexParent, new String(name)), dataOffset));
		}
		return ret;
	}

	@Override
	public void close() throws IOException
	{
		buffer.close();
	}

	public java.util.Set<String> getChannelNames()
	{
		return anchors.keySet();
	}
}
