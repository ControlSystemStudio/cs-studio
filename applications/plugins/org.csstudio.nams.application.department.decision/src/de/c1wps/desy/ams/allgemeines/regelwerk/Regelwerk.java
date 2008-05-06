package de.c1wps.desy.ams.allgemeines.regelwerk;

import de.c1wps.desy.ams.allgemeines.AlarmNachricht;
import de.c1wps.desy.ams.allgemeines.Millisekunden;


public interface Regelwerk {

	public Regelwerkskennung gibRegelwerkskennung();
	
	/**
	 * Liefert eine neue Pruefliste.
	 * @param zuPruefendeNachricht 
	 */
	Pruefliste gibNeueLeerePruefliste();

	void pruefeNachrichtErstmalig(AlarmNachricht alarmNachricht, Pruefliste pruefliste);

	/**
	 * Soll aufgerufen werden wenn eine Terminnotiz herein kommt.
	 */
	void pruefeNachrichtAufTimeOuts(Pruefliste pruefliste,
			Millisekunden verstricheneZeitSeitLetzterPruefung);
	
	void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(AlarmNachricht alarmNachricht, Pruefliste pruefliste);
	
	


	public Pruefliste gibDummyPrueflisteNichtSenden();
	
}