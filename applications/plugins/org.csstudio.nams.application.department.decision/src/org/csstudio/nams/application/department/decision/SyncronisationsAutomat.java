package org.csstudio.nams.application.department.decision;

import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

/**
 * 
 * Automat zum syncronisieren der globalen und lokalen Konfiguration.
 */
public class SyncronisationsAutomat {
	
	private static volatile boolean macheweiter;
	private static Thread workingThread;
	private static volatile boolean isRunning;
	private static boolean canceled;

	/**
	 * Fordert distributor auf die globale Konfiguration in die lokale zu
	 * uebertragen. Die Zugangsdaten zu den Datenbanken kennt der distributor
	 * selber. Die operation blockiert bis zur erfolgreichen Rückmeldung oder
	 * einem interrupt auf dem ausführerendem Thread. Es ist erforderlich das
	 * vor der ausführung dieser Operation keine Zugriffe auf die lokale DB
	 * erfolgen.
	 * @param localStoreConfigurationService 
	 * @throws MessagingException 
	 * 
	 * FIXME Database-Flags setzen mit LocalStoreConfigurationServie (TEST!!).
	 * @throws InconsistentConfiguration 
	 * @throws StorageException 
	 * @throws StorageError 
	 * @throws UnknownConfigurationElementError 
	 */
	public static void syncronisationUeberDistributorAusfueren(
			Producer producer, Consumer consumer, LocalStoreConfigurationService localStoreConfigurationService) throws MessagingException, StorageError, StorageException, InconsistentConfiguration, UnknownConfigurationElementError {
		
		// TODO logger benutzen und nicht sysout
		
		/**
		 * Wenn der ReplicationState gerade auf einem Zustand des Distributors ist keine neue Aufforderung an ihn zum synchronisieren senden
		 */
		ReplicationStateDTO stateDTO = localStoreConfigurationService.getCurrentReplicationState();
		ReplicationState replicationState = stateDTO.getReplicationState();
		if (replicationState != ReplicationState.FLAGVALUE_SYNCH_DIST_RPL && replicationState != ReplicationState.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR) {
			stateDTO.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
			localStoreConfigurationService.saveCurrentReplicationState(stateDTO);
			producer.sendeSystemnachricht(new SyncronisationsAufforderungsSystemNachchricht());
		}
		
		
		macheweiter = true;
		workingThread = Thread.currentThread();
		canceled = false;
		isRunning = true;
		System.out.println("vor");
		while (macheweiter) {
			try {
				NAMSMessage receiveMessage = consumer.receiveMessage();
				
				if (receiveMessage.enthaeltSystemnachricht()) {
					if (receiveMessage.alsSystemachricht().istSyncronisationsBestaetigung()) {
						System.out.println("richtige nachricht");
						macheweiter = false;
					} else {
						// TODO systemnachricht die uns nicht interresiert?!?
					}
				} else {
					// TODO keine systemnachricht behandeln 
					System.out.println("falsche nachricht");
				}
				// TODO Klären: Alle Arten von Nachrichten acknowledgen?
				receiveMessage.acknowledge();
			} catch (MessagingException e) {
				throw new MessagingException(e);
			}
		}
		System.out.println("ende");
		isRunning = false;
		
//		// MapMessage mapMsg = amsSenderSession.createMapMessage();
//		MapMessage mapMsg = amsPublisherDist2.createMapMessage();
//		mapMsg.setString(MSGPROP_COMMAND,
//				MSGVALUE_TCMD_RELOAD_CFG_START);
//		// amsPublisherDist.send(mapMsg);
//		amsPublisherDist2.send(producerId, mapMsg);
//		boolean bRet = FlagDAO.bUpdateFlag(conDb, FLG_RPL,
//				FLAGVALUE_SYNCH_FMR_RPL,
//				FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
//		if (bRet) {
//			iCmd = CMD_RPL_WAITFOR_DIST;
//		} else {
//			Log.log(this, Log.FATAL,
//					"update not successful, could not update db flag to "
//							+ FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
//			return FilterManagerStart.STAT_ERR_FLG_RPL; // force new
//			// initialization,
//			// no recover()
//			// needed
//		}
		
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
	
	public static boolean hasBeenCanceled() {
		return canceled;
	}
	
	public static void cancel() {
		macheweiter = false;
		workingThread.interrupt();
		canceled = true;
		while( isRunning() ) {
			Thread.yield();
		}
	}

}
