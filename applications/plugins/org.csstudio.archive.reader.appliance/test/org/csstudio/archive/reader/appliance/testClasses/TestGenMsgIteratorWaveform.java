package org.csstudio.archive.reader.appliance.testClasses;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.epics.archiverappliance.retrieval.client.EpicsMessage;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorChar;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorDouble;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorEnum;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorFloat;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorInt;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorShort;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.VectorString;

public class TestGenMsgIteratorWaveform extends TestGenMsgIteratorRaw {

	public static final double[] VALUE_DOUBLE = {6,8,3,2,5,8,9,0,3,4,6,7,9,7,10,5};
	public static final double[] VALUE_FLOAT = {0.0, 2.375, 0.0, 2.5, 0.0, 2.125, 0.0, 2.0, 0.0, 
		2.3125, 0.0, 2.5, 0.0, 2.53125, 0.0, 0.0, 0.0, 2.125, 0.0, 2.25, 0.0, 2.375, 0.0, 2.4375,
		0.0, 2.53125, 0.0, 2.4375, 0.0, 2.5625, 0.0, 2.3125
	};
	public static final short[] VALUE_SHORT = {4, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 1, 3};
	public static final byte[] VALUE_BYTE = {4, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 1, 3};
	public static final int[] VALUE_INT = {7, 8, 9, 0, 11, 12, 13, 15, 16, 19, 20, 1};
	
	
	public TestGenMsgIteratorWaveform(String name, Timestamp start, Timestamp end) {
		super(name, start, end);
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
		PayloadType payloadType = PayloadType.WAVEFORM_DOUBLE;
		ByteString byteString = ByteString.copyFrom(new byte[]{8, -70, -111, -52, 1, 16, -35, -61, -15, -105, 2, 26, -128, 1, 0, 0, 0, 0, 0, 0, 24, 64, 0, 0, 0, 0, 0, 0, 32, 64, 0, 0, 0, 0, 0, 0, 8, 64, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 20, 64, 0, 0, 0, 0, 0, 0, 32, 64, 0, 0, 0, 0, 0, 0, 34, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 64, 0, 0, 0, 0, 0, 0, 16, 64, 0, 0, 0, 0, 0, 0, 24, 64, 0, 0, 0, 0, 0, 0, 28, 64, 0, 0, 0, 0, 0, 0, 34, 64, 0, 0, 0, 0, 0, 0, 28, 64, 0, 0, 0, 0, 0, 0, 36, 64, 0, 0, 0, 0, 0, 0, 20, 64, 58, 21, 10, 4, 68, 69, 83, 67, 18, 13, 116, 101, 115, 116, 32, 112, 118, 32, 119, 97, 118, 101, 49, 58, 12, 10, 3, 69, 71, 85, 18, 5, 117, 110, 105, 116, 115, 58, 9, 10, 4, 80, 82, 69, 67, 18, 1, 50, 64, 0});
		GeneratedMessage message = VectorDouble.parseFrom(byteString);
		
		Number[] values = new Number[MESSAGE_LIST_LENGTH];
		
		if (name.contains("double")) {
			payloadType = PayloadType.WAVEFORM_DOUBLE;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_DOUBLE[i];
			}
			message = VectorDouble.parseFrom(byteString);
		} else if (name.contains("float")) {
			payloadType = PayloadType.WAVEFORM_FLOAT;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_FLOAT[i];
			}
			message = VectorFloat.parseFrom(byteString);
		} else if (name.contains("int")) {
			payloadType = PayloadType.WAVEFORM_INT;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_INT[i];
			}
			message = VectorInt.parseFrom(new byte[]{8, -35, -29, -49, 1, 16, -103, -69, -86, -49, 1, 26, 48, 7, 0, 0, 0, 8, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 12, 0, 0, 0, 13, 0, 0, 0, 15, 0, 0, 0, 16, 0, 0, 0, 19, 0, 0, 0, 20, 0, 0, 0, 1, 0, 0, 0});
		} else if (name.contains("short")) {
			payloadType = PayloadType.WAVEFORM_SHORT;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_SHORT[i];
			}
			message = VectorShort.parseFrom(new byte[]{8, -48, -56, -97, 2, 16, -1, -20, -87, 126, 26, 16, 8, 8, 10, 12, 14, 16, 18, 0, 2, 4, 6, 8, 10, 12, 2, 6, 58, 31, 10, 17, 99, 110, 120, 114, 101, 103, 97, 105, 110, 101, 100, 101, 112, 115, 101, 99, 115, 18, 10, 49, 51, 57, 51, 50, 52, 53, 57, 48, 51, 58, 27, 10, 13, 99, 110, 120, 108, 111, 115, 116, 101, 112, 115, 101, 99, 115, 18, 10, 49, 51, 57, 51, 50, 52, 53, 56, 54, 57, 64, 0});
		} else if (name.contains("byte")) {
			payloadType = PayloadType.WAVEFORM_BYTE;
			for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
				values[i] = VALUES_BYTE[i];
			}
			message = VectorChar.parseFrom(new byte[]{8, -57, -56, -97, 2, 16, -22, -115, -60, -127, 3, 26, 16, 4, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 1, 3, 58, 31, 10, 17, 99, 110, 120, 114, 101, 103, 97, 105, 110, 101, 100, 101, 112, 115, 101, 99, 115, 18, 10, 49, 51, 57, 51, 50, 52, 53, 56, 57, 53, 58, 27, 10, 13, 99, 110, 120, 108, 111, 115, 116, 101, 112, 115, 101, 99, 115, 18, 10, 49, 51, 57, 51, 50, 52, 53, 56, 54, 57, 64, 0});
		} else if (name.contains("string")) {
			payloadType = PayloadType.WAVEFORM_STRING;
			message = VectorString.parseFrom(byteString);
		} else if (name.contains("enum")) {
			payloadType = PayloadType.WAVEFORM_ENUM;
			message = VectorEnum.parseFrom(byteString);
		}
		
		info = PayloadInfo.newBuilder().setPvname(name)
				.setType(payloadType)
				.setYear(startCal.get(Calendar.YEAR)).build();
		epicsMessageList = new ArrayList<EpicsMessage>();
		
		long s = start.getTime();
		for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
			epicsMessageList.add(new TestEpicsMessage(s + i, 0d,SEVERITIES[i],STATUS[i%STATUS.length],message, info));
		}
	
	}

}
