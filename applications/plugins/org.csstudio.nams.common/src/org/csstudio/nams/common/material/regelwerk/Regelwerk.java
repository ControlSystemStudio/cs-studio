package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;


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
			Millisekunden verstricheneZeitSeitLetzterPruefung, AlarmNachricht initialeNachricht);
	
	void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(AlarmNachricht alarmNachricht, Pruefliste pruefliste);
	
	
//	void injectHistoryService(HistoryService historyService);

	public Pruefliste gibDummyPrueflisteNichtSenden();
	
}