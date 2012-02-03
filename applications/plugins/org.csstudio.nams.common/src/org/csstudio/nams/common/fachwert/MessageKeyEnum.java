
package org.csstudio.nams.common.fachwert;

public enum MessageKeyEnum {
	
    /**
     * XXX Comment
     */
    ACK("ACK"),
    /**
     * XXX Comment
     */
    ACK_TIME("ACK_TIME"),
    /**
	 * XXX Comment
	 */
	AMS_FILTERID("AMS-FILTERID"),
	/**
	 * XXX Comment
	 */
	AMS_REINSERTED("AMS-REINSERTED"),
	/**
	 * XXX Comment
	 */
	APPLICATION("APPLICATION"),
	/**
	 * XXX Comment
	 */
	APPLICATION_ID("APPLICATION-ID"),
	/**
	 * XXX Comment
	 */
	CLASS("CLASS"),
	/**
	 * XXX Comment
	 */
	CREATETIME("CREATETIME"),
	/**
	 * XXX Comment
	 */
	DESTINATION("DESTINATION"),
	/**
	 * XXX Comment
	 */
	DOMAIN("DOMAIN"),
	/**
	 * XXX Comment
	 */
	EVENTTIME("EVENTTIME"),
	/**
	 * XXX Comment
	 */
	FACILITY("FACILITY"),
    /**
     * XXX Comment
     */
	FILENAME("FILENAME"),
	/**
	 * XXX Comment
	 */
	GROUP("GROUP"),
	/**
	 * XXX Comment
	 */
	HOST("HOST"),
	/**
	 * XXX Comment
	 */
	HOST_PHYS("HOST-PHYS"),
	/**
	 * XXX Comment
	 */
	HOWTO("HOWTO"),
	/**
	 * XXX Comment
	 */
	LOCATION("LOCATION"),
	/**
	 * XXX Comment
	 */
	LOST("LOST"),
    /**
     * XXX Comment
     */
	MSGPROP_COMMAND("COMMAND"),
	/**
	 * XXX Comment
	 */
	NAME("NAME"),
	/**
	 * XXX Comment
	 */
	OVERWRITES("OVERWRITES"),
	/**
	 * XXX Comment
	 */
	PROCESS_ID("PROCESS-ID"),
	/**
	 * XXX Comment
	 */
	SEVERITY("SEVERITY"),
	/**
	 * XXX Comment
	 */
	SEVERITY_MAX("SEVERITY-MAX"),
	/**
	 * XXX Comment
	 */
	SEVERITY_OLD("SEVERITY-OLD"),
	/**
	 * XXX Comment
	 */
	STATUS("STATUS"),
	/**
	 * XXX Comment
	 */

	STATUS_OLD("STATUS-OLD"),
	/**
	 * XXX Comment
	 */
	TEXT("TEXT"),

	/**
	 * XXX Comment
	 */
	TYPE("TYPE"),
	/**
	 * XXX Comment
	 */
	USER("USER"),
	/**
	 * XXX Comment
	 */
	VALUE("VALUE"),
    /**
     * XXX Comment
     */
	VALUE_MAX("VALUE-MAX"),
    /**
     * XXX Comment
     */
	VALUE_MIN("VALUE_MIN"),
    /**
     * XXX Comment
     */
	VALUE_OLD("VALUE-OLD");

	public static MessageKeyEnum getEnumFor(final String value) {
		for (final MessageKeyEnum mke : MessageKeyEnum.values()) {
			if (mke.getStringValue().equalsIgnoreCase(value)) {
				return mke;
			}
		}
		throw new IllegalArgumentException("No value available for " + value);
	}

	/**
	 * Liefert die Stringdarstellung aller Enum-Values.
	 */
	public static String[] valuesAsStringArray() {
		final MessageKeyEnum[] values = MessageKeyEnum.values();
		final String[] result = new String[values.length];
		for (int index = 0; index < values.length; index++) {
			result[index] = values[index].getStringValue();
		}
		return result;
	}

	private final String stringValue;

	MessageKeyEnum(final String value) {
		this.stringValue = value;
	}

	public String getStringValue() {
		return this.stringValue;
	}
}
