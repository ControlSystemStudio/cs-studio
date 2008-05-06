/**
 * 
 */
package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.csstudio.nams.common.material.AlarmNachricht;

import de.c1wps.desy.ams.allgemeines.Millisekunden;

/**
 * @author Goesta Steen
 *
 */
public class ProcessVariableRegel implements VersandRegel {

	/* (non-Javadoc)
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht, de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		// TODO nothing to do here
	}

	/* (non-Javadoc)
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste, de.c1wps.desy.ams.allgemeines.Millisekunden)
	 */
	public Millisekunden pruefeNachrichtAufTimeOuts(
			Pruefliste bisherigesErgebnis,
			Millisekunden verstricheneZeitSeitErsterPruefung) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht, de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
			Pruefliste ergebnisListe) {
		// TODO Auto-generated method stub
		return null;
	}

}
