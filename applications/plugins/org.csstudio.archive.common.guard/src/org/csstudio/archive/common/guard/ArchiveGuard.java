package org.csstudio.archive.common.guard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

public class ArchiveGuard {

	private ConnectionHandler _connectionHandler;
	private int rangeCount = 0;

	public ArchiveGuard(ConnectionHandler connectionHandler) {
		_connectionHandler = connectionHandler;
	}


	public List<PvIntervalList> checkForLostSamples(List<PvIntervalList> listOfPvIntervals) {
		Map<String, String> channelSet = null;
		try {
			channelSet = getAllConnectedSamples();
//			channelSet = new HashMap<>();
//			channelSet.put("3562", "HeVol_Halle3_calc.VAL");
		System.out.println("-------size: " + channelSet.size());
		TimeInstant start =   TimeInstantBuilder.fromSeconds(Settings.RANGE_START_IN_SECONDS);
		TimeInstant end = TimeInstantBuilder.fromSeconds(Settings.RANGE_END_IN_SECONDS);
		Set<String> channelIds = channelSet.keySet();
		for (String channelId : channelIds) {
			PvIntervalList pvIntervals = new PvIntervalList(channelSet.get(channelId));
			pvIntervals = getGapsInTimeRange(channelId, start, end, pvIntervals);
			listOfPvIntervals.add(pvIntervals);
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfPvIntervals;
	}

	private PvIntervalList getGapsInTimeRange(String channelId, TimeInstant start,
			TimeInstant end, PvIntervalList pvIntervals) throws Exception {
		Connection con = _connectionHandler.getConnection();
		TimeInstant rangeStart = TimeInstantBuilder.fromNanos(end.getNanos() - (long) Settings.RANGE_IN_NANO);
		TimeInstant rangeEnd = end;
		PreparedStatement stmtSamples = createCountSamplesInRangeStatement(con);
		PreparedStatement stmtHourSamples = createCountHourSamplesInRangeStatement(con);
		rangeCount = 0;
		while(rangeStart.getNanos() > start.getNanos()) {
	        stmtSamples.setInt(1, Integer.valueOf(channelId));
	        stmtSamples.setLong(2, rangeStart.getNanos());
	        stmtSamples.setLong(3, rangeEnd.getNanos());
	        stmtHourSamples.setInt(1, Integer.valueOf(channelId));
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
		return pvIntervals;
	}


	private Map<String, String> getAllConnectedSamples() throws Exception {
		Map<String, String> channelSet = new HashMap<String, String>();
		Connection con = _connectionHandler.getConnection();
		PreparedStatement stm = createReadAllChannelsStatement(con);
		ResultSet resultSet = stm.executeQuery();
		while (resultSet.next()) {
			String channelName = resultSet.getString(2);
			if (channelName.contains("VAL")) {
				channelSet.put(resultSet.getString(1), channelName);
			}
		}
		return channelSet;
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
}
