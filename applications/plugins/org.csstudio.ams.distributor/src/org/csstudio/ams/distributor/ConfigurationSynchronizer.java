package org.csstudio.ams.distributor;

import java.sql.Connection;
import java.sql.SQLException;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.ExitException;
import org.csstudio.ams.Log;
import org.csstudio.ams.configReplicator.ConfigReplicator;
import org.csstudio.ams.dbAccess.configdb.FlagDAO;

/**
 * Service that copies the configuration from the master configuration database
 * into the local application database.
 */
class ConfigurationSynchronizer {
    private static enum SynchronizerState {
        NONE,
        SYNCHRONIZATION_STARTED,
        ;
    }
    
    private final Connection _localDatabaseConnection;
    private final Connection _masterDatabaseConnection;
    private SynchronizerState _state;
    
    ConfigurationSynchronizer(Connection localDatabaseConnection,
                              Connection masterDatabaseConnection) {
        _localDatabaseConnection = localDatabaseConnection;
        _masterDatabaseConnection = masterDatabaseConnection;
        _state = SynchronizerState.NONE;
    }
    
    int startReplication() {
        if (_state != SynchronizerState.NONE) {
            throw new IllegalStateException("Synchronization already in progress");
        }
        
        try {
            boolean success = FlagDAO.bUpdateFlag(_localDatabaseConnection,
                                               AmsConstants.FLG_RPL,
                                               AmsConstants.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED,
                                               AmsConstants.FLAGVALUE_SYNCH_DIST_RPL);
            if (success) {
                _state = SynchronizerState.SYNCHRONIZATION_STARTED;
                DistributorWork.logHistoryRplStart(_localDatabaseConnection, true);
                Log.log(this, Log.DEBUG, "accept reload cfg");
            } else {
                Log.log(this, Log.FATAL, "ignore start msg, could not update db flag to "
                        + AmsConstants.FLAGVALUE_SYNCH_DIST_RPL);
                return DistributorStart.STAT_ERR_FLG_RPL; // force new initialization, no recover() needed
            }
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
            return DistributorStart.STAT_ERR_APPLICATION_DB_SEND;
        }
        
        return DistributorStart.STAT_OK;
    }
    
    int executeReplication() throws Exception {
        // FIXME erstmal auskommentiert, weil es verhindert, dass die Sync nach Neustart fortgesetzt wird basierend auf DB-Flag
//        if (_state != SynchronizerState.SYNCHRONIZATION_STARTED) {
//            throw new IllegalStateException("Cannot execute synchronization: must be started first.");
//        }
        
        try {
            ConfigReplicator.replicateConfiguration(_masterDatabaseConnection, _localDatabaseConnection);
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not replicateConfiguration", e);
            return DistributorStart.STAT_ERR_APPLICATION_DB;
        } catch (ExitException ex) {
            Log.log(this, Log.FATAL, "could not replicateConfiguration", ex);
            return DistributorStart.STAT_ERR_FLG_BUP;
        }

        // set flag value and iCmd
        try {
            boolean success = FlagDAO.bUpdateFlag(_localDatabaseConnection, 
                                                  AmsConstants.FLG_RPL,
                                                  AmsConstants.FLAGVALUE_SYNCH_DIST_RPL,
                                                  AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR);
            if (!success) {
                Log.log(this, Log.FATAL,
                        "update not successful, could not update " + AmsConstants.FLG_RPL
                                + " from " + AmsConstants.FLAGVALUE_SYNCH_DIST_RPL + " to "
                                + AmsConstants.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR);
                return DistributorStart.STAT_ERR_FLG_RPL; // force new initialization, no recover() needed
            }
        } catch (SQLException e) {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
            return DistributorStart.STAT_ERR_APPLICATION_DB;
        }

        return DistributorStart.STAT_OK;
    }
    
}
