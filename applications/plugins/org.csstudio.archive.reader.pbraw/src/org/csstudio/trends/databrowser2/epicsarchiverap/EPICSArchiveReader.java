package org.csstudio.trends.databrowser2.epicsarchiverap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.archiverappliance.retrieval.client.RawDataRetrieval;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;

/*
 * @author Luofeng Li
 */
public class EPICSArchiveReader implements ArchiveReader {
    final public static String REPLACEsTR = "http";
    private String serverURL;
    private String getRawDataURL;
    private String searchingPVURL;

    public static String parseURL(final String url) throws Exception {

	if (!url.startsWith("pbraw:"))
	    throw new Exception("URL does not start with 'pbraw': " + url);

	return url.replace(EPICSArchiveReaderFactory.PREFIX, REPLACEsTR);
    }

    public EPICSArchiveReader(String serverURL, String getRawDataURL,
	    String searchingPVURL) {
	this.serverURL = serverURL;
	this.getRawDataURL = getRawDataURL;
	this.searchingPVURL = searchingPVURL;
    }

    @Override
    public String getServerName() {

	return serverURL;
    }

    @Override
    public String getURL() {

	return serverURL;
    }

    @Override
    public String getDescription() {

	return "EPICS Archiver Application";
    }

    @Override
    public int getVersion() {

	return 1;
    }

    @Override
    public ArchiveInfo[] getArchiveInfos() {

	return new ArchiveInfo[] { new ArchiveInfo(serverURL,
		"EPICS Archiver Appliaction", 1) };
    }

    @Override
    public String[] getNamesByPattern(int key, String glob_pattern)
	    throws Exception {
	String reg_exp = EPICSArchiveReader.convertGlobToRegular(glob_pattern,
		true);

	return getNamesByRegExp(key, reg_exp);
    }

    @Override
    public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {

	ArrayList<String> pvNamesList = new ArrayList<String>();
	String searchURL = EPICSArchiveReader.parseURL(serverURL)
		+ searchingPVURL + URLEncoder.encode(reg_exp, "UTF-8");
	URL url = new URL(searchURL);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.connect();
	if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	    try (InputStream is = connection.getInputStream();
		    LineNumberReader reader = new LineNumberReader(
			    new InputStreamReader(is))) {
		String pvName = reader.readLine();
		while (pvName != null) {
		    pvNamesList.add(pvName);
		    pvName = reader.readLine();
		}
	    }

	    String[] pvArray = new String[pvNamesList.size()];
	    for (int i = 0; i < pvNamesList.size(); i++) {
		pvArray[i] = pvNamesList.get(i);
	    }
	    return pvArray;
	}
	return null;

    }

    class MyValueIterator implements ValueIterator {
	private GenMsgIterator msgIter = null;
	private Iterator<EpicsMessage> theIter = null;
	public MyValueIterator(GenMsgIterator msgIter) { 
	    this.msgIter = msgIter;
	    this.theIter = msgIter.iterator();
	}

	@Override
	public boolean hasNext() {
	    return theIter.hasNext();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#next()
	 */
	@Override
	public VType next() throws Exception {
	    EpicsMessage dbrevent = theIter.next();
	    return org.epics.vtype.ValueFactory.newVNumber(
		    dbrevent.getNumberValue(),
		    tranformToISeverity(dbrevent.getSeverity()),
		    org.epics.vtype.ValueFactory.newTime(org.epics.util.time.Timestamp
			    .of(dbrevent.getTimestamp().getTime() / 1000,
				    dbrevent.getTimestamp().getNanos())),
		    org.epics.vtype.ValueFactory.displayNone());
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#close()
	 */
	@Override
	public void close() {
	    try {
		msgIter.close();
	    } catch(IOException ex) { 
		ex.printStackTrace();
	    }
	} 
	
    }
    
    @Override
    public ValueIterator getRawValues(int key, String name,
	    org.epics.util.time.Timestamp start,
	    org.epics.util.time.Timestamp end) throws UnknownChannelException,
	    Exception {

	Timestamp start2 = new java.sql.Timestamp(start.getSec()*1000);
	Timestamp end2 = new java.sql.Timestamp(end.getSec()*1000);
	RawDataRetrieval rawDataRetrieval = new RawDataRetrieval(
		EPICSArchiveReader.parseURL(serverURL) + getRawDataURL);
	GenMsgIterator strm = rawDataRetrieval.getDataForPV(name, start2, end2);
	

	if (strm != null) {
		return new MyValueIterator(strm);
	}
	return null;
    }

    @Override
    public ValueIterator getOptimizedValues(int key, String name,
	    org.epics.util.time.Timestamp start,
	    org.epics.util.time.Timestamp end, int count)
	    throws UnknownChannelException, Exception {
	
	return getRawValues(key, "mean_600("+name+")", start, end);
    }

    @Override
    public void cancel() {

    }

    @Override
    public void close() {

    }

    private static Alarm tranformToISeverity(int severity) {
	return org.epics.vtype.ValueFactory.newAlarm(
		AlarmSeverity.values()[severity], "N/a");

    }

    /**
     * Converts the specified glob expression to the equal regular expression.
     * 
     * @param glob
     *            the glob expression
     * @return the regular expression
     */
    private static String convertGlobToRegular(String glob,
	    boolean caseSensitive) {
	StringBuffer reg = new StringBuffer();
	int len = glob.length();

	for (int i = 0; i < len; i++) {
	    switch (glob.charAt(i)) {
	    case '*':
		reg.append(".*");
		;

		break;

	    case '?':
		reg.append(".");

		break;

	    default: {
		if (caseSensitive)
		    reg.append(glob.substring(i, i + 1));
		else {
		    reg.append("[");
		    reg.append(glob.substring(i, i + 1).toLowerCase());
		    reg.append(glob.substring(i, i + 1).toUpperCase());
		    reg.append("]");
		}
	    }
	    }
	}

	return reg.toString();
    }

}
