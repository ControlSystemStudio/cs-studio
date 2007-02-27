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
		
		thisServer = new InterconnectionServer( "Server-2");
        
        //System.out.println ("vor start init");
        //Timer.Start.init();
        
        //System.out.println ("vor start all timer");
        //Timer.Start.all();
		
		System.out.println( "Commands : " + ServerCommands.getCommands());

        
        thisServer.executeMe();
		
		///testJob job = new testJob("hello");
		
        thisServer.addJobChangeListener(new JobChangeAdapter() {
	        public void done(IJobChangeEvent event) {
	        //if (event.getResult().isOK())
	        	SHUTDOWN = true;
	        }
	     });
		
        thisServer.schedule();
		
		while(SHUTDOWN == false) {
			Thread.sleep(10000);
		}

		System.out.println("vor exit_ok");
		return EXIT_OK;
	}

	
//    public static void main(String[] args)
//    {
//        String  n;
//        
//        if(args.length > 0)
//        {
//            n = args[0];
//        }
//        else
//        {
//            n = "NO NAME";
//        }
//        
//        thisServer = new InterconnectionServer( );
//        
//        //System.out.println ("vor start init");
//        //Timer.Start.init();
//        
//        //System.out.println ("vor start all timer");
//        //Timer.Start.all();
//
//        
//        thisServer.executeMe();
//        
//        System.exit(0);
//    }
	
}
