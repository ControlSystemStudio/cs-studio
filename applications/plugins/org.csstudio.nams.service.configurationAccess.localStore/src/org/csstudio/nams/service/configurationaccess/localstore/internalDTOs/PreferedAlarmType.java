
/**
 * 
 */

package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

public enum PreferedAlarmType {
	NONE, SMS, VOICE, EMAIL;

	public static PreferedAlarmType getValueForId(final int id) {
		// FIXME probably false mapping
		switch (id) {
		case 0:
			return NONE;
		case 1:
			return SMS;
		case 2:
			return VOICE;
		case 3:
			return EMAIL;
		default:
			throw new IllegalArgumentException(
					"Unsupported PreferedAlarmType id: " + id);
		}
	}
}