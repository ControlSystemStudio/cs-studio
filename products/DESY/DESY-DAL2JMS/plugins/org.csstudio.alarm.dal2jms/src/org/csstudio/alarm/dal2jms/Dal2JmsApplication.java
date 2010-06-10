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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * The alarm handler is started and the application waits for the stop command being sent via remote command.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 02.06.2010
 */
public class Dal2JmsApplication implements IApplication {

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(Dal2JmsApplication.class);

    /**
     * TODO (jpenning) : implement remote command to stop the server
     */
    private volatile boolean _stopped = false;

    @Override
    public Object start(@Nullable final IApplicationContext context) throws Exception {
        LOG.info("dal2jms headless application starting");

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
    public void stop() {
        LOG.debug("dal2jms: stop() was called, stopping server");
        _stopped = true;
        notifyAll();
    }

    private void runServerUntilStopped(@Nonnull final IAlarmService alarmService) throws InterruptedException {
        IAlarmConnection connection = null;
        try {
            connection = alarmService.newAlarmConnection();

            // JmsMessageService is a local service to handle jms communication
            AlarmHandler alarmHandler = new AlarmHandler(connection, new JmsMessageService());
            alarmHandler.connect();

            synchronized (this) {
                while (!_stopped) {
                    wait();
                }
            }
        } catch (AlarmConnectionException e) {
            LOG.debug("dal2jms could not connect", e);
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

}
