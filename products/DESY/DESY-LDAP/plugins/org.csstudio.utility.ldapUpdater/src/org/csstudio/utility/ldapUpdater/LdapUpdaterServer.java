package org.csstudio.utility.ldapUpdater;

import java.text.SimpleDateFormat;
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

//	CALCULATE THE DELAY FOR THE AUTOMATIC LDAP PROCESSING
	long startTime = System.currentTimeMillis();
	long one_hour=3600; // s
    long one_day=one_hour*24; // s 
    
    long time_since_last_midnight = ( startTime / 1000 ) % (one_day); // s
    long delay=0;

    IPreferencesService prefs = Platform.getPreferencesService();
    String startSecString = prefs.getString(Activator.PLUGIN_ID, LdapUpdaterPreferenceConstants.LDAP_AUTO_START, "", null);
    String intervalString = prefs.getString(Activator.PLUGIN_ID, LdapUpdaterPreferenceConstants.LDAP_AUTO_INTERVAL, "", null);

    long startSec = Long.parseLong(startSecString);
    long interval = Long.parseLong(intervalString);
   
    if ( time_since_last_midnight < startSec ) {
    	delay = one_day - time_since_last_midnight; // start at 1 o'clock am
    } else {
    	if ( time_since_last_midnight < interval ) {
    		   delay = (interval) - time_since_last_midnight; // start at "startTime[Sec]"
    	   }else{
    		   delay = (interval*2) - time_since_last_midnight; // start at "startTime[Sec]" + 12 hours    		   
    	   }
    }
    
    myDateTimeString dateTimeString = new myDateTimeString();   
//    String autostart = dateTimeString.getDateTimeString( "", "HH:mm:ss", (startSec)*1000);
//    _log.debug(this, "Time interval until autostart is " + autostart + " (UTC)");
//    CentralLogger.getInstance().debug(this, "Time interval until autostart is " + autostart + " (UTC)");            

    String delayStr = dateTimeString.getDateTimeString( "", "HH:mm:ss", (delay)*1000);
    _log.debug(this, "Delay until autostart is " + delayStr + " (UTC)" );
    CentralLogger.getInstance().debug(this, "Delay until autostart is " + delayStr + " (UTC)");            

    for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
        _log.debug(this, "Running startup service: " + s.toString());
        CentralLogger.getInstance().debug(this, "Running startup service: " + s.toString());
        s.run();
    }
 	delay = delay * 1000;
 	interval = interval * 1000;
	new TimerProcessor ( delay, interval); // every 12 hours

// 		next call was working - for test only (starts the ldapUpdater every 180 seconds):
//        new TimerProcessor ( 5000, 1000*180 );        

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
		_log.debug(this, "stop() was called, stopping server.");
		_stopped = true;
		notifyAll();
	}

}
