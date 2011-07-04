
/**
 * 
 */

package org.csstudio.nams.common.fachwert;

/* 1 - User, 2 - UserGroup, 3 - FilterCond, 4 - Filter, 5 - Topic */
public enum RubrikTypeEnum {
	USER(1), USER_GROUP(2), FILTER_COND(3), FILTER(4), TOPIC(5);

	public static RubrikTypeEnum valueOf(final short value) {
		switch (value) {
		case 1:
			return USER;
		case 2:
			return USER_GROUP;
		case 3:
			return FILTER_COND;
		case 4:
			return FILTER;
		case 5:
			return TOPIC;
		default:
			throw new IllegalArgumentException();
		}
	}

	private final short ordinal;

	private RubrikTypeEnum(final int o) {
		this.ordinal = (short) o;
	}

	public short getShortFor() {
		return this.ordinal;
	}
}
