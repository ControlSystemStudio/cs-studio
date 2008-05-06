/**
 * 
 */
package de.c1wps.desy.ams.allgemeines;

/**
 * @author Goesta Steen
 *
 */
public interface Arbeitsfaehig {
	/**
	 * Beginnt mit der Arbeit.
	 */
	public void beginneArbeit();
	
	/**
	 * Ist gerade am arbeiten
	 * @return
	 */
	public boolean istAmArbeiten();
	
	/**
	 * Beendet die Arbeit.
	 */
	public void beendeArbeit();
}
