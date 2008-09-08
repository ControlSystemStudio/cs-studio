package org.csstudio.nams.common.fachwert;

public enum MessageKeyEnum {
	TYPE("TYPE"), EVENTTIME("EVENTTIME"), TEXT("TEXT"), USER("USER"), HOST(
			"HOST"), APPLICATION_ID("APPLICATION-ID"), PROCESS_ID("PROCESS-ID"), NAME(
			"NAME"), CLASS("CLASS"), DOMAIN("DOMAIN"), FACILITY("FACILITY"), LOCATION(
			"LOCATION"), SEVERITY("SEVERITY"), STATUS("STATUS"), VALUE("VALUE"), DESTINATION(
			"DESTINATION"), AMS_REINSERTED("AMS-REINSERTED"), MSGPROP_COMMAND(
			"COMMAND"), AMS_FILTERID("AMS-FILTERID"),
			
			STATUS_OLD("STATUS-OLD"),
			HOST_PHYS("HOST-PHYS"),
			SEVERITY_OLD("SEVERITY-OLD"),
			APPLICATION("APPLICATION"),
			CREATETIME("CREATETIME"),
			
			OVERWRITES("OVERWRITES"),
			SEVERITY_MAX("SEVERITY-MAX");

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

	MessageKeyEnum(final String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return this.stringValue;
	}
}
