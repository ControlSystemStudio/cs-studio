package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.diag.interconnectionServer.server.ServerCommands;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

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
        
        thisServer.executeMe();

		return EXIT_OK;
	}
}
