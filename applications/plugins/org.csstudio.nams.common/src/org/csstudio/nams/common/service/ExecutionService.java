package org.csstudio.nams.common.service;

/**
 * This service is used to execute {@link Runnable}s asynchronously instead of
 * usind new {@link Thread}(myRunnable).
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
public interface ExecutionService {

	/**
	 * Führt das Runnable asynchron aus.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @param group
	 *            Die Gruppenidentifikation, zu der das Runnable gehört
	 *            (vornehmlich zur Identifikation bei Tests.
	 * @param runnable
	 *            Das Runnable, welches ausgeführt werden soll.
	 */
	public <GT extends Enum<?>> void executeAsynchronsly(GT groupId,
			Runnable runnable); // FIXME Use StepByStepProcessor!!!

	/**
	 * Liefert alle bis dato benutzten Gruppen-Ids.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @return Etwas aufzählbare, welches über die Gruppen-Ids iterieren kann.
	 */
	public Iterable<Enum<?>> getCurrentlyUsedGroupIds();

	/**
	 * 
	 * Der Typ der Gruppenidentifikation - ein beliebiges Enum-Element.
	 * 
	 * @param groupId
	 *            Die Gruppenidentifikation, zu der die zu liefernden Runnable
	 *            zählen.
	 * @return Etwas aufzählbare, welches über die Runnables iterieren kann.
	 */
	public <GT extends Enum<?>> Iterable<Runnable> getRunnablesOfGroupId(
			GT groupId);
}
