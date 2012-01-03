
/**
 * 
 */

package org.csstudio.nams.common.decision;

/**
 * @author Goesta Steen
 * 
 */
public interface Arbeitsfaehig {
	/**
	 * Beendet die Arbeit.
	 */
	public void beendeArbeit();

	/**
	 * Beginnt mit der Arbeit.
	 */
	public void beginneArbeit();

	/**
	 * Ist gerade am arbeiten
	 * 
	 * @return True or False
	 */
	public boolean istAmArbeiten();
}
