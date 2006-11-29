package org.csstudio.platform.libs.epics.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	
	public static String[] constants = new String[]{
			"addr_list",
			"auto_addr_list",
			"conn_tmo",
			"beacon_period",
			"reapeter_port",
			"server_port",
			"max_array_bytes"
	};
	
	public static String[] defaults = new String[]{
			"",
			"true",
			"30.0",
			"15.0",
			"5065",
			"5064",
			"16384"
	};

}
