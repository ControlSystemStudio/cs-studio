package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;

public interface SynchronizeService {

	/**
	 * Synchronisiert die eingegebenen und gespeicherten Änderungen mit dem
	 * Hintergrundsystem.
	 * 
	 * <li>
	 * <ol>
	 * Überprüfung auf offenen Editoren - ggf. Rückfrage der UI an den Benutzer
	 * </ol>
	 * <ol>
	 * Erstellen/Füllen der Synchronisations-Daten über
	 * {@link LocalStoreConfigurationService#prepareSynchonization()}
	 * </ol>
	 * <ol>
	 * Senden der JMS-Synauforderung (TODO NEUER Nachrichten-Typ!)
	 * </ol>
	 * <ol>
	 * Warten auf Antwort des Hintergrundsystems
	 * </ol>
	 * <ol>
	 * Tool informieren über Erfolg / Nicht Erfolg.
	 * </ol>
	 * </li>
	 * 
	 * @param callback
	 *            Über diesen Callback werden Rückfragen an den Aufrufer
	 *            gestellt und (Zwischen-)Ergebnisse mitgeteilt.
	 */
	public void sychronizeAlarmSystem(Callback callback) throws Throwable;

	public abstract static interface Callback {
		/**
		 * Prüft, ob es ungesicherte Änderungen gibt, aufgrund derer nicht
		 * synchronisiert werden kann oder sollte. Ggf. Rückfrage an den
		 * Anwender.
		 * 
		 * @return {@code true} if and only if synchronize should be proceeded,
		 *         {@code false} otherwise.
		 */
		public abstract boolean pruefeObSynchronisationAusgefuehrtWerdenDarf();
		
		public void bereiteSynchronisationVor();
		public void fehlerBeimVorbereiteDerSynchronisation(Throwable t);
		
		public void sendeNachrichtAnHintergrundSystem();
		
		public void synchronisationsBestaetigungDesHintergrundSystemsErhalten();
		
		/**
		 * Informiert über den abbruch den Synchrisationsvorhabens.
		 */
		public abstract void synchronisationAbgebrochen();
	}
}
