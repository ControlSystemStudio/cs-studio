package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.vtype.VType;


/**
 * Used to return data from the Fast Archiver
 * Backed by an Array, uses VType ArchiveVString, as channelArchiver
 * @author Friederike Johlinger
 *
 */
public class FAValueIterator implements ValueIterator{
	private ArchiveVDisplayType[] values;
	private int index;

	
	public FAValueIterator(ArchiveVDisplayType[] values){
		this.values = values;
		index = 0;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean hasNext() {
		return index + 1 < values.length;
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


}
