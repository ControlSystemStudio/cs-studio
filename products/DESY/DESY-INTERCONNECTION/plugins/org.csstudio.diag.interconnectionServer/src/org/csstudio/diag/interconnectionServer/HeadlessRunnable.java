package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.diag.interconnectionServer.server.ServerCommands;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class HeadlessRunnable implements IPlatformRunnable {

	public static boolean SHUTDOWN = false;
    private static InterconnectionServer thisServer = null;
	
	public Object run(Object args) throws Exception {

		System.out.println("start IcServer");
		
		thisServer = InterconnectionServer.getInstance();
        
        //System.out.println ("vor start init");
        //Timer.Start.init();
        
        //System.out.println ("vor start all timer");
        //Timer.Start.all();
		
		System.out.println( "Head Commands : " + ServerCommands.getCommands());
        
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
        thisServer.executeMe();
        
        if ( SHUTDOWN) {
        	return IPlatformRunnable.EXIT_OK;
        } else {
        	return IPlatformRunnable.EXIT_RESTART;
        }
	}
}
