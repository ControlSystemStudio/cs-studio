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


	public ArchiveGuard(ConnectionHandler connectionHandler) {
		_connectionHandler = connectionHandler;
	}


	public List<SampleGapsForChannel> checkForLostSamples() {
		List<SampleGapsForChannel> listOfChannelGaps = new ArrayList<>();
		Map<String, String> channelSet = null;
		try {
			channelSet = getAllConnectedSamples();
//			channelSet = new HashMap<>();
//			channelSet.put("3562", "HeVol_Halle3_calc.VAL");
		System.out.println("-------size: " + channelSet.size());
		TimeInstant end =   TimeInstantBuilder.fromSeconds(1327937579);
		TimeInstant start = TimeInstantBuilder.fromSeconds(1327923779);
		Set<String> channelIds = channelSet.keySet();
		for (String channelId : channelIds) {
			SampleGapsForChannel gapsInTimeRange = new SampleGapsForChannel((String) channelSet.get(channelId));
			gapsInTimeRange.addGapList(getGapsInTimeRange(channelId, start, end));
			listOfChannelGaps.add(gapsInTimeRange);
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfChannelGaps;
	}

	private List<SampleGap> getGapsInTimeRange(String channelId, TimeInstant start,
			TimeInstant end) throws Exception {
		List<SampleGap> gapList = new ArrayList<>();
		Connection con = _connectionHandler.getConnection();
		TimeInstant rangeStart = TimeInstantBuilder.fromNanos(end.getNanos() - (long) (20*60*1e9));
		TimeInstant rangeEnd = end;
		PreparedStatement stmt = createCountSamplesInRangeStatement(con);
		while(rangeStart.getNanos() > start.getNanos()) {
	        stmt.setInt(1, Integer.valueOf(channelId));
	        stmt.setLong(2, rangeStart.getNanos());
	        stmt.setLong(3, rangeEnd.getNanos());
	        ResultSet result = stmt.executeQuery();
	        result.next();
	        int sum = result.getInt(1);
			if (sum == 0) {
//				System.out.println(channelId + " stamp start: " + rangeStart.getSeconds() + " end: " + 
//						rangeEnd.getSeconds() + " sum: " + sum);
	        	gapList.add(new SampleGap(rangeStart, rangeEnd));
	        }
			result.close();
			result = null;
	        rangeEnd = rangeStart;
	        rangeStart = TimeInstantBuilder.fromNanos(rangeEnd.getNanos() - (long) (20*60*1e9));
		}
		return gapList;
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
						+ "where channel.group_id = '12'; ");
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
	
}
