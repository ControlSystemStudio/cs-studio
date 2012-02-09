package org.csstudio.archive.common.guard;

import org.csstudio.domain.desy.time.TimeInstant;

public class Interval {

	private final TimeInstant start;
	
	private final TimeInstant end;

	private final Integer sampleCount;

	private int hourSampleCount;

	public Interval(TimeInstant start, TimeInstant end, Integer count) {
		this.start = start;
		this.end = end;
		this.sampleCount = count;
	}
	
	public TimeInstant getStart() {
		return start;
	}
	
	public TimeInstant getEnd() {
		return end;
	}

	public Integer getSampleCount() {
		return sampleCount;
	}

	public void setHourSample(int hourSampleCount) {
		this.hourSampleCount = hourSampleCount;
		if (hourSampleCount > 1) {
			System.out.println("More than one hour sample");
		}
	}
	
	public boolean isHourSampleLost() {
		if(hourSampleCount == 0 && sampleCount > 0) {
			return true;
		}
		return false;
	}
	

}
