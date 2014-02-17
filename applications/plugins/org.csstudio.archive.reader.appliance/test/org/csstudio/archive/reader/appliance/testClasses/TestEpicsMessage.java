package org.csstudio.archive.reader.appliance.testClasses;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.csstudio.archive.reader.appliance.ApplianceArchiveReaderConstants;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;

import com.google.protobuf.GeneratedMessage;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;

/**
 * Dummy {@code EpicsMessage} implementation.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class TestEpicsMessage extends EpicsMessage{
	
	private final int index;
	public static final long[] TIME = new long[]{
		0,1,2,3,4,5,6,7,8,9};
	public static final double[] VALUES = new double[]{
		1,2,3,4,5,6,7,8,9,10};
	public static final int[] SEVERITIES = new int[]{
		3,2,2,1,0,0,0,1,2,4};
	
	/**
	 * Constructor.
	 */
	public TestEpicsMessage(int index, GeneratedMessage message, PayloadInfo info) {
		super(message, info);
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getElementCount()
	 */
	@Override
	public int getElementCount() {
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getFieldValues()
	 */
	@Override
	public HashMap<String, String> getFieldValues() {
		HashMap<String,String> fields = new HashMap<String, String>();
		fields.put(ApplianceArchiveReaderConstants.EGU, "kV");
		fields.put(ApplianceArchiveReaderConstants.LOPR, "0");
		fields.put(ApplianceArchiveReaderConstants.HOPR, "10");
		fields.put(ApplianceArchiveReaderConstants.LOW, "4");
		fields.put(ApplianceArchiveReaderConstants.HIGH, "8");
		fields.put(ApplianceArchiveReaderConstants.HIHI, "9");
		fields.put(ApplianceArchiveReaderConstants.LOLO, "3");
		fields.put(ApplianceArchiveReaderConstants.PREC, "3");
		return fields;
	}
		
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getNumberAt(int)
	 */
	@Override
	public Number getNumberAt(int index) throws IOException {
		return 4000;
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getNumberValue()
	 */
	@Override
	public Number getNumberValue() throws IOException {
		return VALUES[index];
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getSeverity()
	 */
	@Override
	public int getSeverity() {
		return SEVERITIES[index];
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getStatus()
	 */
	@Override
	public int getStatus() {
		return 3904;
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#getTimestamp()
	 */
	@Override
	public Timestamp getTimestamp() {
		return new Timestamp(TIME[index]);
	}
	
	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.EpicsMessage#hasFieldValues()
	 */
	@Override
	public boolean hasFieldValues() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Epics Message";
	}
}