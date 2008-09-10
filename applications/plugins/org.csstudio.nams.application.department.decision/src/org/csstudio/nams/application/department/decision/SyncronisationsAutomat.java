package org.csstudio.nams.application.department.decision;

import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.history.declaration.HistoryService;
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

	public static void cancel() {
		SyncronisationsAutomat.macheweiter = false;
		SyncronisationsAutomat.workingThread.interrupt();
		SyncronisationsAutomat.canceled = true;
		while (SyncronisationsAutomat.isRunning()) {
			Thread.yield();
		}
	}

	/**
	 * Indicates weather this automaton has been canceled. This may be done by
	 * calling {@link #cancel()} or if an error has been occured during
	 * processing.
	 * 
	 * @return true is canceled.
	 */
	public static boolean hasBeenCanceled() {
		return SyncronisationsAutomat.canceled;
	}

	public static boolean isRunning() {
		return SyncronisationsAutomat.isRunning;
	}

	/**
	 * Fordert distributor auf die globale Konfiguration in die lokale zu
	 * uebertragen. Die Zugangsdaten zu den Datenbanken kennt der distributor
	 * selber. Die operation blockiert bis zur erfolgreichen Rückmeldung oder
	 * einem interrupt auf dem ausführerendem Thread. Es ist erforderlich das
	 * vor der ausführung dieser Operation keine Zugriffe auf die lokale DB
	 * erfolgen.
	 * 
	 * @param localStoreConfigurationService
	 * @throws MessagingException
	 * 
	 * FIXME Database-Flags setzen mit LocalStoreConfigurationServie (TEST!!).
	 * (gs) Wird doch gemacht, oder?!?!
	 * 
	 * @throws InconsistentConfigurationException
	 * @throws StorageException
	 * @throws StorageError
	 * @throws UnknownConfigurationElementError
	 */
	public static void syncronisationUeberDistributorAusfueren(
			final Producer producer,
			final Consumer consumer,
			final LocalStoreConfigurationService localStoreConfigurationService,
			final HistoryService historyService) throws MessagingException,
			StorageError, StorageException, InconsistentConfigurationException,
			UnknownConfigurationElementError {

		/**
		 * Wenn der ReplicationState gerade auf einem Zustand des Distributors
		 * ist keine neue Aufforderung an ihn zum synchronisieren senden
		 */
		final ReplicationStateDTO stateDTO = localStoreConfigurationService
				.getCurrentReplicationState();
		final ReplicationState replicationState = stateDTO
				.getReplicationState();
		if ((replicationState != ReplicationState.FLAGVALUE_SYNCH_DIST_RPL)
				&& (replicationState != ReplicationState.FLAGVALUE_SYNCH_DIST_NOTIFY_FMR)) {
			stateDTO
					.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
			localStoreConfigurationService
					.saveCurrentReplicationState(stateDTO);
			producer
					.sendeSystemnachricht(new SyncronisationsAufforderungsSystemNachchricht());
		}

		SyncronisationsAutomat.macheweiter = true;
		SyncronisationsAutomat.workingThread = Thread.currentThread();
		SyncronisationsAutomat.canceled = false;
		SyncronisationsAutomat.isRunning = true;
		while (SyncronisationsAutomat.macheweiter) {
			try {
				final NAMSMessage receiveMessage = consumer.receiveMessage();

				if (receiveMessage.enthaeltSystemnachricht()) {
					if (receiveMessage.alsSystemachricht()
							.istSyncronisationsBestaetigung()) {
						SyncronisationsAutomat.macheweiter = false;
						historyService.logReceivedReplicationDoneMessage();
					}
				}
				// Alle Arten von Nachrichten acknowledgen
				receiveMessage.acknowledge();
			} catch (final MessagingException e) {
				// TODO Prüfen ob nötig.
				if (e.getCause() instanceof InterruptedException) {
					SyncronisationsAutomat.canceled = true;
				}
				throw e;
			} catch (final InterruptedException is) {
				SyncronisationsAutomat.canceled = true;
			}
		}
		SyncronisationsAutomat.isRunning = false;
	}

}
