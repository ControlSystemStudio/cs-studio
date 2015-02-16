package org.csstudio.dal.simple.impl;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.Timestamp;

/**
 * Utility methods for accessing characteristics and dealing with DAL
 * properties.
 *
 * @author Sven Wende
 *
 */
public class DynamicValueConditionConverterUtil {

	/**
	 * Extracts EPICS favored <code>Timestamp</code> info for a DAL <code>DynamicValueCondition</code>.
	 * @param cond DAL <code>DynamicValueCondition</code>
	 * @return EPICS favored <code>Timestamp</code> info for DAL condition
	 */
	public static final Timestamp extractTimestampInfo(final DynamicValueCondition cond) {
		return cond.getTimestamp();
	}

	/**
	 * Extracts EPICS favored status info string for a DAL <code>DynamicValueCondition</code>.
	 * @param cond DAL <code>DynamicValueCondition</code>
	 * @return EPICS favored status info string for DAL condition
	 */
	public static final String extractStatusInfo(final DynamicValueCondition cond) {
		if (cond == null || cond.getDescription() == null) {
			return "N/A";
		}
		return cond.getDescription();
	}

	/**
	 * Extracts EPICS favored severity info string for a DAL <code>DynamicValueCondition</code>.
	 * @param cond DAL <code>DynamicValueCondition</code>
	 * @return EPICS favored severity info string for DAL condition
	 */
	public static final String extractSeverityInfo(final DynamicValueCondition condition) {
		if (condition.isNormal()) {
			return DynamicValueState.NORMAL.toString();
		}
		if (condition.isWarning()) {
			return DynamicValueState.WARNING.toString();
		}
		if (condition.isAlarm()) {
			return DynamicValueState.ALARM.toString();
		}
		if (condition.isError()) {
			return DynamicValueState.ERROR.toString();
		}
		return "UNKNOWN";
	}

}
