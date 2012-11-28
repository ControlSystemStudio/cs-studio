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
package org.csstudio.utility.ldapupdater;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.net.HostAddress;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.joda.time.DateTimeFieldType;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAP Updater server.
 *
 * @author bknerr
 * @since 13.04.2010
 */
public class LdapUpdaterServer implements IApplication,
                                          IGenericServiceListener<ISessionService> {

    private static final Logger LOG = LoggerFactory.getLogger(LdapUpdaterServer.class);

    /**
     * The running instance of this server.
     */
    private static LdapUpdaterServer INSTANCE;

    private volatile boolean _stopped;

    private final ScheduledExecutorService _updaterExecutor =
        Executors.newSingleThreadScheduledExecutor();

    private final LdapUpdaterPreferencesService _prefsService;

    /**
     * Constructor.
     */
    public LdapUpdaterServer() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Application LdAP Updater Server does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!

        _prefsService = LdapUpdaterActivator.getDefault().getLdapUpdaterPreferencesService();
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


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Object start(@Nonnull final IApplicationContext context)
    throws Exception {

    	LdapUpdaterActivator.getDefault().addSessionServiceListener(this);

    	final long startTimeSec = _prefsService.getLdapAutoStart();
        final long intervalSec = _prefsService.getLdapStartInterval();

        final TimeInstant now = TimeInstantBuilder.fromNow();

        LOG.info(now.formatted());


        final long delayInS = getDelayInS(context, startTimeSec, now);

        logStartAndPeriod(startTimeSec, intervalSec);

        final ScheduledFuture<?> taskHandle =
            _updaterExecutor.scheduleAtFixedRate(new LdapUpdaterTask(_prefsService),
                                                 delayInS,
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

    private long getDelayInS(@Nonnull final IApplicationContext context,
                             final long startTimeSec,
                             @Nonnull final TimeInstant now) {
        final Map<?, ?> ctxArgs = context.getArguments();
        long delayInS = -1;
        if (ctxArgs.containsKey(IApplicationContext.APPLICATION_ARGS)) {
            final String[] strArgs = (String[]) ctxArgs.get(IApplicationContext.APPLICATION_ARGS);
            for (int i = 0; i < strArgs.length; i++) {
                if ("-delayInS".equals(strArgs[i]) && i+1 < strArgs.length) {
                    delayInS = Long.parseLong(strArgs[i+1], 10);
                }
            }
        }
        if (delayInS == -1) {
            delayInS = calculateDelayForLocalTZInS(startTimeSec, now);
        }
        return delayInS;
    }



    private void logStartAndPeriod(final long startTimeSec,
                                   final long intervalSec) {
        final long minute = startTimeSec % 60L;
        final long second = startTimeSec % 60L % 60L;
        final long hour = startTimeSec / 3600L;
        final String startTime = hour + ":" + minute + ":" + second;

        LOG.info("\nLDAP Updater autostart scheduled at {} every {} seconds",
                 startTime, intervalSec);
    }


    private long calculateDelayForLocalTZInS(final long startTimeInSecondsOfDay,
                                             @Nonnull final TimeInstant now) {

        final int nowInSecSinceMidnight = now.getInstant().get(DateTimeFieldType.secondOfDay());

        long delayInS = startTimeInSecondsOfDay - nowInSecSinceMidnight;
        if (delayInS < 0) {
            delayInS = 3600*24 + delayInS; // start at startTimeSec on the next day
        }
        return delayInS;
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


    /**
     * {@inheritDoc}
     */
    @Override
    public void bindService(@Nonnull final ISessionService sessionService) {

        final String username = _prefsService.getXmppUser();
        final String password = _prefsService.getXmppPassword();
        final HostAddress server = _prefsService.getXmppServer();

        try {
            sessionService.connect(username, password, server.getHostAddress());
        } catch (final Exception e) {
            LOG.warn("XMPP connection is not available, {}", e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindService(@Nonnull final ISessionService service) {
        service.disconnect();
    }
}
