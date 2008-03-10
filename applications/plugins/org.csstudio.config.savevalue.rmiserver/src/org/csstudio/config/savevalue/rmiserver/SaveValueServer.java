/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.config.savevalue.rmiserver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueService;
import org.csstudio.config.savevalue.service.SocketFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * Server application for the save value services. 
 * 
 * @author Joerg Rathlev
 */
public class SaveValueServer implements IApplication {

	/**
	 * Whether this application should stop.
	 */
	private boolean _stopped = false;

	/**
	 * {@inheritDoc}
	 */
	public final Object start(final IApplicationContext context) throws Exception {
		try {
			// Create the registry
			Registry reg = LocateRegistry.createRegistry(1099);
			
			SocketFactory sf = new SocketFactory();
			
			// Create the services and publish them
			
			// EPICS Ora is not implemented yet			
//			SaveValueService epicsOra = new EpicsOraService();
//			SaveValueService eoStub = (SaveValueService) UnicastRemoteObject.exportObject(epicsOra, 0, sf, sf);
//			reg.bind("SaveValue.EpicsOra", eoStub);

			// Database Service is not implemented yet
//			SaveValueService db = new DatabaseService();
//			SaveValueService dbStub = (SaveValueService) UnicastRemoteObject.exportObject(db, 0, sf, sf);
//			reg.bind("SaveValue.Database", dbStub);

			SaveValueService caput = new CaPutService();
			SaveValueService caputStub = (SaveValueService) UnicastRemoteObject.exportObject(caput, 0, sf, sf);
			reg.bind("SaveValue.caput", caputStub);
			
			ChangelogService changelog = new ChangelogServiceImpl();
			ChangelogService changelogStub = (ChangelogService) UnicastRemoteObject.exportObject(changelog, 0, sf, sf);
			reg.bind("SaveValue.changelog", changelogStub);
			
			System.out.println("Server ready.");
			context.applicationRunning();
			synchronized (this) {
				while (!_stopped) {
					wait();
				}
			}
		} catch (Exception e) {
			System.err.println("Server error: " + e.getMessage());
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	/**
	 * {@inheritDoc}
	 */
	public final synchronized void stop() {
		_stopped = true;
		notifyAll();
	}

}
