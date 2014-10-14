package org.csstudio.utility.pvmanager.fa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.trends.databrowser2.preferences.ArchiveServerURL;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;

/**
 * Provides FAChannelHandlers to connect to the live stream of the FA Archiver.
 * 
 * @author FJohlinger
 */
public class FADataSource extends DataSource {
	// Maps ChannelNames to URLs
	private static HashMap<String, String> availablePVs;
	// Maps ChannelNames to BPM number and coordinate
	private static HashMap<String, int[]> mappingNameToBPMs;
	private static String prefix = "fa://";
	private static String defaultUrl = "fads://fa-archiver:8888";

	static {
		availablePVs = new HashMap<String, String>();
		mappingNameToBPMs = new HashMap<String, int[]>();
		List<String> urls = new ArrayList<String>();
		try {
			ArchiveServerURL[] archiveURLs = Preferences.getArchiveServerURLs();
			for (ArchiveServerURL archiveURL : archiveURLs) {
				String url = archiveURL.getURL();
				String prefix = url.substring(0, url.indexOf(':'));
				if (prefix.equals("fads"))
					urls.add(url);
			}
		} catch (NullPointerException e) {
		} finally {
			if (urls.size() == 0)
				urls.add(defaultUrl);
		}

		HashMap<String, int[]> mapping;
		for (String url : urls) {
			try {
				mapping = new FAInfoRequest(url).fetchMapping();
				mappingNameToBPMs.putAll(mapping);
				for (String pvName : mapping.keySet())
					availablePVs.put(pvName, url);
			} catch (IOException | FADataNotAvailableException e) {
				// invalid URL, cannot put into HashMap
			}
		}
	}

	/**
	 * Default constructor, sets writable to false, as the live stream of the FA
	 * Archiver is never writable.
	 */
	public FADataSource() {
		super(false);
	}

	/** Creates a channel handler for the given name. In the simplest case, this is the only method a data source needs to implement.
	 * @param channelName the name for a new channel
	 * @return a new handler
	 */
	@Override
	protected ChannelHandler createChannel(String channelName) {
		if (availablePVs.containsKey(prefix + channelName))
			return new FAChannelHandler(channelName, availablePVs.get(prefix
					+ channelName), mappingNameToBPMs.get(prefix + channelName));
		return null;
	}

	/**
	 * Returns the lookup name to use to find the channel handler in the cache.
	 * 
	 * @param channelName the channel name
	 * @return the channel handler to look up in the cache
	 */
	@Override
	protected String channelHandlerLookupName(String channelName) {
		return channelName;
	}

}
