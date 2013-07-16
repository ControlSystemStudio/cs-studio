package org.csstudio.askap.sb;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class Preferences {

	public static final String OD_SCHEDULE_BLOCK_NAME = "operatordisplay.filename";
	
	public static final String SB_OBS_VAR_VERSION = "schedulingblock.version_executed";
	public static final String SB_OBS_VAR_START_TIME = "executive.schedulingblock.start_time";
	public static final String SB_OBS_VAR_DURATION = "executive.schedulingblock.duration";
	public static final String SB_OBS_VAR_ERROR_TIME = "schedulingblock.error.time";
	public static final String SB_OBS_VAR_ERROR_MESSAGE = "schedulingblock.error.message";

	public static final String CALIBRATION_SOURCE = "source";
	public static final String CALIBRATION_SOURCE_NAME = CALIBRATION_SOURCE + ".name";
	public static final String CALIBRATION_SOURCE_FRAME = CALIBRATION_SOURCE + ".frame";
	public static final String CALIBRATION_SOURCE_C1 = CALIBRATION_SOURCE + ".c1";
	public static final String CALIBRATION_SOURCE_C2 = CALIBRATION_SOURCE + ".c2";

	static final String SB_TEMPLATE_ICE_NAME = "schedulingblock_sbtemplate_icename";
	static final String SCHEDULING_BLOCK_ICE_NAME = "schedulingblock_sb_icename";
	static final String OBS_PROGRAM_ICE_NAME = "schedulingblock_obsprogram_icename";
	static final String SB_EXECUTION_STATE_POLLING_PERIOD = "schedulingblock_execution_pollingPeriod";		
	static final String SB_MAINTENANCE_POLLING_PERIOD = "schedulingblock_maintenance_pollingPeriod";

	static final String OBS_DEFAULT_PROGRAM_NAME = "schedulingblock_default_obsprogram";

	static final String DATA_CAPTURE_ICE_NAME = "schedulingblock_datacapture_icename";

	static final String SOURCE_SEARCH_MAX_MESSAGES = "ephemeris_sourcesearch_maxmessages";

	static final String EPHEMERIS_ICE_NAME = "ephemeris_icename";

	
	static final String EXECUTIVE_ICE_NAME = "scheduler_executive";
	
	public static final String getDefaultObsProgram() {
		return getString(OBS_DEFAULT_PROGRAM_NAME, "");
	}	

	public static final String getEphemerisIceName() {
		return getString(EPHEMERIS_ICE_NAME, "");
	}
	
	public static final String getDataCaptureIceName() {
		return getString(DATA_CAPTURE_ICE_NAME, "");
	}
	
	public static final String getSBTemplateIceName() {
		return getString(SB_TEMPLATE_ICE_NAME, "");
	}
	
	public static final String getSBIceName() {
		return getString(SCHEDULING_BLOCK_ICE_NAME, "");
	}
	
	
	public static final String getOBSProgramIceName() {
		return getString(OBS_PROGRAM_ICE_NAME, "");
	}
	
	public static final String getExecutiveIceName() {
		return getString(EXECUTIVE_ICE_NAME, "");
	}
	
	
	public static final int getSBExecutionStatePollingPeriod() {
		return getInt(SB_EXECUTION_STATE_POLLING_PERIOD, 1000);
	}
	
	public static final int getSBMaintenancePollingPeriod() {
		return getInt(SB_MAINTENANCE_POLLING_PERIOD, 1000);
	}	
	
	public static final int getSourceSearchMaxMessages() {
		return getInt(SOURCE_SEARCH_MAX_MESSAGES, 3000);
	}	
    /** Get long preference
     *  @param key Preference key
     *  @return long or <code>null</code>
     */
    private static int getInt(final String key, final int default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        
        return prefs.getInt(Activator.PLUGIN_ID, key, default_value, null);
    }
    

    /** Get string preference
     *  @param key Preference key
     *  @return String or <code>null</code>
     */
    private static String getString(final String key, final String default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        return prefs.getString(Activator.PLUGIN_ID, key, default_value, null);
    }

	
}
