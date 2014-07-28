package org.csstudio.archive.reader.fastarchiver;

import java.io.IOException;
import java.util.HashMap;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAArchivedDataRequest2;
import org.csstudio.archive.reader.fastarchiver.exceptions.DataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

public class FAValueIterator2 implements ValueIterator{
	private ArchiveVDisplayType[] values;
	private int index;
	private FAArchivedDataRequest2 faDataRequest;
	private int count;
	private String name;
	
	
	public FAValueIterator2 (String name, Timestamp start, Timestamp end, int count, String url, HashMap<String, int[]> mapping) throws IOException, DataNotAvailableException{
		//create new ArchiveVDisplayType[] using FADataRequest(2)
		this.index = 0;
		this.count = count;
		this.name = name;
		faDataRequest = new FAArchivedDataRequest2(url, mapping);
		if (count == -1){
			values = faDataRequest.getRawValues(name, start, end);
		} else {
			values = faDataRequest.getOptimisedValues(name, start, end, count);
		}
		
		
	}

	@Override
	public boolean hasNext() {
		return values != null;
	}

	@Override
	public VType next() throws Exception {
		if (index < values.length){
			VType value = values[index];
			index++;
			return value;
		}
		ArchiveVDisplayType lastValue = values[index - 1];
		Timestamp start = lastValue.getTimestamp();
		// use timestamp to get samples until now
		if (count == -1){
			values = faDataRequest.getRawValues(name, start, Timestamp.now());
		} else {
			values = faDataRequest.getOptimisedValues(name, start, Timestamp.now(), count);
		}
		System.out.println("values length: "+ values.length);
		// make sure only new samples are returned: set index to first new sample
		for (index = 0; index < values.length; index++){ //what if only same sample is returned?
			if (values[index].getTimestamp().compareTo(lastValue.getTimestamp()) > 0) break;
		}

		// return first value of new values
		VType value = values[index];
		index++;
		return null;
		//return value;
	}

	@Override
	public void close() {
		values = null;
	}

}
