
package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;

public interface Regelwerk {

	public Pruefliste gibDummyPrueflisteNichtSenden();

	public Regelwerkskennung gibRegelwerkskennung();

	/**
	 * Liefert eine neue Pruefliste.
	 * 
	 * @param zuPruefendeNachricht
	 */
	Pruefliste gibNeueLeerePruefliste();

	void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht alarmNachricht, Pruefliste pruefliste);

	/**
	 * Soll aufgerufen werden wenn eine Terminnotiz herein kommt.
	 */
	void pruefeNachrichtAufTimeOuts(Pruefliste pruefliste,
			Millisekunden verstricheneZeitSeitLetzterPruefung);

	// void injectHistoryService(HistoryService historyService);

	void pruefeNachrichtErstmalig(AlarmNachricht alarmNachricht,
			Pruefliste pruefliste);
}