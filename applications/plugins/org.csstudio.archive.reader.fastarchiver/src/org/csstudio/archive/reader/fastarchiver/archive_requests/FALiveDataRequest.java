package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;

public class FALiveDataRequest extends FARequest {

	public FALiveDataRequest(String url) throws FADataNotAvailableException {
		super(url);		
		// TODO create a usable constructor, maybe collecting values on an own
		// Thread?
	}

	public List<ArchiveVDisplayType> fetchNewValues() throws FADataNotAvailableException{
		// TODO Should create a list of VTypes with new Values from the Archiver
		// Throw exception if T is not a VType, if VType return same type as
		// FAArchivedDataRequest
		
		//Dummy implementation
		LinkedList<ArchiveVDisplayType> newValues = new LinkedList<ArchiveVDisplayType>();
		for (int i = 0; i < 50; i++){
			newValues.add((ArchiveVDisplayType)new ArchiveVNumber(Timestamp.now(), AlarmSeverity.NONE,
				"status", null, 5.0));
		}		
		return newValues;
		
	}


	public void close() {
		// TODO Need to implement if there is a thread running collecting data
		// from the live stream 
		
	}

}
