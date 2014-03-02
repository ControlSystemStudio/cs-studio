package org.csstudio.archive.reader.appliance.testClasses;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.archiverappliance.retrieval.client.InfoChangeHandler;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.ScalarDouble;

/**
 * 
 * <code>TestGenMsgIteratorRaw</code> generates epics messages for raw data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class TestGenMsgIteratorRaw implements GenMsgIterator {

	public static final int MESSAGE_LIST_LENGTH = 10;
	
	protected PayloadInfo info;	
	protected ArrayList<EpicsMessage> epicsMessageList;
	private int counter = -1;
	
	public static final double[] VALUES_DOUBLE = new double[]{
		1,2,3,4,5,6,7,8,9,10};
	public static final float[] VALUES_FLOAT = new float[]{
		1,2,3,4,5,6,7,8,9,10};
	public static final int[] VALUES_INT = new int[]{
		1,2,3,4,5,6,7,8,9,10};
	public static final short[] VALUES_SHORT = new short[]{
		1,2,3,4,5,6,7,8,9,10};
	public static final byte[] VALUES_BYTE = new byte[]{
		1,2,3,4,5,6,7,8,9,10};
	public static final int[] SEVERITIES = new int[]{
		3,2,2,1,0,0,0,1,2,4};
	public static final int[] STATUS = new int[]{
		3,2,2,1,0,0,0,1,2,4};
	
	
	/**
	 * Constructor
	 * 
	 * @param name the name of the PV
	 * @param start the start time of the requested samples
	 * @param end the end time of the requested samples
	 */
	public TestGenMsgIteratorRaw(String name, Timestamp start, Timestamp end) {
		try {
			initialize(name,start,end);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the epics messages.
	 * 
	 * @param name the name of the PV
	 * @param start the start time of the requested samples
	 * @param end the end time of the requested samples
	 * @throws InvalidProtocolBufferException 
	 */
	protected void initialize(String name, Timestamp start, Timestamp end) throws InvalidProtocolBufferException {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		PayloadType payloadType = PayloadType.SCALAR_DOUBLE;
		Number[] values = new Number[MESSAGE_LIST_LENGTH];
		ByteString byteString = ByteString.copyFrom(new byte[]{8, -52, -13, -57, 14, 16, -36, -78, -35, -51, 2, 25, 0, 0, 0, 0, 0, -87, -62, 64, 32, 2, 40, 3});
		GeneratedMessage message = ScalarDouble.parseFrom(byteString);
		
		if (name.contains("double")) {
			payloadType = PayloadType.SCALAR_DOUBLE;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_DOUBLE[i];
			}
		} else if (name.contains("float")) {
			payloadType = PayloadType.SCALAR_FLOAT;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_FLOAT[i];
			}
		} else if (name.contains("int")) {
			payloadType = PayloadType.SCALAR_INT;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_INT[i];
			}
		} else if (name.contains("short")) {
			payloadType = PayloadType.SCALAR_SHORT;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_SHORT[i];
			}
		} else if (name.contains("byte")) {
			payloadType = PayloadType.SCALAR_BYTE;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_BYTE[i];
			}
		} else if (name.contains("string")) {
			payloadType = PayloadType.SCALAR_STRING;
		} else if (name.contains("enum")) {
			payloadType = PayloadType.SCALAR_ENUM;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_INT[i];
			}
		}
		
		info = PayloadInfo.newBuilder().setPvname(name)
				.setType(payloadType)
				.setYear(startCal.get(Calendar.YEAR)).build();
		epicsMessageList = new ArrayList<EpicsMessage>();
		
		long s = start.getTime();
		for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
			epicsMessageList.add(new TestEpicsMessage(s + i, values[i],SEVERITIES[i],STATUS[i],message, info));
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<EpicsMessage> iterator() {
		return new Iterator<EpicsMessage>() {

			@Override
			public boolean hasNext() {
				return counter != MESSAGE_LIST_LENGTH - 1;
			}

			@Override
			public EpicsMessage next() {
				counter ++;
				return epicsMessageList.get(counter);
			}

			@Override
			public void remove() { }
		};
	}

	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.GenMsgIterator#getPayLoadInfo()
	 */
	@Override
	public PayloadInfo getPayLoadInfo() {
		return info;
	}

	/* (non-Javadoc)
	 * @see org.epics.archiverappliance.retrieval.client.GenMsgIterator#onInfoChange(org.epics.archiverappliance.retrieval.client.InfoChangeHandler)
	 */
	@Override
	public void onInfoChange(InfoChangeHandler arg0) { }
	
	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException { }
}