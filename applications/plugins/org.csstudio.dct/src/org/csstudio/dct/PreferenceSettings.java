package org.csstudio.dct;

public enum PreferenceSettings {
	FIELD_DESCRIPTION_SHOW_DESCRIPTION("show field description"),
	FIELD_DESCRIPTION_SHOW_INITIAL_VALUE("show initial value"),
	
	DATALINK_FUNCTION_PARAMETER_3_PROPOSAL("datalink() function, auto completion proposal for parameter 3"),
	DATALINK_FUNCTION_PARAMETER_4_PROPOSAL("datalink() function, auto completion proposal for parameter 4");
	
	private String label;
	
	private PreferenceSettings(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
}
