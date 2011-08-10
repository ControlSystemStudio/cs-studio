
/*
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

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
 * 
 * Normaler Ablauf einer Synchronization:
 * <ol>
 * <li>empfangen einer SyncAufforderung auf externem Command Topic</li>
 * <li>schreiben des ReplicationStates</li>
 * <li>senden der Aufforderung an den Distributor ueber das "normale" Ausgangskorb Topic</li>
 * <li>warten auf SyncBestaetigung auf internem Command Topic</li>
 * <li>schreiben eines History Eintrages</li>
 * </ol>
 * 
 * Wichtig:
 * internes und externen Command Topic muessen unterschiedlich sein.
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
		}
		
		// Muss immer gesendet werden da kurz aufeinander folgende Synchronizationsaufforderungen
		// dazu fuehren koennen, dass der ReplicationState vom Distributor noch nicht geaendert wurde.
		producer.sendeSystemnachricht(new SyncronisationsAufforderungsSystemNachchricht());

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
