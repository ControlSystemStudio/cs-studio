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
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.dal2jms.preferences.Dal2JmsPreferences;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAcknowledgeService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.declaration.IAlarmService.IListener;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.persister.declaration.IPersistenceService;
import org.csstudio.remote.jms.command.ClientGroup;
import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.remote.jms.command.RemoteCommandException;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
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
public class Dal2JmsApplication implements IApplication {
    
    private static final Logger LOG = LoggerFactory.getLogger(Dal2JmsApplication.class);
    
    /*
     * Flag indicating the remote stop command
     */
    private volatile boolean _stopped = false;
    
    private IListener _configurationUpdateListener;
    
    // the running instance of this server.
    private static Dal2JmsApplication INSTANCE;
    
    private static ClientGroup CLIENT_GROUP;
    
    private AlarmHandler _alarmHandler;
    
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
    @Nonnull
    public final Object start(@Nullable final IApplicationContext context) throws Exception {
        LOG.info("dal2jms headless application starting");
        INSTANCE = this;
        
        // just retrieve once, restart dal2jms is necessary anyway if the client group is changed
        CLIENT_GROUP = AlarmPreference.getClientGroup();
        LOG.info("dal2jms headless application running for client group " + CLIENT_GROUP);
        
        if (!createRemoteServer()) {
            return IApplication.EXIT_OK;
        }
        
        if (!createAckListener()) {
            return IApplication.EXIT_OK;
        }
        
        final ISessionService sessionService = ServiceLocator.getService(ISessionService.class);
        if (sessionService != null) {
            connectToXmpp(sessionService);
        } else {
            LOG.error("dal2jms headless application could not connect to Xmpp. Session service was not available.");
        }
        
        final IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        createAndRegisterReloadCommand(alarmService);
        
        if (alarmService != null) {
            LOG.info("dal2jms headless application running");
            runServerUntilStopped(alarmService); // returns when stopped
        } else {
            LOG.error("dal2jms headless application could not be started: Alarm service must not be null.");
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
        try {
            connection = alarmService.newAlarmConnection();
            
            _alarmHandler = new AlarmHandler(connection, new JmsMessageService());
            _alarmHandler.connect();
            
            sendCommand(IRemoteCommandService.Dal2JmsStartedCommand);
            synchronized (this) {
                while (!_stopped) {
                    wait();
                }
                sendCommand(IRemoteCommandService.Dal2JmsWillStopCommand);
            }
        } catch (AlarmConnectionException e) {
            LOG.debug("dal2jms could not connect", e);
        } finally {
            tryToShutdown(connection);
        }
    }
    
    private void tryToShutdown(@CheckForNull final IAlarmConnection connection) {
        if (connection != null) {
            LOG.debug("dal2jms disconnecting from Jms");
            connection.disconnect();
        }
        ISessionService sessionService = ServiceLocator.getService(ISessionService.class);
        if (sessionService != null) {
            LOG.debug("dal2jms disconnecting from Xmpp");
            sessionService.disconnect();
        }
        IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        if (alarmService != null) {
            LOG.debug("dal2jms disconnecting from alarm service (configuration update)");
            alarmService.deregister(_configurationUpdateListener);
        }
        IAcknowledgeService ackService = ServiceLocator.getService(IAcknowledgeService.class);
        if (ackService != null) {
            LOG.debug("dal2jms disconnecting from acknowledge service");
            ackService.disconnect();
        }
        final IPersistenceService persistenceService = ServiceLocator
                .getService(IPersistenceService.class);
        if (persistenceService != null) {
            LOG.debug("dal2jms is stopping the persistence service");
            persistenceService.stopPersister();
        }
    }
    
    private void connectToXmpp(@Nonnull final ISessionService sessionService) {
        String username = Dal2JmsPreferences.XMPP_DAL2JMS_USER_NAME.getValue();
        String password = Dal2JmsPreferences.XMPP_DAL2JMS_PASSWORD.getValue();
        String server = Dal2JmsPreferences.XMPP_DAL2JMS_SERVER_NAME.getValue();
        
        try {
            sessionService.connect(username, password, server);
        } catch (Exception e) {
            LOG.warn("XMPP connection is not available, ", e);
        }
    }
    
    private boolean createRemoteServer() {
        boolean result = false;
        try {
            final Registry registry = LocateRegistry
                    .createRegistry(AlarmPreference.ALARMSERVICE_RMI_REGISTRY_PORT.getValue());
            final IRemoteAcknowledgeService ackService = ServiceLocator
                    .getService(IRemoteAcknowledgeService.class);
            final IRemoteAcknowledgeService stub = (IRemoteAcknowledgeService) UnicastRemoteObject
                    .exportObject(ackService, 0);
            registry.bind(IRemoteAcknowledgeService.class.getCanonicalName(), stub);
            result = true;
            LOG.info("Acknowledge service registered at rmi registry");
        } catch (RemoteException e) {
            LOG.error("dal2jms headless application could not be started: Failed to create the remote service registry or to export the ack service.");
        } catch (AlreadyBoundException e) {
            LOG.error("dal2jms headless application could not be started: The acknowledge service is already bound.");
        }
        return result;
    }
    
    private boolean createAckListener() {
        boolean result = false;
        final IAcknowledgeService ackService = ServiceLocator.getService(IAcknowledgeService.class);
        
        try {
            String ackTopicName = Dal2JmsPreferences.JMS_ACK_SOURCE_TOPIC_NAME.getValue();
            ackService.connectToAcknowledgeTopic(ackTopicName);
            if (createPersister(ackService)) {
                result = true;
                LOG.info("dal2jms headless application started acknowledge service.");
            } else {
                LOG.error("dal2jms headless application could not be started: Cannot start persistence service.");
            }
        } catch (AlarmConnectionException e) {
            LOG.error("dal2jms headless application could not be started: Cannot connect to acknowledge topic.");
        }
        return result;
    }
    
    private boolean createPersister(@Nonnull final IAcknowledgeService ackService) {
        boolean result = false;
        final IPersistenceService persistenceService = ServiceLocator
                .getService(IPersistenceService.class);
        if (persistenceService != null) {
            String filename = Dal2JmsPreferences.SNAPSHOT_FILENAME.getValue();
            persistenceService.init(ackService, filename);
            tryToRestoreMemento(persistenceService);
            int delayInSeconds = Dal2JmsPreferences.SNAPSHOT_INTERVAL_SECS.getValue();
            persistenceService.runPersister(delayInSeconds);
            result = true;
        }
        return result;
    }
    
    private void tryToRestoreMemento(@Nonnull final IPersistenceService persistenceService) {
        try {
            persistenceService.restoreMemento();
            LOG.info("dal2jms headless application restored memento of acknowledge server.");
        } catch (IOException e) {
            LOG.warn("dal2jms headless application failed to restoreMemento.", e);
        } catch (ClassNotFoundException e) {
            LOG.warn("dal2jms headless application failed to restoreMemento.", e);
        }
    }
    
    private void createAndRegisterReloadCommand(@Nonnull final IAlarmService alarmService) {
        _configurationUpdateListener = new MyAlarmServiceListener();
        alarmService.register(_configurationUpdateListener);
    }
    
    private void sendCommand(@Nonnull final String command) {
        IRemoteCommandService remoteCommandService = ServiceLocator
                .getService(IRemoteCommandService.class);
        try {
            remoteCommandService.sendCommand(CLIENT_GROUP, command);
            LOG.info("dal2jms: Command '" + command + "' was sent to clients");
        } catch (RemoteCommandException e) {
            LOG.error("dal2jms: Sending command '" + command + "' failed", e);
        }
    }
    
    /**
     * alarm service listener
     */
    private class MyAlarmServiceListener implements IAlarmService.IListener {
        
        public MyAlarmServiceListener() {
            // nothing to do
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void configurationUpdated() {
            LOG.info("'Configuration Update' command is detected by alarm server");
            try {
                _alarmHandler.reconnect();
                LOG.info("Configuration update: Reconnect successful");
                sendCommand(IRemoteCommandService.Dal2JmsReloadedCommand);
            } catch (AlarmConnectionException e) {
                LOG.error("Configuration update: Reconnect failed", e);
            }
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void alarmServerReloaded() {
            LOG.info("'Alarm Server Reloaded' command is detected by alarm server");
            // nothing to do for the server
            // clients of this server will reload here
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void alarmServerStarted() {
            LOG.info("'Alarm Server Started' command is detected by alarm server");
            // nothing to do for the server
            // clients of this server will display a message then
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void alarmServerWillStop() {
            LOG.info("'Alarm Server Will Stop' command is detected by alarm server");
            // nothing to do for the server
            // clients of this server will display an error message then
        }
    }
    
}
