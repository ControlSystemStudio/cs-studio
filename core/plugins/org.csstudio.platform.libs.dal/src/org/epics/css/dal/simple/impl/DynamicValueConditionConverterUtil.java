package org.epics.css.dal.simple.impl;

import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.Timestamp;

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
	public static final Timestamp extractTimestampInfo(DynamicValueCondition cond) {
		return cond.getTimestamp();
	}
	
	/**
	 * Extracts EPICS favored status info string for a DAL <code>DynamicValueCondition</code>.
	 * @param cond DAL <code>DynamicValueCondition</code>
	 * @return EPICS favored status info string for DAL condition
	 */
	public static final String extractStatusInfo(DynamicValueCondition cond) {
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
	public static final String extractSeverityInfo(DynamicValueCondition condition) {
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
