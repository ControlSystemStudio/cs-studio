package org.csstudio.archive.common.guard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class PvManagerMySqlComparator {

	private ConnectionHandler _connectionHandler;
	private final String _logFilePath;
	private Map<String, PvIntervalList> _pvIntervalMap;

	public PvManagerMySqlComparator(ConnectionHandler connectionHandler,
			String logFilePath) {
		_connectionHandler = connectionHandler;
		_logFilePath = logFilePath;
	}

	public void getSampleCountFromLogFile() {
		LogFileParser parser = new LogFileParser();
		_pvIntervalMap = parser.readLogFile(_logFilePath);
	}

	private PreparedStatement createCountSamplesInRangeStatementWithChannelName(
			Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("select count(*) from "
				+ Settings.DATABASE + ".sample, " + Settings.DATABASE
				+ ".channel " + "where channel.name = ? "
				+ "and channel.id = sample.channel_id "
				+ "and sample.time between ? and ?");
		return stmt;
	}

	public void getSampleCountFromMySqlForLogFileIntervals() {
		Set<String> channelNames = _pvIntervalMap.keySet();
		int counter = 0;
		try {
			Connection con = _connectionHandler.getConnection();
			for (String channelName : channelNames) {
				counter++;
				if (counter > 10) {
					break;
				}
				PvIntervalList pvIntervalList = _pvIntervalMap.get(channelName);
				for (Interval interval : pvIntervalList.getIntervalList()) {
					PreparedStatement stmtSamples = createCountSamplesInRangeStatementWithChannelName(con);
					stmtSamples.setString(1, channelName);
					stmtSamples.setLong(2, interval.getStart().getNanos());
					stmtSamples.setLong(3, interval.getEnd().getNanos());
					ResultSet resultSamples = stmtSamples.executeQuery();
					resultSamples.next();
					interval.setSampleInDbCount(resultSamples.getInt(1));
					resultSamples.close();
				}
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkDifferencePvManagerDb() {
		Set<String> keySet = _pvIntervalMap.keySet();
		for (String channelName : keySet) {
			PvIntervalList pvIntervalList = _pvIntervalMap.get(channelName);
			for (Interval interval : pvIntervalList.getIntervalList()) {
				if (interval.isDifferenceValid()
						&& interval.getSampleDifferencePvManagerDb() != 0) {
					System.out.println("Difference! channel " + channelName
							+ " start: " + interval.getStart() + " end: "
							+ interval.getEnd() + " db: "
							+ interval.getSamplesInDb() + " pvM: "
							+ interval.getSampleCount());
				}
			}
		}
	}

}
