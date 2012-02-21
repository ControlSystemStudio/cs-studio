package org.csstudio.archive.common.guard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.domain.desy.time.TimeInstant;

public class Evaluater {

	public void printIntervals(List<PvIntervalList> listOfPvIntervals) {
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			System.out.println("Channel: " + pvIntervalList.getChannelName());
			for (Interval interval : pvIntervalList.getIntervalList()) {
				System.out
						.println("Interval : " + interval.getStart() + " - "
								+ interval.getEnd() + " : "
								+ interval.getSampleCount());
			}
		}
	}

	public List<PvIntervalList> removeEmptyPvLists(
			List<PvIntervalList> listOfPvIntervals) {
		List<PvIntervalList> intervalListsToRemove = new ArrayList<>();
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			if (pvIntervalList.isAllIntervalsEmpty()) {
				System.out.println("Remove Channel: "
						+ pvIntervalList.getChannelName());
				intervalListsToRemove.add(pvIntervalList);
			}
		}
		listOfPvIntervals.removeAll(intervalListsToRemove);
		return listOfPvIntervals;
	}

	public void printAverageAndVariance(List<PvIntervalList> listOfPvIntervals) {
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			System.out.println("Channel: " + pvIntervalList.getChannelName()
					+ "  avg: " + pvIntervalList.getAverageIntervalCount()
					+ " Variance: " + pvIntervalList.getVariance());
		}
	}

	public List<PvIntervalList> removeSmallAvgPvLists(
			List<PvIntervalList> listOfPvIntervals, int threshold) {
		List<PvIntervalList> intervalListsToRemove = new ArrayList<>();
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			if (pvIntervalList.getAverageIntervalCount() < threshold) {
				System.out.println("Small Avg, Remove Channel: "
						+ pvIntervalList.getChannelName());
				intervalListsToRemove.add(pvIntervalList);
			}
		}
		listOfPvIntervals.removeAll(intervalListsToRemove);
		return listOfPvIntervals;
	}

	public List<PvIntervalList> removeSmallWithoutMaxAvgPvLists(
			List<PvIntervalList> listOfPvIntervals, int threshold,
			int maxIntervalsToRemove) {
		List<PvIntervalList> intervalListsToRemove = new ArrayList<>();
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			double averageIntervalWithoutMaxCount = pvIntervalList
					.getAverageIntervalWithoutMaxCount(maxIntervalsToRemove);
			System.out.println("avg: "
					+ pvIntervalList.getAverageIntervalCount() + "; avgwm: "
					+ averageIntervalWithoutMaxCount);
			if (averageIntervalWithoutMaxCount < threshold) {
				System.out.println("Small Without Max Avg, Remove Channel: "
						+ pvIntervalList.getChannelName());
				intervalListsToRemove.add(pvIntervalList);
			}
		}
		listOfPvIntervals.removeAll(intervalListsToRemove);
		return listOfPvIntervals;
	}

	public Map<TimeInstant, List<String>> aggregateGapsForRange(
			List<PvIntervalList> listOfPvIntervals) {
		Map<TimeInstant, List<String>> gapsMap = prepareGapsMap(listOfPvIntervals);
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			for (Interval interval : pvIntervalList.getIntervalList()) {
				if (interval.getSampleCount() == 0) {
					List<String> list = gapsMap.get(interval.getStart());
					list.add(pvIntervalList.getChannelName());
				}
			}
		}
		return gapsMap;
	}

	private Map<TimeInstant, List<String>> prepareGapsMap(
			List<PvIntervalList> listOfPvIntervals) {
		Map<TimeInstant, List<String>> gaps = new HashMap<>();
		PvIntervalList intervalList = listOfPvIntervals.get(0);
		if (intervalList != null) {
			for (Interval interval : intervalList.getIntervalList()) {
				List<String> list = new ArrayList<>();
				gaps.put(interval.getStart(), list);
			}
		}
		return gaps;
	}

	public void printAggregatedGaps(Map<TimeInstant, List<String>> gaps) {
		Set<TimeInstant> keySet = gaps.keySet();
		for (TimeInstant timeInstant : keySet) {
			List<String> channelNames = gaps.get(timeInstant);
			for (String channelName : channelNames) {
				System.out.println("Time: " + timeInstant.toString()
						+ " - channel: " + channelName);
			}
		}
	}

	public void printLostHourSamples(List<PvIntervalList> listOfPvIntervals) {
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			System.out.println("channel: " + pvIntervalList.getChannelName());
			for (Interval interval : pvIntervalList.getIntervalList()) {
				if (interval.isHourSampleLost()) {
					System.out.println("Lost: " + interval.getStart());
				}

			}
		}
	}

	public void checkDifferencePvManagerDb(
			Map<String, PvIntervalList> pvIntervalMap) {
		Set<String> keySet = pvIntervalMap.keySet();
		for (String channelName : keySet) {
			PvIntervalList pvIntervalList = pvIntervalMap.get(channelName);
			for (Interval interval : pvIntervalList.getIntervalList()) {
				if (interval.isDifferenceValid() && interval.getSampleDifferencePvManagerDb() != 0) {
					System.out.println("Difference! channel " + channelName
							+ " start: " + interval.getStart() + " end: "
							+ interval.getEnd() + " db: " + interval.getSamplesInDb()
							+ " pvM: " + interval.getSampleCount());
				}
			}
		}
	}

}