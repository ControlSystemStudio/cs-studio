package org.csstudio.archive.reader.appliance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.epics.archiverappliance.retrieval.client.DataRetrieval;
import org.epics.archiverappliance.retrieval.client.RawDataRetrieval;
import org.epics.util.time.Timestamp;

/**
 * Appliance archive reader which reads data from EPICS archiver appliance.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class ApplianceArchiveReader implements ArchiveReader {
	
	private final String httpURL;
	private final String pbrawURL;
		
	/**
	 * Constructor that sets appliance archiver reader url.
	 * 
	 * @param url, appliance archiver reader url (with specific prefix)
	 */
	public ApplianceArchiveReader(String url) {
		//if the url ends with /, strip the url of the last character
		if (url.charAt(url.length()-1) == '/') {
			url = url.substring(0,url.length()-1);
		}
		this.pbrawURL = url;
		this.httpURL = pbrawURL.replace("pbraw://", "http://");
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getServerName()
	 */
	@Override
	public String getServerName() {
		return ApplianceArchiveReaderConstants.ARCHIVER_NAME;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getURL()
	 */
	@Override
	public String getURL() {
		return pbrawURL;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getDescription()
	 */
	@Override
	public String getDescription() {
		StringBuilder description = new StringBuilder();
		description.append("Archiver appliance v ");
		description.append(getVersion()); 
		description.append('\n');
		description.append("Server url: ");
		description.append(pbrawURL);
		description.append('\n');
		return description.toString();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getVersion()
	 */
	@Override
	public int getVersion() {
		return ApplianceArchiveReaderConstants.VERSION;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getArchiveInfos()
	 */
	@Override
	public ArchiveInfo[] getArchiveInfos() {
		return new ArchiveInfo[] { new ArchiveInfo(
				ApplianceArchiveReaderConstants.ARCHIVER_NAME, 
				ApplianceArchiveReaderConstants.ARCHIVER_DESCRIPTION, 
				1) 
		};
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getNamesByPattern(int, java.lang.String)
	 */
	@Override
	public String[] getNamesByPattern(int key, String glob_pattern) throws Exception {
		return getNamesByRegExp(key, RegExHelper.fullRegexFromGlob(glob_pattern));
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getNamesByRegExp(int, java.lang.String)
	 */
	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		return search(reg_exp);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getRawValues(int, java.lang.String, org.epics.util.time.Timestamp, org.epics.util.time.Timestamp)
	 */
	@Override
	public ValueIterator getRawValues(int key, String name, Timestamp start, Timestamp end) throws UnknownChannelException, Exception {
		try {
			return new ApplianceRawValueIterator(this, name, start, end);
		} catch (ArchiverApplianceException ex) {
			throw new UnknownChannelException(name);
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#getOptimizedValues(int, java.lang.String, org.epics.util.time.Timestamp, org.epics.util.time.Timestamp, int)
	 */
	@Override
	public ValueIterator getOptimizedValues(int key, String name, Timestamp start, Timestamp end, int count) throws UnknownChannelException, Exception {
		try {
			return new ApplianceOptimizedValueIterator(this, name, start, end, count, Activator.getDefault().isUseStatistics());
		} catch (ArchiverApplianceException e) {
			try {
				return new ApplianceRawValueIterator(this, name, start, end);
			} catch (ArchiverApplianceException ex) {
				throw new UnknownChannelException(name);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#cancel()
	 */
	@Override
	public void cancel() {}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReader#close()
	 */
	@Override
	public void close() {}
	
	/**
	 * Creates and returns DataRetrieval 
	 * 
	 * @param dataRetrievalURL
	 * @return DataRetrieval instance
	 */
	public DataRetrieval createDataRetriveal(String dataRetrievalURL) {
		return new RawDataRetrieval(dataRetrievalURL);
	}
	
	/**
	 * Returns data retrieval URL. A data retrieval URL looks like
	 * http://domain:port/retrieval/data/getData.raw where /data/getData is
	 * fixed and .raw identifies the MIME-type of the returned data.
	 * 
	 * @return Data retrieval URL.
	 */
	public String getDataRetrievalURL() {
		return httpURL + ApplianceArchiveReaderConstants.RETRIEVAL_PATH;
	}
	
	/**
	 * Search for PV names that match to the specified regular expression.
	 * 
	 * @param reg, regular expression
	 * @return Array with PV names that match to the given regular expression.
	 * @throws IOException 
	 */
	private String[] search(String reg) throws IOException {
		String searchURL = httpURL + ApplianceArchiveReaderConstants.SEARCH_PATH + URLEncoder.encode(reg, "UTF-8");
		URL url = new URL(searchURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		ArrayList<String> names = new ArrayList<String>();
		BufferedReader br = null;
		try {
			connection.connect();
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String pvName = null;
				while ((pvName = br.readLine()) != null) {
					names.add(pvName);
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
			connection.disconnect();
		}
		return names.toArray(new String[names.size()]);
	}
}