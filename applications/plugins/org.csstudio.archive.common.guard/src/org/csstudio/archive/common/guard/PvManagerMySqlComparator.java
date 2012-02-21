package org.csstudio.archive.common.guard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import org.csstudio.domain.desy.time.TimeInstant;

public class PvManagerMySqlComparator {

	private ConnectionHandler _connectionHandler;

	public PvManagerMySqlComparator(ConnectionHandler connectionHandler) {
		_connectionHandler = connectionHandler;
	}

	public Map<String, PvIntervalList> checkForLostSamples(
			Map<String, PvIntervalList> pvIntervalMap) {
		Set<String> channelNames = pvIntervalMap.keySet();
		int counter = 0;
		for (String channelName : channelNames) {
			counter++;
			if (counter > 10) {
				break;
			}
			PvIntervalList pvIntervalList = pvIntervalMap.get(channelName);
			for (Interval interval : pvIntervalList.getIntervalList()) {
				int samplesInDb = 0;
				try {
					samplesInDb = retrieveSampleCountFromDb(channelName, interval.getStart(), interval.getEnd());
				} catch (Exception e) {
					e.printStackTrace();
				}
				interval.setSampleInDbCount(samplesInDb);
			}
		}
		return pvIntervalMap;
		
	}

	private int retrieveSampleCountFromDb(String channelName,
			TimeInstant start, TimeInstant end) throws Exception {
		Connection con = _connectionHandler.getConnection();
		PreparedStatement stmtSamples = createCountSamplesInRangeStatementWithChannelName(con);
        stmtSamples.setString(1, channelName);
        stmtSamples.setLong(2, start.getNanos());
        stmtSamples.setLong(3, end.getNanos());
        ResultSet resultSamples = stmtSamples.executeQuery();
        resultSamples.next();
        return resultSamples.getInt(1);
	}

	private PreparedStatement createCountSamplesInRangeStatementWithChannelName(
			Connection con) throws SQLException {
		PreparedStatement stmt = con
				.prepareStatement("select count(*) from " + Settings.DATABASE + ".sample, " + Settings.DATABASE + ".channel "
						+ "where channel.name = ? "
						+ "and channel.id = sample.channel_id "
						+ "and sample.time between ? and ?");
		return stmt;
	}
}
