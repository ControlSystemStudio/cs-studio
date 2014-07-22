package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.exceptions.DataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.csstudio.archive.vtype.ArchiveVNumber; // only for checking
import org.epics.util.time.Timestamp;// only for checking
import org.epics.vtype.AlarmSeverity;// only for checking
import org.epics.vtype.VType;


/**
 * Used to return data from the Fast Archiver
 * Backed by an Array, uses VType ArchiveVString, as channelArchiver
 * @author Friederike Johlinger
 *
 */
public class FAValueIterator implements ValueIterator{
	private VType[] values;
	private int index;
	long lastTime; //for checking
	long thisTime; // for checking
	
	public FAValueIterator(ArchiveVDisplayType[] values){
		this.values = values;
		index = -1;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean hasNext() {
		//return index < 5;
		return index + 1 < values.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VType next() throws Exception {
		index++;

		VType nextItem = values[index];
		/*if (index == 0){
			lastTime = ((ArchiveVNumber)nextItem).getTimestamp().getSec();
			
		} else {
			thisTime = ((ArchiveVNumber)nextItem).getTimestamp().getSec();
			
			if (thisTime - lastTime  >  4){
				System.out.println("ValueIterator: lastTime: "+lastTime+", thisTime: "+ thisTime);
				throw new DataNotAvailableException("Gap in time");
			}
			lastTime = thisTime;
		}*/
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
