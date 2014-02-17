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
import com.google.protobuf.InvalidProtocolBufferException;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.ScalarDouble;

/**
 * Dummy {@code GenMsgIterator} implementation.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class TestGenMsgIterator implements GenMsgIterator {

	private static final int MESSAGE_LIST_LENGTH = 10;
	
	private PayloadInfo info;	
	private ArrayList<EpicsMessage> epicsMessageList;
	private int counter = -1;
	
	/**
	 * Constructor in which list of epics messages and payload info are
	 * initialized.
	 */
	public TestGenMsgIterator() {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(new Timestamp(System.currentTimeMillis()));
		info = PayloadInfo.newBuilder().setPvname("testPV")
				.setType(PayloadType.SCALAR_DOUBLE)
				.setYear(startCal.get(Calendar.YEAR)).build();
		
		epicsMessageList = new ArrayList<EpicsMessage>();
		ByteString byteString = ByteString.copyFrom(new byte[]{8, -52, -13, -57, 14, 16, -36, -78, -35, -51, 2, 25, 0, 0, 0, 0, 0, -87, -62, 64, 32, 2, 40, 3});
		for (int i = 0; i < MESSAGE_LIST_LENGTH; i++) {
			try {
				epicsMessageList.add(new TestEpicsMessage(i,ScalarDouble.parseFrom(byteString), info));
			} catch (InvalidProtocolBufferException e) {}
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