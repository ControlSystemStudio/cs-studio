package org.csstudio.dct;

import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Record function for archiving records.
 * 
 * FIXME: svw: Replace the dummy implementation with real stuff!
 * 
 * @author Sven Wende
 * 
 */
public final class ArchiveRecordsFunction implements IRecordFunction {
	private static final String ATTR_ARCHIVE = "archive";

	/**
	 *{@inheritDoc}
	 */
	public void run(IRecord record, Map<String, String> attributes) {
		if (Boolean.parseBoolean(attributes.get(ATTR_ARCHIVE))) {
			CentralLogger.getInstance().info(null, "Archiving Record [" + AliasResolutionUtil.getEpicsNameFromHierarchy(record) + "]");
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public Map<String, String> getAttributes() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put(ATTR_ARCHIVE, "true");
		result.put("FUKC", "true");
		return result;
	}

}
