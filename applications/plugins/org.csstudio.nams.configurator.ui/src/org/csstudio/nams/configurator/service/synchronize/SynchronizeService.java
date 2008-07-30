package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.service.ThreadType;

/**
 * Ein Service zum Synchronisieren mit dem Hintergrundsystem.
 * 
 * XXX The name SynchronizeAutomaton may be better cause this "service" has
 * s.th. like a state (a own thread which is special automaton behavior).
 */
public interface SynchronizeService {

	/**
	 * Callback für den Service um den Aufrufer über den Arbeitsverlauf zu
	 * informieren.
	 */
	public abstract static interface Callback {
		/**
		 * (2) Informiert die Callback darüber, dass der Service nun beginnt,
		 * die Synchronisation vorzubereiten.
		 */
		public void bereiteSynchronisationVor();

		/**
		 * (2, Fehler) Informiert die Callback darüber, dass während der
		 * Vorbereitung der Synchronisation ein Fehler auftrat; die
		 * Synchronisation wird anschließend abgebrochen.
		 * 
		 * @param t
		 *            Der aufgetreten Fehler.
		 */
		public void fehlerBeimVorbereitenDerSynchronisation(Throwable t);

		/**
		 * (1) Prüft, ob es ungesicherte Änderungen gibt, aufgrund derer nicht
		 * synchronisiert werden kann oder sollte. Ggf. Rückfrage an den
		 * Anwender.
		 * 
		 * @return {@code true} if and only if synchronize should be proceeded,
		 *         {@code false} otherwise.
		 */
		public abstract boolean pruefeObSynchronisationAusgefuehrtWerdenDarf();

		/**
		 * (3) Informiert die Callback darüber, dass der Service nun eine
		 * Nachricht an das Hintergrundsystem senden wird.
		 */
		public void sendeNachrichtAnHintergrundSystem();

		/**
		 * (3, Fehler) Informiert die Callback darüber, dass beim senden der
		 * Nachricht an das Hintergrundsystem senden ein Fehler aufgetreten ist.
		 * 
		 * @param t
		 *            Der aufgetreten Fehler.
		 */
		public void sendenDerNachrichtAnHintergrundSystemFehlgeschalgen(
				Throwable t);

		/**
		 * (Fehler-Folge) Informiert über den abbruch den
		 * Synchrisationsvorhabens, wird gerufen, wenn der Benutzer
		 * {@link #pruefeObSynchronisationAusgefuehrtWerdenDarf()} verneint oder
		 * die synchronisation nach einem zuvor gemeldeten Fehler abgebrochen
		 * wird.
		 */
		public abstract void synchronisationAbgebrochen();

		/**
		 * (5) Gibt an, dass das der Service eine Antwort des Hintergrundsystems
		 * mit Erfolgsmeldung erhalten hat.
		 */
		public void synchronisationsDurchHintergrundsystemsErfolgreich();

		/**
		 * (5, Fehler) Gibt an, dass das der Service eine Antwort des
		 * Hintergrundsystems mit Meldung eines Fehlschlags erhalten hat.
		 * 
		 * @param fehlertext
		 *            Ein mit der Nachricht erhaltener Text zur Beschreibung des
		 *            Fehlers in der Regel der Stack-Trace als String.
		 */
		public void synchronisationsDurchHintergrundsystemsFehlgeschalgen(
				String fehlertext);

		/**
		 * (4) Informiert die Callback darüber, dass der Service eine Nachricht
		 * an das Hintergrundsystem gesendet hat und nun auf Antwort warten
		 * wird..
		 */
		public void wartetAufAntowrtDesHintergrundSystems();
	}

	static public enum ThreadTypes implements ThreadType {
		SYNCHRONIZER
	}

	/**
	 * Synchronisiert asynchron(!) die eingegebenen und gespeicherten Änderungen
	 * mit dem Hintergrundsystem. Zum Verlauf studieren Sie die Dokumentation
	 * der Operationen der Klasse {@link SynchronizeService.Callback}.
	 * 
	 * @param callback
	 *            Über diesen Callback werden Rückfragen an den Aufrufer
	 *            gestellt und (Zwischen-)Ergebnisse mitgeteilt.
	 */
	public void sychronizeAlarmSystem(Callback callback) throws Throwable;
}
