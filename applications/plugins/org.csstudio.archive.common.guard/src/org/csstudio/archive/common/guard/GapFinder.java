package org.csstudio.archive.common.guard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

/**
 * Find gaps in a time range defined in class Settings.
 * The time range is split in intervals.
 * The list of PvIntervalList (_listOfPvIntervals) is the main structure to store the
 * sample number for each channel. 
 * Class PvIntervalsList holds for one channel the interval list of the time range and
 * for each interval the number of samples.
 *  *  
 * @author jhatje
 *
 */
public class GapFinder {

	private ConnectionHandler _connectionHandler;
	private int rangeCount = 0;
	List<PvIntervalList> _listOfPvIntervals = new ArrayList<PvIntervalList>();

	public GapFinder(ConnectionHandler connectionHandler) {
		_connectionHandler = connectionHandler;
	}

	
	public void retrieveSamplesForChannels(TimeInstant start, TimeInstant end) {
		try {
			initialiseIntervalListForChannels();
			System.out.println("-------size: " + _listOfPvIntervals.size());
		
			for (PvIntervalList intervalList : _listOfPvIntervals) {
				retrieveSamplesInIntervals(intervalList, start, end);
				
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PvIntervalList retrieveSamplesInIntervals(PvIntervalList pvIntervals, TimeInstant start,
			TimeInstant end) throws Exception {
		Connection con = _connectionHandler.getConnection();
		TimeInstant rangeStart = TimeInstantBuilder.fromNanos(end.getNanos() - (long) Settings.RANGE_IN_NANO);
		TimeInstant rangeEnd = end;
		PreparedStatement stmtSamples = createCountSamplesInRangeStatement(con);
		PreparedStatement stmtHourSamples = createCountHourSamplesInRangeStatement(con);
		rangeCount = 0;
		while(rangeStart.getNanos() > start.getNanos()) {
	        stmtSamples.setInt(1, Integer.valueOf(pvIntervals.getChannelId()));
	        stmtSamples.setLong(2, rangeStart.getNanos());
	        stmtSamples.setLong(3, rangeEnd.getNanos());
	        stmtHourSamples.setInt(1, Integer.valueOf(pvIntervals.getChannelId()));
	        stmtHourSamples.setLong(2, rangeStart.getNanos());
	        stmtHourSamples.setLong(3, rangeEnd.getNanos());
	        ResultSet resultSamples = stmtSamples.executeQuery();
	        ResultSet resultHourSamples = stmtHourSamples.executeQuery();
	        resultSamples.next();
	        resultHourSamples.next();
	        int sum = resultSamples.getInt(1);
	        Interval interval = new Interval(rangeStart, rangeEnd, sum);
	        interval.setHourSample(resultHourSamples.getInt(1));
			pvIntervals.addInterval(interval);
			resultSamples.close();
			resultSamples = null;
			resultHourSamples.close();
			resultHourSamples = null;
	        rangeEnd = rangeStart;
	        rangeStart = TimeInstantBuilder.fromNanos(rangeEnd.getNanos() - (long) Settings.RANGE_IN_NANO);
	        rangeCount++;
		}
		con.close();
		return pvIntervals;
	}


	private void initialiseIntervalListForChannels() throws Exception {
		Connection con = _connectionHandler.getConnection();
		PreparedStatement stm = createReadAllChannelsStatement(con);
		ResultSet resultSet = stm.executeQuery();
		while (resultSet.next()) {
			String channelName = resultSet.getString(2);
			if (!channelName.contains("ADEL")) {
				_listOfPvIntervals.add(new PvIntervalList(channelName, resultSet.getString(1)));
			}
		}
		resultSet.close();
	}


	private PreparedStatement createReadAllChannelsStatement(Connection con)
			throws SQLException {
		PreparedStatement stmt = con
				.prepareStatement("select channel.id, channel.name from " + Settings.DATABASE + ".channel "
//						+ Settings.DATABASE + ".channel_status where channel.id = channel_status.channel_id "
						+ "where channel.group_id = '" + Settings.GROUP_ID + "'; ");
//						+ "and channel_status.connected like 'TRUE' group by channel.id;");
		return stmt;
	}

	private PreparedStatement createCountSamplesInRangeStatement(Connection con)
			throws SQLException {
		PreparedStatement stmt = con
				.prepareStatement("select count(*) from " + Settings.DATABASE + ".sample "
						+ "where sample.channel_id = ? "
						+ "and sample.time between ? and ?");
		return stmt;
	}

	private PreparedStatement createCountHourSamplesInRangeStatement(Connection con)
			throws SQLException {
		PreparedStatement stmt = con
				.prepareStatement("select count(*) from " + Settings.DATABASE + ".sample_h "
						+ "where sample_h.channel_id = ? "
						+ "and sample_h.time between ? and ?");
		return stmt;
	}


	public int getRangeCount() {
		return rangeCount;
	}
	
	public void removeIfAllIntervalsOfPvEmpty() {
		List<PvIntervalList> intervalListsToRemove = new ArrayList<PvIntervalList>();
		for (PvIntervalList pvIntervalList : _listOfPvIntervals) {
			if (pvIntervalList.isAllIntervalsEmpty()) {
				System.out.println("Remove Channel: "
						+ pvIntervalList.getChannelName());
				intervalListsToRemove.add(pvIntervalList);
			}
		}
		_listOfPvIntervals.removeAll(intervalListsToRemove);
	}
	
	/**
	 * Remove PvIntervalLists where the average number of samples over all intervals
	 * are below threshold.
	 * 
	 * @param threshold
	 */
	public void removeSmallAvgPvLists(int threshold) {
		List<PvIntervalList> intervalListsToRemove = new ArrayList<PvIntervalList>();
		for (PvIntervalList pvIntervalList : _listOfPvIntervals) {
			if (pvIntervalList.getAverageIntervalCount() < threshold) {
				System.out.println("Small Avg, Remove Channel: "
						+ pvIntervalList.getChannelName());
				intervalListsToRemove.add(pvIntervalList);
			}
		}
		_listOfPvIntervals.removeAll(intervalListsToRemove);
	}
	
	/**
	 * Remove PvIntervalLists where the average number of samples over all intervals
	 * are below threshold. Remove BEFORE calculation of average number the intervals 
	 * with largest number of samples.
	 * 
	 * @param threshold
	 * @param maxIntervalsToRemove
	 */
	public void removeSmallWithoutMaxAvgPvLists(int threshold,
			int maxIntervalsToRemove) {
		List<PvIntervalList> intervalListsToRemove = new ArrayList<PvIntervalList>();
		for (PvIntervalList pvIntervalList : _listOfPvIntervals) {
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
		_listOfPvIntervals.removeAll(intervalListsToRemove);
	}

	public Map<TimeInstant, List<String>> getAggregatedGapsForRange() {
		Map<TimeInstant, List<String>> gapsMap = prepareGapsMap(_listOfPvIntervals);
		for (PvIntervalList pvIntervalList : _listOfPvIntervals) {
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
		Map<TimeInstant, List<String>> gaps = new HashMap<TimeInstant, List<String>>();
		if (listOfPvIntervals.size() > 0) {
			PvIntervalList intervalList = listOfPvIntervals.get(0);
			if (intervalList != null) {
				for (Interval interval : intervalList.getIntervalList()) {
					List<String> list = new ArrayList<String>();
					gaps.put(interval.getStart(), list);
				}
			}
		}
		return gaps;
	}
}
