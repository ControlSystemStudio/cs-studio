package org.csstudio.diag.interconnectionServer.server;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

import org.csstudio.platform.logging.CentralLogger;

/**
 * Watching the ClientRequest thread
 * If it takes too long to process -> stop() the ClientRequest thread
 * Even though stop() is really a bad idea to kill a thread - we ran into problems with hanging
 * JMS and LDAP connections. Now - that we have removed all uncertainties and reconnect properly -
 * we might even be able to leave this 'killer' out 
 * To be tested .
 * 
 * @author Matthias Clausen
 *
 */
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
