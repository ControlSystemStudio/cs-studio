package org.csstudio.alarm.beast.server;

import java.io.FileInputStream;
import java.util.Properties;

import org.junit.Test;

/** JUnit test of the alarm configuration reader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmRDBTest
{
	private Properties getTestSettings() throws Exception
    {
        Properties settings = new Properties();
    	settings.load(new FileInputStream("/Kram/Eclipse/Workspace/CustomizationFiles/tests.ini"));
        return settings;
    }

	@Test
	public void readAlarmConfiguration() throws Exception
	{
		final Properties settings = getTestSettings();
        final AlarmRDB rdb = new AlarmRDB(
				settings.getProperty("rdb_url"),
				settings.getProperty("rdb_user"),
				settings.getProperty("rdb_password"),
				settings.getProperty("alarm_root"));
		final AlarmHierarchy root = rdb.readConfiguration();
		root.dump(System.out);
	}
}
