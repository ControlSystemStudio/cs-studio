package org.csstudio.archive.common.guard;

import java.util.ArrayList;
import java.util.List;

public class PvIntervalList {

	private final String channelName;

	private boolean allIntervalsEmpty = true;
	
	private double averageIntervalCount = 0;
	
	private double variance = 1;
	
	private List<Interval> _intervalList = new ArrayList<>();
	
	public PvIntervalList(String chanName) {
		channelName = chanName;
	}
	
	public void addInterval(Interval interval) {
		
		averageIntervalCount = (_intervalList.size()*averageIntervalCount+interval.getSampleCount())/(_intervalList.size()+1);
		
		if(interval.getSampleCount() != 0) {
			allIntervalsEmpty = false;
		}
		
		_intervalList.add(interval);
	}

	public void addIntervalList(List<Interval> intervalList) {
		_intervalList = intervalList;
		int sum = 0;
		for (Interval interval : intervalList) {
			if(interval.getSampleCount() != 0) {
				sum = interval.getSampleCount()+sum;
				allIntervalsEmpty = false;
				break;
			}
		}
		averageIntervalCount = sum/intervalList.size();
	}

	public String getChannelName() {
		return channelName;
	}

	public List<Interval> getIntervalList() {
		return _intervalList;
	}

	public boolean isAllIntervalsEmpty() {
		return allIntervalsEmpty;
	}

	public double getAverageIntervalCount() {
		return averageIntervalCount;
	}

	public double getVariance() {
		double sum = 0;
		for (Interval interval : _intervalList) {
			sum = sum+Math.pow((averageIntervalCount-interval.getSampleCount()),2.0);
		}
		variance = sum/_intervalList.size();
		return variance;
	}
}
