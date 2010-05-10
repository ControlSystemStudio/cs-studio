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

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.LDAP_AUTO_INTERVAL;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.LDAP_AUTO_START;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.XMPP_PASSWD;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.XMPP_SERVER;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.XMPP_USER;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;

/**
 * LDAP Updater server.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 */
public class LdapUpdaterServer implements IApplication {

    /**
     * The running instance of this server.
     */
    private static LdapUpdaterServer INSTANCE;

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    private volatile boolean _stopped;

    /**
     * Constructor.
     */
    public LdapUpdaterServer() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Application LdAP Updater Server does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }


    /**
     * Returns a reference to the currently running server instance. Note: it
     * would probably be better to use the OSGi Application Admin service.
     *
     * @return the running server.
     */
    @CheckForNull
    public static LdapUpdaterServer getRunningServer() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object start(@Nullable final IApplicationContext context)
    throws Exception {
        final String startSecString = getValueFromPreferences(LDAP_AUTO_START, "0");
        final String intervalString = getValueFromPreferences(LDAP_AUTO_INTERVAL, "43200");
        final int startTimeSec = Integer.parseInt(startSecString == null ? "0" : startSecString);
        final long intervalSec = Long.parseLong(intervalString);



        final TimeZone timeZone = TimeZone.getTimeZone("ECT");
        final Calendar cal = new GregorianCalendar(timeZone);

        System.out.println(cal.getTime());

        final int hour = (startTimeSec) / 3600;
        cal.set(Calendar.HOUR, hour);
        final int minutes = (startTimeSec / 60) % 60;
        cal.set(Calendar.MINUTE, minutes);
        final int seconds = startTimeSec % 3600;
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, 0);

        final String delayStr = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());

        _log.info("Autostart scheduled at " + delayStr +  " (ECT) every " + intervalSec + " seconds");


        final String username = getValueFromPreferences(XMPP_USER, "anonymous");
        final String password = getValueFromPreferences(XMPP_PASSWD, "anonymous");
        final String server = getValueFromPreferences(XMPP_SERVER, "krynfs.desy.de");


        HeadlessConnection.connect(username, password, server, ECFConstants.XMPP);
        ServiceLauncher.startRemoteServices();


        new TimerProcessor(cal.getTime(), intervalSec * 1000);

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
    @Override
    public final synchronized void stop() {
        _log.debug("stop() was called, stopping server.");
        _stopped = true;
        notifyAll();
    }

}
