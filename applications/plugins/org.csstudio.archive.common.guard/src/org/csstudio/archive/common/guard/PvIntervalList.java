package org.csstudio.archive.common.guard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	
	/**
	 * Average samples over all intervals without the intervals with most samples.
	 * (Otherwise Pvs with one small interval with a lot of samples and otherwise no samples will
	 * be detected as Pvs with gaps.
	 */
	public double getAverageIntervalWithoutMaxCount(Integer maxIntervalsToRemove) {
		TreeSet<Integer> sampleCount = new TreeSet<>();
		for (Interval interval : _intervalList) {
			sampleCount.add(interval.getSampleCount());
		}
		if(maxIntervalsToRemove > sampleCount.size()) {
			maxIntervalsToRemove = sampleCount.size();
		}
		for (int i = 0; i < maxIntervalsToRemove; i++) {
			sampleCount.remove(sampleCount.last());
		}
		double averageIntervalWithoutMaxCount = 0;
		int sum = 0;
		for (Integer sampleSize : sampleCount) {
			sum = sum+sampleSize;
		}
		if (sampleCount.size() != 0) {
			averageIntervalWithoutMaxCount = sum/sampleCount.size();
		} else {
			return 0;
		}
		return averageIntervalWithoutMaxCount;
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
