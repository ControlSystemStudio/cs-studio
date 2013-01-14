package org.csstudio.alarm.table.dataModel;

import javax.annotation.Nonnull;

import org.csstudio.alarm.table.preferences.ISeverityMapping;

/**
 * BasicMessage is dependent on JmsLogsPlugin to retrieve the SeverityMapping.
 * To get rid of this dependency to ease testing, this registry provides
 * configurable access to the SeverityMapping.
 * 
 * This is not intended to be around very long.
 * However, if it is going to be extended, one should consider building a real singleton.
 * 
 * @author jpenning
 * @author $Author: bknerr $
 * @since 04.10.2010
 */
public final class SeverityRegistry {

	private static ISeverityMapping SEVERITY_MAPPING;

	private SeverityRegistry() {
		// Utility class without constructor
	}

	public static void setSeverityMapping(
			@Nonnull ISeverityMapping severityMapping) {
		SEVERITY_MAPPING = severityMapping;
	}

	@Nonnull
	public static ISeverityMapping getSeverityMapping() {
		assert SEVERITY_MAPPING != null : "The Severity mapping must not be null";
		return SEVERITY_MAPPING;
	}
}
