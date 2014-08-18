package org.csstudio.utility.pvmanager.fa;

import java.io.IOException;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;

public class FADataSource extends DataSource {

	// is called from extension point
	public FADataSource() {
		super(false);
	}

	/** {@inheritDoc} */
	@Override
	protected ChannelHandler createChannel(String channelName) {
		// TODO Need to find a way to find out the url(Add to the ChannelName?)
		String[] urls = new String[] { "fads://fa-archiver",
				"fads://fa-archiver:8889" };
		for (String url : urls) {
			try {
				return new FAChannelHandler(channelName, url);
			} catch (FADataNotAvailableException | IOException e) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Returns the lookup name to use to find the channel handler in the cache.
	 * It removes the coordinate
	 */
	@Override
	protected String channelHandlerLookupName(String channelName) {
		return channelName;
	}

}
