package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ValueIterator;
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
	
	public FAValueIterator(ArchiveVNumber[] values){
		this.values = values;
		index = -1;
	}

	@Override
	public boolean hasNext() {
		//return index < 5;
		return index + 1 < values.length;
	}

	@Override
	public VType next() throws Exception {
		index++;
		//System.out.println("valuesLength: "+ values.length+", index: "+index);
		/*System.out.println("Iterator.next is called");
		System.out.println("value returned is: " + ((ArchiveVNumber)values[index]).getValue() );
		System.out.println("class returned is: " + values[index].getClass() );
		System.out.println("time returned is: " + ((ArchiveVNumber)values[index]).getTimestamp() );*/
		VType nextItem = values[index];
		if (index == 0){
			lastTime = ((ArchiveVNumber)nextItem).getTimestamp().getSec();
		} else {
			thisTime = ((ArchiveVNumber)nextItem).getTimestamp().getSec();
			if (thisTime - lastTime  >  15){
				System.out.println("ValueIterator: Gap in time");
			}
			lastTime = thisTime;
		}
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
