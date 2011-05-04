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
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;


/**
 * Server application for the save value services.
 *
 * @author Joerg Rathlev
 */
public class SaveValueServer implements IApplication, IGenericServiceListener<ISessionService> {

	/**
	 * Whether this application should stop.
	 */
	private boolean _stopped = false;

	/**
	 * The logger that is used by this class.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * The running instance of this server.
	 */
	private static SaveValueServer INSTANCE;

	/**
	 * Returns a reference to the currently running server instance. Note: it
	 * would probably be better to use the OSGi Application Admin service.
	 *
	 * @return the running server.
	 */
	static SaveValueServer getRunningServer() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Object start(final IApplicationContext context) throws Exception {

		INSTANCE = this;


        for (final IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
            _log.debug(this, "Running startup service: " + s.toString());
            s.run();
        }

		try {
			// Create the registry
			final Registry reg = LocateRegistry.createRegistry(1099);

			final SocketFactory sf = new SocketFactory();

			// Create the services and publish them

			// EPICS Ora is not implemented yet
//			SaveValueService epicsOra = new EpicsOraService();
//			SaveValueService eoStub = (SaveValueService) UnicastRemoteObject.exportObject(epicsOra, 0, sf, sf);
//			reg.bind("SaveValue.EpicsOra", eoStub);

			// Database Service is not implemented yet
//			SaveValueService db = new DatabaseService();
//			SaveValueService dbStub = (SaveValueService) UnicastRemoteObject.exportObject(db, 0, sf, sf);
//			reg.bind("SaveValue.Database", dbStub);

			final SaveValueService caput = new CaPutService();
			final SaveValueService caputStub = (SaveValueService) UnicastRemoteObject.exportObject(caput, 0, sf, sf);
			reg.bind("SaveValue.caput", caputStub);

			final ChangelogService changelog = new ChangelogServiceImpl();
			final ChangelogService changelogStub = (ChangelogService) UnicastRemoteObject.exportObject(changelog, 0, sf, sf);
			reg.bind("SaveValue.changelog", changelogStub);

			_log.info(this, "Server ready.");
			context.applicationRunning();
			synchronized (this) {
				while (!_stopped) {
					wait();
				}
			}
		} catch (final Exception e) {
			_log.error(this, "Server error.", e);
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

    public void bindService(ISessionService sessionService) {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String username = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_USERNAME, "anonymous", null);
        final String password = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        final String server = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krykxmpp.desy.de", null);
    	
    	try {
			sessionService.connect(username, password, server);
		} catch (Exception e) {
			CentralLogger.getInstance().warn(this,
					"XMPP connection is not available, " + e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	service.disconnect();
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
