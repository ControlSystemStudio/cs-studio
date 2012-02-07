package org.csstudio.archive.common.guard;

import org.csstudio.domain.desy.time.TimeInstant;

public class SampleGap {

	private TimeInstant start;
	
	private TimeInstant end;

	public SampleGap(TimeInstant start, TimeInstant end) {
		this.start = start;
		this.end = end;
	}
	
	public TimeInstant getStart() {
		return start;
	}
	
	public TimeInstant getEnd() {
		return end;
	}
}
