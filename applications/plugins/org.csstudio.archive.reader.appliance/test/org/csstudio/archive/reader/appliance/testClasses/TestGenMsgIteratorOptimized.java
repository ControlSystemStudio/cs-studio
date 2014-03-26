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

public class TestGenMsgIteratorOptimized implements GenMsgIterator {
	
	protected PayloadInfo info;	
	protected ArrayList<EpicsMessage> epicsMessageList;
	private int counter = -1;
	
	private int size = 10;
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
	public TestGenMsgIteratorOptimized(String name, Timestamp start, Timestamp end) {
		int step = 0;
		if (name.startsWith("mean_")) {
			int index = name.indexOf('(');
			step = Integer.parseInt(name.substring(5,index));
		} else if (name.startsWith("std_")) {
			int index = name.indexOf('(');
			step = Integer.parseInt(name.substring(5,index));
		}
		
		try {
			initialize(name,start,end,step);
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
	protected void initialize(String name, Timestamp start, Timestamp end,int step) throws InvalidProtocolBufferException {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		PayloadType payloadType = PayloadType.SCALAR_DOUBLE;
		
		ByteString byteString = ByteString.copyFrom(new byte[]{8, -52, -13, -57, 14, 16, -36, -78, -35, -51, 2, 25, 0, 0, 0, 0, 0, -87, -62, 64, 32, 2, 40, 3});
		GeneratedMessage message = ScalarDouble.parseFrom(byteString);
		
		size = (int)(((end.getTime()-start.getTime())/1000)/step);
		Number[] values = new Number[size];
		
		if (name.contains("double")) {
			payloadType = PayloadType.SCALAR_DOUBLE;
			for (int i = 0; i < size; i++) {
				values[i] = VALUES_DOUBLE[i%VALUES_DOUBLE.length];
			}
		} else if (name.contains("float")) {
			payloadType = PayloadType.SCALAR_FLOAT;
			for (int i = 0; i < size; i++) {
				values[i] = VALUES_FLOAT[i%VALUES_FLOAT.length];
			}
		} else if (name.contains("int")) {
			payloadType = PayloadType.SCALAR_INT;
			for (int i = 0; i < size; i++) {
				values[i] = VALUES_INT[i%VALUES_INT.length];
			}
		} else if (name.contains("short")) {
			payloadType = PayloadType.SCALAR_SHORT;
			for (int i = 0; i < size; i++) {
				values[i] = VALUES_SHORT[i%VALUES_SHORT.length];
			}
		} else if (name.contains("byte")) {
			payloadType = PayloadType.SCALAR_BYTE;
			for (int i = 0; i < size; i++) {
				values[i] = VALUES_BYTE[i%VALUES_BYTE.length];
			}
		}
		
		info = PayloadInfo.newBuilder().setPvname(name)
				.setType(payloadType)
				.setYear(startCal.get(Calendar.YEAR)).build();
		epicsMessageList = new ArrayList<EpicsMessage>();
				
		long s = start.getTime();
		long st = step*1000;
		for (int i = 0; i < size; i++) {
			long time = s + i*st;
			epicsMessageList.add(new TestEpicsMessage(time, values[i],SEVERITIES[i%SEVERITIES.length],STATUS[i%STATUS.length],message, info));
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
				return counter != size - 1;
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