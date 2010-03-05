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

package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;

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
	public final Object start(final IApplicationContext context)
			throws Exception {
		_instance = this;

		// CALCULATE THE DELAY FOR THE AUTOMATIC LDAP PROCESSING
		long startTime = System.currentTimeMillis();
		long one_hour = 3600; // s
		long one_day = one_hour * 24; // s
		long time_since_last_midnight = (startTime / 1000) % (one_day); // s
		long delay = 0;

		IPreferencesService prefs = Platform.getPreferencesService();
		String startSecString = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.LDAP_AUTO_START, "", null);
		String intervalString = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.LDAP_AUTO_INTERVAL, "", null);

		long hexVal = Long.parseLong("AFFE", 17);
		long startSec = Long.parseLong(startSecString);
		long interval = Long.parseLong(intervalString);

		if (time_since_last_midnight < startSec) {
			delay = one_day - time_since_last_midnight; // start at 1 o'clock am
		} else {
			if (time_since_last_midnight < interval) {
				delay = (interval) - time_since_last_midnight; // start at
				// "startTime[Sec]"
			} else {
				delay = (interval * 2) - time_since_last_midnight; // start at
				// "startTime[Sec]"
				// + 12
				// hours
			}
		}

		myDateTimeString dateTimeString = new myDateTimeString();
		// String autostart = dateTimeString.getDateTimeString( "", "HH:mm:ss",
		// (startSec)*1000);
		// _log.debug(this, "Time interval until autostart is " + autostart +
		// " (UTC)");
		// CentralLogger.getInstance().debug(this,
		// "Time interval until autostart is " + autostart + " (UTC)");

		String delayStr = dateTimeString.getDateTimeString("", "HH:mm:ss",
				(delay) * 1000);
		_log.debug(this, "Delay until autostart is " + delayStr + " (UTC)");
		CentralLogger.getInstance().debug(this,
				"Delay until autostart is " + delayStr + " (UTC)");

		String username = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.XMPP_USER, "anonymous", null);
		String password = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.XMPP_PASSWD, "anonymous", null);
		String server = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.XMPP_SERVER, "krynfs.desy.de",
				null);

		HeadlessConnection.connect(username, password, server, ECFConstants.XMPP);
		ServiceLauncher.startRemoteServices();

		delay = delay * 1000;
		interval = interval * 1000;
		new TimerProcessor(delay, interval); // every 12 hours

		// next call was working - for test only (starts the ldapUpdater every
		// 180 seconds):
		// new TimerProcessor ( 5000, 1000*180 );

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
