package org.csstudio.archive.common.guard;

import org.csstudio.domain.desy.time.TimeInstant;

public class Interval {

	private final TimeInstant _start;
	
	private final TimeInstant _end;

	private Integer _sampleCount = -1;

	private int hourSampleCount;

	private int _samplesInDb = -1;

	public Interval(TimeInstant start, TimeInstant end, Integer count) {
		_start = start;
		_end = end;
		_sampleCount = count;
	}
	
	public TimeInstant getStart() {
		return _start;
	}
	
	public TimeInstant getEnd() {
		return _end;
	}

	public Integer getSampleCount() {
		return _sampleCount;
	}

	public void setHourSample(int hourSampleCount) {
		this.hourSampleCount = hourSampleCount;
		if (hourSampleCount > 1) {
//			System.out.println("More than one hour sample");
		}
	}
	
	public boolean isHourSampleLost() {
		if(hourSampleCount == 0 && _sampleCount > 0) {
			return true;
		}
		return false;
	}

	public void setSampleInDbCount(int samplesInDb) {
		_samplesInDb = samplesInDb;
	}

	public Integer getSampleDifferencePvManagerDb() {
		if (_samplesInDb == -1 || _sampleCount == -1) {
			return null;
		}
		return _sampleCount-_samplesInDb;
	}

	public boolean isDifferenceValid() {
		if (_samplesInDb == -1 || _sampleCount == -1) {
			return false;
		} else {
			return true;
		}
	}

	public int getSamplesInDb() {
		return _samplesInDb;
	}

}

