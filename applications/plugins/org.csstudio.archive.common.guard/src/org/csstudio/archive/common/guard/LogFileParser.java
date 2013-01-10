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

public class LogFileParser {
	
	public Map<String, PvIntervalList> readLogFile(String filePath) {
		List<String> logContentByLines = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(
					filePath);
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

	private Map<String, PvIntervalList> parseLines(
			List<String> logContentByLines) {
		TimeInstant start = null;
		TimeInstant end = null;
		Map<String, PvIntervalList> mapOfPvIntervals = new HashMap<String, PvIntervalList>();
		String[] strings;
		for (String line : logContentByLines) {
			if (line.contains("##")) {
				strings = line.split("##");
				if (strings.length == 3) {
					start = TimeInstantBuilder.fromNanos(Long
							.parseLong(strings[1]));
					end = TimeInstantBuilder.fromNanos(Long
							.parseLong(strings[2]));
				}
			}
			if (line.contains(">>")) {
				strings = line.split(">>");
				if (strings.length == 3) {
					Interval interval = new Interval(start, end,
							Integer.parseInt(strings[2]));
					PvIntervalList pvIntervalList = mapOfPvIntervals
							.get(strings[1]);
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
