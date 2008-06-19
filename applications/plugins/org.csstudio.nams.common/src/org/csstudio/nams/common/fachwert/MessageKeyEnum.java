package org.csstudio.nams.common.fachwert;

public enum MessageKeyEnum {
	TYPE("TYPE"), EVENTTIME("EVENTTIME"), TEXT("TEXT"), USER("USER"), HOST("HOST"), APPLICATION_ID("APPLICATION-ID"),
	PROCESS_ID("PROCESS-ID"), NAME("NAME"), CLASS("CLASS"), DOMAIN("DOMAIN"), FACILITY("FACILITY"), LOCATION("LOCATION"),
	SEVERITY("SEVERITY"), STATUS("STATUS"), VALUE("VALUE"), DESTINATION("DESTINATION"), AMS_REINSERTED("AMS-REINSERTED"),
	MSGPROP_COMMAND("COMMAND"), AMS_FILTERID("AMS-FILTERID");
	
	private final String stringValue;

	MessageKeyEnum(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public static MessageKeyEnum getEnumFor(String value) {
		for (MessageKeyEnum mke : values()) {
			if (mke.getStringValue().equalsIgnoreCase(value)) {
				return mke;
			}
		}
		return null;
	}
}
