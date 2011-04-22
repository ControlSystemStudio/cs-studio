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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

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

    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUpdaterServer.class);

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
    @Nonnull
    public static LdapUpdaterServer getRunningServer() {
        return INSTANCE;
    }

    private final ScheduledExecutorService _updaterExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Object start(@Nullable final IApplicationContext context)
    throws Exception {
        final String startSecString = getValueFromPreferences(LDAP_AUTO_START, "0");
        final String intervalString = getValueFromPreferences(LDAP_AUTO_INTERVAL, "43200");
        final int startTimeSec = Integer.parseInt(startSecString == null ? "0" : startSecString);
        final long intervalSec = Long.parseLong(intervalString);

        final TimeZone timeZone = TimeZone.getTimeZone("ECT");
        final Calendar now = new GregorianCalendar(timeZone);

        LOG.info(now.getTime());

        final long delaySec = getDelayInSeconds(startTimeSec, now);
        logStartAndPeriod(startTimeSec, intervalSec, timeZone);

        final String username = getValueFromPreferences(XMPP_USER, "anonymous");
        final String password = getValueFromPreferences(XMPP_PASSWD, "anonymous");
        final String server = getValueFromPreferences(XMPP_SERVER, "krynfs.desy.de");


        HeadlessConnection.connect(username, password, server, ECFConstants.XMPP);
        ServiceLauncher.startRemoteServices();

        final ScheduledFuture<?> taskHandle =
            _updaterExecutor.scheduleAtFixedRate(new LdapUpdaterTask(),
                                                 delaySec,
                                                 intervalSec,
                                                 TimeUnit.SECONDS);
        synchronized (this) {
            while (!_stopped) {
                wait();
            }
        }

        taskHandle.cancel(true); // cancel the task, when it runs

        _updaterExecutor.shutdown();

        return IApplication.EXIT_OK;
    }



    private void logStartAndPeriod(final int startTimeSec,
                                   final long intervalSec,
                                   @Nonnull final TimeZone timeZone) {
        final Calendar start = new GregorianCalendar(timeZone);
        start.set(Calendar.SECOND, startTimeSec % 60);
        start.set(Calendar.MINUTE, startTimeSec % 60 % 60);
        start.set(Calendar.HOUR, startTimeSec / 3600);
        final String startStr = new SimpleDateFormat("HH:mm:ss").format(start.getTime());

        LOG.info("\nLDAP Updater autostart scheduled at " + startStr +  " (ECT) every " + intervalSec + " seconds");
    }


    private long getDelayInSeconds(final int startTimeSec, @Nonnull final Calendar now) {
        final int s = now.get(Calendar.SECOND);
        final int m = now.get(Calendar.MINUTE);
        final int h = now.get(Calendar.HOUR_OF_DAY);

        final long secondsSinceMidnight = s + m*60 + h*3600;

        long delaySec = startTimeSec - secondsSinceMidnight;
        if (delaySec < 0) {
            delaySec = 3600*24 + delaySec; // turn over to new day
        }
        return delaySec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void stop() {
        LOG.debug("stop() was called, stopping server.");
        _stopped = true;
        notifyAll();
    }

}
