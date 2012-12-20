
package org.csstudio.ams.distributor;

import java.sql.Connection;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.configReplicator.ConfigReplicator;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.ConfigDbProperties;
import org.csstudio.ams.dbAccess.configdb.FlagDAO;

/**
 * Service that copies the configuration from the master configuration database
 * into the local application database.
 */
class ConfigurationSynchronizer implements Runnable {
    
    private static enum SynchronizerState {
        WAITING_FOR_REQUEST,
        SYNCHRONIZATION_REQUESTED,
        SYNCHRONIZATION_STARTED,
        SYNCHRONIZATION_COMPLETED;
    }
    
    private final Connection _localDatabaseConnection;
    private final Connection _cacheDatabaseConnection;
    private final ConfigDbProperties dbProperties;
    private SynchronizerState _state;
    private boolean _stopped = false;
    private MessageProducer _jmsProducer;
    private Session _jmsSession;
    
    /**
     * Creates a new synchronizer object.
     */
    public ConfigurationSynchronizer(Connection localDatabaseConnection,
                                     Connection cacheDatabaseConnection,
                                     ConfigDbProperties prop,
                                     Session jmsSession,
                                     MessageProducer jmsProducer) {
        _localDatabaseConnection = localDatabaseConnection;
        _cacheDatabaseConnection = cacheDatabaseConnection;
        dbProperties = prop;
        _jmsSession = jmsSession;
        _jmsProducer = jmsProducer;
        _state = readSynchronizationState();
    }
    
    /**
     * Reads the current synchronization state from the application database.
     */
    private SynchronizerState readSynchronizationState() {
        try {
            short flag = FlagDAO.selectFlag(_localDatabaseConnection, AmsConstants.FLG_RPL);
            switch (flag) {
                case AmsConstants.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED:
                    return SynchronizerState.SYNCHRONIZATION_REQUESTED;
                case AmsConstants.FLAGVALUE_SYNCH_DIST_RPL:
                    return SynchronizerState.SYNCHRONIZATION_STARTED;
                case AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR:
                    return SynchronizerState.SYNCHRONIZATION_COMPLETED;
                default:
                    return SynchronizerState.WAITING_FOR_REQUEST;
            }
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not get flag value from application db", e);
            throw new RuntimeException("Synchronizer initialization failed", e);
        }
    }
    
    /**
     * Stops this synchronizer.
     */
    synchronized void stop() {
        _stopped = true;
        notifyAll();
    }
    
    /**
     * Runs this synchronizer.
     * ALTER PROFILE DEFAULT LIMIT IDLE_TIME 60;
     */
    @Override
    synchronized public void run() {
        _stopped = false;
        while (!_stopped) {
            switch (_state) {
                case WAITING_FOR_REQUEST:
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    break;
                case SYNCHRONIZATION_REQUESTED:
                    startSynchronization();
                    break;
                case SYNCHRONIZATION_STARTED:
                    copyConfiguration();
                    break;
                case SYNCHRONIZATION_COMPLETED:
                    sendSynchronizationCompletedMessage();
                    break;
            }
        }
    }
    
    /**
     * Requests this synchronizer to perform a synchronization.
     */
    synchronized void requestSynchronization() {
        if (_state != SynchronizerState.WAITING_FOR_REQUEST) {
            throw new IllegalStateException("Synchronization already in progress");
        }
        _state = SynchronizerState.SYNCHRONIZATION_REQUESTED;
        notifyAll();
    }
    
    /**
     * Starts the synchronization.
     */
    private void startSynchronization() {
        if (_state != SynchronizerState.SYNCHRONIZATION_REQUESTED) {
            throw new IllegalStateException("Not in state SYNCHRONIZATION_REQUESTED");
        }
        
        try {
            boolean success = FlagDAO.bUpdateFlag(_localDatabaseConnection,
                                                  AmsConstants.FLG_RPL,
                                                  AmsConstants.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED,
                                                  AmsConstants.FLAGVALUE_SYNCH_DIST_RPL);
            if (success) {
                _state = SynchronizerState.SYNCHRONIZATION_STARTED;
                HistoryWriter.logHistoryRplStart(_localDatabaseConnection, true);
                Log.log(this, Log.DEBUG, "accept reload cfg");
            } else {
                Log.log(this, Log.FATAL, "ignore start msg, could not update db flag to "
                        + AmsConstants.FLAGVALUE_SYNCH_DIST_RPL);
            }
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
        }
    }
    
    /**
     * Copies the configuration from the master database to the local application database.
     */
    private void copyConfiguration() {
        if (_state != SynchronizerState.SYNCHRONIZATION_STARTED) {
            throw new IllegalStateException("Not in state SYNCHRONIZATION_STARTED");
        }
        
        Connection masterDatabaseConnection = null;
        try {
            masterDatabaseConnection = AmsConnectionFactory.getConfigurationDB(dbProperties);
            ConfigReplicator.replicateConfiguration(masterDatabaseConnection, _localDatabaseConnection);
            ConfigReplicator.replicateConfigurationToHsql(_localDatabaseConnection, _cacheDatabaseConnection);
            AmsConnectionFactory.closeConnection(masterDatabaseConnection);
        } catch (Exception e) {
            Log.log(this, Log.FATAL, "Could not replicateConfiguration", e);
        }
        
        try {
            boolean success = FlagDAO.bUpdateFlag(_localDatabaseConnection,
                                                  AmsConstants.FLG_RPL,
                                                  AmsConstants.FLAGVALUE_SYNCH_DIST_RPL,
                                                  AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR);
            if (success) {
                _state = SynchronizerState.SYNCHRONIZATION_COMPLETED;
            } else {
                Log.log(this, Log.FATAL, "update not successful, could not update "
                        + AmsConstants.FLG_RPL + " from " + AmsConstants.FLAGVALUE_SYNCH_DIST_RPL
                        + " to " + AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR);
            }
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
        }
    }
    
    /**
     * Sends a JMS message to the filter manager.
     */
    private void sendSynchronizationCompletedMessage() {
        if (_state != SynchronizerState.SYNCHRONIZATION_COMPLETED) {
            throw new IllegalStateException("Not in state SYNCHRONIZATION_COMPLETED");
        }
        
        try {
            Log.log(this, Log.INFO,
                            "send MSGVALUE_TCMD_RELOAD_CFG_END to FMR via Ams Cmd Topic");
            MapMessage msg = _jmsSession.createMapMessage();
            msg.setString(AmsConstants.MSGPROP_TCMD_COMMAND, AmsConstants.MSGVALUE_TCMD_RELOAD_CFG_END);
            _jmsProducer.send(msg);

            boolean success = FlagDAO.bUpdateFlag(_localDatabaseConnection,
                                                  AmsConstants.FLG_RPL,
                                                  AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR,
                                                  AmsConstants.FLAGVALUE_SYNCH_IDLE);
            if (success) {
                _state = SynchronizerState.WAITING_FOR_REQUEST;
                HistoryWriter.logHistoryRplStart(_localDatabaseConnection, false);
            } else {
                Log.log(this, Log.FATAL,
                        "update not successful, could not update " + AmsConstants.FLG_RPL
                                + " from " + AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR
                                + " to " + AmsConstants.FLAGVALUE_SYNCH_IDLE);
            }
        } catch (JMSException e) {
            Log.log(this, Log.FATAL, "could not publishReplicateEndToFMgr", e);
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
        }
    }
    
}
