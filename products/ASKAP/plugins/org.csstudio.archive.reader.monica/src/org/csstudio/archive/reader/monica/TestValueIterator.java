package org.csstudio.archive.reader.monica;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

public class TestValueIterator implements ValueIterator {

	private int maxCount = -1;
	private int currentIndex = 0;
	private ITimestamp startTimeStamp = null;
	private ITimestamp endTimeStamp = null;

	public TestValueIterator(int maxCount, ITimestamp start, ITimestamp end) {
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
	public IValue next() throws Exception {
		double dTime = startTimeStamp.toDouble();
		double eTime = endTimeStamp.toDouble();
		
		double time = dTime + currentIndex * (eTime-dTime)/maxCount;
		
		ITimestamp timeStamp =  TimestampFactory.fromDouble(time);
				
		IValue value = ValueFactory.createLongValue(timeStamp,
				new ISeverity() {
					
					@Override
					public boolean isOK() {
						return true;
					}
					
					@Override
					public boolean isMinor() {
						return false;
					}
					
					@Override
					public boolean isMajor() {
						return false;
					}
					
					@Override
					public boolean isInvalid() {
						return true;
					}
					
					@Override
					public boolean hasValue() {
						return true;
					}
				},
				"Connected",
				null,
				Quality.Original,
				new long[]{currentIndex});

		currentIndex++;
		
		return value;
	}

	@Override
	public void close() {
		currentIndex = 0;
	}

}
