package org.csstudio.archive.reader.monica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.askap.utility.AskapHelper;
import org.epics.util.time.Timestamp;

import atnf.atoms.mon.PointData;
import atnf.atoms.mon.PointDescription;
import atnf.atoms.mon.comms.MoniCAClientIce;
import atnf.atoms.time.AbsTime;

public class MonicaArchiveReader implements ArchiveReader {
	private static final Logger logger = Logger.getLogger(MonicaArchiveReader.class.getName());
	
	String url;
	String adaptorName;
	private MoniCAClientIce monicaIceClient;
	private Map<String, String> pvToMonicaNameMap = new HashMap<String, String>();
	

	public MonicaArchiveReader(String url, String adaptorName, MoniCAClientIce monicaIceClient) {
		this.url = url;
		this.adaptorName = adaptorName;
		this.monicaIceClient = monicaIceClient;
		
		try {
			Vector<PointDescription> allpoints = monicaIceClient
					.getPoints(new Vector<String>(Arrays.asList(monicaIceClient
							.getAllPointNames())));

			// Make the mapping between EPICS PV's and MoniCA names
			for (PointDescription point : allpoints) {
				String[] thistrans = point.getInputTransactionsAsStrings()[0].split("-");
				if (thistrans.length == 0)
					continue;
				if (thistrans[0].indexOf("EPICS") != -1) {
					// This is an EPICS point
					String pv = thistrans[1].replace('\"', ' ').trim();
					// Some points use $1 to mean 'my source name'
					pv = pv.replaceAll("\\$1", point.getSource());
					String monicaName = point.getFullName();
					
					// put upper case of PV names into map for easier searching
//					System.out.println(pv.toUpperCase() + " : " + monicaName);
					pvToMonicaNameMap.put(pv.toUpperCase(), monicaName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getServerName() {
		return "Monica";
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public String getDescription() {
        return "Monica Archive V" + getVersion() + "\n" +
        		"url: " + url + "\n" +
        		"adaptorName: " + adaptorName;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos() {
        return new ArchiveInfo[]
        {
            new ArchiveInfo("monica", adaptorName, 2)
        };
	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern)
			throws Exception {		
		ArrayList<String> matchingNames = new ArrayList<String>();
		
		for (String pointName : pvToMonicaNameMap.keySet()) {
			if (pointName.contains(glob_pattern.toUpperCase())) {
				matchingNames.add(pointName);
			}
		}
		
		return matchingNames.toArray(new String[]{});
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		ArrayList<String> matchingNames = new ArrayList<String>();
		
		for (String pointName : pvToMonicaNameMap.keySet()) {
			if (pointName.matches(reg_exp)) {
				matchingNames.add(pointName);
			}
		}
		
		return matchingNames.toArray(new String[]{});
	}

	@Override
	public ValueIterator getRawValues(int key, String name, Timestamp start,
			Timestamp end) throws UnknownChannelException, Exception {
		
		String monicaName = pvToMonicaNameMap.get(name.toUpperCase());
		logger.log(Level.INFO, "Get Raw values for pv: " + name + " monicaName: " + monicaName 
				+ " for time interval:" + AskapHelper.getFormatedData(start.toDate(), null) + " to " 
				+ AskapHelper.getFormatedData(end.toDate(), null));
		
		AbsTime startTime = AbsTime.factory(start.toDate());
		AbsTime endTime = AbsTime.factory(end.toDate());
		
		Vector<PointData> monicaValues = monicaIceClient.getArchiveData(monicaName, startTime, endTime);
		
		logger.log(Level.INFO, "Got monica values: " + monicaValues.size());
		
		return new MonicaValueIterator(monicaValues);

	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			Timestamp start, Timestamp end, int count)
			throws UnknownChannelException, Exception {
		
		// TODO: I'm not sure how optimizedValues work, so I'm just going to return normal values
		
		return getRawValues(key, name, start, end);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		logger.log(Level.INFO, "Close MonicaArchiveReader for " + adaptorName);
	}

}
