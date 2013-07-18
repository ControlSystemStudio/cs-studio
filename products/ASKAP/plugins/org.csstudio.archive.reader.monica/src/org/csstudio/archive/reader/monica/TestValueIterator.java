package org.csstudio.archive.reader.monica;

import java.util.Date;

import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VInt;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

public class TestValueIterator implements ValueIterator {

    private static final Display DisplayNone = ValueFactory.newDisplay(Double.NaN, Double.NaN, 
            Double.NaN, "", NumberFormats.toStringFormat(), Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN);

	private int maxCount = -1;
	private int currentIndex = 0;
	private Timestamp startTimeStamp = null;
	private Timestamp endTimeStamp = null;

	public TestValueIterator(int maxCount, Timestamp start, Timestamp end) {
		this.maxCount  = maxCount;
		this.startTimeStamp = start;
		this.endTimeStamp = end;
	}

	@Override
	public boolean hasNext() {
		if (currentIndex<maxCount)
			return true;
		
		return false;
	}

	@Override
	public VType next() throws Exception {
		long dTime = startTimeStamp.toDate().getTime();
		long eTime = endTimeStamp.toDate().getTime();
		
		long time = dTime + currentIndex * (eTime-dTime)/maxCount;
		
		Timestamp timeStamp =  Timestamp.of(new Date(time));
				
		VInt value = ValueFactory.newVInt(new Integer(currentIndex), 
				ValueFactory.newAlarm(AlarmSeverity.NONE, "NONE"), 
				ValueFactory.newTime(timeStamp), 
				DisplayNone);

		currentIndex++;
		
		return value;
	}

	@Override
	public void close() {
		currentIndex = 0;
	}

}
