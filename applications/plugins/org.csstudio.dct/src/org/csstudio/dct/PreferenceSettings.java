package org.csstudio.dct;

/**
 * Collection of all DCT preference identifiers.
 *
 * @author Sven Wende
 *
 */
public enum PreferenceSettings {
    FIELD_DESCRIPTION_SHOW_DESCRIPTION("show description for record fields"),

    FIELD_DESCRIPTION_SHOW_INITIAL_VALUE("show initial value for record fields"),

    DATALINK_FUNCTION_PARAMETER_3_PROPOSAL("datalink() function, auto completion proposal for parameter 3"),

    DATALINK_FUNCTION_PARAMETER_4_PROPOSAL("datalink() function, auto completion proposal for parameter 4"),

    IO_NAME_SERVICE_ID("io name service"),

    SENSOR_ID_SERVICE_ID("sensor id service");

    private String label;

    private PreferenceSettings(String label) {
        this.label = label;
    }

    /**
     * Returns a label for this preference.
     *
     * @return a label for this preference
     */
    public String getLabel() {
        return label;
    }
}
