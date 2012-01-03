/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.alarm.dal2jms;

import java.io.IOException;
import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.dal2jms.preferences.Preference;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The alarm handler is connected and the application waits for the stop command being sent via remote command.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 02.06.2010
 */
public class Dal2JmsApplication implements IApplication, IGenericServiceListener<ISessionService> {

    private static final Logger LOG = LoggerFactory.getLogger(Dal2JmsApplication.class);

    /*
     * Flag indicating the remote stop command
     */
    private volatile boolean _stopped = false;

    /**
     * The running instance of this server.
     */
    private static Dal2JmsApplication INSTANCE;

    /**
     * Returns a reference to the currently running server instance. Note: it
     * would probably be better to use the OSGi Application Admin service.
     *
     * @return the running server.
     */
    @Nonnull
    static Dal2JmsApplication getRunningServer() {
        return INSTANCE;
    }

    @Override
    public final Object start(@Nullable final IApplicationContext context) throws Exception {
        LOG.info("dal2jms headless application starting");
        // TODO (jpenning) Singleton defined in start?!
        INSTANCE = this;


        final IAlarmService alarmService = Activator.getDefault().getAlarmService();
        if (alarmService != null) {
            LOG.info("dal2jms headless application running");
            runServerUntilStopped(alarmService);
        } else {
            LOG.error("dal2jms headless application could not be started. Alarm service must not be null.");
        }

        LOG.info("dal2jms headless application stopped");
        return IApplication.EXIT_OK;
    }

    @Override
    public final synchronized void stop() {
        LOG.debug("dal2jms: stop() was called, stopping server");
        _stopped = true;
        notifyAll();
    }

    private void runServerUntilStopped(@Nonnull final IAlarmService alarmService) throws InterruptedException {
        IAlarmConnection connection = null;
        String filePath = null;
        try {
            connection = alarmService.newAlarmConnection();

            // JmsMessageService is a local service to handle jms communication
            AlarmHandler alarmHandler = new AlarmHandler(connection, new JmsMessageService());

            filePath = getFilePath();
            alarmHandler.connect(filePath);

            synchronized (this) {
                while (!_stopped) {
                    wait();
                }
            }
        } catch (AlarmConnectionException e) {
            LOG.debug("dal2jms could not connect", e);
        } catch (IOException e) {
            LOG.debug("dal2jms could not retrieve pv configuration file at {}", filePath, e);
        } finally {
            tryToDisconnect(connection);
        }
    }

    private void tryToDisconnect(@CheckForNull final IAlarmConnection connection) {
        if (connection != null) {
            LOG.debug("dal2jms disconnecting");
            connection.disconnect();
        }
    }

    @Nonnull
    private String getFilePath() throws IOException {
        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
        Path path = new Path(Preference.ALARM_CONFIG_XML_FILE_NAME.getValue());
        URL url = FileLocator.find(bundle, path, null);
        String result = FileLocator.toFileURL(url).getPath();
        return result;
    }

    
    @Override
    public void bindService(ISessionService sessionService) {
        String username = Preference.XMPP_DAL2JMS_USER_NAME.getValue();
        String password = Preference.XMPP_DAL2JMS_PASSWORD.getValue();
        String server = Preference.XMPP_DAL2JMS_SERVER_NAME.getValue();
    	
    	try {
			sessionService.connect(username, password, server);
		} catch (Exception e) {
			CentralLogger.getInstance().warn("XMPP connection is not available, ", e);
		}
    }
    
    @Override
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
}
