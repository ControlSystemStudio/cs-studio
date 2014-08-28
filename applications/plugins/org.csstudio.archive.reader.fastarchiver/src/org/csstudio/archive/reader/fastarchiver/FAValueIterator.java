package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.vtype.VType;

/**
 * Used to return data from the Fast Archiver. Backed by an Array, uses VType
 * ArchiveVNumber and ArchiveVStatistics, subclasses of ArchiveVDisplayType
 * 
 * @author FJohlinger
 */
public class FAValueIterator implements ValueIterator {
	private ArchiveVDisplayType[] values;
	private int index;

	/**
	 * @param values, the array to iterate over.
	 */
	public FAValueIterator(ArchiveVDisplayType[] values) {
		this.values = values;
		index = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return (values != null && index < values.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VType next() throws Exception {

		ArchiveVDisplayType nextItem = values[index];
		index++;
		return nextItem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		values = null;
	}

	/**
	 * To be used for testing
	 * 
	 * @return the number of remaining elements in the iterator, -1 if iterator
	 *         is closed.
	 */
	public int remaining() {
		if (values == null)
			return -1;
		return values.length - index;
	}

}
