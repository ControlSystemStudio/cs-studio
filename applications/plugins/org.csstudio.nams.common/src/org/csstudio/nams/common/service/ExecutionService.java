
package org.csstudio.nams.common.service;

/**
 * This service is used to execute {@link StepByStepProcessor}s asynchronously
 * instead of usind new {@link Thread}(myRunnable).
 * 
 * TODO interupt aller Thread ermöglichen
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 */
public interface ExecutionService {

	/**
	 * Führt das Runnable asynchron aus.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @param groupId
	 *            Die Gruppenidentifikation, zu der das Runnable gehört
	 *            (vornehmlich zur Identifikation bei Tests); existiert diese
	 *            Gruppe nicht, so wird eine Fehler verursacht.
	 * @param runnable
	 *            Das Runnable, welches ausgeführt werden soll.
	 * @require hasGroupRegistered(groupId)
	 */
	public <GT extends Enum<?> & ThreadType> void executeAsynchronsly(
			GT groupId, StepByStepProcessor runnable);

	/**
	 * Liefert alle bis dato benutzten Gruppen-Ids.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @return Etwas aufzählbare, welches über die Gruppen-Ids iterieren kann.
	 */
	public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds();

	/**
	 * Liefert die Gruppe, welche unter der angegebenen Id registriert ist.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @param groupId
	 *            Die Gruppenidentifikation, über welche die {@link ThreadGroup}
	 *            identifiziert wird.
	 * @return Liefert die ThreadGroup.
	 * @require hasGroupRegistered(groupId)
	 */
	public <GT extends Enum<?> & ThreadType> ThreadGroup getRegisteredGroup(
			GT groupId);

	/**
	 * 
	 * Der Typ der Gruppenidentifikation - ein beliebiges Enum-Element.
	 * 
	 * @param groupId
	 *            Die Gruppenidentifikation, zu der die zu liefernden Runnable
	 *            zählen.
	 * @return Etwas aufzählbare, welches über die Runnables iterieren kann.
	 */
	public <GT extends Enum<?> & ThreadType> Iterable<StepByStepProcessor> getRunnablesOfGroupId(
			GT groupId);

	/**
	 * Prüft, ob unter der angegebenen Id eine Gruppe registriert ist.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @param groupId
	 *            Die Gruppenidentifikation, über welche die {@link ThreadGroup}
	 *            identifiziert wird.
	 * @return {@code true} wenn eine Gruppe mit der angegebenen Id existiert,
	 *         {@code false} sonst.
	 */
	public <GT extends Enum<?> & ThreadType> boolean hasGroupRegistered(
			GT groupId);

	/**
	 * Registriert eine neue {@link ThreadGroup}.
	 * 
	 * @param <GT>
	 *            Der Typ der Gruppenidentifikation - ein beliebiges
	 *            Enum-Element.
	 * @param groupId
	 *            Die Gruppenidentifikation, über welche die {@link ThreadGroup}
	 *            identifiziert wird.
	 * @param group
	 *            Die {@link ThreadGroup}.
	 */
	public <GT extends Enum<?> & ThreadType> void registerGroup(GT groupId,
			ThreadGroup group);
}
