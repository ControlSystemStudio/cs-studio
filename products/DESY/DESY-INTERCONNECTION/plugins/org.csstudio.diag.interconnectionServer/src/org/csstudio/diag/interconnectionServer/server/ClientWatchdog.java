package org.csstudio.diag.interconnectionServer.server;

import org.csstudio.platform.logging.CentralLogger;

public class ClientWatchdog extends Thread{
	private ClientRequest clientID = null;
	private int	timeout	= 0;
	
	ClientWatchdog ( ClientRequest clientID, int timeout) {
		this.clientID = clientID;
		this.timeout = timeout;
		
		this.start();
	}
	
	public void run() {
		/*
		 * wait
		 */
		
		try {
			Thread.sleep( this.timeout);
		
		/*
		 * check whether ClientRequest is still running
		 * if so -> stop it!
		 */
		if ( this.clientID.isAlive()) {
			/*
			 * dammed - still running -> kill it!
			 */
			this.clientID.stop();
			InterconnectionServer.getInstance().getClientRequestTheadCollector().decrementValue();
			/*
			 * another time we had to stop a thread! - countdown
			 */
			InterconnectionServer.getInstance().setSuccessfullJmsSentCountdown(false);
			CentralLogger.getInstance().debug(this, "InterconnectionServer: Hard STOP for ClientRequest");
			System.out.print("#");
		}
		} catch (InterruptedException e) {
			// TODO: handle exception
		}
		finally {
			//clean up
		}
		
	}

}
