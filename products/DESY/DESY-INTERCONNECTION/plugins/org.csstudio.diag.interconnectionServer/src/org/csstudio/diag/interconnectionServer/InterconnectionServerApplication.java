package org.csstudio.diag.interconnectionServer;
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

import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * The application class for the interconnection server.
 * 
 * @author Matthias Clausen, Joerg Rathlev
 */
public class InterconnectionServerApplication implements IApplication {

	// FIXME: This is currently set from the outside by the RestartIcServer
	// and StopIcServer actions. That's not good.
	public static boolean SHUTDOWN = false;
	
	/**
	 * {@inheritDoc}
	 */
	public Object start(IApplicationContext context) throws Exception {
		System.out.println("start IcServer");
		CentralLogger.getInstance().info(this, "start IcServer");
		
		runStartupServices();
		
		context.applicationRunning();
		InterconnectionServer ics = InterconnectionServer.getInstance();
        ics.executeMe();
        
        if ( SHUTDOWN) {
        	return EXIT_OK;
        } else {
        	return EXIT_RESTART;
        }
	}

	/**
	 * Runs the startup services.
	 */
	private void runStartupServices() {
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		// TODO: implement code to forcibly stop the application
	}
}
