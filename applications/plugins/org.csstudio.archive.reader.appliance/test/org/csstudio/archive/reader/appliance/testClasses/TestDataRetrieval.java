package org.csstudio.archive.reader.appliance.testClasses;

import java.sql.Timestamp;
import java.util.HashMap;

import org.epics.archiverappliance.retrieval.client.DataRetrieval;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;

/**
 * Dummy {@code DataRetrieval} implementation.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class TestDataRetrieval implements DataRetrieval{

	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.DataRetrieval#getDataForPV(java.lang.String, java.sql.Timestamp, java.sql.Timestamp)
	 */
	@Override
	public GenMsgIterator getDataForPV(String name, Timestamp start, Timestamp end) {
		return new TestGenMsgIterator();
	}

	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.DataRetrieval#getDataForPV(java.lang.String, java.sql.Timestamp, java.sql.Timestamp, boolean)
	 */
	@Override
	public GenMsgIterator getDataForPV(String name, Timestamp start, Timestamp end, boolean arg3) {
		return new TestGenMsgIterator();
	}

	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.DataRetrieval#getDataForPV(java.lang.String, java.sql.Timestamp, java.sql.Timestamp, boolean, java.util.HashMap)
	 */
	@Override
	public GenMsgIterator getDataForPV(String name, Timestamp start, Timestamp end, boolean arg3, HashMap<String, String> arg4) {
		return new TestGenMsgIterator();
	}
}