/**
 * 
 */
package org.csstudio.utility.pvmanager.jms.beast;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.jms.JMSException;

import org.epics.pvmanager.jms.beast.BeastDataSource;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Client to be regestered to the extension point.
 * 
 * @author berryma4
 * 
 */
@SuppressWarnings("deprecation")
public class BeastDataSourceFromPreferences extends BeastDataSource {

	private static Logger log = Logger
			.getLogger(BeastDataSourceFromPreferences.class.getName());
	private static String topic_name;
	private static String server;

	/**
	 * 
	 */

	static {
		final IPreferencesService prefs = Platform.getPreferencesService();

		topic_name = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.topic_name, "ALARM_900W", null);

		server = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.server,
				"tcp://alarm.hlc.nscl.msu.edu:61616", null);
	}

	private static BeastDataSource builder() {

		BeastDataSource source = null;
		try {
			source = new BeastDataSource(topic_name, server);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return source;
		
	}

	public BeastDataSourceFromPreferences() throws JMSException, Exception {
		super(builder());
	}

}