package org.csstudio.archive.common.guard;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

public class ArchiveMonitor {

	private ConnectionHandler _connectionHandler;
	private ArchiveGuard _guard;

	public ArchiveMonitor() {
		_connectionHandler = new ConnectionHandler();
		_guard = new ArchiveGuard(_connectionHandler);
	}

	public void checkArchiveDbForGaps() {
		List<PvIntervalList> listOfPvIntervals = new ArrayList<>();
		listOfPvIntervals = _guard.checkForGaps(listOfPvIntervals);
		_connectionHandler.close();
		Evaluater eval = new Evaluater();
		listOfPvIntervals = eval.removeEmptyPvLists(listOfPvIntervals);
		listOfPvIntervals = eval.removeSmallAvgPvLists(listOfPvIntervals, 20);
		listOfPvIntervals = eval.removeSmallWithoutMaxAvgPvLists(
				listOfPvIntervals, 20, 40);
		// eval.printAverageAndVariance(listOfPvIntervals);
		// eval.printLostHourSamples(listOfPvIntervals);
		// eval.printIntervals(listOfPvIntervals);
		Map<TimeInstant, List<String>> aggregateGapsForRange = eval
				.aggregateGapsForRange(listOfPvIntervals);
		eval.printAggregatedGaps(aggregateGapsForRange);
	}

	public void compareArchiveDbWithLog() {
		Map<String, PvIntervalList> pvIntervalMap = readLogFile();
		PvManagerMySqlComparator comparator = new PvManagerMySqlComparator(_connectionHandler);
		pvIntervalMap = comparator.checkForLostSamples(pvIntervalMap);
		pvIntervalMap = _guard.checkForLostSamples(pvIntervalMap);
		Evaluater eval = new Evaluater();
		eval.checkDifferencePvManagerDb(pvIntervalMap);
	}

	private Map<String, PvIntervalList> readLogFile() {
		List<String> logContentByLines = new ArrayList<>();
		try {
			FileInputStream fstream = new FileInputStream("D://eclipse//e370//rpSF//cs-studio//applications//plugins//org.csstudio.archive.common.guard//count.log");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				logContentByLines.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return parseLines(logContentByLines);
	}

	private Map<String, PvIntervalList> parseLines(List<String> logContentByLines) {
		TimeInstant start = null; 
		TimeInstant end = null;
		Map<String, PvIntervalList> mapOfPvIntervals = new HashMap<>();
		String[] strings;
		for (String line : logContentByLines) {
			if (line.contains("##")) {
				strings = line.split("##");
				if (strings.length == 3) {
					start = TimeInstantBuilder.fromNanos(Long.parseLong(strings[1]));
					end = TimeInstantBuilder.fromNanos(Long.parseLong(strings[2]));
				}
			}
			if (line.contains(">>")) {
				strings = line.split(">>");
				if (strings.length == 3) {
					Interval interval = new Interval(start, end, Integer.parseInt(strings[2]));
					PvIntervalList pvIntervalList = mapOfPvIntervals.get(strings[1]);
					if (pvIntervalList == null) {
						pvIntervalList = new PvIntervalList(strings[1]);
						pvIntervalList.addInterval(interval);
						mapOfPvIntervals.put(strings[1], pvIntervalList);
					}
				}
			}
			
		}
		return mapOfPvIntervals;
	}
}
