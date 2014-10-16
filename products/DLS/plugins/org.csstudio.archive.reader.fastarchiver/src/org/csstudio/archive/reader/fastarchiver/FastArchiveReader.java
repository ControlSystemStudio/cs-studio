package org.csstudio.archive.reader.fastarchiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAArchivedDataRequest;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.epics.util.time.Timestamp;

/**
 * Archive Reader to fetch data from the FA Archiver.
 * 
 * @author FJohlinger
 */
public class FastArchiveReader implements ArchiveReader {

	private final String url;
	private final int version = 1;
	private final String description;
	private HashMap<String, int[]> mapping;

	/**
	 * Connect to the Fast Archiver
	 * 
	 * @param url
	 *            String must start with "fads://" followed by the host and
	 *            optionally a colon and a port. Default port is 8888.
	 * @throws IOException if the connection to the archiver fails
	 * @throws FADataNotAvailableException
	 *             for an invalid URL
	 */
	public FastArchiveReader(String url) throws IOException,
			FADataNotAvailableException {
		this.url = url;
		description = createDescription();
		this.mapping = new FAInfoRequest(url).fetchMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServerName() {
		try {
			return new FAInfoRequest(url).getName();
		} catch (IOException | FADataNotAvailableException e) {
			e.printStackTrace();
			return "Could not connect to server";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getURL() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVersion() {
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArchiveInfo[] getArchiveInfos() {
		int numOfArchives = 1;
		ArchiveInfo[] archiveInfo = new ArchiveInfo[numOfArchives];
		archiveInfo[0] = new ArchiveInfo(getServerName(),
				"Fast Archiver of DLS", 1);
		return archiveInfo;
	}

	/** {@inheritDoc} */
	@Override
	public String[] getNamesByPattern(int key, String glob_pattern) {
		return getNamesByRegExp(key,
				RegExHelper.fullRegexFromGlob(glob_pattern));
	}

	/** {@inheritDoc} */
	// ignores key
	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) {
		TreeSet<String> allNames = new TreeSet<String>(mapping.keySet());

		// find matching names
		List<String> matches = new ArrayList<String>();
		for (String name : allNames) {
			if (Pattern.matches(reg_exp.toLowerCase(), name.toLowerCase()))
				matches.add(name);
		}
		return matches.toArray(new String[matches.size()]);// matching names;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 * @throws FADataNotAvailableException
	 */
	@Override
	public ValueIterator getRawValues(int key, String name, Timestamp start,
			Timestamp end) throws IOException, FADataNotAvailableException {
		FAArchivedDataRequest faDataRequest = new FAArchivedDataRequest(url,
				mapping);
		return faDataRequest.getRawValues(name, start, end);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 * @throws FADataNotAvailableException
	 */
	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			Timestamp start, Timestamp end, int count) throws IOException,
			FADataNotAvailableException {
		FAArchivedDataRequest faDataRequest = new FAArchivedDataRequest(url,
				mapping);
		return faDataRequest.getOptimisedValues(name, start, end, count);
	}

	/** {@inheritDoc} */
	@Override
	public void cancel() {
		// NOP
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// Methods using sockets directly close them after use. Nothing to
		// close.
	}

	/**
	 * Creates a brief description of the ArchiverReader
	 * 
	 * @return description as a String
	 */
	private String createDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("ArchiveReader to communicate with the Fast Archiver.\n");
		return sb.toString();

	}
}
