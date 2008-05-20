package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;


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