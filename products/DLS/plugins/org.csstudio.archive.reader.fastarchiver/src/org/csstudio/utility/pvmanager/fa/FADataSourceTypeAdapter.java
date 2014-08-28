package org.csstudio.utility.pvmanager.fa;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FALiveDataRequest;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.pvmanager.DataSourceTypeAdapter;
import org.epics.pvmanager.ValueCache;

/**
 * Matches and fills a cache with the data from the FA Archiver.
 * 
 * @author FJohlinger
 */
public class FADataSourceTypeAdapter implements
		DataSourceTypeAdapter<FALiveDataRequest, ArchiveVDisplayType> {

	/**
	 * FALiveDataRequest always returns values as ArchiveVDisplayTypes. Hence
	 * match() checks whether the type in the ValueCache can store these.
	 * 
	 * @return 0 if ArchiveVDisplayType cannot be stored in ValueCache, 1 if it
	 *         can.
	 */
	@Override
	public int match(ValueCache<?> cache, FALiveDataRequest connection) {
		if (cache.getType().isAssignableFrom(ArchiveVDisplayType.class))
			return 1;
		return 0;
	}

	/**
	 * Not implemented, throws an UnsupportedOperationException when to accessed
	 * during runtime
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object getSubscriptionParameter(ValueCache cache,
			FALiveDataRequest connection) {
		throw new UnsupportedOperationException(
				"Not implemented: FAChannelHandler is multiplexed, will not use this method");
	}

	/**
	 * Updates the given cache with the given message. Checks whether the
	 * message is valid using match(cache, connection).
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean updateCache(ValueCache cache, FALiveDataRequest connection,
			ArchiveVDisplayType message) {
		if (message == null || match(cache, connection) == 0)
			return false;
		cache.writeValue(message);
		return true;
	}

}
