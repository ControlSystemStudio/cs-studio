package org.csstudio.utility.ldapUpdater;

import java.util.Timer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class LdapUpdaterServer implements IApplication {

	private boolean _stopped;

	/**
	 * The logger that is used by this class.
	 */
	private CentralLogger _log = CentralLogger.getInstance();

	/**
	 * The running instance of this server.
	 */
	private static LdapUpdaterServer _instance;
	
	/**
	 * Returns a reference to the currently running server instance. Note: it
	 * would probably be better to use the OSGi Application Admin service.
	 * 
	 * @return the running server.
	 */
	static LdapUpdaterServer getRunningServer() {
		return _instance;
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	public final Object start(final IApplicationContext context) throws Exception {

	_instance = this;

    for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
        _log.debug(this, "Running startup service: " + s.toString());
        s.run();
 		IPreferencesService prefs = Platform.getPreferencesService();
		String interval = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.LDAP_AUTO_INTERVAL, "", null);

		new TimerProcessor ( 5000, Integer.parseInt(interval)); // every 12 hours

// 		next call was working - for test only (starts the ldapUpdater every 180 seconds):

//        new TimerProcessor ( 5000, 1000*180 );        

    }

	
	synchronized (this) {
		while (!_stopped) {
			wait();
		}
	}
	return IApplication.EXIT_OK;

	}

	/**
	 * {@inheritDoc}
	 */
	public final synchronized void stop() {
		_log.debug(this, "stop() was called, stopping server");
		_stopped = true;
		notifyAll();
	}

}
