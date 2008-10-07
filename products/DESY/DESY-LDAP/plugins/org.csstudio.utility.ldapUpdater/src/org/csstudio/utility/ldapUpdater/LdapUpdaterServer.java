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


	public final String makeTimestring ( String leading, long hh ,long mm ,long ss, String trailing) {
	    String hhs=String.valueOf(hh); if ( hhs.length()==1 ) { hhs="0"+hhs; }
	    String mms=String.valueOf(mm); if ( mms.length()==1 ) { mms="0"+mms; }
	    String sss=String.valueOf(ss); if ( sss.length()==1 ) { sss="0"+sss; }

	    String timestring=leading + hhs+":"+mms+":"+sss + trailing;		
		return timestring;
	}
		
	
	/**
	 * {@inheritDoc}
	 */
	public final Object start(final IApplicationContext context) throws Exception {

	_instance = this;

//	CALCULATE THE DELAY FOR THE AUTOMATIC LDAP PROCESSING
	long startTime = System.currentTimeMillis();
long startTime_s=startTime/1000L; // s
	long one_hour=3600; // s
    long delay=0;
    
long one_day=one_hour*24; // s 
    long time_since_midnight=startTime_s % (one_day); // s
    if (time_since_midnight < ( one_hour ) ) {
    	delay=time_since_midnight; // start at 1 o'clock am
    }else{

    	if (time_since_midnight < ( one_hour*12 ) ) {
    		   delay=(one_hour*12)-time_since_midnight; // start at 1 o'clock pm
    	   }else{
    		   delay=(one_hour*24)-time_since_midnight; // start at 1 o'clock am    		   
    	   }
    }
//    long delay_ss=delay / 1000;
//    long start_hh=delay_ss/3600;
    long start_hh=delay/3600;
    long start_mm=(delay-(start_hh*3600))/60;
    long start_ss=delay-(start_hh*3600)-(start_mm*60);
 
    String timestring = makeTimestring ( "time interval until autostart is [", start_hh, start_mm, start_ss, "]") ;

    _log.debug(this, timestring );
        
    for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
        _log.debug(this, "Running startup service: " + s.toString());
        s.run();
 		IPreferencesService prefs = Platform.getPreferencesService();
		String interval = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.LDAP_AUTO_INTERVAL, "", null);

		new TimerProcessor ( delay, Integer.parseInt(interval)); // every 12 hours

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
