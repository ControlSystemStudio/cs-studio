package org.csstudio.archive.common.guard;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

public class ArchiveGuard {

	private ConnectionHandler _connectionHandler;

	public ArchiveGuard() {
		_connectionHandler = new ConnectionHandler();
	}

	public void checkArchiveDbForGaps() {
		GapFinder gapFinder = new GapFinder(_connectionHandler);
		TimeInstant start =   TimeInstantBuilder.fromSeconds(Settings.RANGE_START_IN_SECONDS);
		TimeInstant end = TimeInstantBuilder.fromSeconds(Settings.RANGE_END_IN_SECONDS);
		gapFinder.retrieveSamplesForChannels(start, end);
		gapFinder.removeIfAllIntervalsOfPvEmpty();
		gapFinder.removeSmallAvgPvLists(10);
		gapFinder.removeSmallWithoutMaxAvgPvLists(10, 5);
		// eval.printLostHourSamples(listOfPvIntervals);
		Map<TimeInstant, List<String>> aggregateGapsForRange = gapFinder
				.getAggregatedGapsForRange();
		printAggregatedGaps(aggregateGapsForRange);
	}

	public void compareArchiveDbWithLog() {
		PvManagerMySqlComparator comparator = new PvManagerMySqlComparator(
				_connectionHandler,
				"D://eclipse//e370//rpSF//cs-studio//applications//plugins//org.csstudio.archive.common.guard//count.log");
		comparator.getSampleCountFromLogFile();
		comparator.getSampleCountFromMySqlForLogFileIntervals();
		comparator.checkDifferencePvManagerDb();
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

	public void printAverageAndVariance(List<PvIntervalList> listOfPvIntervals) {
		for (PvIntervalList pvIntervalList : listOfPvIntervals) {
			System.out.println("Channel: " + pvIntervalList.getChannelName()
					+ "  avg: " + pvIntervalList.getAverageIntervalCount()
					+ " Variance: " + pvIntervalList.getVariance());
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

}
