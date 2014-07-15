package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVNumber; // only for checking
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;


/**
 * Used to return data from the Fast Archiver
 * Backed by an Array, uses VType ArchiveVString, as channelArchiver
 * @author Friederike Johlinger
 *
 */
public class FastArchiverValueIterator implements ValueIterator{
	private VType[] values;
	int index;
	
	public FastArchiverValueIterator(VType[] values){
		this.values = values;
		index = -1;
	}

	@Override
	public boolean hasNext() {
		//return index < 5;
		return index+2 != values.length;
	}

	@Override
	public VType next() throws Exception {
		index++;
		/*System.out.println("Iterator.next is called");
		System.out.println("value returned is: " + ((ArchiveVNumber)values[index]).getValue() );
		System.out.println("class returned is: " + values[index].getClass() );
		System.out.println("time returned is: " + ((ArchiveVNumber)values[index]).getTimestamp() );*/
		VType nextItem = values[index];
//		System.out.println(((ArchiveVNumber)nextItem).getValue());
		return nextItem;
	}
	/*public VType next(){
		index++;
		return new ArchiveVNumber (Timestamp.now(), AlarmSeverity.NONE, "status", null, 3);
	}*/

	@Override
	public void close() {
		values = null;		
	}


}
